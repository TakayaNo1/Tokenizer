package test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import util.struct.Node;
import util.struct.html.HTMLElement;
import util.struct.html.HTMLTokenizer;

public class NarouNovel {
	
	private static final String rootUrl="https://ncode.syosetu.com";
	private int page;
	private HTMLTokenizer com;
	private Node<HTMLElement> root;
	
	public NarouNovel(String id) throws IOException{
		this.page=0;
		this.com=new HTMLTokenizer(new URL(rootUrl+"/"+id+"/"));
		this.root=com.getRootNode();
	}
	public NarouNovel(String id, int page) throws IOException{
		this.page=page;
		this.com=new HTMLTokenizer(new URL(rootUrl+"/"+id+"/"+page+"/"));
		this.root=com.getRootNode();
	}
	
	public void show(){
		if(page<=0)showMain();
		else showPage();
	}
	private void showMain(){
		String titleMatch="html/body/"
				+ "div[id=container]/"
				+ "div[id=novel_contents]/"
				+ "div[id=novel_color]/p";
		String subtitleMatch="html/body/"
				+ "div[id=container]/"
				+ "div[id=novel_contents]/"
				+ "div[id=novel_color]/"
				+ "div[class=index_box]/"
				+ "dl[class=novel_sublist2]/"
				+ "dd[class=subtitle]/a";
		List<Node<HTMLElement>> titlelist=HTMLElement.sort(root, titleMatch);
		List<Node<HTMLElement>> subtitlelist=HTMLElement.sort(root, subtitleMatch);
		
		System.out.println(titlelist.get(0).getChildren(0).getElement().getContent());
		for(int i=0;i<subtitlelist.size();i++){
			Node<HTMLElement> node=subtitlelist.get(i);
			System.out.println(node.getElement().getMeta().get(0)+" "+node.getChildren(0).getElement().getContent());
		}
	}
	private void showPage(){
		String subtitleMatch="html/body/"
				+ "div[id=container]/"
				+ "div[id=novel_contents]/"
				+ "div[id=novel_color]/p";
		String honbunMatch="html/body/"
				+ "div[id=container]/"
				+ "div[id=novel_contents]/"
				+ "div[id=novel_color]/"
				+ "div[id=novel_honbun]/p";
		List<Node<HTMLElement>> subtitlelist=HTMLElement.sort(root, subtitleMatch);
		List<Node<HTMLElement>> honbunlist=HTMLElement.sort(root, honbunMatch);
		
		System.out.println(subtitlelist.get(0).getChildren(0).getElement().getContent()+"\n");
		for(int i=0;i<honbunlist.size();i++){
			Node<HTMLElement> node=honbunlist.get(i);
			HTMLElement e=node.getChildren(0).getElement();
			if(e.getContent()!=null)System.out.println(e.getContent());
			else System.out.println();
		}
	}
	
	public static void main(String[] args) throws IOException{
//		NarouNovel nn=new NarouNovel("n2267be", 1);
//		nn.show();
		matchTest();
	}
	
	public static void matchTest() {
		String[] ids={"n2267be", "n1111", "abcdefg", "n111.1", "n", "nn"};
		String match="n[\\w]+";
		for(String id:ids) {
			System.out.println(id.matches(match));
		}
	}
}
