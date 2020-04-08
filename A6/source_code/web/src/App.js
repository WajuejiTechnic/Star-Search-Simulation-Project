import React, { Component } from 'react';
import './App.css';
import PlotMap from './PlotMap.js';
import Stats from "./Stats.js";
import Outputs from "./Outputs.js";
import Buttons from "./Buttons.js";
import SideMenu from './SideMenu.js';
import Report from './Report.js'
import M from 'materialize-css';

class App extends Component{
   
  constructor(props) {
    super(props);
    this.state = {
      files: [],
      filesToResume: [],
      fileToInitial:[],
      fileName: "",
      mode: "initial",
      mapWidth: 20,
      mapHeight: 15,
      maxTurns: 0,
      turns: 0,
      fuels: [],
      squares: [],
      safeSquares: 0,
      totalDrones: 0,
      drones: [],
      outputs: [],
      finalReport: "",
      disabled: false,
      show: false,
    }
    this.fastForward = false;
    this.forward = null;
  }

  fetchInitData = () => {
    console.log("fetch file = "+this.state.fileName)
    console.log("currnet mode:" + this.state.mode)
    fetch("/starSearch/?fileName=" + this.state.fileName + "&mode="+ this.state.mode)
    .then(res => res.json())
    .then(result => {
      //console.log(result)
      this.setState({
        mapWidth: result["width"],
        mapHeight: result["height"],
        maxTurns: result["maxTurns"],
        turns: result["turns"],
        fuels: result["fuels"],
        totalDrones: result["totalDrones"],
        squares: result["squares"],
        safeSquares: result["safeSquares"],
        drones: result["drones"],
        outputs: result["outputs"]
      })
    
      console.log("Fetch InitData ")
      console.log("width, heigth = " + result["width"] +  " " + result["height"])
      console.log("maxTurns, turns, totalDrones = " + result["maxTurns"] + " " 
                  +result["turns"] +" "+ result["totalDrones"])
      console.log("squares = " + JSON.stringify(result["squares"]))
      console.log("safeSquares = " + JSON.stringify(result["safeSquares"]))
      console.log("fuels = " + JSON.stringify[result["fuels"]])
      console.log("totalDrones" + result["totalDrones"])
      console.log("drones = " + JSON.stringify(result["drones"]))
      console.log("outputs = " + JSON.stringify(result["outputs"]))
      console.log("finalReport = " + JSON.stringify(result["finalReport"]))
    })
    .catch(error => {
      console.log('err', error)
    })
  }

  fetchFiles = () => {
    console.log("fetch files")
    console.log("currnet mode:" + this.state.mode)
    fetch("/files?fileName="+ this.state.fileName)
    .then(res => res.json())
    .then(result => {
      //console.log(result)
      this.setState({
        filesToResume: result["filesToResume"],
        filesToInitial: result["filesToInitial"],
        files: this.state.mode === "initial" ? result["filesToInitial"] : result["filesToResume"]
      })
      console.log("filesToResume = " + JSON.stringify(result["filesToResume"]))
      console.log("filesToInitial = " + JSON.stringify(result["filesToInitial"]))
    })
    .catch(error => {
      console.log('err', error)
    })
  }

  fetchUpdates = () => {
    fetch("/nextAction")
    .then(res => res.json())
    .then(result => {
      console.log(result)
      console.log("Fetch updates") 
      console.log("turns = " + JSON.stringify(result["turns"]))
      console.log("squares = " + JSON.stringify(result["squares"]))
      console.log("safeSquares = " + JSON.stringify(result["safeSquares"]))
      console.log("drones = " + JSON.stringify(result["drones"])) 
      console.log("outputs = " + JSON.stringify(result["outputs"]))
      console.log("finalReport = " + JSON.stringify(result["finalReport"]))

      this.setState({
        squares: result["squares"],
        safeSquares: result["safeSquares"],
        drones: result["drones"],
        turns: result["turns"],
        outputs: result["outputs"],
        finalReport: result["finalReport"],
        disabled: result["finalReport"] === "" ? "yes": ""
      })

      if(this.fastForward && result["finalReport"] === ""){
        console.log("keeping update")
        this.forward = setTimeout(this.fetchUpdates, 500)
      }
      else{
        this.forward = null
      } 
      if(result["finalReport"] !== "") this.fetchFiles() 
    })
    .catch(error => {
      console.log('err', error)
    })
  }

  // handle button click
  handleStart = (e) => {
    if(this.state.fileName.endsWith(".csv")){
      console.log("press START...")
      this.fetchInitData()
      this.setState({
        disabled: true,
        show: true,
      })
    }
    else{
      e.preventDefault()
    }
  }
  
  handleNext = () => {
    console.log("press NEXT...")  
    this.fastForward = false
    this.fetchUpdates()
  }

  handleForward = (e) => {
    console.log("press FORWARD...")
    this.fastForward = true
    this.fetchUpdates() 
  }

  handlePause = (e) => {
    console.log("press PAUSE...")
    this.fastForward = false
  }

  handleStop = (e) => {
    console.log("press STOP...")
    if (this.forward !== null) {
      clearTimeout(this.forward)
    }
    this.reset()
    this.fetchFiles()
    this.fastForward = false;
    this.forward = null;
  }

  // handle input file
  handleFile = (e) => {
    this.reset()
    this.setState({
      fileName: e.target.value,
    });
    
    console.log("select file...")
    console.log("fileName: " + e.target.value)
  }

  // handle mode 
  handleMode = (e) => {
    //console.log("handleMode: ", e.target.value === "initial")
    this.reset()
    this.setState({ 
      mode: e.target.value,
      files: e.target.value === "initial"? this.state.filesToInitial : this.state.filesToResume
    });
    console.log("choose mode...")
    console.log("mode = "+ e.target.value)
    console.log("files = " + this.state.files)
  }

  reset = () => {
    this.setState({
      mapWidth: 20,
      mapHeight: 15,
      maxTurns: 100,
      turns: 0,
      fuels:[],
      squares: [],
      totalDrones: 0,
      drones: [],
      outputs:[],
      finalReport: "",
      disabled: false,
      show: false
    });
  }

  componentDidMount = () => {
    M.AutoInit()
    this.fetchFiles()
  }

  render(){
    return (
      <div className="App"> 
        <div className="simulator">
          <SideMenu mode = {this.state.mode} handleMode = {this.handleMode} disabled = {this.state.disabled} 
            files = {this.state.files} handleFile = {this.handleFile} autoClick = {this.state.autoClick}/>

          <Outputs show = {this.state.show} outputs = {this.state.outputs} />

          <Stats fileName = {this.state.fileName} mode = {this.state.mode}  mapWidth = {this.state.mapWidth}
            mapHeight = {this.state.mapHeight}  maxTurns = {this.state.maxTurns} squares = {this.state.squares}
            totalDrones =  {this.state.totalDrones} drones = {this.state.drones} show = {this.state.show} 
            turns = {this.state.turns} fuels = {this.state.fuels} safeSquares = {this.state.safeSquares}/>   

          <Buttons disabled = {this.state.disabled} handleStart = {this.handleStart} handleNext = {this.handleNext} 
            handleForward = {this.handleForward} handlePause = {this.handlePause} handleStop = {this.handleStop}
            fileName = {this.state.fileName} mode = {this.state.mode} finalReport = {this.state.finalReport}/>

          <PlotMap mapWidth = {this.state.mapWidth}  mapHeight = {this.state.mapHeight} 
            squares = {this.state.squares} drones = {this.state.drones}/>

          <Report finalReport = {this.state.finalReport}></Report>
        </div>
      </div>
    );
  }
}

export default App;
