import React from "react";

function InputAction(props) {
  console.log("InputAction", props)
  var directions = ["north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest"]
  var steps  = Array.from(Array(props.maxSteps), (_,index) => index+1)
  console.log("steps " + steps)
  var data = "press GO to continue"
  if(props.actionPair.endsWith("go") || props.actionPair === "") data = "Please choose an action"
  const steer_options = directions.map((d, index) => {
    var steer = "steer," + d
    return(
      <option key={"steer" + index} value={steer}>{steer}</option>
    )
  });

  const thurst_options = steps.map((n, index) => {
    var thrust = "thrust," + n
    return(
    <option key={"thrust" + index} value={thrust}>{thrust}</option>
    )
  });

  return (
    <div className = "row action-go" hidden = {!props.isStrat2}>
        <div className = "col 9 action">
          <select className= "browser-default" onChange = {props.handleAction} >
            <option value = {props.actionByUser}>Choose an action to go</option>
            <option value = "scan, ">scan</option>
            <option value = "recharge, ">recharge</option>
            <option value = "pass, ">pass</option>
            {thurst_options}
            {steer_options}
          </select>
        </div>
        <div className = "col 2 mtop-5">
          <button className = "waves-effect waves-light btn yellow darken-3 pulse tooltipped"
          data-tooltip = {data} onClick={props.handleClickGo}>Go</button>
          
        </div>
    </div>
  )
};

export default InputAction;
