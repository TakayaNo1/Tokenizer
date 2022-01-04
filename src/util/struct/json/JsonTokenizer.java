package util.struct.json;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import util.struct.ElementState;
import util.struct.Node;
import util.struct.Tokenizer;
import util.struct.json.JsonElement.JsonElementState;
import util.struct.json.element.EndElementState;
import util.struct.json.element.StartElementState;

public class JsonTokenizer extends Tokenizer{
	private static final boolean Display=false;
	
	private List<JsonElement> activeElements=new ArrayList<>();

	public JsonTokenizer(URL url) throws IOException {
		super(url);
	}
	public JsonTokenizer(File file) throws IOException{
		super(file);
	}
	public JsonTokenizer(String text){
		super(text);
	}

	public boolean isArrayState() {return activeElements.get(activeElements.size()-1).getState()==JsonElementState.ARRAY_START;}
	protected JsonElement getNextJsonElement(){
		ElementState es=new StartElementState(this);
		
		while(es != null && !(es instanceof EndElementState) ){
			es=es.state();
		}
		
		if(es instanceof EndElementState) {
			JsonElement e=((EndElementState) es).get();
			
			if(e.getState()==JsonElementState.ARRAY_START || e.getState()==JsonElementState.OBJECT_START) {
				activeElements.add(e);
			}else if(e.getState()==JsonElementState.ARRAY_END || e.getState()==JsonElementState.OBJECT_END) {
				activeElements.remove(activeElements.size()-1);
			}
			
			return e;
		}
		
		return null;
	}
	protected List<Node<JsonElement>> getNodeChild(int i, JsonElementState endState, boolean display){
		List<Node<JsonElement>> child=new ArrayList<>();
		JsonElement element=getNextJsonElement();
		Node<JsonElement> node=new Node<>(element);
		
		while(element != null && element.getState() != endState){
			if(display && element.toString()!=null){
				System.out.print(i);
				for(int n=0;n<i;n++)System.out.print("  ");
				System.out.println(element);
			}
			
			if(element.getState()==JsonElementState.ARRAY_START){
				node.addChild(getNodeChild(i+1,JsonElementState.ARRAY_END,display));
			}else if(element.getState()==JsonElementState.OBJECT_START){
				node.addChild(getNodeChild(i+1,JsonElementState.OBJECT_END,display));
			}
			child.add(node);
			
			element=getNextJsonElement();
			node=new Node<>(element);
		}
		if(display && element!=null && element.toString()!=null){
			System.out.print(i);
			for(int n=0;n<i;n++)System.out.print("  ");
			System.out.println(element);
		}
		
		child.add(node);//end
		return child;
	}
	@Override
	public Node<JsonElement> getRootNode() {
		Node<JsonElement> root=new Node<>();
		Node<JsonElement> node;
		JsonElement element;
		
		while((element=getNextJsonElement()) != null) {
			node=new Node<>(element);
			if(element.getState()==JsonElementState.ARRAY_START){
				node.addChild(getNodeChild(0,JsonElementState.ARRAY_END,Display));
			}else if(element.getState()==JsonElementState.OBJECT_START){
				node.addChild(getNodeChild(0,JsonElementState.OBJECT_END,Display));
			}
			root.addChild(node);
		}
		return root;
	}

}
