package util.struct.html;

import java.io.File;
import java.io.IOException;

import util.struct.Node;
import util.struct.html.HTMLElement.HTMLElementState;

public class XMLTokenizer extends HTMLTokenizer{
	private static final boolean Display=false;
	
	public XMLTokenizer(File file) throws IOException {
		super(file);
	}
	public XMLTokenizer(String text) {
		super(text);
	}
	@Override
	public Node<HTMLElement> getRootNode(){
		HTMLElement element=null;
		Node<HTMLElement> root=new Node<>();
		Node<HTMLElement> node=new Node<>();
		
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
