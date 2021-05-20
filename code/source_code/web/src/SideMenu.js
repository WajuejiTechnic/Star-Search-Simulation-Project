import React from "react";

function SideMenu (props) {
  console.log("sideMenu", props)

  const options = props.files.map((fl, index) => {
    //console.log("fl:", fl)
    return(
      <option key={index} value={fl}>{fl}</option>
    )
  });

  return (
    <div className="side-menu">
      <button data-target="setting" className="btn-floating pulse sidenav-trigger tooltipped" 
        data-position="right" data-tooltip = "menu" disabled = {props.disabled} >
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
          <select className= "browser-default" onChange = {props.handleFile} >
            <option value = {props.fileName}>Select Scenario File</option>
              {options}
          </select>
        </div>
      </div>
    </div>
  )
}

export default SideMenu;
