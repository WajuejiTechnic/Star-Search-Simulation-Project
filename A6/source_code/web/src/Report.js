import React from "react";

function Report (props) {
  return(
    <div id="report" className="modal">
      <div className="card cyan darken-4">
          <div className="card-content white-text left-align">
            <h6 className="card-title bold">Final Report</h6>
            <p>MapSize: {props.finalReport["mapSize"]}</p>
            <p>ExplorableSquares: {props.finalReport["numOfExplorableSquares"]}</p>
            <p>ExploredSafeSquares: {props.finalReport["numOfExploredSafeSquares"]}</p>
            <p>CompletedTurns {props.finalReport["completeTurns"]}</p>
        </div>
        <div className="card-action modal-close right-align">
          <span className ="orange-text">OK</span>
        </div>
    </div>
  </div>
  )
}

export default Report;
