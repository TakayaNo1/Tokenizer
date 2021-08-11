package util.struct.html.element;

import util.struct.ElementState;
import util.struct.html.HTMLElement;

public class EndElementState implements ElementState{
	public HTMLElement e;
	public EndElementState(HTMLElement e){this.e=e;}
	@Override
	public ElementState state() {
		return null;
	}
	public HTMLElement get(){return e;}
}
