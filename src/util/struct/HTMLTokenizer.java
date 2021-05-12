package util.struct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import util.struct.HTMLElement.HTMLElementState;
import util.struct.element.ElementState;
import util.struct.element.EndElementState;
import util.struct.element.StartElementState;

public class HTMLTokenizer {
	public static final String charset="UTF-8";
	private static final boolean Display=false;
	
	private BufferedReader reader;
	private String line;
	private int line_index;
	private HTMLElement prevElement;
	private List<HTMLElement> activeElements=new ArrayList<>();
	
	public HTMLTokenizer(String url){
		try {
			URLConnection conn = new URL(url).openConnection();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is,charset);
			reader=new BufferedReader(isr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public HTMLTokenizer(File file){
		if(file.exists()){
			try {
				FileReader fr=new FileReader(file);
				reader=new BufferedReader(fr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addIndex(int i){
		line_index+=i;
	}
	public char getChar(){
		try{
			if(line==null){
				line=reader.readLine();
			}
			if(line.length()<=line_index){
				line=reader.readLine();
				if(line!=null){
					line_index=0;
					return ' ';
				}else{
					reader.close();
					return (char)0;
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			return (char)0;
		}
		return line.charAt(line_index++);
	}
//	public char getChar(){
//		if(line==null)line=list.remove(0);
//		if(line.length()<=line_index && list.size()>0){
//			line=list.remove(0);
//			line_index=0;
//			if(list.size()%1000==0)System.out.println(list.size());
//			return ' ';
//		}
//		if(list.size()==0&&line.length()<=line_index){
//			return (char)0;
//		}
//		return line.charAt(line_index++);
//	}
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
		while(!element.getTag().equalsIgnoreCase("!DOCTYPE")){
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
	protected List<Node<HTMLElement>> getNodeChild(int i,boolean display){
		List<Node<HTMLElement>> child=new ArrayList<>();
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
				node.addChild(getNodeChild(i+1,display));
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
