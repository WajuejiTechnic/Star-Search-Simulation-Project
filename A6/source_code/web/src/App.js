import React, { Component } from 'react';
import './App.css';
import PlotMap from './PlotMap';
import Reports from "./Reports.js";
import Outputs from "./Outputs.js";
import Buttons from "./Buttons.js";
import SideMenu from './SideMenu';
import M from 'materialize-css';

class App extends Component{
   
  constructor(props) {
    super(props);
    this.state = {
      files: [],
      fileName: "",
      mode: "initial",
      mapWidth: 20,
      mapHeight: 15,
      maxTurns: 100,
      numOfDrones: 0,
      squares: [],
      drones: [],
      output: [],
      outputs: [],
      finalReport: "",
      disabled: false,
      show: false
    }

    this.generatefiles();
    this.fastForward = false;
    this.forward = null;
  }

  fetchInitData = () => {
    console.log("fetch file = "+this.state.fileName)
    fetch("/star-search?fileName="+ this.state.fileName)
    .then(res => res.json())
    .then(result => {
      //console.log(result)
      this.setState({
        mapWidth: result["width"],
        mapHeight: result["height"],
        maxTurns: result["maxTurns"],
        numOfDrones: result["numOfDrones"],
        squares: result["squares"],
        drones: result["drones"],
      })
     
      console.log("Fetch InitData ")
      console.log("width, heigth = " + result["width"] +  " " + result["height"])
      console.log("maxTurns, numOfDrones = " + result["maxTurns"] + " " + result["numOfDrones"])
      console.log("squares = " + JSON.stringify(result["squares"]))
      console.log("drones = " + JSON.stringify(result["drones"]))
    })
    .catch(error => {
      console.log('err', error)
    })
  }

  fetchUpdates = () => {
    fetch("/next-action")
    .then(res => res.json())
    .then(result => {
      //console.log(result)
      console.log("Fetch updates") 
      console.log("squares =" + JSON.stringify(result["squares"]))
      console.log("drones = " + JSON.stringify(result["drones"]))
      console.log("output = " + result["output"])
      console.log("finalReport = " + JSON.stringify(result["finalReport"]))
      this.state.outputs.push(result["output"][0])
      this.state.outputs.push(result["output"][1])
      this.setState({
        squares: result["squares"],
        drones: result["drones"],
        outputs: this.state.outputs,
        finalReport: result["finalReport"],
        disabled: result["finalReport"] === "" ? "yes": ""
      })
      if(this.fastForward && result["finalReport"] === ""){
        console.log("keeping update")
        this.forward = setTimeout(this.fetchUpdates, 1000)
      }
      else{
        this.forward = null
      }
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
        outputs:[]
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
    this.fastForward = false
    if (this.forward !== null) {
      clearTimeout(this.forward)
    }
      this.forward = null
      this.setState({
        mode: "initial",
        mapWidth: 20,
        mapHeight: 15,
        maxTurns: 100,
        numOfDrones: 0,
        squares: [],
        drones: [],
        output: [],
        outputs: [],
        finalReport: "",
        disabled: false,
        show: false
    });
  }

  // handle input file
  handleFile = (e) => {
    this.setState({
      fileName: e.target.value,
    });
    console.log("select file...")
    console.log("fileName: " + e.target.value)
  }

  // handle mode 
  handleMode = (e) => {
    this.setState({ 
      mode: e.target.value
    });
    console.log("choose mode...")
    console.log("initialMode:"+ e.target.value)
  }

  // generate scenario filenames
  generatefiles = () => {
    for(var i = 0; i <= 30; i++){
      this.state.files.push("scenario" + i + ".csv")
    }
  }

  componentDidMount = () => {
    M.AutoInit()
  }

  /*<Mode mode = {this.state.mode} handleMode = {this.handleMode} disabled = {this.state.disabled}/>
  <InputFile files = {this.state.files} handleFile = {this.handleFile} disabled = {this.state.disabled}/>*/
  render(){
    return (
      <div className="App"> 
        <div className="simulator">
          <SideMenu mode = {this.state.mode} handleMode = {this.handleMode} disabled = {this.state.disabled} 
            files = {this.state.files} handleFile = {this.handleFile}/>
          <Outputs show = {this.state.show} outputs = {this.state.outputs} />

          <Reports mapWidth = {this.state.mapWidth} mapHeight = {this.state.mapHeight} squares = {this.state.squares}
            maxTurns = {this.state.maxTurns} drones = {this.state.drones} show = {this.state.show}/>   

          <Buttons disabled = {this.state.disabled} handleStart = {this.handleStart} handleNext = {this.handleNext} 
            handleForward = {this.handleForward} handlePause = {this.handlePause} handleStop = {this.handleStop}
            fileName = {this.state.fileName} mode = {this.state.mode}/>

          <PlotMap mapWidth = {this.state.mapWidth}  mapHeight = {this.state.mapHeight} 
            squares = {this.state.squares} drones = {this.state.drones}/>
        </div>
      </div>
    );
  }
}

export default App;
