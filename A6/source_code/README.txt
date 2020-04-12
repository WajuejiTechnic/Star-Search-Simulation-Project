
1. To generate star_search.jar file - under A6/source_code, run the command:
   gradle shadowjar
   
2. save output to a scenario<N>_results file, run the command:
   java -cp star_search.jar simulator.Main scenario<N>.csv > scenario<N>_results.csv
   
   save all scenario files, run the command:
   for i in {0..30}; do
      java -cp star_search.jar simulator.Main scenario${i}.csv > scenario${i}_results.csv
   done

3. to generate the fuel_test.jar file - under fule modification/test, run the command:
   javac *.java
   jar cfe fuel_test.jar Main *.class
   
4. test(copy fuel_test.jar to A6/source_code) and run the command:
   java -jar fuel_test.jar 0 30 > result.log
   
 
