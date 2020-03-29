import React from "react";

class Stats extends React.Component {
  render() {
    return (
      <div className="buttons">
        <button className="waves-effect waves-light btn cyan darken-2 mtop">
          <i className="material-icons right">cloud</i>button
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
}

export default Stats;
