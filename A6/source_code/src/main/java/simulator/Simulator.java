package simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Simulator {
	private int maxNumOfTurns;
	private SystemMap systemMap;
	private Controller controller;

	private static HashMap<ActionType, Integer> FUEL_COST = new HashMap<ActionType, Integer>(){{
		put(ActionType.STEER, 1);
		put(ActionType.THRUST, 1);
		put(ActionType.SCAN, 1);
		put(ActionType.PASS, 0);
		put(ActionType.RECHARGE, 0);
	}};

	public Simulator(String testFileName) throws Exception{
		this.systemMap = new SystemMap(testFileName);
		this.maxNumOfTurns = this.systemMap.getTurnLimit();
		this.controller = new Controller(this);
		//System.out.println("maxNumOfTurns = " + this.maxNumOfTurns);
	}
	
	public void startGame(Boolean showState) throws Exception{
		for(int turns = 1; turns <= this.maxNumOfTurns; turns++) {
			this.controller.startTurn(turns, showState);
			if(this.isGameOver() || turns == this.maxNumOfTurns) {
				this.displayReport(turns);
				break;
			}
		}
	}

	private String checkSteer(Direction direction, Drone drone) {
		if(drone.getFuel() >= FUEL_COST.get(ActionType.STEER)) {
			drone.setDirection(direction);
			drone.costFuel(FUEL_COST.get(ActionType.STEER));
			return "ok";
		}
		else{
			return "not_enough_energy_recharge_first";
		}
	}
	
	// "NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST"
	public Coordinate getNewXY(int cx, int cy, Direction direction) {
		switch(direction.toString()) {
		case "NORTH":
			return new Coordinate(cx, cy + 1);
		case "NORTHEAST":
			return new Coordinate(cx + 1, cy + 1);
		case "EAST":
			return new Coordinate(cx + 1, cy);
		case "SOUTHEAST":
			return new Coordinate(cx + 1, cy - 1);
		case "SOUTH":
			return new Coordinate(cx, cy - 1);
		case "SOUTHWEST":
			return new Coordinate(cx - 1, cy - 1);
		case "WEST":
			return new Coordinate(cx - 1, cy);
		case "NORTHWEST":
			return new Coordinate(cx - 1, cy + 1);	
		default:
			return new Coordinate(cx, cy);	
		}
	}
	
	private String checkThrust(int steps, Drone drone) {
		if (steps < 1 || steps > 3) {
			return "action_not_recognized";
		}
		
		for(int i = 0; i < steps; i++) {
			if(drone.getFuel() < FUEL_COST.get(ActionType.THRUST)) return "not_enough_energy_recharge_first";


			int x = drone.getX();
			int y = drone.getY();
			Direction direction = drone.getDirection();
			Coordinate coord = this.getNewXY(x, y, direction);
			int nx = coord.getX();
			int ny = coord.getY();
			if (nx < 0 || nx >= this.systemMap.getWidth() || ny < 0 || ny >= this.systemMap.getHeight()) {
				if (nx == this.systemMap.getWidth()) this.controller.setMapWidth(nx);
				if (ny == this.systemMap.getHeight()) this.controller.setMapHeight(ny);
			}	
			else {
				Square newSqr = this.systemMap.getSquare(nx, ny);
				Square oldSqr = this.systemMap.getSquare(x, y);
				//System.out.println("d" + String.valueOf(this.id) + " thrust: " 
				//	+ String.valueOf(this.x) + " " + String.valueOf(this.y) 
				//	+ " " + String.valueOf(nx) + " " + String.valueOf(ny));
				if (newSqr.getName() == SquareType.SUN) {
					newSqr.setState(State.EXPLORED);
					drone.setCrashed(true);
				}
				else if (newSqr.getName() == SquareType.DRONE){
					newSqr.setName(SquareType.EMPTY);
					Drone d = this.systemMap.getActiveDrone(nx, ny);
					d.setCrashed(true);
					drone.setCrashed(true);
				}
				else { // STAR OR EMPTY
					//make the move and cost fuel
					drone.costFuel(FUEL_COST.get(ActionType.THRUST));

					newSqr.setState(State.EXPLORED);
					newSqr.setName(SquareType.DRONE);
					drone.setX(nx);
					drone.setY(ny);
				}
				oldSqr.setName(SquareType.EMPTY);
				if (drone.getCrashed()) {
					return "crash";
				}
			}
		}
		return "ok";
	}
	
	private String checkScan(Drone drone){
		if(drone.getFuel() < FUEL_COST.get(ActionType.SCAN)) return "not_enough_energy_recharge_first";
		drone.costFuel(FUEL_COST.get(ActionType.SCAN));

		List<String> result = new ArrayList<>();
		int x = drone.getX();
		int y = drone.getY();
		for(Direction dir: Direction.values()){
			//System.out.println("direction" + dir.toString());
			Coordinate coord = getNewXY(x, y, dir);
			int nx = coord.getX();
			int ny = coord.getY();
			if (nx < 0 || nx >= this.systemMap.getWidth() || ny < 0 || ny >= this.systemMap.getHeight()) {
				if (nx == this.systemMap.getWidth()) this.controller.setMapWidth(nx);
				if (ny == this.systemMap.getHeight()) this.controller.setMapHeight(ny);
				result.add("barrier");
			}
			else {
				Square square = this.systemMap.getSquare(nx, ny);
				switch(square.getName()){
				case DRONE:
					result.add("drone");
					break;
				case SUN:
					if(square.getState() == State.HIDE) {
						this.systemMap.getSquare(nx, ny).setState(State.EXPLORED);
					}
					result.add("sun");
					break;
				case STARS: 
					if(square.getState() == State.HIDE) {
						this.systemMap.getSquare(nx, ny).setState(State.SCANNED);
					}
					result.add("stars");
					break;
				case EMPTY:
					result.add("empty");
					break;
				default:
					result.add("unkonw");
					break;
				}
			}
		}
		return String.join(",", result);
	}
	
	private String checkPass(){
		return "ok";
	}

	private String checkRecharge(Drone drone){
		if(drone.fuelFull()) return "fuel_already_full";

		drone.addFuel(this.systemMap.getRechargeFuel());
		return "ok";
	}
	
	// validate action for a specific drone
	public String validateAction(Drone drone, ActionPair actionPair) {
		try {
			Object act = actionPair.getAction();
			Object param = actionPair.getParam();
			ActionType action = ActionType.valueOf(act.toString().toUpperCase());
			if(action == ActionType.STEER) {
				String dir = param.toString().toUpperCase();
				Direction direction = Direction.valueOf(dir);	
				return this.checkSteer(direction, drone);
			}
			if(action == ActionType.THRUST) {
				int steps = (int)param;
				return this.checkThrust(steps, drone);
			}
			if(action == ActionType.SCAN){
				return this.checkScan(drone);
			}
			if(action == ActionType.PASS) {
				return this.checkPass();
			}
			if(action == ActionType.RECHARGE) {
				return this.checkRecharge(drone);
			}
			return "action_not_recognized";
		}
		catch (Exception e) {
			//System.out.println("EXCEPTION RETURN TRUE");
			return "action_not_recognized";
		}
	}
	
	// check if simulation run should be halted
	public Boolean isGameOver(){
		if(this.systemMap.allDronesCrashed() || this.systemMap.allSafeSquaresExplored()) 
			return true;
		else 
			return false;
	}
	
	// display two lines output for each action taken by drone
	public void displayActionAndResponses(int id, ActionPair actionPair, String response) {
        // display the drone's actions
		Object act = actionPair.getAction();
		Object param = actionPair.getParam();
		
        System.out.print("d" + String.valueOf(id) + "," + act.toString().toLowerCase());
        if(param != null) {
        	System.out.println("," + param.toString().toLowerCase());
        }
        else {
        	System.out.println();
        }

        // display the simulation checks and/or responses
        System.out.println(response);
    }
	
	// display the last line output with four numbers
	private void displayReport(int completeTurns){
		int mapSize = this.systemMap.getMapSize();
		int numOfSuns = this.systemMap.getNumOfSuns();
		int numOfExplorableSquares = mapSize - numOfSuns; 
		int numOfExploredSafeSquares = this.systemMap.getAllSafeSquaresExplored().size();
		
		System.out.println(String.valueOf(mapSize) + "," + String.valueOf(numOfExplorableSquares)
		+ "," + String.valueOf(numOfExploredSafeSquares) + "," + String.valueOf(completeTurns));
	}
	
	private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }
	 
	public void renderRegion() {
        int i, j;
        int regionWidth = this.systemMap.getWidth();
        int regionHeight = this.systemMap.getHeight();
        int charWidth = 2 * regionWidth + 2;

        // display the rows of the region from top to bottom
        for (j = regionHeight - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < regionWidth; i++) {
                System.out.print("|");

                // the drone overrides all other contents
                Square square = this.systemMap.getSquare(i, j);
                if (square.getName() == SquareType.DRONE) {
                	int id = this.systemMap.getActiveDrone(i, j).getId();
                    System.out.print(String.valueOf(id));
                } else {
                    switch (square.getName()) {
                        case EMPTY:
                            System.out.print(" ");
                            break;
                        case STARS:
                            System.out.print(".");
                            break;
                        case SUN:
                            System.out.print("s");
                            break;
                        default:
                            break;
                    }
                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < regionWidth; i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the drone's directions
        List<Drone> activeDrones = this.systemMap.getAllActiveDrones();
        for(Drone drone: activeDrones) {
            System.out.println("dir d" + String.valueOf(drone.getId()) + ": " 
            		+ drone.getDirection().toString() + " with fuel " + String.valueOf(drone.getFuel()));
        }
        System.out.println("");
    }
	
	public SystemMap getSystemMap() {
		return this.systemMap;
	}
}
