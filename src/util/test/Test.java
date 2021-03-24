package util.test;

import util.struct.HTMLElement;
import util.struct.HTMLTokenizer;
import util.struct.Node;

public class Test {
	public static void main(String[] args){
		test3();
	}
	private static void test1(){
		//HTMLTokenizer com=new HTMLTokenizer("https://www.futbin.com/20/player/44079/lionel-messi");
		HTMLTokenizer com=new HTMLTokenizer("https://ncode.syosetu.com/n2267be/");
		Node<HTMLElement> root=com.getRootNode();
		//HTMLElement.show(tree.getRoot());
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
		HTMLElement.sort(root, "html/body/div[id=\"container\"]/div[id=\"novel_contents\"]/div[id=\"novel_color\"]/div[class=\"index_box\"]/dl/dd/a").forEach((e)->{
			//System.out.println(e.getElement());
			//for(int i=0;i<e.getChildrenSize();i++)System.out.println(" "+e.getChildren(i).getElement());
			System.out.print("\n"+e.getParent().getElement()+"\n");HTMLElement.show(e);
		});
//		List<Node<HTMLElement>> titlelist=HTMLElement.sort(tree.getRoot(), titleMatch);
//		List<Node<HTMLElement>> subtitlelist=HTMLElement.sort(tree.getRoot(), subtitleMatch);
		
//		System.out.println(titlelist.get(0).getChildren(0).getElement().getContent());
//		for(int i=0;i<subtitlelist.size();i++){
//			Node<HTMLElement> node=subtitlelist.get(i);
//			System.out.println(node.getElement().getMeta()+" "+node.getChildren(0).getElement().getContent());
//		}
	}
	private static void test2(){
		HTMLTokenizer com=new HTMLTokenizer("https://www.netflix.com/jp/title/81262169");
		Node<HTMLElement> node=com.getRootNode();
		
		//HTMLElement.show(node);
		String match="html/body/"
				+ "div[id=\"appMountPoint\"]/"
				+ "div[class=\"basicLayout nonmemberTitle dark\"]/"
				+ "div[class=\"nmtitle-wrapper with-fixed-header\"]/"
				+ "section[id=\"section-seasons-and-episodes\"]/"
				+ "div[id=\"seasons-and-episodes-list-container\"]/"
				+ "div[data-uia=\"season-active\"]/"
				+ "div[class=\"episodes-container\"]/"
				+ "div[class=\"episode\"]";
		
		HTMLElement.sort(node, match).forEach((e)->{
			System.out.println(e.getElement());
			System.out.println("\t"+e.getChildren(1).getChildren(0).getChildren(0).getElement().getContent());
			System.out.println("\t"+e.getChildren(2).getChildren(0).getElement().getContent());
			//for(int i=0;i<e.getChildrenSize();i++)System.out.println("\t"+e.getChildren(i).getElement());
		});
	}
	private static void test3(){
		HTMLTokenizer com=new HTMLTokenizer("https://www.weblio.jp/content/source");
		Node<HTMLElement> node=com.getRootNode();
		
		//HTMLElement.show(node);
		String match="html/body/"
				+ "div[ID=base]/"
				+ "div[ID=wrp]/"
				+ "div[ID=main]/div[ID=cont]/"
				+ "div[class=kijiWrp]/div[class=kiji]";
		
		HTMLElement.sort(node, match).forEach((e)->{
			System.out.println();
			HTMLElement.getContents(e).forEach(s->System.out.print(s));
		});
	}
}
