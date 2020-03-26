public class Main {

    public static void main(String[] args) throws Exception {
    	Simulator simulator;
        Boolean showState = false;

        // check for the test scenario file name
        if (args.length < 1) {
            System.out.println("ERROR: Test scenario file name not found.");
            return;
        }
        if (args.length >= 2 && (args[1].equals("-v") || args[1].equals("-verbose"))) { 
        	showState = true; 
        }


        // run simulator and start new game
        simulator = new Simulator(args[0]);
        simulator.startGame(showState);
    }

}
