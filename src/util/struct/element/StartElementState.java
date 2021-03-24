package util.struct.element;

import java.util.ArrayList;
import java.util.List;

import util.struct.HTMLElement;
import util.struct.HTMLTokenizer;
import util.struct.HTMLElement.HTMLElementState;

public class StartElementState implements ElementState{
	
	private HTMLTokenizer ht;
	
	public StartElementState(HTMLTokenizer ht) {this.ht=ht;}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		HTMLElement node =new HTMLElement();
		if(c=='<'){
			return new B(ht, node);
		}else if(c==0){
			return null;
		}else{
			node.setState(HTMLElementState.CONTENT);
			return new Content(ht,node,""+c);
		}
	}

}

class B implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	
	public B(HTMLTokenizer ht,HTMLElement e) {
		this.ht=ht;
		this.e=e;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='/'){
			return new C(ht,e,"/");
		}else if(c=='!'){
			return new CommentOut(ht,e);
		}else{
			return new F(ht,e,""+c);
		}
	}

}

class C implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	private String tag;
	
	public C(HTMLTokenizer ht,HTMLElement e,String tag) {
		this.ht=ht;
		this.e=e;
		this.tag=tag;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c==' '){
			//System.out.print("tag: "+tag);
			e.setTag(tag);
			return new D(ht,e,"",new ArrayList<>());
		}else if(c=='>'){
			//System.out.print("tag: "+tag);
			//System.out.println(" END.");
			e.setTag(tag);
			e.setState(HTMLElementState.END);
			return new EndElementState(e);
		}else{
			return new C(ht,e,tag+c);
		}
	}

}

class D implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	private String meta;
	private List<String> metaList;
	
	public D(HTMLTokenizer ht,HTMLElement e,String meta,List<String> metaList) {
		this.ht=ht;
		this.e=e;
		this.meta=meta;
		this.metaList=metaList;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='\"'){
			return new E(ht,e,meta+c,metaList);
		}else if(c=='>'){
			metaList.add(meta);
			//System.out.println(metaList+" END.");
			e.setMeta(metaList);
			e.setState(HTMLElementState.END);
			return new EndElementState(e);
		}else if(c==' '){
			metaList.add(meta);
			return new D(ht,e,"",metaList);
		}else{
			return new D(ht,e,meta+c,metaList);
		}
	}
}

class E implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	private String meta;
	private List<String> metaList;
	
	public E(HTMLTokenizer ht,HTMLElement e,String meta,List<String> metaList) {
		this.ht=ht;
		this.e=e;
		this.meta=meta;
		this.metaList=metaList;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='\"'){
			return new D(ht,e,meta+c,metaList);
		}else{
			return new E(ht,e,meta+c,metaList);
		}
	}
}

class F implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	private String tag;
	
	public F(HTMLTokenizer ht,HTMLElement e,String tag) {
		this.ht=ht;
		this.e=e;
		this.tag=tag;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		//if(tag.equals("a")&&c!=32)System.err.println(c+" "+(int)c);
		if(c==' '){
			if(isStayTag(tag)){
				e.setState(HTMLElementState.STAY);
			}else{
				e.setState(HTMLElementState.START);
			}
			e.setTag(tag);
			return new G(ht,e,"",new ArrayList<>());
		}else if(c=='>'){
			if(isStayTag(tag)){
				e.setState(HTMLElementState.STAY);
			}else{
				e.setState(HTMLElementState.START);
			}
			e.setTag(tag);
			return new EndElementState(e);
		}else if(c=='/'){
			e.setState(HTMLElementState.STAY);
			e.setTag(tag);
			ht.getChar();
			return new EndElementState(e);
		}else{
			return new F(ht,e,tag+c);
		}
	}
	private boolean isStayTag(String tag){
		String[] stayTags={"br","img","hr","meta","input","embed","area","base","col","keygen","link","param","source"};
		for(String t:stayTags){
			if(tag.equalsIgnoreCase(t))return true;
		}
		return false;
	}
}

