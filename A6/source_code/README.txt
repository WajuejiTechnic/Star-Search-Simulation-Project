To generate star_search.jar file:
1. under A6/source_code, run the command:
   gradle shadowjar
2. test a scenario file, run the command:
   java -cp star_search.jar simulator.Main scenario<N>.csv
