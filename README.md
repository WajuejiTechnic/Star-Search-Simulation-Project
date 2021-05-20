# Star Search Simulation Project

## Testing Fuel
1. To generate star_search.jar file - under code/source_code, run the command:
   
   gradle shadowjar
   
2. save output to a scenario<N>_results file, run the command:
   
   java -cp star_search.jar simulator.Main scenario<N>.csv > scenario<N>_results.csv
   
   save all scenario files, run the command:
   
   for i in {0..30}; do
      
      java -cp star_search.jar simulator.Main scenario${i}.csv > scenario${i}_results.csv
   
   done

3. to generate the fuel_test.jar file - under code/source_code/src/test/java, run the command:
   
   javac *.java
   
   jar cfe fuel_test.jar Main *.class
   
4. test(copy fuel_test.jar to A6/source_code) and run the command:
   
   java -jar fuel_test.jar 0 30 > result.log
   
 
## Start GUI
1. Download virtual machine via Google Drive Link

2. Download repo Github and store it in VM

3. Open terminal and go to /code/source_code,R. un the command below to build and run file in the current directory:
   
   gradle run
   
4. Open Visual Studio Code, Click File -> Add folder to Workspace and select the folder
   
   /code/source_code
   
   Then click Terminal -> New Terminal, cd web to go to the folder web
   
5. To Install the dependencies in the local node_modules folder, run the command (once):
   
   npm install
   
6. To run your server file, use the command:
  
   npm start
 
   
   
