package test;

import java.net.URL;
import java.util.List;

import test.wikipedia.WikiPage;
import test.wikipedia.Wikipedia;
import util.struct.Node;
import util.struct.html.HTMLElement;
import util.struct.html.HTMLTokenizer;

public class HTMLTest {
	public static void main(String[] args) throws Exception{
		test1();
	}
	private static void test1() throws Exception{
		//HTMLTokenizer com=new HTMLTokenizer("https://www.futbin.com/20/player/44079/lionel-messi");
		HTMLTokenizer com=new HTMLTokenizer(new URL("https://ncode.syosetu.com/n2267be/"));
		Node<HTMLElement> root=com.getRootNode();
		//HTMLElement.show(tree.getRoot());
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
		HTMLElement.search(root, "html/body/div[id=container]/div[id=novel_contents]/div[id=novel_color]/div[class=index_box]/dl/dd/a").forEach((e)->{
			//System.out.println(e.getElement());
			//for(int i=0;i<e.getChildrenSize();i++)System.out.println(" "+e.getChildren(i).getElement());
			System.out.print("\n"+e.getParent().getElement()+"\n");HTMLElement.show(e);
		});
		List<Node<HTMLElement>> titlelist=HTMLElement.search(root, titleMatch);
		List<Node<HTMLElement>> subtitlelist=HTMLElement.search(root, subtitleMatch);
		
		System.out.println(titlelist.get(0).getChildren(0).getElement().getContent());
		for(int i=0;i<subtitlelist.size();i++){
			Node<HTMLElement> node=subtitlelist.get(i);
			System.out.println(node.getElement().getMeta()+" "+node.getChildren(0).getElement().getContent());
		}
	}
	private static void test2() throws Exception{
		HTMLTokenizer com=new HTMLTokenizer(new URL("https://www.netflix.com/jp/title/81262169"));
		Node<HTMLElement> node=com.getRootNode();
		
		//HTMLElement.show(node);
		String match="html/body/"
				+ "div[id=appMountPoint]/"
				+ "div[class=basicLayout nonmemberTitle dark]/"
				+ "div[class=nmtitle-wrapper with-fixed-header]/"
				+ "section[id=section-seasons-and-episodes]/"
				+ "div[id=seasons-and-episodes-list-container]/"
				+ "div[data-uia=season-active]/"
				+ "div[class=episodes-container]/"
				+ "div[class=episode]";
		
		HTMLElement.search(node, match).forEach((e)->{
			System.out.println(e.getElement());
			System.out.println("\t"+e.getChildren(1).getChildren(0).getChildren(0).getElement().getContent());
			System.out.println("\t"+e.getChildren(2).getChildren(0).getElement().getContent());
			//for(int i=0;i<e.getChildrenSize();i++)System.out.println("\t"+e.getChildren(i).getElement());
		});
	}
	private static void test3() throws Exception{
		HTMLTokenizer com=new HTMLTokenizer(new URL("https://www.weblio.jp/content/source"));
		Node<HTMLElement> node=com.getRootNode();
		
		HTMLElement.show(node);
		String match="html/body/"
				+ "div[ID=base]/"
				+ "div[ID=wrp]/"
				+ "div[ID=main]/div[ID=cont]/"
				+ "div[class=kijiWrp]/div[class=kiji]";
		
		HTMLElement.search(node, "DOCTYPE").forEach((e)->{
			System.out.println(e.getElement());
		});
		HTMLElement.search(node, match).forEach((e)->{
			System.out.println();
			HTMLElement.getContents(e).forEach(s->System.out.print(s));
		});
	}
	private static void test4() throws Exception{
		String url="https://ja.wikipedia.org";
		HTMLTokenizer com=new HTMLTokenizer(new URL(url+"/wiki/猫"));
//		HTMLTokenizer com=new HTMLTokenizer(url+"/wiki/人工知能");
		Node<HTMLElement> node=com.getRootNode();
		
		//HTMLElement.show(node);
		final String headMatch="html/body/div[id=content]/h1[id=firstHeading]";
		final String bodyMatch="html/body/div[id=content]/div[id=bodyContent]/div[id=mw-content-text]/div[class=mw-parser-output]";
		final String tableOfContexts=bodyMatch+"div[id=toc]";
		
		HTMLElement.search(node, headMatch).forEach((e)->{
			HTMLElement.getContents(e).forEach(s->System.out.println("Title="+s));
		});
		HTMLElement.search(node, bodyMatch).forEach((e1)->{
			for(Node<HTMLElement> e2:HTMLElement.search(e1, "p/a")){
				HTMLElement.getContents(e2).forEach(e3->System.out.println(e3));
			}
			/*
			for(Node<HTMLElement> e2:HTMLElement.sort(e1, "p/a")){
				String subUrl=e2.getElement().getMeta().get("href");
				HTMLTokenizer subcom=null;
				
				if(subUrl.startsWith("/")){
					subcom=new HTMLTokenizer(url+subUrl);
				}else if(subUrl.startsWith("http://") || subUrl.startsWith("https://")){
					subcom=new HTMLTokenizer(subUrl);
				}
				
				if(subcom!=null){
					HTMLElement.sort(subcom.getRootNode(), headMatch).forEach((e)->{
						HTMLElement.getContents(e).forEach(s->System.out.println("Title="+s+", url="+subUrl));
					});
				}
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e3) {
					e3.printStackTrace();
				}
			}
			*/
//			HTMLElement.sort(e1, "p/a").forEach((e2)->{
//				HTMLElement.getContents(e2).forEach(s->System.out.println(s));
//			});
			//HTMLElement.getContents(e).forEach(s->System.out.println(s));
		});
	}
	private static void test5() throws Exception{
		Wikipedia wiki=new Wikipedia();
		WikiPage page=null;
		int i=0;
		
		while((page=wiki.nextPage())!=null && i++<1){
			//WikiPage.write(page, new File("src/file/"+page.getTitle()+".xml"));
			HTMLElement.show(page.getNode());
		}
	}
}
