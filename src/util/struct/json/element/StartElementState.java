package util.struct.json.element;

import util.struct.ElementState;
import util.struct.json.JsonElement;
import util.struct.json.JsonElement.JsonElementState;
import util.struct.json.JsonTokenizer;

public class StartElementState implements ElementState{
	
	private JsonTokenizer tkn;
	
	public StartElementState(JsonTokenizer tkn) {this.tkn=tkn;}
	
	@Override
	public ElementState state() {
		char c=tkn.getChar();
		
		if(c=='{') {
			return new EndElementState(new JsonElement(JsonElementState.OBJECT_START));
		}else if(c=='}') {
			return new EndElementState(new JsonElement(JsonElementState.OBJECT_END));
		}else if(c=='[') {
			return new EndElementState(new JsonElement(JsonElementState.ARRAY_START));
		}else if(c==']') {
			return new EndElementState(new JsonElement(JsonElementState.ARRAY_END));
		}else if(c=='\"') {
			if(tkn.isArrayState())return new StringValueState(tkn, new JsonElement(JsonElementState.STRING_VALUE));
			return new KeyState(tkn, new JsonElement());
		}else if(c=='t'||c=='f') {
			return new BooleanOrNullValueState(tkn, new JsonElement(JsonElementState.BOOLEAN_VALUE), c);
		}else if(c=='n') {
			return new BooleanOrNullValueState(tkn, new JsonElement(JsonElementState.NULL_VALUE), c);
		}else if(c=='0'||c=='1'||c=='2'||c=='3'||c=='4'||
				c=='5'||c=='6'||c=='7'||c=='8'||c=='9'||
				c=='.'||c=='+'||c=='-'||c=='e'||c=='E') {
			return new NumberValueState(tkn, new JsonElement(JsonElementState.NUMBER_VALUE), c);
		}else if(c==0)return null;
		
		return this;
	}
}

class KeyState implements ElementState{
	
	private JsonTokenizer tkn;
	private JsonElement e;
	private String value="";
	
	public KeyState(JsonTokenizer tkn,JsonElement e) {
		this.tkn=tkn;
		this.e=e;
	}
	
	@Override
	public ElementState state() {
		char c;
		while((c=tkn.getChar())!='\"') {
			value+=c;
//			System.out.println("key "+value);
		}
		e.setKey(value);
		while((c=tkn.getChar())!=':');
		while((c=tkn.getChar())==' ');
		
		if(c=='{') {
			e.setState(JsonElementState.OBJECT_START);
			return new EndElementState(e);
		}else if(c=='[') {
			e.setState(JsonElementState.ARRAY_START);
			return new EndElementState(e);
		}else if(c=='\"') {
			e.setState(JsonElementState.STRING_VALUE);
			return new StringValueState(tkn, e);
		}else if(c=='t'||c=='f') {
			e.setState(JsonElementState.BOOLEAN_VALUE);
			return new BooleanOrNullValueState(tkn, e, c);
		}else if(c=='n') {
			e.setState(JsonElementState.NULL_VALUE);
			return new BooleanOrNullValueState(tkn, e, c);
		}else if(c=='0'||c=='1'||c=='2'||c=='3'||c=='4'||
				c=='5'||c=='6'||c=='7'||c=='8'||c=='9'||
				c=='.'||c=='+'||c=='-'||c=='e'||c=='E') {
			e.setState(JsonElementState.NUMBER_VALUE);
			return new NumberValueState(tkn, e, c);
		}
		
		throw new IllegalArgumentException("Illegal char value("+c+") start.");
	}

}

class StringValueState implements ElementState{
	
	private JsonTokenizer tkn;
	private JsonElement e;
	private String value="";
	
	public StringValueState(JsonTokenizer tkn,JsonElement e) {
		this.tkn=tkn;
		this.e=e;
	}
	
	@Override
	public ElementState state() {
		char c=tkn.getChar();
		
		if(c=='\"') {
			e.setValue(value);
			return new EndElementState(e);
		}
		
		value+=c;
//		System.out.println("value "+value);
		return this;
	}

}

class BooleanOrNullValueState implements ElementState{
	
	private JsonTokenizer tkn;
	private JsonElement e;
	private String value="";
	
	public BooleanOrNullValueState(JsonTokenizer tkn,JsonElement e, char c) {
		this.tkn=tkn;
		this.e=e;
		value+=c;
	}
	
	@Override
	public ElementState state() {
		char c=tkn.getChar();
		value+=c;
		
		if(value.length()>5)throw new IllegalArgumentException("Illegal value :"+value+".");
		else if(value.length()>=4) {
			if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("null")) {
				e.setValue(value);
				return new EndElementState(e);
			}
		}
		
		return this;
	}
}

class NumberValueState implements ElementState{
	
	private JsonTokenizer tkn;
	private JsonElement e;
	private String value="";
	
	public NumberValueState(JsonTokenizer tkn,JsonElement e, char c) {
		this.tkn=tkn;
		this.e=e;
		value+=c;
	}
	
	@Override
	public ElementState state() {
		char c=tkn.getChar();
		
		if(c=='0'||c=='1'||c=='2'||c=='3'||c=='4'||
				c=='5'||c=='6'||c=='7'||c=='8'||c=='9'||
				c=='.'||c=='+'||c=='-'||c=='e'||c=='E') {
			value+=c;
		} else if (c == ','){
			e.setValue(value);
			return new EndElementState(e);
		} else if (c == '}' || c == ']'){
			e.setValue(value);
			tkn.addIndex(-1);
			return new EndElementState(e);
		}
		
		return this;
	}
}

class CommaState implements ElementState{
	private JsonTokenizer tkn;
	private JsonElement e;
	
	public CommaState(JsonTokenizer tkn,JsonElement e) {
		this.tkn=tkn;
		this.e=e;
	}
	
	@Override
	public ElementState state() {
		char c=tkn.getChar();
		if(c==',')return new EndElementState(e);
		else if(c==']')return new EndElementState(e);
		
		return this;
	}
}
