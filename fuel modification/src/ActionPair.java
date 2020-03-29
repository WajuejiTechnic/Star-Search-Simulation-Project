
public class ActionPair {
	private Object action;
	private Object param;
	
	public ActionPair(Object action, Object param){
		this.action = action;
		this.param = param;
	}

	public Object getAction() {
		return action;
	}

	public Object getParam() {
		return param;
	}
}
