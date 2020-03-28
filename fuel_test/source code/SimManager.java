import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SimManager {
    private static Random randGenerator;
    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 60;
    private Integer regionWidth;
    private Integer regionHeight;
    private Integer[][] regionInfo;
    private Integer numberOfDrones;
    private Integer[] droneX;
    private Integer[] droneY;
    private String[] droneDirection;
    private Integer[] droneStrategy;
    private Integer[] droneStatus;
    private HashMap<String, Integer> xDIR_MAP;
    private HashMap<String, Integer> yDIR_MAP;
    private Integer currentDrone;
    private String trackAction;
    private String trackNewDirection;
    private Integer trackThrustDistance;
    private String trackMoveCheck;
    private String trackScanResults;
    private Boolean trackActionValid;
    private Integer turnLimit;
    private final int EMPTY_CODE = 0;
    private final int STARS_CODE = 1;
    private final int SUN_CODE = 2;
    private final int MAX_DRONES = 10;
    private final int OK_CODE = 1;
    private final int CRASH_CODE = -1;
    private final int NO_DRONE = -1;
    private final String[] ORIENT_LIST = new String[]{"north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest"};
    private String[] droneFileID;
    private String[] droneFileAction;
    private String[] droneFileNewDirection;
    private Integer[] droneFileThrustDistance;
    private String[] droneFileResponse;
    private String droneFileFinalReport;
    private Integer droneFileTurnCount;
    private Boolean[] droneActionValid;
    private final int TURN_LIMIT = 3001;
    private Integer simulationMaxTurns;
    private final String[] ACTION_LIST = new String[]{"thrust", "steer", "scan", "pass", "recharge"};
    private final String[] DRONE_LIST = new String[]{"d0", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9"};

    //for fuel test
    private int initialFuel;
    private int maxFuel;
    private int rechargeFuel;
    private Integer[] droneFuel; //track each drone's fuel
    private static HashMap<String, Integer> FUEL_COST = new HashMap<String, Integer>(){{
        put("steer", 1);
        put("thrust", 1);
        put("scan", 1);
        put("pass", 0);
        put("recharge", 0);
    }};

    public SimManager() {
        randGenerator = new Random();
        this.regionHeight = 0;
        this.regionWidth = 0;
        this.regionInfo = new Integer[100][60];
        this.numberOfDrones = -1;
        this.droneX = new Integer[10];
        this.droneY = new Integer[10];
        this.droneDirection = new String[10];
        this.droneStrategy = new Integer[10];
        this.droneStatus = new Integer[10];
        this.currentDrone = -1;
        this.droneFuel = new Integer[10];

        for(int k = 0; k < 10; ++k) {
            this.droneX[k] = -1;
            this.droneY[k] = -1;
            this.droneDirection[k] = "north";
            this.droneStrategy[k] = -1;
            this.droneStatus[k] = -1;
            this.droneFuel[k] = -1;
        }

        this.xDIR_MAP = new HashMap();
        this.xDIR_MAP.put("north", 0);
        this.xDIR_MAP.put("northeast", 1);
        this.xDIR_MAP.put("east", 1);
        this.xDIR_MAP.put("southeast", 1);
        this.xDIR_MAP.put("south", 0);
        this.xDIR_MAP.put("southwest", -1);
        this.xDIR_MAP.put("west", -1);
        this.xDIR_MAP.put("northwest", -1);
        this.yDIR_MAP = new HashMap();
        this.yDIR_MAP.put("north", 1);
        this.yDIR_MAP.put("northeast", 1);
        this.yDIR_MAP.put("east", 0);
        this.yDIR_MAP.put("southeast", -1);
        this.yDIR_MAP.put("south", -1);
        this.yDIR_MAP.put("southwest", -1);
        this.yDIR_MAP.put("west", 0);
        this.yDIR_MAP.put("northwest", 1);
        this.turnLimit = -1;
        this.droneFileID = new String[3001];
        this.droneFileAction = new String[3001];
        this.droneFileNewDirection = new String[3001];
        this.droneFileThrustDistance = new Integer[3001];
        this.droneFileResponse = new String[3001];
        this.droneActionValid = new Boolean[3001];
    }

    public void uploadStartingFile(String testFileName) {
        String DELIMITER = ",";

        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens = takeCommand.nextLine().split(",");
            this.regionWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(",");
            this.regionHeight = Integer.parseInt(tokens[0]);
            this.regionInfo = new Integer[this.regionWidth][this.regionHeight];

            for(int i = 0; i < this.regionWidth; ++i) {
                for(int j = 0; j < this.regionHeight; ++j) {
                    this.regionInfo[i][j] = 1;
                }
            }

            tokens = takeCommand.nextLine().split(",");
            this.numberOfDrones = Integer.parseInt(tokens[0]);

            //read in fuel configuration
            tokens = takeCommand.nextLine().split(DELIMITER);
            this.initialFuel = Integer.parseInt(tokens[0]);

            tokens = takeCommand.nextLine().split(DELIMITER);
            this.rechargeFuel = Integer.parseInt(tokens[0]);

            tokens = takeCommand.nextLine().split(DELIMITER);
            this.maxFuel = Integer.parseInt(tokens[0]);

            int k;
            for(k = 0; k < this.numberOfDrones; ++k) {
                tokens = takeCommand.nextLine().split(",");
                this.droneX[k] = Integer.parseInt(tokens[0]);
                this.droneY[k] = Integer.parseInt(tokens[1]);
                this.droneDirection[k] = tokens[2];
                this.droneStrategy[k] = Integer.parseInt(tokens[3]);
                this.droneStatus[k] = 1;
                //add drone fuel status
                this.droneFuel[k] = this.initialFuel;
                this.regionInfo[this.droneX[k]][this.droneY[k]] = 0; //drone is 0 in regionInfo
            }

            this.currentDrone = this.numberOfDrones - 1;
            tokens = takeCommand.nextLine().split(",");
            int numSuns = Integer.parseInt(tokens[0]);

            for(k = 0; k < numSuns; ++k) {
                tokens = takeCommand.nextLine().split(",");
                this.regionInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = 2;//sun is 2 in regionInfo
            }

            tokens = takeCommand.nextLine().split(",");
            this.turnLimit = Integer.parseInt(tokens[0]);
            takeCommand.close();
        } catch (Exception var9) {
            var9.printStackTrace();
            System.out.println();
        }

    }

    public Boolean uploadDroneFile(String testFileName) {
        String DELIMITER = ",";

        for(int i = 0; i < 3001; ++i) {
            this.droneFileID[i] = "";
            this.droneFileAction[i] = "";
            this.droneFileNewDirection[i] = "north";
            this.droneFileThrustDistance[i] = -1;
            this.droneFileResponse[i] = "";
            this.droneActionValid[i] = Boolean.TRUE;
        }

        this.droneFileFinalReport = "";
        this.droneFileTurnCount = 0;
        Boolean fileConfigError = Boolean.FALSE;

        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            int lineCount = -1;

            do {
                String[] tokens = takeCommand.nextLine().split(",");
                ++lineCount;
                if (tokens.length >= 2 && tokens.length <= 4) {
                    String var10001;
                    if (tokens.length == 4) { //for final report
                        var10001 = tokens[0].trim();
                        this.droneFileFinalReport = var10001 + "," + tokens[1].trim() + "," + tokens[2].trim() + "," + tokens[3].trim();
                    } else { //d0,pass  d0,scan  d0,recharge  d0,steer,south  d0,thrust,3
                        this.droneFileID[lineCount] = tokens[0].toLowerCase().trim(); //read d0 or d1 or d2....
                        List<String> droneList = Arrays.asList(this.DRONE_LIST);
                        PrintStream var10000;
                        if (!droneList.contains(this.droneFileID[lineCount])) {
                            var10000 = System.out;
                            var10001 = String.valueOf(lineCount);
                            var10000.println("FAULT/line [" + var10001 + "]: drone value is invalid (" + this.droneFileID[lineCount] + ")");
                            fileConfigError = Boolean.TRUE;
                            this.droneActionValid[lineCount] = Boolean.FALSE;
                        }

                        this.droneFileAction[lineCount] = tokens[1].toLowerCase().trim(); //read scan or recharge or steer or pass or thrust
                        List<String> actionList = Arrays.asList(this.ACTION_LIST);
                        if (!actionList.contains(this.droneFileAction[lineCount])) {
                            var10000 = System.out;
                            var10001 = String.valueOf(lineCount);
                            var10000.println("WARNING/line [" + var10001 + "]: action value is invalid (" + this.droneFileAction[lineCount] + ")");
                            this.droneActionValid[lineCount] = Boolean.FALSE;
                            fileConfigError = Boolean.TRUE;
                        }
                        //check steer
                        if (this.droneFileAction[lineCount].equals("steer")) {
                            this.droneFileNewDirection[lineCount] = tokens[2].toLowerCase().trim();
                            List<String> dirList = Arrays.asList(this.ORIENT_LIST);
                            if (!dirList.contains(this.droneFileNewDirection[lineCount])) {
                                var10000 = System.out;
                                var10001 = String.valueOf(lineCount);
                                var10000.println("WARNING/line [" + var10001 + "]: direction value is invalid (" + this.droneFileNewDirection[lineCount] + ")");
                                this.droneActionValid[lineCount] = Boolean.FALSE;
                                fileConfigError = Boolean.TRUE;
                            }
                        }
                        //check thrust
                        if (this.droneFileAction[lineCount].equals("thrust")) {
                            this.droneFileThrustDistance[lineCount] = Integer.parseInt(tokens[2]);
                            if (this.droneFileThrustDistance[lineCount] < 1 || this.droneFileThrustDistance[lineCount] > 3) {
                                var10000 = System.out;
                                var10001 = String.valueOf(lineCount);
                                var10000.println("WARNING/line [" + var10001 + "]: thrust distance is invalid (" + this.droneFileThrustDistance[lineCount] + ")");
                                this.droneActionValid[lineCount] = Boolean.FALSE;
                                fileConfigError = Boolean.TRUE;
                            }
                        }

                        this.droneFileResponse[lineCount] = takeCommand.nextLine().toLowerCase().replaceAll("\\s", ""); //read response like ok, remove single space
                    }
                }
            } while(takeCommand.hasNextLine() && lineCount < 3001);

            this.droneFileTurnCount = lineCount;
        } catch (Exception var13) {
            var13.printStackTrace();
        }

        return fileConfigError;
    }

    public Integer simulationDuration() {
        return this.turnLimit;
    }

    public Integer simulationTestDuration() {
        return this.droneFileTurnCount;
    }

    public Integer droneCount() {
        return this.numberOfDrones;
    }

    public Boolean pollDroneForAction(int step, int id) {
        if (!this.droneFileID[step].equals("d" + String.valueOf(id))) {
            System.out.println("FAULT/command [" + String.valueOf(step) + "]: Wrong drone attempts to move");
            return Boolean.TRUE;
        } else {
            this.trackAction = this.droneFileAction[step];
            this.trackNewDirection = this.droneFileNewDirection[step];
            this.trackThrustDistance = this.droneFileThrustDistance[step];
            this.trackActionValid = this.droneActionValid[step];
            //need to add: this.trackFuel = this.droneFileFuel[step]
            return Boolean.FALSE;
        }
    }

    public Boolean validateDroneAction(int step, int id) {
        if (!this.trackActionValid) {
            this.trackMoveCheck = "action_not_recognized";
        } else if (this.trackAction.equals("scan")) {
            if(this.droneFuel[id] < FUEL_COST.get("scan")){
                this.trackScanResults = "Not_enough_energy_Recharge_first";
                this.trackMoveCheck = "Not_enough_energy_Recharge_first";
            }
            else{
                this.droneFuel[id] -= FUEL_COST.get("scan");
                this.trackScanResults = this.scanAroundSquare(this.droneX[id], this.droneY[id]);
                this.trackMoveCheck = "ok";
            }
        } else if (this.trackAction.equals("pass")) {
            this.droneFuel[id] -= FUEL_COST.get("pass");
            this.trackMoveCheck = "ok";
        } else if (this.trackAction.equals("steer")) {
            this.droneDirection[id] = this.trackNewDirection;
            if(this.droneFuel[id]<FUEL_COST.get("steer")){
                this.trackMoveCheck = "Not_enough_energy_Recharge_first";
            }
            else{
                this.droneFuel[id] -= FUEL_COST.get("steer");
                this.trackMoveCheck = "ok";
            }
        } else if (this.trackAction.equals("thrust")) {
            int xOrientation = (Integer)this.xDIR_MAP.get(this.droneDirection[id]);
            int yOrientation = (Integer)this.yDIR_MAP.get(this.droneDirection[id]);
            this.trackMoveCheck = "ok";

            for(int remainingThrust = this.trackThrustDistance; remainingThrust > 0 && this.trackMoveCheck.equals("ok"); --remainingThrust) {
                //check fuel first
                if(this.droneFuel[id]<FUEL_COST.get("thrust")){
                    this.trackMoveCheck = "Not_enough_energy_Recharge_first";
                    break;
                }

                int newSquareX = this.droneX[id] + xOrientation;
                int newSquareY = this.droneY[id] + yOrientation;
                if (newSquareX >= 0 && newSquareX < this.regionWidth && newSquareY >= 0 && newSquareY < this.regionHeight) {
                    if (this.regionInfo[newSquareX][newSquareY] == 2) {
                        this.droneStatus[id] = -1;
                        this.trackMoveCheck = "crash";
                    } else if (this.droneDetected(newSquareX, newSquareY)) {
                        this.droneStatus[id] = -1;
                        this.droneStatus[this.identifyDrone(newSquareX, newSquareY)] = -1;
                        this.trackMoveCheck = "crash";
                    } else {
                        this.droneX[id] = newSquareX;
                        this.droneY[id] = newSquareY;
                        this.regionInfo[newSquareX][newSquareY] = 0;
                        this.droneFuel[id] -= FUEL_COST.get("thrust"); //make the move
                    }
                }
            }
        }
        else if(this.trackAction.equals("recharge")){
            if(this.droneFuel[id] == this.maxFuel) {
                this.trackMoveCheck = "fuel_already_full";
            } else{
                this.droneFuel[id] += this.rechargeFuel;
                this.trackMoveCheck = "ok";
                if (this.droneFuel[id] > this.maxFuel) {
                    this.droneFuel[id] = this.maxFuel;
                }
            }

        }
        else {
            this.trackMoveCheck = "action_not_recognized";
        }

        PrintStream var10000;
        String var10001;
        if (this.droneFileAction[step].equals("scan") && !this.droneFileResponse[step].equals(this.trackScanResults)) {
            System.out.println("FAULT[" + String.valueOf(step) + "]: Scan results don't match");
            var10000 = System.out;
            var10001 = String.valueOf(id);
            var10000.print("COMMAND: d" + var10001 + "," + this.trackAction);
            if (this.trackAction.equals("steer")) {
                System.out.println("," + this.trackNewDirection);
            } else if (this.trackAction.equals("thrust")) {
                System.out.println("," + this.trackThrustDistance);
            } else {
                System.out.println();
            }

            System.out.println("EXPECTED: " + this.trackScanResults);
            var10001 = this.droneFileResponse[step];
            System.out.println("RETURNED: " + var10001);
            return Boolean.TRUE; //error!
        } else if (!this.droneFileAction[step].equals("scan") && !this.droneFileResponse[step].equals(this.trackMoveCheck)) {
            System.out.println("FAULT[" + String.valueOf(step) + "]: Command results don't match");
            var10000 = System.out;
            var10001 = String.valueOf(id);
            var10000.print("COMMAND: d" + var10001 + "," + this.trackAction);
            if (this.trackAction.equals("steer")) {
                System.out.println("," + this.trackNewDirection);
            } else if (this.trackAction.equals("thrust")) {
                System.out.println("," + this.trackThrustDistance);
            } else {
                System.out.println();
            }

            System.out.println("EXPECTED: " + this.trackMoveCheck);
            var10001 = this.droneFileResponse[step];
            System.out.println("RETURNED: " + var10001);
            return Boolean.TRUE; //error!
        } else {
            return Boolean.FALSE; //indicate no error
        }
    }

    public String scanAroundSquare(int targetX, int targetY) {
        String resultString = "";

        for(int k = 0; k < this.ORIENT_LIST.length; ++k) {
            String lookThisWay = this.ORIENT_LIST[k];
            int offsetX = (Integer)this.xDIR_MAP.get(lookThisWay);
            int offsetY = (Integer)this.yDIR_MAP.get(lookThisWay);
            int checkX = targetX + offsetX;
            int checkY = targetY + offsetY;
            String nextSquare;
            if (checkX >= 0 && checkX < this.regionWidth && checkY >= 0 && checkY < this.regionHeight) {
                if (this.droneDetected(checkX, checkY)) {
                    nextSquare = "drone";
                } else {
                    switch(this.regionInfo[checkX][checkY]) {
                        case 0:
                            nextSquare = "empty";
                            break;
                        case 1:
                            nextSquare = "stars";
                            break;
                        case 2:
                            nextSquare = "sun";
                            break;
                        default:
                            nextSquare = "unknown";
                    }
                }
            } else {
                nextSquare = "barrier";
            }

            if (resultString.isEmpty()) {
                resultString = nextSquare;
            } else {
                resultString = resultString + "," + nextSquare;
            }
        }

        return resultString;
    }

    public void displayActionAndResponses(int id) {
        PrintStream var10000 = System.out;
        String var10001 = String.valueOf(id);
        var10000.print("d" + var10001 + "," + this.trackAction);
        if (this.trackAction.equals("steer")) {
            System.out.println("," + this.trackNewDirection);
        } else if (this.trackAction.equals("thrust")) {
            System.out.println("," + this.trackThrustDistance);
        } else {
            System.out.println();
        }

        if (this.trackActionValid && !this.trackAction.equals("thrust") && !this.trackAction.equals("steer") && !this.trackAction.equals("pass")&& !this.trackAction.equals("recharge")) {
            if (this.trackAction.equals("scan")) {
                System.out.println(this.trackScanResults);
            } else {
                System.out.println("action_not_recognized");
            }
        } else {
            System.out.println(this.trackMoveCheck);
        }

    }

    private void renderHorizontalBar(int size) {
        System.out.print(" ");

        for(int k = 0; k < size; ++k) {
            System.out.print("-");
        }

        System.out.println("");
    }

    public void renderRegion() {
        int charWidth = 2 * this.regionWidth + 2;

        int i;
        for(int j = this.regionHeight - 1; j >= 0; --j) {
            this.renderHorizontalBar(charWidth);
            System.out.print(j);

            for(i = 0; i < this.regionWidth; ++i) {
                System.out.print("|");
                if (this.droneDetected(i, j)) {
                    System.out.print(this.identifyDrone(i, j));
                } else {
                    switch(this.regionInfo[i][j]) {
                        case 0:
                            System.out.print(" ");
                            break;
                        case 1:
                            System.out.print(".");
                            break;
                        case 2:
                            System.out.print("s");
                    }
                }
            }

            System.out.println("|");
        }

        this.renderHorizontalBar(charWidth);
        System.out.print(" ");

        for(i = 0; i < this.regionWidth; ++i) {
            System.out.print(" " + i);
        }

        System.out.println("");

        for(int k = 0; k < this.numberOfDrones; ++k) {
            if (this.droneStatus[k] != -1) {
                PrintStream var10000 = System.out;
                String var10001 = String.valueOf(k);
                var10000.println("dir d" + var10001 + ": " + this.droneDirection[k]);
            }
        }

        System.out.println("");
    }

    public Boolean dronesAllStopped() {
        for(int k = 0; k < this.numberOfDrones; ++k) {
            if (this.droneStatus[k] == 1) {
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    public Boolean droneDetected(Integer coordX, Integer coordY) {
        for(int k = 0; k < this.numberOfDrones; ++k) {
            if (this.droneStatus[k] == 1 && coordX == this.droneX[k] && coordY == this.droneY[k]) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    public Integer identifyDrone(Integer coordX, Integer coordY) {
        for(int k = 0; k < this.numberOfDrones; ++k) {
            if (this.droneStatus[k] == 1 && coordX == this.droneX[k] && coordY == this.droneY[k]) {
                return k;
            }
        }

        return -1;
    }

    public Integer getNextDrone() {
        for(int j = 1; j <= this.numberOfDrones; ++j) {
            Integer nextDrone = (this.currentDrone + j) % this.numberOfDrones;
            if (this.droneStatus[nextDrone] == 1) {
                this.currentDrone = nextDrone;
                return nextDrone;
            }
        }

        this.currentDrone = -1;
        return -1;
    }

    public Boolean droneStopped(int id) {
        return this.droneStatus[id] == -1;
    }

    public void finalReport(int completeTurns) {
        int regionSize = this.regionWidth * this.regionHeight;
        int numSuns = 0;
        int numStars = 0;

        int i;
        int j;
        for(i = 0; i < this.regionWidth; ++i) {
            for(j = 0; j < this.regionHeight; ++j) {
                if (this.regionInfo[i][j] == 2) {
                    ++numSuns;
                }

                if (this.regionInfo[i][j] == 1) {
                    ++numStars;
                }
            }
        }

        i = regionSize - numSuns;
        j = i - numStars;
        String var10000 = String.valueOf(regionSize);
        String actualFinalReportMinusTurn = var10000 + "," + String.valueOf(i) + "," + String.valueOf(j) + "," + String.valueOf(completeTurns - 1);
        var10000 = String.valueOf(regionSize);
        String actualFinalReport = var10000 + "," + String.valueOf(i) + "," + String.valueOf(j) + "," + String.valueOf(completeTurns);
        var10000 = String.valueOf(regionSize);
        String actualFinalReportPlusTurn = var10000 + "," + String.valueOf(i) + "," + String.valueOf(j) + "," + String.valueOf(completeTurns + 1);
        if (!actualFinalReport.equals(this.droneFileFinalReport) && !actualFinalReportPlusTurn.equals(this.droneFileFinalReport) && !actualFinalReportMinusTurn.equals(this.droneFileFinalReport)) {
            System.out.println("FAULT/turn [" + String.valueOf(completeTurns) + "]: Final report results don't match");
            System.out.println("EXPECTED: " + actualFinalReport);
            System.out.println("RETURNED: " + this.droneFileFinalReport);
        } else {
            System.out.println(actualFinalReport);
        }

    }
}
