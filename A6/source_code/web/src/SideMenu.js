import React from "react";

function SideMenu(props) {

  var options = props.files.map(fl => {
    //console.log("fl:", fl)
    return(
      <option key={fl} value={fl}>{fl}</option>
    )
  });

  return (
    <div className="side-menu">
      <button data-target="setting" className="btn-floating pulse sidenav-trigger tooltipped" 
        data-position="right" data-tooltip = "menu">
        <i className="material-icons">menu</i></button>
    
        <div id="setting" className="sidenav" hidden = {props.disabled}>
          <div className = "container mtop-40">
          <h4> star search </h4>

          <h6> Please select mode:</h6>
            <p>
            <label className = "label">
              <input type="radio" className="with-gap" name = "setMode" value = "initial" 
                  onChange = {props.handleMode} checked={props.mode === "initial"}/>
              <span>Initial Mode</span>
            </label>
            </p>
            <p>
            <label className = "label">
              <input type="radio" className="with-gap" name = "setMode" value = "resume" 
                  onChange = {props.handleMode} checked={props.mode === "resume"}/>
              <span>Resume Mode</span>
            </label>
            </p>


          <h6> Please select scenario file:</h6>
          <div className ="input-field">
            <select value = {props.fileName} onChange = {props.handleFile}>
            <option value="" >Select Scenario File</option>
              {options}
            </select>
          </div>
        </div>
        </div>
      
    </div>
  
  )
}

export default SideMenu;
