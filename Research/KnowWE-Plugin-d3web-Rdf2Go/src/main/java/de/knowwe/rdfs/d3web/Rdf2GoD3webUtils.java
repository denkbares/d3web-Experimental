package de.knowwe.rdfs.d3web;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.Session;
import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class Rdf2GoD3webUtils {

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

	public static String getIdentifierExternalForm(NamedObject namedObject) {
		Identifier termIdentifier;
		if (namedObject instanceof Choice) {
			termIdentifier = new Identifier(((Choice) namedObject).getQuestion().getName(),
					namedObject.getName());

		}
		else {
			termIdentifier = new Identifier(namedObject.getName());
		}
		String externalForm = Rdf2GoUtils.getCleanedExternalForm(termIdentifier);
		return externalForm;
	}

}
