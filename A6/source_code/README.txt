Link to mts_video.mp4: https://drive.google.com/open?id=1bY04mp7z66iFwiAOJdciH7EN5i95iaJZ

Start Web Application
1. Download virtual machine
   team_10.ova: https://drive.google.com/open?id=16u1_TMGzOWvij-xwguOu52lv8VLBoa11
   (Md5sum: c11ffe3f4b0105c262471c521257c008 Password: group10)
   
2. Download and unzip source_code.zip from our group submission and store it in VM

3. Open terminal and go to /source_code

4. Run the command below to build and run file in the current directory: gradle run

5. Build and run successfully if you see the information below：
   Tomcat started on port(s): 8080
   <========----> 75% EXECUTING [3M 26S]
   > :run

Once backend build and run successfully, there are two ways to start GUI:

- Production Mode
6. Open the browser (Firefox), enter http://127.0.0.1:8080

- Development mode (OR you can start it with development mode)
6. Open Visual Studio Code, Click File -> Add folder to Workspace and select the folder source_code

7. Click Terminal -> New Terminal, cd web to go to the folder /web

8. To Install the dependencies in the local folder node_modules, run the command (once): 
   npm install

9. To run server file and app in the development mode, use the command: 
   npm start

10. The browser(Firefox) will open automatically with an address http://127.0.0.1:3000


Fuel Test
1. To regenerate star_search.jar (already generated under source_code ), run the command under source_code :
   gradle shadowjar

2. To save output to a scenario_results file, run the command:
   java -cp star_search.jar simulator.Main scenario<N>.csv > scenario<N>_results.csv

3. Test one file as following (only test scenario files 2) :
   java -jar fuel_test.jar 2 2
   
4. Test mulitple files as following (only test scenario files 3 to 5) :
   java -jar fuel_test.jar 3 5

5. Or you can test all scenario files and save it to report.log, run the command:
   ./test.sh > report.log

 
