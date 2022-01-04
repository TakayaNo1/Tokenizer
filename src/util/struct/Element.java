package util.struct;

public abstract class Element {
	public static <T extends Element> void show(Node<T> root){show(root, 0);}
	private static <T extends Element> void show(Node<T> node,int n){
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
}
