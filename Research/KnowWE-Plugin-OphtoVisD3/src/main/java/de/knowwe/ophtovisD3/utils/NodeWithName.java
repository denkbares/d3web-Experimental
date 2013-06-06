package de.knowwe.ophtovisD3.utils;

import java.util.HashSet;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.strings.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.termbrowser.HierarchyProvider;
import de.knowwe.termbrowser.util.HierarchyNode;
import de.knowwe.wisskont.util.MarkupUtils;

public class NodeWithName implements HierarchyNode<NodeWithName> {

	String name;
	String data;
	boolean highlighted;

	public NodeWithName(String name) {
		this.name = name;
	}

	public NodeWithName(String name, String data) {
		this.name = name;
		this.data = data;
	}

	public NodeWithName(String name, String data, boolean highlighted) {
		this.name = name;
		this.data = data;
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

	@Override
	public boolean isSubNodeOf(NodeWithName term, HierarchyProvider hierarchy) {
		if (name.equals(term.name)) return false;
		String baseUrl = Rdf2GoCore.getInstance().getLocalNamespace();

		String thisConceptURLString = Strings.encodeURL(this.name);
		String thisURL = baseUrl + thisConceptURLString;
		URI thisURI = new URIImpl(thisURL);

		String otherConceptURLString = Strings.encodeURL(term.name);
		String otherURL = baseUrl + otherConceptURLString;
		URI otherURI = new URIImpl(otherURL);

		return MarkupUtils.isSubConceptOf(thisURI, otherURI, new HashSet<URI>());
	}

	@Override
	public String toString() {
		return name;
	}

}
