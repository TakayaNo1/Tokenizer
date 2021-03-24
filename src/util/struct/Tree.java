package util.struct;

import java.util.List;

public class Tree<T>{
	private Node<T> root;
	private Node<T> current;
	
	public Tree(){
		root=new Node<T>();
		current=root;
	}
	public Tree(Node<T> root){
		this.root=root;
		this.current=root;
	}
	
	public void setRoot(Node<T> root){
		this.root=root;
		this.current=root;
	}
	public Node<T> getRoot(){
		return this.root;
	}
	public void addTree(T[] elements){
		current=root;
		for(int i=0;i<elements.length;i++){
			if(!current.isExistChild(elements[i])){
				current.addChild(new Node<T>(elements[i]));
			}
			current=current.getChildNode(elements[i]);
		}
	}
	public void addTree(List<T> elements){
		current=root;
		for(int i=0;i<elements.size();i++){
			if(!current.isExistChild(elements.get(i))){
				current.addChild(new Node<T>(elements.get(i)));
			}
			current=current.getChildNode(elements.get(i));
		}
	}
	public void showTree(){
		showTree(root, 0);
	}
	private void showTree(Node<T> node,int n){
		for(int i=0;i<n;i++){
			System.out.print("\t");
		}
		if(node.getElement()==null){
			System.out.print("ROOT\n");
		}else{
			System.out.print(node.getElement()+"\n");
		}
		for(int i=0;i<node.getChildrenSize();i++){
			showTree(node.getChildren(i), n+1);
		}
	}
}
