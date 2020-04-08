import React, {useEffect} from "react";

function Outputs (props){
  console.log("Output", props)
  const outputRef = React.createRef();
  
  useEffect(()=>{
    const scrollToBottom = () => {
      outputRef.current.scrollIntoView({
        behavior: 'smooth'
      });
    }
    if(outputRef.current){
      scrollToBottom();
    }
  },[outputRef])
 
  var name = props.show ? "content" : "hide"
  var lines = []
  for (var x of props.outputs){
    lines = lines.concat(x)
  }
  var outputs = lines.map((output, index) => {
    return (
        <p key = {index} ref={outputRef}>{output}</p>
    )
  });

  return (
    <div className="output">
        <div className = "card-panel black radius center">
        <h5>Outputs</h5>
      </div>
      <div className = {name}>
        {outputs}
      </div>
    </div>
  );
}

export default Outputs;
