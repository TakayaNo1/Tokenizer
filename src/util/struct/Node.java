package util.struct;

import java.util.ArrayList;
import java.util.List;

public class Node<T>{
	private List<Node<T>> children=new ArrayList<Node<T>>();
	private Node<T> parent = null;
	private T element;
	
	public Node(){
		this(null);
	}
	
	public Node(T element){
		this.element=element;
	}
	
	public void addChild(Node<T> element){
		children.add(element);
		element.parent = this;
	}
	public void addChild(List<Node<T>> elements){
		for(Node<T> e : elements){
			addChild(e);
		}
	}
	public Node<T> getChildNode(T element){
		Node<T> node=null;
		for(int i=0;i<children.size();i++){
			if(children.get(i).getElement().toString().equals(element.toString())){
				node=children.get(i);
			}
		}
		return node;
	}
	public boolean isExistChild(T element){
		for(int i=0;i<getChildrenSize();i++){
			if(getChildren(i).getElement().toString().equals(element.toString())){
				return true;
			}
		}
		return false;
	}
	public int getChildrenSize(){return children.size();}
	public Node<T> getChildren(int i){return children.get(i);}
	public Node<T> getParent(){return parent;}
	public T getElement(){return this.element;}
	public void setElement(T element){this.element=element;}
}
