package util.struct.element;

import util.struct.HTMLElement;

public class EndElementState implements ElementState{
	public HTMLElement e;
	public EndElementState(HTMLElement e){this.e=e;}
	@Override
	public ElementState state() {
		return null;
	}
	public HTMLElement get(){return e;}
}
