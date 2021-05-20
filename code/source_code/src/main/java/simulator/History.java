package simulator;

import java.util.List;
import java.util.Map;

public class History {
	private int turns;
	private List<Square> squares;
	private List<Drone> allDrones;
	private List<List<String>> outputs;
	private int numOfActiveDrones;
	private Map<String, Integer> finalReport;

	public int getTurns() {
		return turns;
	}

	public void setTurns(int turns) {
		this.turns = turns;
	}

	public List<Square> getSquares() {
		return squares;
	}

	public void setSquares(List<Square> squares) {
		this.squares = squares;
	}

	public List<List<String>> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<List<String>> outputs) {
		this.outputs = outputs;
	}

	public List<Drone> getAllDrones() {
		return allDrones;
	}

	public void setAllDrones(List<Drone> allDrones) {
		this.allDrones = allDrones;
	}

	public int getNumOfActiveDrones() {
		return numOfActiveDrones;
	}

	public void setNumOfActiveDrones(int numOfActiveDrones) {
		this.numOfActiveDrones = numOfActiveDrones;
	}

	public Map<String, Integer> getFinalReport() {
		return finalReport;
	}

	public void setFinalReport(Map<String, Integer> finalReport) {
		this.finalReport = finalReport;
	}
	
}
