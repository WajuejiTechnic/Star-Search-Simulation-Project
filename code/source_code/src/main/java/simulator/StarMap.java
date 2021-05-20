package simulator;

import java.util.List;

public class StarMap {
	private SystemMap systemMap;

	public StarMap(SystemMap systemMap){
		this.systemMap = systemMap;
	}
	
	public List<Square> getAllSquaresExplored(){
		return this.systemMap.getAllSquaresExplored();
	}
	
	public List<Square> getAllSquaresExploredOrScanned(){
		return this.systemMap.getAllSquaresExploredOrScanned();
	}
	
	public List<Drone> getAllActiveDrones(){
		return this.systemMap.getAllActiveDrones();
	}

	public void setSystemMap(SystemMap systemMap) {
		this.systemMap = systemMap;
	}
}
