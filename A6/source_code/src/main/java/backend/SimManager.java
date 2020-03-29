package backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simulator.SystemMap;

// put all RESTful APIs in this java file  
@RestController
public class SimManager {
	private SystemMap systemMap;

	public void initialize (@RequestParam (value = "testFileName") 
	String testFileName) throws Exception {
		this.systemMap = new SystemMap(String.format(testFileName));
	}

	@GetMapping("/getsize")
	public int[] getMapSize() throws Exception {
		this.initialize("scenario0.csv");
		int[] mapSize = new int[2];
		mapSize[0] = this.systemMap.getWidth();
		mapSize[1] = this.systemMap.getHeight();
		return mapSize;
	}
}
