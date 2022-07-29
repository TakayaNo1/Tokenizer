package test.weblio;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.struct.Node;
import util.struct.html.HTMLElement;
import util.struct.html.HTMLTokenizer;

public class WeblioData {
	public static final String https="https:";
	
	private static final String match="html/body/"
			+ "div[ID=base]/"
			+ "div[ID=wrp]/"
			+ "div[ID=main]/div[ID=cont]/"
			+ "div[class=kijiWrp]";
	private static final String match1="html/body/"
			+ "div[ID=base]/"
			+ "div[ID=wrp]/"
			+ "div[ID=main]/div[ID=cont]/"
			+ "div[class=kijiWrp]/div[class=kiji]/div/p";
	private static final String match2="html/body/"
			+ "div[ID=base]/"
			+ "div[ID=wrp]/"
			+ "div[ID=main]/div[ID=cont]/"
			+ "div[class=kijiWrp]/div[class=kiji]/div/table/tr/td/p";
	
	private String url;
	private String word;
	private String explain;
	private Map<String, String> wordList;//url,word
	private boolean isNull=false;
	
	public String getURL() {return url;}
	public String getWord() {return word;}
	public String getExplain() {return explain;}
	public Map<String, String> getWordList() {return wordList;}
	public boolean isNull() {return isNull;}
	
	public static WeblioData load(String url,String word) throws IOException {
		if(url.startsWith(https+https)) {
			url=url.substring(6);
		}
		HTMLTokenizer tkn = new HTMLTokenizer(new URL(url));
		Node<HTMLElement> root=tkn.getRootNode();
		List<Node<HTMLElement>> data;
		
		data=HTMLElement.search(root, match);
//		data=HTMLElement.search(root, match1);
//		data.addAll(HTMLElement.search(root, match2));
		
		WeblioData wdata=new WeblioData();
		
		wdata.url=url;
		wdata.word=word;
		wdata.explain=getContent(data);
		wdata.wordList=new HashMap<>();
		List<Link> links=getLinks(data);
		for(Link l:links) {
			wdata.wordList.put(l.getUrl(), l.getWord());
		}
//		data.forEach((n)->{
//			n.getChildren().forEach((c)->{
//				HTMLElement e=c.getElement();
//				if(e.getState()==HTMLElementState.CONTENT) {
//					wdata.explain+=e.getContent();
//				}else {
//					String childWord = null;
//					if(c.getChildrenSize()>0) {
//						childWord=c.getChildren(0).getElement().getContent();
//					}
//					String href=e.getMeta().get("href");
//					
//					if(href!=null && childWord!=null) {
//						wdata.explain+=childWord;
//						if(!href.startsWith(https)) {
//							System.out.println("\t"+href);
//							href=https+href;
//						}
//						wdata.wordList.put(href, childWord);
//					}
//				}
//			});
//			wdata.explain+="\n";
//		});
		
		wdata.isNull=wdata.explain.isBlank();
		
		return wdata;
	}
	
	private static String getContent(List<Node<HTMLElement>> nodes) {
		String text="";
		for(Node<HTMLElement> n:nodes) {
			List<String> ts=HTMLElement.getContents(n);
			for(int i=0;i<ts.size();i++) {
				text+=ts.get(i);
			}
			text+="\n";
		}
		text=text.replaceAll("\"", "\\\"");
		text=text.replaceAll("\'", "\\\'");
		//text.replaceAll("‘", "\\\'");
		//text.replaceAll("’", "\\\'");
		return text;
	}
	private static List<Link> getLinks(List<Node<HTMLElement>> nodes){
		List<Link> a=new ArrayList<>();
		for(Node<HTMLElement> n:nodes) {
			addLinks(n, a);
		}
		return a;
	}
	private static void addLinks(Node<HTMLElement> node, List<Link> a){
		HTMLElement e=node.getElement();
		if(e.getTag().equalsIgnoreCase("a")) {
			String url=e.getMeta().get("href");
			if(url!=null && url.contains("//www.weblio.jp/content/")) {
				if(!url.startsWith(https)) {
					url=https+url;
				}
				a.add(new Link(url, node.getChildren(0).getElement().getContent()));
			}
		}
		
		for(int i=0;i<node.getChildrenSize();i++) {
			addLinks(node.getChildren(i),a);
		}
	}
	
	public static void main(String[] args) throws IOException {
//		WeblioData.load("https://www.weblio.jp/content/%E6%A5%BD%E5%99%A8", "楽器");
		WeblioData data=WeblioData.load("https://www.weblio.jp/content/%E9%8D%B5%E7%9B%A4", "鍵盤");
//		WeblioData data=WeblioData.load("https://www.weblio.jp/content/%E3%81%BE%E3%81%AA%E3%81%84%E3%81%9F", "まないた");
//		WeblioData data=WeblioData.load("https://www.weblio.jp/content/GEO", "GEO");
		System.out.println(data.getWord()+"\n"+data.getExplain());
	}
}
class Link{
	private String url,word;
	public Link(String url,String word) {
		this.url=url;
		this.word=word;
	}
	public String getUrl() {return url;}
	public String getWord() {return word;}
}
