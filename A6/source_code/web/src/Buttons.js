import React from "react";

function Buttons(props) {
    console.info("Button", props);
    return (
      <div className="buttons">
        <button disabled = {props.isDisabled} className = "waves-effect waves-light btn cyan darken-2 mtop" 
          onClick={props.handleClick}>next
        </button>

        <button className="waves-effect waves-light btn cyan darken-2 mleft mtop">
          <i className="material-icons right">cloud</i>button
        </button>

        <button className="waves-effect waves-light btn cyan darken-2 mleft mtop">
          <i className="material-icons right">cloud</i>button
        </button>
      </div>
    );
}

export default Buttons;
