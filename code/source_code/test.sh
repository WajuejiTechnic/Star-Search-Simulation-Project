#!/bin/bash

 
for i in {0..30}
   do
   echo "################# Starting file $i ##################"
   rm  scenario${i}_results.csv
   java -cp star_search.jar simulator.Main scenario$i.csv > scenario${i}_results.csv
   java -jar fuel_test.jar $i $i
done

