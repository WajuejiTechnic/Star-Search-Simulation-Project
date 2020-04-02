import React, { Component } from 'react';
import './App.css';
import PlotMap from './PlotMap';
import Reports from "./Reports.js";
import Outputs from "./Outputs.js";
import Buttons from "./Buttons.js";

class App extends Component{
  constructor(props) {
    super(props);
    this.state = {
      file: "scenario0.csv",
      mapWidth: 20,
      mapHeight: 15,
      maxTurns: 100,
      numOfDrones: 0,
      squares: [],
      drones: [],
      output: "",
      finalReport: [],
      isDisabled: ""
    }
  }

  fetchInitData = () => {
    fetch("/star-search?file="+ this.state.file)
    .then(res => res.json())
    .then(result => {
      //console.log(result)
      this.setState({
        mapWidth: result["width"],
        mapHeight: result["height"],
        maxTurns: result["maxTurns"],
        numOfDrones: result["numOfDrones"],
        squares: result["squares"],
        drones: result["drones"]
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
      console.log("finalReport = " + JSON.stringify(result["finalReport"]));
      console.log("disabled = " + JSON.stringify(result["disabled"]))
      this.setState({
        squares: result["squares"],
        drones: result["drones"],
        output: result["output"],
        finalReport: result["finalReport"],
        isDisabled: result["disabled"]
      })
    })
    .catch(error => {
      console.log('err', error)
    })
  }

  handleClick = () => {
    //this.setState({
    //  mapWidth: 10,
    //})
    //console.log("after click: width = "+ this.state.mapWidth);
    this.fetchUpdates();
  }

  componentDidMount = () => {
    this.fetchInitData();
  }

  render(){
    return (
      <div className="App"> 
        <div className="simulator">
          <Outputs/>
          <Reports/>
          <Buttons isDisabled = {this.state.isDisabled} handleClick = {this.handleClick}/>
          <PlotMap mapWidth = {this.state.mapWidth}  mapHeight = {this.state.mapHeight} 
            squares = {this.state.squares}/>
        </div>
      </div>
    );
  }
}

export default App;
