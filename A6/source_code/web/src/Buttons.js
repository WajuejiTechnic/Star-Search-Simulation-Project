import React from "react";

function Buttons(props) {
    console.info("Button", props);
    var data = "<= please select a scenario file"
    if (props.fileName.endsWith(".csv")) data =  props.mode + " mode with " + props.fileName 
    var name = "waves-effect waves-light btn cyan darken-2 mleft-30 mtop-30 btn"
    var menu = name + "tooltipped"
    var hide = Object.keys(props.finalReport).length === 0 ? true:false
    //var model = props.finalReport !== "" ?  name + "modal-trigger" : "hide"
    if (props.finalReport === "") {}
    return (
      <div className="button">
        <button className = {menu} data-position="left" data-tooltip = {data} onClick={props.handleStart} 
          disabled = {props.disabled}> <i className="material-icons right">play_arrow</i>start</button>

        <button className = {name} onClick={props.handleNext} disabled = {!props.disabled}>
          <i className="material-icons right">skip_next</i>next</button>

        <button className={name} onClick={props.handleForward} disabled = {!props.disabled}>
          <i className="material-icons right">forward</i>fastforward</button>

        <button className={name} onClick={props.handlePause} disabled = {!props.disabled}>
          <i className="material-icons right">pause</i>pause</button>

        <button className={name} onClick={props.handleStop} disabled = {!props.disabled}>
           <i className="material-icons right">stop</i>stop</button>

        <button className="waves-effect waves-light btn cyan darken-2 mleft-30 mtop-20 btn modal-trigger" data-target="report"
          disabled = {hide}><i className="material-icons right">message</i>Final Report</button>
      </div>
    );
}

export default Buttons;
