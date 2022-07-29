package test.wikipedia;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.struct.Node;
import util.struct.html.HTMLElement;
import util.struct.html.HTMLTokenizer;
import util.struct.html.HTMLElement.HTMLElementState;

public class Wikipedia extends HTMLTokenizer{
	
	private final static String articleDir="D:/Download/Wikipedia/jawiki-latest-pages-articles.xml";
	private final static String[] head={
			"Wikipedia:","Help:","Category:","Portal:","MediaWiki:","Template:","Book:",
			"WP:","WT:","H:","CAT:","P:","PJ:","PJT:","LTA:","SP:",
			"プロジェクト:","ファイル:","ノート:","モジュール:","新記事原案:"};
	
	public Wikipedia() throws IOException {
		super(new File(articleDir));
		getNextHTMLElement();//read mediawiki tag.
	}
	
	public WikiPage nextPage(){
		while(true){
			Node<HTMLElement> page=nextPage0();
			
			if(page==null)return null;
			
			List<Node<HTMLElement>> elements=HTMLElement.search(page, "page/title");
			
			for(Node<HTMLElement> e:elements){
				String title=HTMLElement.getContents(e).get(0);
				
				if(isDefaultPage(title)){
					return new WikiPage(title, page);
				}
			}
		}
	}
	private Node<HTMLElement> nextPage0(){
		HTMLElement element=getNextHTMLElement();
		
		if(element==null)return null;
		
		Node<HTMLElement> root=new Node<>();
		Node<HTMLElement> page=new Node<>(element);
		
		if(element.getState()==HTMLElementState.START){
			page.addChild(getNodeChild(0,false));//display Setting
		}
		root.addChild(page);
		
		return root;
	}
	private boolean isDefaultPage(String title){
		for(String h:head){
			if(title.startsWith(h))return false;
		}
		return true;
	}
	@Override
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
	@Override @Deprecated
	public Node<HTMLElement> getRootNode(){return null;}
	
	public static void main(String[] args) throws IOException {
		Wikipedia wiki=new Wikipedia();
		WikiPage page=null;
		int i=0;
		
		while((page=wiki.nextPage())!=null && i++<11){
			//WikiPage.write(page, new File("src/file/"+page.getTitle()+".xml"));
			System.out.println("Index "+i+" "+page.getTitle());
			HTMLElement.show(page.getNode());
		}
	}
}
