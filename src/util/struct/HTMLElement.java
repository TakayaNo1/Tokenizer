package util.struct;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HTMLElement{
	
	private String tag,content;
	private List<String> meta=new ArrayList<>();
	private HTMLElementState state;
	
	public HTMLElement(){
	}
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<String> getMeta() {
		return meta;
	}
	public void setMeta(List<String> meta) {
		for(int i=0;i<meta.size();i++){
			if(meta.get(i).length()<2){
				meta.remove(i);
			}
		}
		this.meta = meta;
	}
	public HTMLElementState getState() {
		return state;
	}
	public void setState(HTMLElementState state) {
		this.state = state;
	}
	
	public String toString(){
		if(state==HTMLElementState.CONTENT){
			if(getContent().length()>256)return "Content:"+getContent().substring(0, 256)+" ,State:"+state;
			if(getContent().replace("\t","").replace(" ","").length()==0)return null;
			return "Content:"+getContent()+" ,State:"+state;
		}
		return "Tag:"+tag+" ,Meta:"+meta+" ,State:"+state;
	}
	
	public static void show(Node<HTMLElement> root){show(root, 0);}
	private static void show(Node<HTMLElement> node,int n){
		System.out.print(n+" ");
		for(int i=0;i<n;i++)System.out.print("  ");
		if(node.getElement()==null || node.getElement().toString()==null){
			System.out.println("null");
		}else{
			System.out.print(node.getElement().toString()+"\n");
		}
		for(int i=0;i<node.getChildrenSize();i++){
			show(node.getChildren(i), n+1);
		}
	}
	public static List<Node<HTMLElement>> sort(Node<HTMLElement> root,String match){return sort(root, match.split("/"), 0);}
	private static List<Node<HTMLElement>> sort(Node<HTMLElement> node,String[] match,int index){
		char[] cs=match[index].toCharArray();
		String tag="", meta="";
		int state=0;
		for(int i=0;i<cs.length;i++){
			if(cs[i]=='[') state=1;
			else if(cs[i]==']') break;
			else if(state==0) tag+=cs[i];
			else if(state==1) meta+=cs[i];
		}
		
		List<Node<HTMLElement>> list=new ArrayList<>();
		for(int i=0;i<node.getChildrenSize();i++){
			Node<HTMLElement> cNode=node.getChildren(i);
			HTMLElement e=cNode.getElement();
			
			if(e != null && e.getTag().equals(tag)){
				if(state==0 || (state==1 && e.getMeta().contains(meta))){
					//System.out.println(index+" "+match.length+" "+tag+" "+meta+" "+state);
					if(match.length-1>index){
						list.addAll(sort(cNode, match, index+1));
					}else if(match.length-1==index){
						list.add(cNode);
					}
				}
			}
		}
		return list;
	}
	public static List<String> getContents(Node<HTMLElement> root){
		List<String> lists=new ArrayList<>();
		if(root.getElement()==null || root.getElement().getTag().equals("script")){
			return lists;
		}
		if(root.getElement().getState()==HTMLElementState.CONTENT){
			lists.add(root.getElement().getContent());
		}
		for(int i=0;i<root.getChildrenSize();i++){
			lists.addAll(getContents(root.getChildren(i)));
		}
		return lists;
	}

	public enum HTMLElementState{
		START,END,STAY,COMMENT_OUT,CONTENT;
	}
	
	public static void main(String[] args){
		String str="abcefg";
		Pattern p=Pattern.compile("[\\[\\]]");
		String[] a=p.split(str);
		System.out.println(a.length);
		for(String b:a)System.out.println(b);
	}
}
