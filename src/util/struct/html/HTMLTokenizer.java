package util.struct.html;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import util.struct.ElementState;
import util.struct.Node;
import util.struct.Tokenizer;
import util.struct.html.HTMLElement.HTMLElementState;
import util.struct.html.element.EndElementState;
import util.struct.html.element.StartElementState;

public class HTMLTokenizer extends Tokenizer{
	public static final String charset="UTF-8";
	private static final boolean Display=false;
	
	private HTMLElement prevElement;
	private List<HTMLElement> activeElements=new ArrayList<>();
	
	public HTMLTokenizer(URL url) throws IOException{
		super(url);
	}
	public HTMLTokenizer(File file) throws IOException{
		super(file);
	}
	@Deprecated
	public HTMLTokenizer(String text){
		super(text);
	}
	
	public HTMLElement getPrevHTMLElement(){return prevElement;}
	protected HTMLElement getNextHTMLElement(){
		ElementState es=new StartElementState(this);
		
		while(es != null && !(es instanceof EndElementState) ){
			es=es.state();
		}
		
		if(es instanceof EndElementState){
			HTMLElement e=((EndElementState) es).get();
			
			if(e.getState()==HTMLElementState.START){
				activeElements.add(0, e);
			}else if(e.getState()==HTMLElementState.END && activeElements.size()>0){
				int containIndex=containTagInActiveElements(e.getTag());
				if(containIndex>=0){
					for(int i=0;i<=containIndex;i++){
						activeElements.remove(0);
					}
					if(containIndex>=1){
						System.err.println("Not found end token ["+e.getTag()+"] ,Removed token count:"+containIndex);
					}
				}else{
					System.err.println("Found Invalid token ["+e.getTag()+"]");
					return getNextHTMLElement();
				}
			}else if(e.getState()==HTMLElementState.CONTENT && e.toString()==null){
				return getNextHTMLElement();
			}
			
			prevElement=e;
			return e;
		}
		return null;
	}
	private int containTagInActiveElements(String tag){
		for(int i=0;i<activeElements.size();i++){
			if(tag.equals("/"+activeElements.get(i).getTag())){
				return i;
			}
		}
		return -1;
	}
	public Node<HTMLElement> getRootNode(){
//		HTMLElement element=getNextHTMLElement();
//		Node<HTMLElement> root=new Node<>();
//		Node<HTMLElement> node=new Node<>(element);
		
		//System.out.println(node);
//		root.addChild(node);
//		while(element.getState()==HTMLElementState.CONTENT){
//			element=getNextHTMLElement();
//			node=new Node<>(element);
//			root.addChild(node);
//		}
//		while(!element.getTag().equalsIgnoreCase("!DOCTYPE")){
//			element=getNextHTMLElement();
//			node=new Node<>(element);
//			root.addChild(node);
//		}
		
		HTMLElement element=null;
		Node<HTMLElement> root=new Node<>();
		Node<HTMLElement> node;
		
		while((element = getNextHTMLElement()) != null){
			node=new Node<>(element);
			if(element.getState()==HTMLElementState.START){
				node.addChild(getNodeChild(0,Display));//display Setting
			}
			root.addChild(node);
		}
		return root;
	}
	protected List<Node<HTMLElement>> getNodeChild(int i,boolean display){
		List<Node<HTMLElement>> child=new ArrayList<>();
		
//		if(i+1!=activeElements.size()) {
////			System.out.println(i+" "+activeElements.size());
//			return child;
//		}
		
		HTMLElement element=getNextHTMLElement();
		Node<HTMLElement> node=new Node<>(element);
		
		while(element != null && element.getState()!=HTMLElementState.END){
		//while(!element.getTag().equalsIgnoreCase(tag) && element.getState()!=HTMLElementState.END){
			//if(node.getState()!=HTMLElementState.CONTENT)System.out.println(node+" "+node.getState().name()+" "+i);
			if(display && element.toString()!=null){
				System.out.print(i);
				for(int n=0;n<i;n++)System.out.print("  ");
				System.out.println(element);
			}
			
			if(element.getState()==HTMLElementState.START){
				List<Node<HTMLElement>> childList=getNodeChild(i+1,display);
				node.addChild(childList);
			}
			
			if(i+1!=activeElements.size()) {
//				System.out.println(i+" "+activeElements.size());
				break;
			}
			child.add(node);
			
			element=getNextHTMLElement();
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
}
