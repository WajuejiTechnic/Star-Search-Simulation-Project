package simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Controller {

	private StarMap starMap;
	private List<Drone> activeDrones;
	private Simulator simulator;
	private Object action;
	private Object param;
	private ActionPair actionPair;
	private Scanner askUser = new Scanner(System.in);
	private int maxWidth = 100;
	private int maxHeight = 100;

	public Controller(Simulator simulator) {
		this.simulator = simulator;
		this.starMap = new StarMap(this.simulator.getSystemMap());
	}
	
	public void startTurn(int turns, Boolean showState) throws Exception {
		this.activeDrones = this.starMap.getAllActiveDrones();
		for(Drone drone: this.activeDrones){
			if(drone.getCrashed()) continue;
			this.actionPair = this.selectAction(drone, turns);
			String response = this.simulator.validateAction(drone, this.actionPair);
			if (showState) this.simulator.renderRegion();
			this.simulator.displayActionAndResponses(drone.getId(), this.actionPair, response);
			//System.out.println("print map .....");
			//this.simulator.getSystemMap().print_map();
			if(this.simulator.isGameOver()) return;
		}
	}
	
	// select action for drone based on its strategy
	private ActionPair selectAction(Drone drone, int turns) throws Exception {
		if (drone.getStrategy() == 0) {
			return this.selectRandomAction();
		}
		else if (drone.getStrategy() == 1) {
			return this.selectBestAction(drone, turns);
		}
		else if (drone.getStrategy() == 2) {
			return this.selectActionByUser();
		}
		else {
			throw new Exception("ERROR: Invalid strategy " + drone.getStrategy());
		}
	}
	
	// for strategy 0
	// select an action randomly
	private ActionPair selectRandomAction() {
		Random rd = new Random();
		this.action = ActionType.values()[rd.nextInt(ActionType.values().length)];
		if(this.action == ActionType.THRUST){
			this.param = rd.nextInt(3) + 1;
		}
		else if (this.action == ActionType.STEER){
			this.param = Direction.values()[rd.nextInt(Direction.values().length)];
		}
		else {
			this.param = null;
		}
		return new ActionPair(this.action, this.param);
				
	}	
	
	// get square have been explored or scanned by coordinate(x, y)
	private Square getSquareExploredOrScanned(int x, int y){
		List<Square> exploredOrScannedSquares = this.starMap.getAllSquaresExploredOrScanned();
		for(Square square: exploredOrScannedSquares) {
			if (square.getX() == x && square.getY() == y) {
				return square;
			}
		}
		return null;
	}

	private List<String> checkAround(int cx, int cy) {
		List<String> result = new ArrayList<>();
		for(Direction dir: Direction.values()){
			Coordinate xy = this.simulator.getNewXY(cx, cy, dir);
			int nx = xy.getX();
			int ny = xy.getY();
			if (nx < 0 || nx >= this.maxWidth || ny < 0 || ny >= this.maxHeight) {
				result.add("barrier");
			}
			else {
				Square square = this.getSquareExploredOrScanned(nx, ny);
				if (square == null) {
					result.add("unknown");
				}
				else result.add(square.getName().toString().toLowerCase());
			}
		}
		//System.out.println("sqrsAround =" + String.join(",", result));
		return result;
	}
	
	private List<Integer> move(int cx, int cy, Direction cdir) {
		List<Integer> rel = new ArrayList<>();
		int stars = 0;
		int steps = 0;
		while(steps < 3) {
			//System.out.println("moves = " + moves);
			Coordinate xy = this.simulator.getNewXY(cx, cy, cdir);
			int nx = xy.getX();
			int ny = xy.getY();
			if (nx < 0 || nx >= this.maxWidth || ny < 0 || ny >= this.maxHeight) {
				break;
			}
			else {
				Square sqr = this.getSquareExploredOrScanned(nx, ny);
				if (sqr == null) {
					break;
				}
				else {
					if (sqr.getName() == SquareType.STARS || sqr.getName() == SquareType.EMPTY) {
						if (sqr.getName() == SquareType.STARS) stars += 1;
						steps += 1;
						cx = nx;
						cy = ny;
						continue;
					}
					else break;
				}
			}
		}
		rel.add(stars);
		rel.add(steps);
		return rel;
	}
	
	private Boolean mapSizeExplored() {
		if (this.maxWidth != 100 && this.maxHeight != 100) 
			return true;
		return false;
	}
	
	private Boolean isSquareExplored(int x, int y){
		List<Square> explored = this.starMap.getAllSquaresExplored();
		for(Square sqr: explored) {
			if(sqr.getX() == x && sqr.getY() == y)
				return true;
		}
		return false;
	}
	
	private List<Coordinate> getSquaresUnexplored() {
		List<Coordinate> sqrs = new ArrayList<>();
		for(int i = 0; i < this.maxWidth; i++) {
			for(int j = 0; j < this.maxHeight; j++) {
				if(!this.isSquareExplored(i, j)) {
					sqrs.add(new Coordinate(i, j));
				}
			}
		}
		return sqrs;
	}
	
	private ActionPair createAction(Direction direction, Direction dir, int steps) {
		if (dir != direction) {
			//System.out.println("DIR: " + dir + " !=" + direction);
			return new ActionPair(ActionType.STEER, dir);
		}
		else if (steps >= 1 && steps <= 3){
			//System.out.println("DIR: " + dir + "==" + direction);
			return new ActionPair(ActionType.THRUST, steps);
		}
		else {
			return new ActionPair(ActionType.SCAN, null);
		}
	}
	
	private ActionPair getActionSelected(int cx, int cy, Direction direction, Direction[] directionToMove) {
		int steps = 0;
		int stars = 0;
		int moves = 0;
		Direction dir = direction;
		Direction dirToMove = direction;
		for(Direction cdir: directionToMove){
			List<Integer> rel = this.move(cx, cy, cdir);
			//System.out.println(cdir + " stars, steps = " + rel.get(0) + " " + rel.get(1));
			if(rel.get(0) > stars || (rel.get(0) == stars && cdir == direction)) {
				dir = cdir;
				stars = rel.get(0);
				steps = rel.get(1);
			}
			if(rel.get(1) > moves) {
				dirToMove = cdir;
				moves = rel.get(1);
			}
		}
		//System.out.println("direction, stars, steps, moves = " 
		//					+ direction + " " + stars + " " + steps + " " + moves);
		if (directionToMove.length == 3 && steps == 3) moves = 2;
		if (stars > 0) return this.createAction(direction, dir, steps);
		return this.createAction(direction, dirToMove, moves);
	}
	
	private Direction[] addDirection(Direction d0, Direction d1, Direction d2) {
		Direction[] dirToMove = new Direction[3];
		dirToMove[0] = d0;
		dirToMove[1] = d1;
		dirToMove[2] = d2;
		return dirToMove;
	}
	
	private Direction[] dirToMove(int x, int y) {	
		if (!this.mapSizeExplored()) return new Direction[0];
		Direction[] dirToMove = new Direction[3];
		List<Coordinate> sqrs = this.getSquaresUnexplored();
		Coordinate coord = sqrs.get(0);
		//System.out.print("coord: " + coord.getX() + " " + coord.getY());
		if (x < coord.getX() && y < coord.getY()) {
			dirToMove = this.addDirection(Direction.NORTHEAST, Direction.EAST, Direction.NORTH);
		}
		else if (x < coord.getX() && y > coord.getY()) {
			dirToMove = this.addDirection(Direction.SOUTHEAST, Direction.EAST, Direction.SOUTH);
		}
		else if (x > coord.getX() && y > coord.getY()) {
			dirToMove = this.addDirection(Direction.SOUTHWEST, Direction.WEST, Direction.SOUTH);
		}
		else if (x > coord.getX() && y < coord.getY()) {
			dirToMove = this.addDirection(Direction.NORTHWEST, Direction.WEST, Direction.NORTH);
		}
		else if (x == coord.getX() && y < coord.getY()) {
			dirToMove = this.addDirection(Direction.NORTH, Direction.NORTHWEST, Direction.NORTHEAST);
		}
		else if (x == coord.getX() && y > coord.getY()) {
			dirToMove = this.addDirection(Direction.SOUTH, Direction.SOUTHWEST, Direction.SOUTHEAST);
		}
		else if (x < coord.getX() && y == coord.getY()) {
			dirToMove = this.addDirection(Direction.EAST, Direction.NORTHEAST, Direction.SOUTHEAST);
		}
		else if (x > coord.getX() && y == coord.getY()) {
			dirToMove = this.addDirection(Direction.WEST, Direction.NORTHWEST, Direction.SOUTHWEST);
		}
		return dirToMove;
	}
		
	private ActionPair selectBestAction(Drone drone, int turns){
		//if fuel less than 3, recharge
		if(drone.getFuel() < 3) return new ActionPair(ActionType.RECHARGE, null);

		int cx = drone.getX();
		int cy = drone.getY();
		Direction direction = drone.getDirection();
		
		List<String> sqrsAround = this.checkAround(cx, cy);
		//System.out.println("width= " + this.maxWidth +  ", height =" + this.maxHeight);
		if (sqrsAround.indexOf("unknown") != -1){
			//System.out.println("try scan......");
			return new ActionPair(ActionType.SCAN, null);
		}
		else if (sqrsAround.indexOf("stars") != -1){
			//System.out.println("try thrust or steer......");
			return this.getActionSelected(cx, cy, direction, Direction.values());
		}
		else if (sqrsAround.indexOf("empty") != -1) {
			//System.out.println("try thrust to move to target......");
			Direction[] dirToMove = this.dirToMove(cx, cy);
			if (dirToMove.length == 0)
				return this.getActionSelected(cx, cy, direction, Direction.values());
			return this.getActionSelected(cx, cy, direction, dirToMove);
		}
		else {
			return new ActionPair(ActionType.SCAN, null);
		}
	}
	
	// for strategy 2 
	// allow the user to select an action via the input prompt (diagnostic only!)
	private ActionPair selectActionByUser() {
		// generate a move by asking the user - DIAGNOSTIC ONLY
		System.out.print("action?: ");
		this.action = this.askUser.nextLine().trim().toUpperCase();
 
		if (this.action.equals(ActionType.STEER.toString())) {
		    System.out.print("direction?: ");
		    this.param = this.askUser.nextLine().trim().toUpperCase();
		} else if (this.action.equals(ActionType.THRUST.toString())) {
		    System.out.print("distance?: ");
		    this.param = Integer.parseInt(this.askUser.nextLine().trim());
		}
		else {
			this.param = null;
		}
		return new ActionPair(this.action, this.param);
	}
	
	public void setMapWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public void setMapHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}
}
