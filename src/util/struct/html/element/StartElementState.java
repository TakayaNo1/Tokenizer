package util.struct.html.element;

import java.util.HashMap;
import java.util.Map;

import util.struct.ElementState;
import util.struct.html.HTMLElement;
import util.struct.html.HTMLTokenizer;
import util.struct.html.HTMLElement.HTMLElementState;

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
			return new CommentOut(ht, e);
		}else if(c=='?'){
			return new J(ht, e);
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
			return new D(ht,e,"",new HashMap<>());
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
	private Map<String, String> metaMap;
	
	public D(HTMLTokenizer ht,HTMLElement e,String meta,Map<String, String> metaMap) {
		this.ht=ht;
		this.e=e;
		this.meta=meta;
		this.metaMap=metaMap;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='\"'){
			return new E(ht,e,meta+c,metaMap);
		}else if(c=='>'){
			addMeta(meta);
			//System.out.println(metaList+" END.");
			e.setMeta(metaMap);
			e.setState(HTMLElementState.END);
			return new EndElementState(e);
		}else if(c==' '){
			addMeta(meta);
			return new D(ht,e,"",metaMap);
		}else{
			return new D(ht,e,meta+c,metaMap);
		}
	}
	
	private void addMeta(String meta){
		if(!meta.contains("="))return;
		String[] m=meta.split("=");
		m[1]=m[1].replaceAll("\"", "");
		metaMap.put(m[0],m[1]);
	}
}

class E implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	private String meta;
	private Map<String, String> metaMap;
	
	public E(HTMLTokenizer ht,HTMLElement e,String meta,Map<String, String> metaMap) {
		this.ht=ht;
		this.e=e;
		this.meta=meta;
		this.metaMap=metaMap;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='\"'){
			return new D(ht,e,meta+c,metaMap);
		}else{
			return new E(ht,e,meta+c,metaMap);
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
			return new G(ht,e,"",new HashMap<>());
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
	private Map<String, String> metaMap;
	
	public G(HTMLTokenizer ht,HTMLElement e,String meta,Map<String, String> metaMap) {
		this.ht=ht;
		this.e=e;
		this.meta=meta;
		this.metaMap=metaMap;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='\"'){
			return new H(ht,e,meta+c,metaMap);
		}else if(c=='>'){
			addMeta(meta);
			//System.out.println(metaList+" START.");
			e.setMeta(metaMap);
			//node.setState(HTMLElementState.START);
			return new EndElementState(e);
		}else if(c=='/'){
			addMeta(meta);
			//System.out.println(metaList);
			e.setMeta(metaMap);
			return new I(ht,e);
		}else if(c==' '){
			addMeta(meta);
			return new G(ht,e,"",metaMap);
		}else if(c=='\'' || c=='\"'){
			return stringState(c);
		}else{
			return new G(ht,e,meta+c,metaMap);
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
	
	private void addMeta(String meta){
		if(!meta.contains("="))return;
		String[] m=meta.split("=");
		m[1]=m[1].replaceAll("\"", "");
		metaMap.put(m[0],m[1]);
	}
}

class H implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	private String meta;
	private Map<String, String> metaMap;
	
	public H(HTMLTokenizer ht,HTMLElement e,String meta,Map<String, String> metaMap) {
		this.ht=ht;
		this.e=e;
		this.meta=meta;
		this.metaMap=metaMap;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='\"'){
			return new G(ht,e,meta+c,metaMap);
		}else{
			return new H(ht,e,meta+c,metaMap);
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

class J implements ElementState{
	/*
	 * processing instraction <?php ..?> <?xml ..?>
	 * */
	private HTMLTokenizer ht;
	private HTMLElement e;
	private String tag;
	
	public J(HTMLTokenizer ht,HTMLElement e) {
		this.ht=ht;
		this.e=e;
		this.tag="";
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c==' '){
			e.setTag(tag);
			return new K(ht, e);
		}else if(c=='?'){
			e.setTag(tag);
			return new L(ht, e);
		}else{
			tag+=c;
			return this;
		}
	}
}

class K implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	private String context;
	
	public K(HTMLTokenizer ht,HTMLElement e) {
		this.ht=ht;
		this.e=e;
		this.context="";
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='?'){
			this.e.setContent(context);
			return new L(ht, e);
		}else{
			this.context+=c;
			return this;
		}
	}
}

class L implements ElementState{
	
	private HTMLTokenizer ht;
	private HTMLElement e;
	
	public L(HTMLTokenizer ht,HTMLElement e) {
		this.ht=ht;
		this.e=e;
	}
	
	@Override
	public ElementState state() {
		char c=ht.getChar();
		if(c=='>'){
			e.setState(HTMLElementState.PROCESSING_STAY);
			//System.out.println(e+" "+e.getContent());
			return new EndElementState(e);
		}else{
			return new L(ht,e);
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
			return doctype(e, ""+c);
			//return new F(ht,e,"!"+c);
		}
	}
	private ElementState co2(HTMLElement node){
		char c=ht.getChar();
		if(c=='-'){
			return co3(node,"");
		}else{
			return doctype(node, "!-"+c);
			//return new F(ht,node,"!-"+c);
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
			Map<String, String> metaMap=new HashMap<String, String>();
			metaMap.put("comment_out", co);
			node.setTag("");
			node.setState(HTMLElementState.COMMENT_OUT);
			node.setMeta(metaMap);
			return new EndElementState(node);
		}else if(c=='-'){
			return co5(node,co+"-");
		}else{
			return co3(node,co+"--"+c);
		}
	}
	private ElementState doctype(HTMLElement node,String tag){
		char c=ht.getChar();
		if(c=='>'){
			node.setTag(tag);
			node.setState(HTMLElementState.COMMENT_OUT);
			return new EndElementState(node);
		}else if(c==' '){
			return doctype2(node, tag, "");
		}else{
			return doctype(node, tag+c);
		}
	}
	private ElementState doctype2(HTMLElement node,String tag,String context){
		char c=ht.getChar();
		if(c=='>'){
			Map<String, String> metaMap=new HashMap<String, String>();
			metaMap.put("comment_out", context);
			node.setTag(tag);
			node.setState(HTMLElementState.COMMENT_OUT);
			node.setMeta(metaMap);
			//System.out.println(node);
			return new EndElementState(node);
		}else{
			return doctype2(node, tag, context+c);
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
		HTMLElement pe=ht.getPrevHTMLElement();
		
		if(pe!=null) {
			String tag=pe.getTag();
			if(tag.equalsIgnoreCase("script") || tag.equalsIgnoreCase("style")){
				return scriptState();
			}
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
