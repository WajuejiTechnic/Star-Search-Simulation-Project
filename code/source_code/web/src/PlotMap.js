import React from "react";
import drone from './drone.png';
import stars from './stars.png';
import sun from './sun.png';

function PlotMap(props) {
    console.info("PlotMap", props);
    var gameMap = [];

    var updateSquare = () => {
        for(var k = 0; k < Object.keys(props.squares).length; k++){
            var sqr = props.squares[k];
            var i = sqr["y"]
            var j = sqr["x"]
            gameMap[i][j] = {
                x: i,
                y: j,
                name: sqr["name"].toString().toLowerCase(),
                state: sqr["state"].toString().toLowerCase(),
                id:"",
                direction:""
            }
        }
    }

    var updateDrone = () => {
        for(var k = 0; k < Object.keys(props.drones).length; k++){
            var drone = props.drones[k];
            var i = drone["y"]
            var j = drone["x"]
            gameMap[i][j] = {
                x: i,
                y: j,
                name: "drone",
                id: drone["id"].toString(),
                direction: drone["direction"].toString().toLowerCase()
            }
        }
    }
       
    var updateGameMap = () => {
        for(var i = 0; i < props.mapHeight; i++) {
            gameMap.push([])
            for (var j = 0; j < props.mapWidth; j++) {
                gameMap[i][j] = {
                    x: i,
                    y: j,
                    name: "",
                    state: "",
                    direction: ""
                }
            }
        }
        updateSquare();
        updateDrone();
    };

    var getDegree = (direc) => {
        if (direc === "northeast") {
            return "rotate(0deg)";
        }    
        if (direc === "east") {
            return "rotate(45deg)";
        }
        if (direc === "southeast") {
            return "rotate(90deg)";
        }
        if (direc === "south") {
            return "rotate(135deg)";
        }
        if (direc === "southwest") {
            return "rotate(180deg)";
        }
        if (direc === "west") {
            return "rotate(225deg)";
        }
        if (direc === "northwest") {
            return "rotate(270deg)";
        }
        if (direc === "north") {
            return "rotate(315deg)";
        }
    }

    var getName = (square, sqrSize) => {
        if (square.name === "stars") {
            return (
                <img src = {stars} alt = "stars"
                    style = {{width: sqrSize + "vmin", height: sqrSize+ "vmin"}}/>
            );
        }
        if (square.name === "drone") {
            var direc = square.direction.toString().toLowerCase()
            return (
                <img src = {drone} alt = "drone" 
                    style = {{width: sqrSize + "vmin", height: sqrSize + "vmin", transform: getDegree(direc)}}/>
            );
        }
        if (square.name === "sun") {
            return (
                <img src = {sun} alt = "sun"
                    style = {{width: sqrSize + "vmin", height: sqrSize + "vmin"}}/>
            );
        }
    }

    var getState = (square) => {
        if (square.state === "scanned") {
            return "lightgreen";
        }
    }

    var getData = (square) => {
        var data = " ("+ square.y + ", " + square.x + ") "
        if (square.name === "drone") {
            data = "d" + square.id + data
            data += square.direction.toUpperCase()
        }
        return data
    }

    updateGameMap();
    var sqrSize = 120 / props.mapWidth;
    if (120 / props.mapWidth * props.mapHeight > 90){
        sqrSize = 90 / props.mapHeight;
        //console.log("case1", tileSize)
    }   
    else if(props.mapHeight >= props.mapWidth) {
        sqrSize = 90 / props.mapHeight;
        //console.log("case2", tileSize)
    }
    
    var squares = [];
    var id = 0;
    for (var r = props.mapHeight-1; r >= 0; r--){
        for (var c = 0; c < props.mapWidth; c++) {
            var square = gameMap[r][c];
            id += 1;
            squares.push(
                <div className = "square tooltipped" data-position="bottom" data-tooltip = {getData(square)} key={id}
                    style={{height: sqrSize + "vmin", width: sqrSize + "vmin", backgroundColor: getState(square)}}>
                    {getName(square, 0.8*sqrSize)}
                </div>
            );
        }
        squares.push(<div key={Math.random()} className = "nextline"> </div>)
    }
    return <div>{squares}</div>;
}

export default PlotMap;