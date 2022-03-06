package util.struct.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.struct.Element;
import util.struct.Node;

public class HTMLElement extends Element{
	
	private String tag,content;
	private Map<String, String> meta=new HashMap<String, String>();
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
	public Map<String, String> getMeta(){
		return meta;
	}
	public void setMeta(Map<String, String> meta){
		this.meta=meta;
	}
	public void addMeta(String key,String value){
		this.meta.put(key, value);
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
	
	public static List<Node<HTMLElement>> search(Node<HTMLElement> root,String match){return search(root, match.split("/"), 0);}
	private static List<Node<HTMLElement>> search(Node<HTMLElement> node,String[] match,int index){
		char[] cs=match[index].toCharArray();
		String tag="", metaKey="",metaValue="";
		int state=0;
		for(int i=0;i<cs.length;i++){
			if(cs[i]=='[') state=1;
			else if(cs[i]==']') break;
			else if(state==0) tag+=cs[i];
			else if(state==1){
				if(cs[i]=='=')state=2;
				else metaKey+=cs[i];
			}
			else if(state==2)metaValue+=cs[i];
		}
		
		List<Node<HTMLElement>> list=new ArrayList<>();
		for(int i=0;i<node.getChildrenSize();i++){
			Node<HTMLElement> cNode=node.getChildren(i);
			HTMLElement e=cNode.getElement();
			
			if(e != null && e.getTag().equals(tag)){
//				System.out.println(metaKey+" "+metaValue);
				if(state==0 || (state==2 && e.getMeta().containsKey(metaKey) && e.getMeta().get(metaKey).equals(metaValue))){
					//System.out.println(index+" "+match.length+" "+tag+" "+meta+" "+state);
					if(match.length-1>index){
						list.addAll(search(cNode, match, index+1));
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
		START,END,STAY,COMMENT_OUT,CONTENT,PROCESSING_STAY;
	}
}
