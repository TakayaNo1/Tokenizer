package test.wikipedia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import util.struct.Node;
import util.struct.html.HTMLElement;
import util.struct.html.XMLTokenizer;
import util.struct.html.HTMLElement.HTMLElementState;

public class WikiPage {
	private String title;
	private Node<HTMLElement> page;
	
	public WikiPage(String title, Node<HTMLElement> page){
		this.title=title;
		
		HTMLElement.show(page);
		Node<HTMLElement> p=HTMLElement.search(page, "page/revision/text").get(0);
		String text=HTMLElement.getContents(p).get(0).replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"").replaceAll("&amp;amp;", "&amp;");//.replaceAll("&amp;", "&");
		XMLTokenizer tkn=new XMLTokenizer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><page>"+text+"</page>");
		this.page=tkn.getRootNode();
	}
	
	public String getTitle(){return title;}
	public Node<HTMLElement> getNode(){return page;}
	public void write(File file) throws IOException{write(this, file);}
	
	public static void write(WikiPage page, File file) throws IOException{
		if(!file.exists()){
			file.createNewFile();
		}
		
		FileWriter fw=new FileWriter(file);
		write(page.getNode(), fw, -1);
		fw.close();
	}
	private static void write(Node<HTMLElement> page, FileWriter fw, int n) throws IOException{
		HTMLElement e=page.getElement();
		
		if(e==null){
			
		}else if(e.getState()==HTMLElementState.PROCESSING_STAY){
			fw.write("<?"+e.getTag()+" "+e.getContent()+"?>\n");
		}else if(e.getState()==HTMLElementState.START || e.getState()==HTMLElementState.STAY){
			for(int i=0;i<n;i++)fw.write("  ");
			
			fw.write("<"+e.getTag());
			for(Entry<String, String> entry:e.getMeta().entrySet()){
				fw.write(" "+entry.getKey()+"=\""+entry.getValue()+"\"");
			}
			
			if(e.getState()==HTMLElementState.START)fw.write(">\n");
			else fw.write("/>\n");
		}else if(e.getState()==HTMLElementState.END){
			for(int i=0;i<n-1;i++)fw.write("  ");
			fw.write("<"+e.getTag()+">\n");
		}else if(e.getState()==HTMLElementState.CONTENT){
			for(int i=0;i<n;i++)fw.write("  ");
			fw.write(e.getContent()+"\n");
		}
		
		List<Node<HTMLElement>> cpages=page.getChildren();
		for(Node<HTMLElement> cp:cpages)write(cp, fw, n+1);
	}
}
