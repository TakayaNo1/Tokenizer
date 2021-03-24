package util.test;

import java.util.List;

import util.struct.HTMLElement;
import util.struct.HTMLTokenizer;
import util.struct.Node;

public class NarouNovel {
	
	private static final String rootUrl="https://ncode.syosetu.com";
	private int page;
	private HTMLTokenizer com;
	private Node<HTMLElement> root;
	
	public NarouNovel(String id){
		this.page=0;
		this.com=new HTMLTokenizer(rootUrl+"/"+id+"/");
		this.root=com.getRootNode();
	}
	public NarouNovel(String id, int page){
		this.page=page;
		this.com=new HTMLTokenizer(rootUrl+"/"+id+"/"+page+"/");
		this.root=com.getRootNode();
	}
	
	public void show(){
		if(page<=0)showMain();
		else showPage();
	}
	private void showMain(){
		String titleMatch="html/body/"
				+ "div[id=\"container\"]/"
				+ "div[id=\"novel_contents\"]/"
				+ "div[id=\"novel_color\"]/p";
		String subtitleMatch="html/body/"
				+ "div[id=\"container\"]/"
				+ "div[id=\"novel_contents\"]/"
				+ "div[id=\"novel_color\"]/"
				+ "div[class=\"index_box\"]/"
				+ "dl[class=\"novel_sublist2\"]/"
				+ "dd[class=\"subtitle\"]/a";
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
				+ "div[id=\"container\"]/"
				+ "div[id=\"novel_contents\"]/"
				+ "div[id=\"novel_color\"]/p";
		String honbunMatch="html/body/"
				+ "div[id=\"container\"]/"
				+ "div[id=\"novel_contents\"]/"
				+ "div[id=\"novel_color\"]/"
				+ "div[id=\"novel_honbun\"]/p";
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
	
	public static void main(String[] args){
		NarouNovel nn=new NarouNovel("n2267be", 2);
		nn.show();
	}
}
