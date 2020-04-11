package backend;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simulator.SystemMap;
import simulator.Controller;
import simulator.Drone;
import simulator.History;
import simulator.Simulator;
import simulator.Square;

// put all RESTful APIs in this java file  
@RestController
public class SimManager {
	private Simulator simulator;
	private SystemMap systemMap;
	private Controller controller;
	private String fileName;
	private int width;
	private int height;
	private int maxTurns;
	private int turns;
	private int[] fuels;
	private int totalDrones;
	private List<Square> squares;
	private int safeSquares; 
	private List<Drone> drones;
	private List<Drone> allDrones;
	private List<List<String>> outputs;
	private Map<String, Integer> finalReport;

	private List<Drone> activeDrones;
	private static final ObjectMapper mapper = new ObjectMapper();

	public String getInitialStates() throws Exception {
		HashMap<String, Object> states = new HashMap<>();
		states.put("width", this.width);
		states.put("height", this.height);
		states.put("maxTurns", this.maxTurns);
		states.put("turns", this.turns);
		states.put("fuels", this.fuels);
		states.put("squares", this.squares);
		states.put("safeSquares", this.safeSquares);
		states.put("totalDrones", this.totalDrones);
		states.put("drones", this.drones);
		states.put("outputs", this.outputs);
		states.put("finalReport", this.finalReport);

		// states.put("disableStart", "yes");
		// states.put("disabled", true);
		return mapper.writeValueAsString(states);
	}

	@GetMapping("/starSearch")
	public String initialize(@RequestParam(value = "fileName") String fileName, 
								@RequestParam(value = "mode") String mode) throws Exception {
		System.out.println(fileName + " " + mode);
		this.simulator = new Simulator(fileName);
		this.systemMap = this.simulator.getSystemMap();
		this.controller = this.simulator.getController();
		this.fileName = fileName;
		this.width = this.systemMap.getWidth();
		this.height = this.systemMap.getHeight();
		this.maxTurns = this.systemMap.getTurnLimit();
		this.turns = 0;
		this.squares = this.systemMap.getAllSquaresExploredOrScanned();
		this.safeSquares = this.systemMap.getAllSafeSquaresExplored().size();
		this.fuels = this.systemMap.getFuels();
		this.totalDrones = this.systemMap.getNumOfDrones();
		this.drones = this.systemMap.getAllActiveDrones();
		this.outputs = new ArrayList<List<String>>();
		this.finalReport = new HashMap<>();

		this.activeDrones = new ArrayList<>();
		if(mode.equals("resume")) {
			this.loadHistory(fileName);
			this.updateSquares(this.squares, this.allDrones);
		}

		System.out.println(" start");
		// System.out.println("isEmpty? " + this.finalReport.isEmpty());
		return this.getInitialStates();
	}

	public int startNewTurn() {
		if (this.simulator.terminate(turns))
			return -1;
		this.activeDrones = this.systemMap.getAllActiveDrones();
		this.turns += 1;
		return 0;
	}

	public String updateState(int rel) throws Exception {
		Map<String, Object> states = new HashMap<>();
		List<String> output = this.controller.getOutput();

		this.outputs.add(output);
		this.squares = this.systemMap.getAllSquaresExploredOrScanned();
		this.drones = this.systemMap.getAllActiveDrones();
		this.safeSquares = this.systemMap.getAllSafeSquaresExplored().size();
		this.allDrones = this.systemMap.getAllDrones();

		states.put("turns", this.turns);
		states.put("squares", this.squares);
		states.put("safeSquares", this.safeSquares);
		states.put("drones", this.drones);
		states.put("outputs", this.outputs);

		if (rel == 0) {
			this.finalReport = this.simulator.displayFinalReport(turns);
			states.put("finalReport", this.finalReport);
		} else {
			states.put("finalReport", "");
		}
		this.saveHistory();

		return mapper.writeValueAsString(states);
	}

