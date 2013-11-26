package de.knowwe.ophtovisD3.utils;


public class NodeWithName implements Comparable<NodeWithName> {

	String name;
	String data;
	String label;
	boolean highlighted;

	public NodeWithName(String name) {
		this.name = name;
	}

	public NodeWithName(String name, String label) {
		this.name = name;
		this.label = label;
		this.data = 50 + "";
	}

	public NodeWithName(String name, String data, boolean highlighted) {
		this.name = name;
		this.data = data;
		this.highlighted = highlighted;
	}

	public NodeWithName(String name, String data, String label, boolean highlighted) {
		this.name = name;
		this.data = data;
		this.label = label;
		this.highlighted = highlighted;
	}

	public NodeWithName(String name, boolean highlighted) {
		this.name = name;
		this.highlighted = highlighted;
	}

	@Override
	public int compareTo(NodeWithName o) {
		return this.name.compareTo(o.name);
	}

	public void setHighligted() {
		this.highlighted = true;
	}



	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof NodeWithName) {
			return this.name.equals(((NodeWithName) arg0).name);
		}
		return false;
	}

}
