import React from "react";

function Reports (props) {

  var name = props.show ? "content" : "hide"
  console.log("show", props.show, name)
  var mapSize = props.mapWidth * props.mapHeight
  var numOfdrone = props.drones.length
  var numOfExploredSquares = props.squares.length + numOfdrone
  var drones = props.drones.map(d => {
    var state = d["crashed"] ? "crash":"alive"
    return(
      <tr key ={d.id}>
          <td>d{d.id} </td>
          <td>({d["y"]},{d["x"]})</td>
          <td> {d["direction"].toString().toLowerCase()}</td>
          <td>{d["strategy"]}</td>
          <td>{state}</td>
      </tr> 
    )
  });

  return (
    <div className="report">
      <div className = "card-panel black radius center">
        <h5>Statistic</h5>
      </div>
      <div className = {name}>
        <p> MapWidth: {props.mapWidth}</p>
        <p> MapHeight: {props.mapHeight}</p>
        <p> MapSize: {mapSize}</p>
        <p> MaxTurns: {props.maxTurns}</p>
        <p> ExploredSquares: {numOfExploredSquares}</p>
        <p> NumOfDrones: {numOfdrone}</p>
        <table className="highlight">
        <thead>
          <tr>
              <th>id</th>
              <th>coord.</th>
              <th>direction</th>
              <th>strategy</th>
              <th>state</th> 
          </tr>
        </thead>
        <tbody>
          {drones}
        </tbody>
      </table>
      </div>
    </div>
    
  );
}

export default Reports;