	// update state and display output if return 1
	// final report if return 0
	@GetMapping("/nextAction")
	public String action() throws Exception {
		// check if a turn finished. If yes, start new turn
		int rel = 1;
		// System.out.println(this.activeDrones.size());
		if (this.activeDrones.size() == 0) {
			if (this.startNewTurn() == -1)
				rel = 0;
		}
		Drone drone = this.activeDrones.get(0);
		//System.out.println(
		//		"before remove:" + this.turns + " " + this.activeDrones + " " + this.activeDrones.size() + " " + drone);
		this.activeDrones.remove(0);
		//System.out.println("after remove: " + this.activeDrones + " " + this.activeDrones.size() + " " + drone);
		rel = this.controller.pullDroneForAction(drone, turns, false);

		if (this.activeDrones.size() == 0 && this.turns == this.maxTurns)
			rel = 0;
		// continue pull next drone to action if rel == -1
		if (rel == -1) {
			this.action();
		}
		// final report if rel = 0, otherwise only update state and display output
		return this.updateState(rel);
	}

	public void saveHistory() throws Exception {
		History hist = new History();
		hist.setTurns(this.turns);
		hist.setSquares(this.squares);
		hist.setSafeSquares(this.safeSquares);
		hist.setFuels(this.fuels);
		hist.setTotalDrones(this.totalDrones);
		hist.setDrones(this.drones);
		hist.setAllDrones(this.allDrones);
		hist.setOutputs(this.outputs);
		hist.setFinalReport(this.finalReport);
		//System.out.println("save finalReport" +  this.finalReport);
		String fl = this.fileName + ".json";
		mapper.writeValue(new File(fl), hist);
	}
	
	public void loadHistory (String fileName) throws Exception {
		String fl = fileName + ".json";
		History hist = mapper.readValue(new File(fl), History.class);
		this.turns = hist.getTurns();
		this.squares = hist.getSquares();
		this.safeSquares = hist.getSafeSquares();
		this.fuels = hist.getFuels();
		this.totalDrones = hist.getTotalDrones();
		this.drones = hist.getDrones();
		this.allDrones = hist.getAllDrones();
		this.outputs = hist.getOutputs();
		this.finalReport = hist.getFinalReport();
	}

	@GetMapping("/files")
	public String getFiles() throws Exception  {
		Map<String, Object> states = new HashMap<>();
		List<String> filesToResume = new ArrayList<>();
		List<String> filesToInitial = new ArrayList<>();
		for (int i = 0; i <= 30; i++){
			String fl = "scenario" + String.valueOf(i) + ".csv";
			//System.out.println("fl: " + fl);
			File tmpFile = new File(fl+".json");
			if (tmpFile.exists()){
				History hist = mapper.readValue(new File(fl + ".json"), History.class);
				if(hist.getFinalReport().isEmpty()) filesToResume.add(fl);
			}
			filesToInitial.add(fl);
		}
		states.put("filesToInitial", filesToInitial);
		states.put("filesToResume", filesToResume);
		return mapper.writeValueAsString(states);
	}

	@GetMapping("/stop")
	public Object stop() {
		Map<String, Object> states = new HashMap<>();
		this.finalReport = this.simulator.displayFinalReport(turns);
		states.put("finalReport", this.finalReport);
		return states;
	}

	public void updateSquares(List<Square> newSquares, List<Drone> newDrones){
		for (Square sqr: newSquares){
			Square square = this.systemMap.getSquare(sqr.getX(), sqr.getY());
			square.setName(sqr.getName());
			square.setState(sqr.getState());
		}

		for (Drone d: newDrones){
			Drone dr = this.systemMap.getDroneById(d.getId());
			//System.out.println("drone:" +  d.getId() +  " " + d.getMaxFuel());
		
			//System.out.println("before:" + dr.getDirection() + " " + dr.getFuel() + " " + d.getFuel());
			dr.setX(d.getX());
			dr.setY(d.getY());
			dr.setDirection(d.getDirection());
			dr.setFuel(d.getFuel());
			dr.setCrashed(d.getCrashed());
			//System.out.println("after:" + dr.getDirection() + " " + dr.getFuel());
		}
		
	}
}
