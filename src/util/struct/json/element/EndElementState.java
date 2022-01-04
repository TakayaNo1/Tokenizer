package util.struct.json.element;

import util.struct.ElementState;
import util.struct.json.JsonElement;

public class EndElementState implements ElementState{
	public JsonElement e;
	public EndElementState(JsonElement e){this.e=e;}
	@Override
	public ElementState state() {
		return null;
	}
	public JsonElement get(){return e;}
}
