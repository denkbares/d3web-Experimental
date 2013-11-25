package de.knowwe.ophtovisD3.utils;

import de.d3web.strings.Identifier;
import de.knowwe.termbrowser.util.HierarchyNode;
import de.knowwe.termbrowser.util.SubnodeRelationProvider;

public class NodeWithName implements HierarchyNode<NodeWithName> {

	String name;
	String data;
	String label;
	boolean highlighted;
	private final SubnodeRelationProvider hierarchy;

	public NodeWithName(String name, SubnodeRelationProvider h) {
		this.name = name;
		this.hierarchy = h;
	}

	public NodeWithName(String name, String label, SubnodeRelationProvider h) {
		this.name = name;
		this.label = label;
		this.data = 50 + "";
		this.hierarchy = h;
	}

	public NodeWithName(String name, String data, boolean highlighted, SubnodeRelationProvider h) {
		this.name = name;
		this.data = data;
		this.highlighted = highlighted;
		this.hierarchy = h;
	}

	public NodeWithName(String name, String data, String label, boolean highlighted, SubnodeRelationProvider h) {
		this.name = name;
		this.data = data;
		this.label = label;
		this.highlighted = highlighted;
		this.hierarchy = h;
	}

	public NodeWithName(String name, boolean highlighted, SubnodeRelationProvider h) {
		this.name = name;
		this.highlighted = highlighted;
		this.hierarchy = h;
	}

	@Override
	public int compareTo(NodeWithName o) {
		return this.name.compareTo(o.name);
	}

	public void setHighligted() {
		this.highlighted = true;
	}

	@Override
	public boolean isSubNodeOf(NodeWithName term) {

		return hierarchy.isSubNodeOf(new Identifier(this.name), new Identifier(term.name));
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
