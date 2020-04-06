import React from "react";

function Outputs (props){

  const outputRef = React.createRef();
  
  React.useEffect(()=>{
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
  var outputs = props.outputs.map((output, index) => {
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
