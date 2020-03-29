import React, { Component } from "react";
import drone from './drone.png';
import stars from './stars.png'

class PlotMap extends Component {
    gameMap = [];

    updateGameMap = () => {
        for(var i = 0; i < this.props.mapHeight; i++) {
            this.gameMap.push([])
            for (var j = 0; j < this.props.mapWidth; j++) {
                this.gameMap[i][j] = {
                    x: i,
                    y: j,
                    name: "stars",
                    state: "",
                }
                
            }
        }
        this.gameMap[2][1].name = "drone"
        this.gameMap[1][0].name = "drone"
        this.gameMap[1][3].name = "drone"
    };

    getName = (square, sqrSize) => {
        if (square.name === "stars") {
            return (
                <img src = {stars} alt = "stars"
                    style = {{width: sqrSize * 0.8 + "vmin", height: sqrSize *0.8 + "vmin"}}/>
            );
        }
        if (square.name === "drone") {
            return (
                <img src = {drone} alt = "drone"
                    style = {{width: sqrSize + "vmin", height: sqrSize + "vmin"}}/>
            );
        }
    }

    render() {
        this.updateGameMap();
        var sqrSize = 120 / this.props.mapWidth;
        if (120 / this.props.mapWidth * this.props.mapHeight > 90){
            sqrSize = 90 / this.props.mapHeight;
            //console.log("case1", tileSize)
        }   
        else if(this.props.mapHeight >= this.props.mapWidth) {
            sqrSize = 90 / this.props.mapHeight;
            //console.log("case2", tileSize)
        }
        
        var squares = [];
        var id = 0;
        for (var r = this.props.mapHeight-1; r >= 0; r--){
            for (var c = 0; c < this.props.mapWidth; c++) {
                var square = this.gameMap[r][c];
                id += 1;
                squares.push(
                    <div style={{ height: sqrSize + "vmin", width: sqrSize + "vmin" }} 
                        className="square" key={id}>
                        {this.getName(square, parseInt(sqrSize * 0.8))}
                    </div>
                );
            }
            squares.push(<div key={Math.random()}className = "nextline"> </div>)
        }
        return <div>{squares}</div>;
    }
};

export default PlotMap;