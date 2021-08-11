package util.struct.html;

import java.io.File;

import util.struct.Node;
import util.struct.html.HTMLElement.HTMLElementState;

public class XMLTokenizer extends HTMLTokenizer{
	private static final boolean Display=false;
	
	public XMLTokenizer(File file) {
		super(file);
	}
	@Override
	public Node<HTMLElement> getRootNode(){
		HTMLElement element=getNextHTMLElement();
		Node<HTMLElement> root=new Node<>();
		Node<HTMLElement> node=new Node<>(element);
		
		//System.out.println(node);
		root.addChild(node);
		while(element.getState()==HTMLElementState.CONTENT){
			element=getNextHTMLElement();
			node=new Node<>(element);
			root.addChild(node);
		}
		while(!element.getTag().equalsIgnoreCase("?xml")){
			element=getNextHTMLElement();
			node=new Node<>(element);
			root.addChild(node);
		}
		
		while((element = getNextHTMLElement()) != null){
			node=new Node<>(element);
			if(element.getState()==HTMLElementState.START){
				node.addChild(getNodeChild(0,Display));//display Setting
			}
			root.addChild(node);
		}
		return root;
	}
}
