
public class Square {
	private int x;
	private int y;
	private SquareType name;
	private State state;
	
	public void createSquare(int x, int y, SquareType name){
		this.x = x;
		this.y = y;
		this.name = name;
		this.state = State.HIDE;
	}
	
	public Boolean isScanned(){
		if(this.state == State.SCANNED)
			return true;
		return false;
	}
	
	public Boolean isExplored(){
		if (this.state == State.EXPLORED)
			return true;
		return false;
	}

	public SquareType getName() {
		return name;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setName(SquareType name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public State getState() {
		return state;
	}
}