class G implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	private String meta;
	private List<String> metaList;
	
	public G(HTMLTokenizer ht,HTMLElement e,String meta,List<String> metaList) {
		this.ht=ht;
		this.e=e;
		this.meta=meta;
		this.metaList=metaList;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='\"'){
			return new H(ht,e,meta+c,metaList);
		}else if(c=='>'){
			metaList.add(meta);
			//System.out.println(metaList+" START.");
			e.setMeta(metaList);
			//node.setState(HTMLElementState.START);
			return new EndElementState(e);
		}else if(c=='/'){
			metaList.add(meta);
			//System.out.println(metaList);
			e.setMeta(metaList);
			return new I(ht,e);
		}else if(c==' '){
			metaList.add(meta);
			return new G(ht,e,"",metaList);
		}else if(c=='\'' || c=='\"'){
			return stringState(c);
		}else{
			return new G(ht,e,meta+c,metaList);
		}
	}
	
	private ElementState stringState(char waitChar){
		meta+=waitChar;
		char c;
		while((c=ht.getChar())!=waitChar){
			meta+=c;
		}
		meta+=c;
		return state();
	}
}

class H implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	private String meta;
	private List<String> metaList;
	
	public H(HTMLTokenizer ht,HTMLElement e,String meta,List<String> metaList) {
		this.ht=ht;
		this.e=e;
		this.meta=meta;
		this.metaList=metaList;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='\"'){
			return new G(ht,e,meta+c,metaList);
		}else{
			return new H(ht,e,meta+c,metaList);
		}
	}
}

class I implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	
	public I(HTMLTokenizer ht,HTMLElement e) {
		this.ht=ht;
		this.e=e;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='>'){
			//System.out.println("START_AND_END.");
			e.setState(HTMLElementState.STAY);
			return new EndElementState(e);
		}else{
			return new I(ht,e);
		}
	}
}

class CommentOut implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	
	public CommentOut(HTMLTokenizer ht,HTMLElement e) {
		this.ht=ht;
		this.e=e;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='-'){
			return co2(e);
		}else if(c==' '){
			return new CommentOut(ht,e);
		}else{
			return new F(ht,e,"!"+c);
		}
	}
	private ElementState co2(HTMLElement node){
		char c=ht.getChar();
		if(c=='-'){
			return co3(node,"");
		}else{
			return new F(ht,node,"!-"+c);
		}
	}
	private ElementState co3(HTMLElement node,String co){
		char c=ht.getChar();
		if(c=='-'){
			return co4(node,co);
		}else{
			//System.out.print(c);
			return co3(node,co+c);
		}
	}
	private ElementState co4(HTMLElement node,String co){
		char c=ht.getChar();
		if(c=='-'){
			return co5(node,co);
		}else{
			return co3(node,co+"-"+c);
		}
	}
	private ElementState co5(HTMLElement node,String co){
		char c=ht.getChar();
		if(c=='>'){
			//System.out.println(" CommentOut.");
			ArrayList<String>list=new ArrayList<>();
			list.add(co);
			node.setTag("");
			node.setState(HTMLElementState.COMMENT_OUT);
			node.setMeta(list);
			return new EndElementState(node);
		}else if(c=='-'){
			return co5(node,co+"-");
		}else{
			return co3(node,co+"--"+c);
		}
	}
}

class Content implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	private String ct;
	
	public Content(HTMLTokenizer ht,HTMLElement e,String ct) {
		this.ht=ht;
		this.e=e;
		this.ct=ct;
	}
	
	@Override
	public ElementState state() {
		String tag=ht.getPrevHTMLElement().getTag();
		if(tag.equalsIgnoreCase("script") || tag.equalsIgnoreCase("style")){
			return scriptState();
		}
		
		char c=ht.getChar();
		while(c!='<' && c!=0){
			ct+=c;
			c=ht.getChar();
		}
		ht.addIndex(-1);
		e.setTag("");
		e.setContent(ct);
		//System.out.println("\t\t\t"+ct);
		return new EndElementState(e);
	}
	private ElementState scriptState(){
		char c=ht.getChar();
		boolean flag1=false,flag2=false;
		while(c!=0){
			if(c=='\"')flag1=!flag1;
			else if(c=='\'')flag2=!flag2;
			
			if(!flag1 && !flag2 && c=='<')break;
			
			ct+=c;
			c=ht.getChar();
		}
		
		if(c=='<'){
			c=ht.getChar();
			if(c=='/'){
				//System.out.println(ht.getPrevHTMLElement().getTag()+"\t"+ct);
				ht.addIndex(-2);
				e.setTag("");
				e.setContent(ct);
				return new EndElementState(e);
			}else{
				return new Content(ht, e, ct+'<'+c);
			}
		}
		return new EndElementState(e);
	}
}
