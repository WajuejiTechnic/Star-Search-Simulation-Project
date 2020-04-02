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
                }
            }
        }
        updateSquare();
    };

    var getName = (square, sqrSize) => {
        if (square.name === "stars") {
            return (
                <img src = {stars} alt = "stars"
                    style = {{width: sqrSize + "vmin", height: sqrSize+ "vmin"}}/>
            );
        }
        if (square.name === "drone") {
            return (
                <img src = {drone} alt = "drone"
                    style = {{width: sqrSize + "vmin", height: sqrSize + "vmin"}}/>
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
        if (square.state === "explored") {
            return "lightgreen";
        }
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
                <div style={{height: sqrSize + "vmin", width: sqrSize + "vmin", backgroundColor: getState(square)}} 
                    className = "square" key={id}>
                    {getName(square, sqrSize)}
                </div>
            );
        }
        squares.push(<div key={Math.random()} className = "nextline"> </div>)
    }
    return <div>{squares}</div>;
}

export default PlotMap;