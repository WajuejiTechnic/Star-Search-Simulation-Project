import React from "react";

function Report (props) {

  return(
    <div id="report" className="middle" hidden = {props.hideReport}>
      <div className="card cyan darken-4">
          <div className="card-content white-text left-align">
            <h6 className="card-title bold">Final Report</h6>
            <p>MapSize: {props.finalReport["mapSize"]}</p>
            <p>ExplorableSquares: {props.finalReport["numOfExplorableSquares"]}</p>
            <p>ExploredSafeSquares: {props.finalReport["numOfExploredSafeSquares"]}</p>
            <p>CompleteTurns: {props.finalReport["completeTurns"]}</p>
        </div>
        <div className="card-action right-align">
          <button className ="waves-effect waves-light btn white orange-text" onClick = {props.handleClickOk}>OK</button>
        </div>
    </div>
  </div>
  )
}

export default Report;
