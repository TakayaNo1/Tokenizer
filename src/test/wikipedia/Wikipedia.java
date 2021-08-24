package test.wikipedia;


import java.io.File;
import java.io.IOException;
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
			
			List<Node<HTMLElement>> elements=HTMLElement.sort(page, "page/title");
			
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
	@Override @Deprecated
	public Node<HTMLElement> getRootNode(){return null;}
}
