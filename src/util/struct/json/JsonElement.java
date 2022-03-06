package util.struct.json;

import java.util.ArrayList;
import java.util.List;

import util.struct.Element;
import util.struct.Node;

public class JsonElement extends Element {
	private String key, value;
	private JsonElementState state;
	
	public JsonElement() {}
	public JsonElement(JsonElementState state) {
		this.state=state;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public JsonElementState getState() {
		return state;
	}
	public void setState(JsonElementState state) {
		this.state = state;
	}
	
	public String toString(){
		return "Key: "+key+", Value: "+value+", State: "+state;
	}
	
	public enum JsonElementState{
		STRING_VALUE,NUMBER_VALUE,BOOLEAN_VALUE,NULL_VALUE,
		OBJECT_START,OBJECT_END,ARRAY_START,ARRAY_END;
	}
	
	public static List<Node<JsonElement>> search(Node<JsonElement> root,String match){return search(root, match.split("/"), 0);}
	private static List<Node<JsonElement>> search(Node<JsonElement> node,String[] match,int index){
		String key = match[index];
		
		List<Node<JsonElement>> list=new ArrayList<>();
		for(int i=0;i<node.getChildrenSize();i++){
			Node<JsonElement> cNode=node.getChildren(i);
			JsonElement e=cNode.getElement();
			
			if(e != null){
				if(e.getKey()==null) {
					if(key.equals("{") && e.getState()==JsonElementState.OBJECT_START || key.equals("[") && e.getState()==JsonElementState.ARRAY_START) {
						if(match.length-1>index){
							list.addAll(search(cNode, match, index+1));
						}else if(match.length-1==index){
							list.add(cNode);
						}
					}
				}else if(e.getKey().equals(key)) {
					if(match.length-1>index){
						list.addAll(search(cNode, match, index+1));
					}else if(match.length-1==index){
						list.add(cNode);
					}
				}
			}
		}
		return list;
	}
}
