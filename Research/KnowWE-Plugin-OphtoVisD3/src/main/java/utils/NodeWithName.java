package utils;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.strings.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.wisskont.util.MarkupUtils;

public class NodeWithName implements HierarchyNode<NodeWithName> {

	 String name;
	 String data;
	 boolean highlighted;
	//String data;

	public NodeWithName(String name) {
		this.name = name;
	}
	public NodeWithName(String name, String data) {
		this.name = name;
		this.data=data;
	}
	public NodeWithName(String name,boolean highlighted) {
		this.name = name;

		this.highlighted=true;
	}
	

	@Override
	public int compareTo(NodeWithName o) {
		// TODO implement
		return this.name.compareTo(o.name);
	}

	@Override
	public boolean isSubNodeOf(NodeWithName term) {
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

	@Override
	public int compareTo(NodeWithNameAndValue o) {
		// TODO Auto-generated method stub
		return this.name.compareTo(o.name);
	}

}