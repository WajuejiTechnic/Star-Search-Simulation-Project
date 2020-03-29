
public class Drone {
	private int id;
	private int x;
	private int y;
	private Direction direction;
	private int strategy;
	private Boolean crashed;
	private int fuel;
	private int maxFuel;
	
	public void createDrone(int id, int x, int y, Direction direction, int strategy, int initialFuel, int maxFuel){
		this.id = id;
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.strategy = strategy;
		this.crashed = false;
		this.fuel = initialFuel;
		this.maxFuel = maxFuel;
	}

	public int getId() {
		return id;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getStrategy() {
		return strategy;
	}

	public Boolean getCrashed() {
		return crashed;
	}

	public void setCrashed(Boolean crashed) {
		this.crashed = crashed;
	}

	//fuel related functions
	public int getFuel() {
		return this.fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
		if(this.fuel > this.maxFuel) this.fuel = this.maxFuel; //maximum fuel allowed is 30
	}

	public void addFuel(int fuel) {
		this.fuel += fuel;
		if(this.fuel > this.maxFuel) this.fuel = this.maxFuel; //maximum fuel allowed is 30
	}

	public void costFuel(int fuel) {
		this.fuel -= fuel;
	}

	public Boolean fuelFull(){
		if (this.fuel == this.maxFuel){
			return true;
		}
		else
			return false;
	}
}
