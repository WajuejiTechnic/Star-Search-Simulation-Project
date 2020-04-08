package simulator;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class History {
	private int width;
	private int height;
	private int maxTurns;
	private int turns;
	private List<Square> squares;
	private int safeSquares;
	private int[] fuels;
	private int totalDrones;
	private List<Drone> drones;
	private List<List<String>> outputs;
	private Map<String, Integer> finalReport;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getMaxTurns() {
		return maxTurns;
	}

	public void setMaxTurns(int maxTurns) {
		this.maxTurns = maxTurns;
	}

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

	public int getTotalDrones() {
		return totalDrones;
	}

	public void setTotalDrones(int totalDrones) {
		this.totalDrones = totalDrones;
	}

	public List<Drone> getDrones() {
		return drones;
	}

	public void setDrones(List<Drone> drones) {
		this.drones = drones;
	}

	public List<List<String>> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<List<String>> outputs) {
		this.outputs = outputs;
	}

	public Map<String, Integer> getFinalReport() {
		return finalReport;
	}

	public void setFinalReport(Map<String, Integer> finalReport) {
		this.finalReport = finalReport;
	}

	public int[] getFuels() {
		return fuels;
	}

	public void setFuels(int[] fuels) {
		this.fuels = fuels;
	}

	public int getSafeSquares() {
		return safeSquares;
	}

	public void setSafeSquares(int safeSquares) {
		this.safeSquares = safeSquares;
	}

	
	
}
