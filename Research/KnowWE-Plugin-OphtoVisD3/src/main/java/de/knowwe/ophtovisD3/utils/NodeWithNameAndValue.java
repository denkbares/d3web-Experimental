package de.knowwe.ophtovisD3.utils;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.strings.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.wisskont.util.MarkupUtils;

public class NodeWithNameAndValue extends NodeWithName {


	 int value =5;
	// String data;
	//String data;

	public NodeWithNameAndValue(String name) {
		super(name);
		this.value=5;
	}

	public int compareTo(NodeWithName o) {
		// TODO implement
		return this.name.compareTo(o.name);
	}

	public boolean isSubNodeOf(NodeWithNameAndValue term) {
		if (name.equals(term.name)) return false;
		String baseUrl = Rdf2GoCore.getInstance().getLocalNamespace();

		String thisConceptURLString = Strings.encodeURL(this.name);
		String thisURL = baseUrl + thisConceptURLString;
		URI thisURI = new URIImpl(thisURL);

		String otherConceptURLString = Strings.encodeURL(term.name);
		String otherURL = baseUrl + otherConceptURLString;
		URI otherURI = new URIImpl(otherURL);

		return MarkupUtils.isSubConceptOf(thisURI, otherURI);
	}

	@Override
	public String toString() {
		return name;
	}

}
