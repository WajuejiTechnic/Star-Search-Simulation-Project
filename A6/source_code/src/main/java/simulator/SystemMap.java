package simulator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SystemMap {
	private int width;
	private int height;
	private int numOfDrones;
	private int numOfSuns;
	private Square[][] square;
	private Drone[] drone;
	private int turnLimit;

	private int initialFuel;
	private int maxFuel;
	private int rechargeFuel;
	
	public SystemMap(String testFileName) throws Exception{
		final String DELIMITER = ",";
		//System.out.println("Open file " + testFileName);
		
        try (Scanner takeCommand = new Scanner(new File(testFileName)))
        {
	        String[] tokens;
	
	        // read in the region information
	        tokens = takeCommand.nextLine().split(DELIMITER);
	        this.width = Integer.parseInt(tokens[0]); 
	        /*
	        if (this.width < 1 || this.width > 20) {
	        	throw new Exception("ERROR: Width (x-axis) of the space region shoule be [min: 1, max: 20]");
	        }
	        */
	        tokens = takeCommand.nextLine().split(DELIMITER);
	        this.height = Integer.parseInt(tokens[0]);
	        /*
	        if (this.height < 1 || this.height > 15) {
	        	throw new Exception("ERROR: Height (y-axis) of the space region shoule be [min: 1, max: 15]");
	        }
	        */
	        
	        this.square = new Square[this.width][this.height];
	        for (int i = 0; i < width; i++) {
	            for (int j = 0; j < this.height; j++) {
	            	this.square[i][j] = new Square();
	            	this.square[i][j].createSquare(i, j, SquareType.STARS);
	            }
	        }
	        // read in the drone information
	        tokens = takeCommand.nextLine().split(DELIMITER);
	        this.numOfDrones = Integer.parseInt(tokens[0]);
	        /*
	        if (this.numOfDrones < 1 || this.numOfDrones > 10) {
	        	throw new Exception("ERROR: Number of drones shoule be [min: 1,  max: 10]");
	        }
	        */
	        this.drone = new Drone[this.numOfDrones];

			//read in fuel configuration
			tokens = takeCommand.nextLine().split(DELIMITER);
			this.initialFuel = Integer.parseInt(tokens[0]);

			tokens = takeCommand.nextLine().split(DELIMITER);
			this.rechargeFuel = Integer.parseInt(tokens[0]);

			tokens = takeCommand.nextLine().split(DELIMITER);
			this.maxFuel = Integer.parseInt(tokens[0]);

	        for (int k = 0; k < this.numOfDrones; k++) {
	            tokens = takeCommand.nextLine().split(DELIMITER);
	            int i = Integer.parseInt(tokens[0]);
	            int j = Integer.parseInt(tokens[1]);
	            String dir = tokens[2].toUpperCase();
	            Direction direction = Direction.valueOf(dir);
	            /*
	            if (direction == null) {
	            	throw new Exception("ERROR: Invalid direction " + dir);
	            }
	            */
	            int strategy = Integer.parseInt(tokens[3]);
	            this.drone[k] = new Drone();
	            this.drone[k].createDrone(k, i, j, direction, strategy, this.initialFuel, this.maxFuel);
	            this.square[i][j].setName(SquareType.DRONE);
	            this.square[i][j].setState(State.EXPLORED);
	            //System.out.println("Drone?"  + square[i][j].getState());
	        }
	
	        // read in the sun information
	        tokens = takeCommand.nextLine().split(DELIMITER);
	        this.numOfSuns = Integer.parseInt(tokens[0]);
	        /*
	        int maxNumOfSuns = (int)(this.height * this.width * 0.5);
	        if (this.numOfSuns < 0 || this.numOfSuns > maxNumOfSuns) {
	        	throw new Exception("ERROR: Number of suns shoule be [min: 1, max: + " + maxNumOfSuns + " ]");
	        }
	        */
	        for (int k = 0; k < this.numOfSuns; k++) {
	            tokens = takeCommand.nextLine().split(DELIMITER);
	            int i = Integer.parseInt(tokens[0]);
	            int j = Integer.parseInt(tokens[1]);
	            this.square[i][j].setName(SquareType.SUN);
	        }
	
	        // read maximum number of turns
	        tokens = takeCommand.nextLine().split(DELIMITER);
	        this.turnLimit = Integer.parseInt(tokens[0]);
	        /*
	        if (turnLimit > 200){
	        	throw new Exception("ERROR: Maximum number of turns is larger than 200");
	        }
	        */
	        
	        //this.print_map();


        }   
	}
	
	/*
	public void print_map(){
		System.out.println("width = " + this.width + " height = " + this.height);
		System.out.println("numOfDrones = " + this.numOfDrones 
        		+ " numOfSuns = " + this.numOfSuns);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
            	if (square[i][j].getName() == SquareType.DRONE){
            		for (int k = 0; k < this.numOfDrones; k++){
            			if (drone[k].getX() == i && drone[k].getY() == j){
            				System.out.println(drone[k].getX() + " " + drone[k].getY() 
            						+ " " + drone[k].getDirection() + " "+ drone[k].getStrategy() 
            						+ " " + drone[k].getCrashed() + " " + drone[k].getId());
            			}
            		}
            	}
            	else {
            		System.out.println(i + " " + j + " " + square[i][j].getName());
            	}
            	System.out.println(square[i][j].getState());
            }
        }
	}*/
	
	// check if all drones crashed
	public Boolean allDronesCrashed(){
		for (int k = 0; k < this.numOfDrones; k++) {
			if (!this.drone[k].getCrashed()) 
				return false; 
		}
		return true;
	}
	
	// get square by coordinate(x, y)
	public Square getSquare(int x, int y) {
		return this.square[x][y];
	}
	
	// get active drone by coordinate(x, y)
	public Drone getActiveDrone(int x, int y) {
		List<Drone> activeDrones = this.getAllActiveDrones();
		for (Drone drone: activeDrones){
			if (drone.getX() == x && drone.getY() == y)
				return drone;
		}
		return null;
	}
	
	public List<Square> getAllSquaresExplored(){
		List<Square> exploredSquares = new ArrayList<>();
		for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
            	if(this.square[i][j].isExplored()){
            		exploredSquares.add(this.square[i][j]);
            	}
            }
        }
		return exploredSquares;
	}
	
	// get all squares have been explored or scanned
	public List<Square> getAllSquaresExploredOrScanned(){
		List<Square> exploredSquares = new ArrayList<>();
		for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
            	if(this.square[i][j].isExplored() || this.square[i][j].isScanned()){
            		exploredSquares.add(this.square[i][j]);
            	}
            }
        }
		return exploredSquares;
	}
	
	// get all safe squares have been explored (excluding sun) 
	public List<Square> getAllSafeSquaresExplored(){
		List<Square> exploredSquares = new ArrayList<>();
		for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
            	if(this.square[i][j].isExplored() && 
            			this.square[i][j].getName()!= SquareType.SUN){
            		//System.out.println("square " + String.valueOf(i) + String.valueOf(j));
            		exploredSquares.add(this.square[i][j]);
            	}
            }
        }
		return exploredSquares;
	}
	
	// check if all safe squares explored
	public Boolean allSafeSquaresExplored(){
		for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
            	if(this.square[i][j].getName()!= SquareType.SUN
            			&& !this.square[i][j].isExplored()) {
            		return false;
            	}
            }
        }
		return true;
	}
	
	// get all active drones
	public List<Drone> getAllActiveDrones(){
		List<Drone> activeDrones = new ArrayList<>();
		for(int k = 0; k < this.numOfDrones; k++){
			if(!this.drone[k].getCrashed()){
				activeDrones.add(this.drone[k]);
			}
		}
		return activeDrones;
	}

	public List<Drone> getAllDrones(){
		List<Drone> drones = new ArrayList<>();
		for(int k = 0; k < this.numOfDrones; k++){
			drones.add(this.drone[k]);
		}
		return drones;
	}

	public int getExplorableSquares(){
		return this.getMapSize() - this.numOfSuns;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
	
	public int getMapSize() {
		return this.width * this.height;
	}
	
	public int getNumOfSuns() {
		return this.numOfSuns;
	}

	public int getTurnLimit() {
		return this.turnLimit;
	}

	public int getRechargeFuel() {
		return this.rechargeFuel;
	}

	public int getNumOfDrones() {
		return this.numOfDrones;
	}

	public Drone getDroneById(int id){
		return this.drone[id];

	}

	public int[] getFuels (){
		int[] fuels = new int[3];
		fuels[0] = this.initialFuel;
		fuels[1] = this.rechargeFuel;
		fuels[2] = this.maxFuel;
		return fuels;
	}
}
