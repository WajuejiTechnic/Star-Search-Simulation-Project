import React from "react";

function Buttons(props) {
    console.info("Button", props);
    var data = "<= please select a scenario file"
    if (props.fileName.endsWith(".csv")) data =  props.mode + " mode with " + props.fileName 
    return (
      <div className="button">
        <button className = "waves-effect waves-light btn cyan darken-2 mleft-30 mtop-20 tooltipped" data-position="left" 
          data-tooltip = {data} onClick={props.handleStart} disabled = {props.disabled}>
          <i className="material-icons right">play_arrow</i>start</button>

        <button className = "waves-effect waves-light btn cyan darken-2 mleft-30 mtop-20" 
           onClick={props.handleNext} disabled = {!props.disabled}><i className="material-icons right">skip_next</i>next</button>

        <button className="waves-effect waves-light btn cyan darken-2 mleft-30 mtop-20"
          onClick={props.handleForward} disabled = {!props.disabled}><i className="material-icons right">forward</i>fastforward</button>

        <button className="waves-effect waves-light btn cyan darken-2 mleft-30 mtop-20"
          onClick={props.handlePause} disabled = {!props.disabled}><i className="material-icons right">pause</i>pause</button>

          <button className="waves-effect waves-light btn cyan darken-2 mleft-30 mtop-20"
            onClick={props.handleStop} disabled = {!props.disabled}> <i className="material-icons right">stop</i>stop</button>
      </div>
    );
}

export default Buttons;
