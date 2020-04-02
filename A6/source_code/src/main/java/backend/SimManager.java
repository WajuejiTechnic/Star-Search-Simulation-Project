package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simulator.SystemMap;
import simulator.Controller;
import simulator.Drone;
import simulator.Simulator;

// put all RESTful APIs in this java file  
@RestController
public class SimManager {
	private Simulator simulator;
	private SystemMap systemMap;
	private Controller controller;
	private int turns = 0;
	private int maxNumOfTurns;
	private List<Drone> activeDrones = new ArrayList<>();
	
	public String getInitialStates() throws Exception {
		HashMap<String, Object> states = new HashMap<>();
		states.put("width", this.systemMap.getWidth());
		states.put("height", this.systemMap.getHeight());
		states.put("maxTurns", this.maxNumOfTurns);
		states.put("numOfDrones", this.systemMap.getNumOfDrones());
		states.put("squares", this.systemMap.getAllSquaresExploredOrScanned());
		states.put("drones", this.systemMap.getAllActiveDrones());
		return new ObjectMapper().writeValueAsString(states);
	}

	@GetMapping("/star-search")
	public String initialize (@RequestParam (value = "testFileName", 
	defaultValue = "scenario0.csv") String file) throws Exception {
		this.simulator = new Simulator(file);
		this.systemMap = this.simulator.getSystemMap();
		this.controller = this.simulator.getController();
		this.maxNumOfTurns = this.systemMap.getTurnLimit();
		System.out.println("start");
		return this.getInitialStates();
	}

	public int startNewTurn() {
		if(this.simulator.terminate(turns)) return -1;
		this.activeDrones = this.systemMap.getAllActiveDrones();
		this.turns += 1;
		return 0;
	}

	public String updateState (int rel) throws Exception {
		HashMap<String, Object> states = new HashMap<>();
		states.put("squares", this.systemMap.getAllSquaresExploredOrScanned());
		states.put("drones", this.systemMap.getAllActiveDrones());
		states.put("output", this.controller.getOutput());
		if (rel == 0) {
			states.put("finalReport", this.simulator.displayFinalReport(turns));
			states.put("disabled", "disabled");
		}
		else {
			states.put("finalReport", null);
			states.put("disabled", "");
		}
		return new ObjectMapper().writeValueAsString(states);
	}

	// update state and display output if return 1
	// final report if return 0
	@GetMapping("/next-action")
	public String action() throws Exception {
		// check if a turn finished. If yes, start new turn
		int rel = 1;
		//System.out.println(this.activeDrones.size());
		if(this.activeDrones.size() == 0) {
			if (this.startNewTurn() == -1) rel = 0;
		}
		Drone drone = this.activeDrones.get(0);
		System.out.println("before remove:" + this.turns + " " + this.activeDrones + " " + this.activeDrones.size() + " " + drone);
		this.activeDrones.remove(0);
		System.out.println("after remove: " + this.activeDrones + " " + this.activeDrones.size() + " " + drone);
		rel = this.controller.pullDroneForAction(drone, turns, false);
		
		if (this.activeDrones.size() == 0 && this.turns == this.maxNumOfTurns) rel = 0;
		// continue pull next drone to action if rel == -1
		if (rel == -1) {
			this.action();
		}
		// final report if rel = 0, otherwise only update state and display output
		return this.updateState(rel);
	}
}
