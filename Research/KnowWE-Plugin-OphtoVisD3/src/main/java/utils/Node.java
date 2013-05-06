package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public  class Node<T extends HierarchyNode<T>> implements Comparable<Node<T>> {

	final T data;
	private transient Node<T> parent;
	List<Node<T>> children = new ArrayList<Node<T>>();;

	@Override
	public String toString() {
		return data.toString();
	}

	/**
	 * 
	 */
	public Node(T data) {
		this.data = data;
	}

	public Node<T> getParent() {
		return parent;
	}

	public void setParent(Node<T> parent) {
		this.parent = parent;
	}

	public T getData() {
		return data;
	}

	public void addChild(Node<T> n) {
		children.add(n);
		n.parent = this;
	}

	public boolean removeChild(Node<T> n) {
		return children.remove(n);
	}

	public boolean containsChild(Node<T> n) {
		return children.contains(n);
	}

	public List<Node<T>> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public List<Node<T>> getChildrenSorted() {
		List<Node<T>> copy = new ArrayList<Node<T>>();
		copy.addAll(children);
		Collections.sort(copy);
		return Collections.unmodifiableList(copy);
	}

	@Override
	public int compareTo(Node<T> o) {
		return data.compareTo(o.data);
	}

}