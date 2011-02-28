package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

public class URIObject {
	
	public enum URIObjectType { Class, objectProperty, datatypeProperty, instance, unspecified};
	
	private URI uri;
	URIObjectType type;
	
	public URIObject(URI rui) {
		this.uri = rui;
		type = URIObjectType.unspecified;
	}
	
	public URIObjectType getURIType() {
		return type;
	}

	public void setURIType(URIObjectType type) {
		this.type = type;
	}

	public URIObject(URI rui, URIObjectType type) {
		this.uri = rui;
		this.type = type;
	}

	public URI getURI() {
		return uri;
	}
	
	public String toString() {
		return uri.toString()+ "("+type.toString()+")";
		
	}

}
