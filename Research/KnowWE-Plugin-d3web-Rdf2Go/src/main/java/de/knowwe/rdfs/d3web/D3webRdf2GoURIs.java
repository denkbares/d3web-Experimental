package de.knowwe.rdfs.d3web;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.core.session.Session;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;

public class D3webRdf2GoURIs {

	public static URI getSessionIdURI(Session session) {
		return Rdf2GoCore.getInstance().createlocalURI(Strings.encodeURL(session.getId()));
	}

	public static URI getFactURI() {
		return Rdf2GoCore.getInstance().createlocalURI("Fact");
	}

	public static URI getHasFactURI() {
		return Rdf2GoCore.getInstance().createlocalURI("hasFact");
	}

	public static URI getHasValueURI() {
		return Rdf2GoCore.getInstance().createlocalURI("hasValue");
	}

	public static URI getHasTerminologyObjectURI() {
		return Rdf2GoCore.getInstance().createlocalURI("hasTerminologyObject");
	}

}
