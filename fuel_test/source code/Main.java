//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        Boolean showState = Boolean.FALSE;
        if (args.length < 2) {
            System.out.println("ERROR: Test scenario index numbers not found.");
        } else {
            Integer startingIndex = Integer.valueOf(args[0]);
            Integer endingIndex = Integer.valueOf(args[1]);
            if (args.length >= 3 && (args[2].equals("-v") || args[2].equals("-verbose"))) {
                showState = Boolean.TRUE;
            }

            for(int i = startingIndex; i <= endingIndex; ++i) {
                SimManager monitorSim = new SimManager();
                System.out.println("--- scenario_" + i + " ---");
                String fileTestScenario = "scenario" + i + ".csv";
                monitorSim.uploadStartingFile(fileTestScenario);
                String fileDroneResults = "scenario" + i + "_results.csv";
                Boolean haltSim = monitorSim.uploadDroneFile(fileDroneResults);
                if (haltSim) {
                    System.out.println("Command errors detected during static analysis and effectively passed when possible");
                }

                int trackTurnsCompleted = 0;
                int currentDrone = monitorSim.droneCount();

                for(int nextStep = 0; nextStep < monitorSim.simulationTestDuration() && !monitorSim.dronesAllStopped(); ++nextStep) {
                    int nextDrone = monitorSim.getNextDrone();
                    haltSim = monitorSim.pollDroneForAction(nextStep, nextDrone);
                    if (haltSim) {
                        break;
                    }

                    haltSim = monitorSim.validateDroneAction(nextStep, nextDrone);
                    if (haltSim) {
                        break;
                    }

                    monitorSim.displayActionAndResponses(nextDrone);
                    if (nextDrone <= currentDrone) {
                        ++trackTurnsCompleted;
                    }

                    currentDrone = nextDrone;
                    if (showState) {
                        monitorSim.renderRegion();
                    }
                }//end for nextStep

                if (!haltSim) {
                    monitorSim.finalReport(trackTurnsCompleted);
                }
            }//end for startingIndex

        }//end else
    }//end main()
}//end Main class
