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
	private int maxTurns;
	private int turns;
	private int[] fuels;
	private int explorableSquares;
	private List<List<String>> outputs;
	private Map<String, Integer> finalReport;

	private List<Drone> activeDrones;
	private static final ObjectMapper mapper = new ObjectMapper();

	public String getInitialStates() throws Exception {
		HashMap<String, Object> states = new HashMap<>();
		states.put("width", this.systemMap.getWidth());
		states.put("height", this.systemMap.getHeight());
		states.put("maxTurns", this.maxTurns);
		states.put("turns", this.turns);
		states.put("fuels", this.fuels);
		states.put("explorableSquares", this.explorableSquares);
		states.put("squares", this.systemMap.getAllSquaresExploredOrScanned());
		states.put("safeSquares", this.systemMap.getAllSafeSquaresExplored().size());
		states.put("totalDrones", this.systemMap.getNumOfDrones());
		states.put("drones", this.systemMap.getAllActiveDrones());
		states.put("outputs", this.outputs);
		//states.put("finalReport", this.finalReport);

		// states.put("disableStart", "yes");
		// states.put("disabled", true);
		return mapper.writeValueAsString(states);
	}

	@GetMapping("/starSearch")
	synchronized public String initialize(@RequestParam(value = "fileName") String fileName, 
								@RequestParam(value = "mode") String mode) throws Exception {
		System.out.println("##############START with " + fileName + " & " + mode +" mode##############");
		this.simulator = new Simulator(fileName);
		this.systemMap = this.simulator.getSystemMap();
		this.controller = this.simulator.getController();
		this.fileName = fileName;
		this.maxTurns = this.systemMap.getTurnLimit();
		this.turns = 0;
		this.explorableSquares = this.systemMap.getExplorableSquares();
		this.fuels = this.systemMap.getFuels();
		this.outputs = new ArrayList<List<String>>();
		this.finalReport = new HashMap<>();

		this.activeDrones = new ArrayList<>();
		if(mode.equals("resume")) {
			History hist = this.loadHistory(fileName);
			this.updateSquares(hist.getSquares(), hist.getAllDrones());
			this.turns = hist.getTurns();
			this.outputs = hist.getOutputs();
			this.finalReport = hist.getFinalReport();

			if(hist.getNumOfActiveDrones() != 0){
				int totalActiveDrones = this.systemMap.getAllActiveDrones().size();
				int index = totalActiveDrones - hist.getNumOfActiveDrones();
				this.activeDrones = this.systemMap.getAllActiveDrones().subList(index, totalActiveDrones);
			}
		}

		// System.out.println("isEmpty? " + this.finalReport.isEmpty());
		return this.getInitialStates();
	}

	public void startNewTurn() {
		this.activeDrones = this.systemMap.getAllActiveDrones();
		this.turns += 1;
	}

	public String updateState(int rel) throws Exception {
		Map<String, Object> states = new HashMap<>();
		List<String> output = this.controller.getOutput();

		this.outputs.add(output);
		//this.allDrones = this.systemMap.getAllDrones();

		states.put("turns", this.turns);
		states.put("squares", this.systemMap.getAllSquaresExploredOrScanned());
		states.put("safeSquares", this.systemMap.getAllSafeSquaresExplored().size());
		states.put("drones", this.systemMap.getAllActiveDrones());
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

	@GetMapping("/getStrat")
	synchronized public Object getStrat(){
		Map<String, Object> states = new HashMap<>();
		if (this.activeDrones.size() == 0) this.startNewTurn();
		Drone drone = this.activeDrones.get(0);
		states.put("strat", drone.getStrategy());
		return states;
	}

	// update state and display output if return 1
	// final report if return 0
	@GetMapping("/nextAction")
	synchronized public String action() throws Exception {
		// check if a turn finished. If yes, start new turn
		int rel = 1;
		// System.out.println(this.activeDrones.size());
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

	@GetMapping("/nextActionByUser")
	synchronized public String actionByUser(@RequestParam(value = "action") String actionByUser, 
								@RequestParam(value = "param") String paramByUser) throws Exception {
		// check if a turn finished. If yes, start new turn
		Object param = null;
		if(actionByUser.equals("steer")) param = paramByUser;
		if(actionByUser.equals("thrust")) param = Integer.parseInt(paramByUser);
		//System.out.println("actionPair=" + actionByUser + " " + paramByUser);
		
		int rel = 1;
		// System.out.println(this.activeDrones.size());
		Drone drone = this.activeDrones.get(0);
		//System.out.println(
		//		"before remove:" + this.turns + " " + this.activeDrones + " " + this.activeDrones.size() + " " + drone);
		this.activeDrones.remove(0);
		//System.out.println("after remove: " + this.activeDrones + " " + this.activeDrones.size() + " " + drone);
		rel = this.controller.pullDroneForActionByUser(drone, turns, false, actionByUser, param);

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
		hist.setSquares(this.systemMap.getAllSquaresExploredOrScanned());
		hist.setAllDrones(this.systemMap.getAllDrones());
		hist.setOutputs(this.outputs);
		hist.setNumOfActiveDrones(this.activeDrones.size());
		hist.setFinalReport(this.finalReport);
		//System.out.println("save finalReport" +  this.finalReport);
		String fl = this.fileName + ".json";
		mapper.writeValue(new File(fl), hist);
	}
	
	public History loadHistory (String fileName) throws Exception {
		String fl = fileName + ".json";
		return mapper.readValue(new File(fl), History.class);
	}

	@GetMapping("/files")
	synchronized public String getFiles() throws Exception  {
		Map<String, Object> states = new HashMap<>();
		List<String> filesToResume = new ArrayList<>();
		List<String> filesToInitial = new ArrayList<>();
		for (int i = 0; i <= 31; i++){
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
	synchronized public Object stop() {
		Map<String, Object> states = new HashMap<>();
		states.put("finalReport", this.simulator.displayFinalReport(turns));
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
