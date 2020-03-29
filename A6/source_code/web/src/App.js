import React, { Component } from 'react';
import './App.css';
import PlotMap from './PlotMap';
import Reports from "./Reports.js";
import Outputs from "./Outputs.js";
import Buttons from "./Buttons.js";

class App extends Component{
  state = {
    mapWidth: 20,
    mapHeight: 15,
  }

  fetchInitData = () => {
    fetch('/getsize')
    .then(res => res.json())
    .then(result => {
      console.log(result[0], result[1])
      this.setState({
        mapWidth: result[0],
        mapHeight: result[1]
      })
    })
    .catch(error => {
      console.log('err', error)
    })
  }

  componentDidMount(){
    console.log('start fetch Data')
    this.fetchInitData()
  }

  render(){
    return (
      <div className="App"> 
        <div className="simulator">
          <Outputs/>
          <Reports/>
          <Buttons/>
          <PlotMap mapWidth = {this.state.mapWidth} 
            mapHeight = {this.state.mapHeight}/>  
        </div>
      </div>
    );
  }
}

export default App;
