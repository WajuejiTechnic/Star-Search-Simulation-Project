import React from "react";

function Stats (props) {

  var name = props.show ? "content" : "hide"
  console.log("show", props.show, name)
  var mapSize = props.mapWidth * props.mapHeight
  //var visibleSquares = props.squares.length

  var drones = props.drones.map(d => {
    var state = d["crashed"] ? "crashed":"alive"
    return(
      <tr key ={d["id"]}>
          <td>d{d["id"]} </td>
          <td>({d["x"]},{d["y"]})</td>
          <td>{d["direction"].toString().toLowerCase()}</td>
          <td>{d["fuel"]}</td>
          <td>{d["strategy"]}</td>
          <td>{state}</td>
          
      </tr> 
    )
  });

  var fuels = () => {
    return(
      <table className="highlight">
        <thead>
          <tr>
              <th>InitailFuel</th>
              <th>RechangeFuel</th>
              <th>MaxFuel</th>
          </tr>
        </thead>
        <tbody>
          <tr key ={Math.random()}>
            <td>{props.fuels[0]}</td>
            <td>{props.fuels[1]}</td>
            <td>{props.fuels[2]}</td>
          </tr> 
        </tbody>
      </table>
    )
  }

  return (
    <div className="report">
      <div className = "card-panel black radius center">
        <h5>Report</h5>
      </div>
      <div className = {name}>
        <p> Mode: {props.mode} </p>
        <p> File: {props.fileName}</p>
        <p> Width, Height: {props.mapWidth} , {props.mapHeight}</p>
        <p> Mapsize: {mapSize} </p>
        <p> MaxTurns: {props.maxTurns} </p>
        <p> CurrentTurns: {props.turns}</p>
        <p> ExplorableSquares: {props.explorableSquares}</p>
        <p> ExploredSafeSquares: {props.safeSquares}</p>
        <p> TotalDrones: {props.totalDrones}</p>
        <p> ActiveDrones: {props.drones.length}</p>
        {fuels()}
        <table className="highlight">
        <thead>
          <tr>
              <th>id</th>
              <th>coord.</th>
              <th>direc.</th>
              <th>fuel</th>
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

export default Stats;
