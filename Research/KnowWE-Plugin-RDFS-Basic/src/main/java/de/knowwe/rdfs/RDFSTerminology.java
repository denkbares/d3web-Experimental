package de.knowwe.rdfs;

import java.util.ResourceBundle;

import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.compile.TerminologyExtension;
import de.knowwe.rdf2go.Rdf2GoCore;

public class RDFSTerminology implements TerminologyExtension {

	ResourceBundle terms = null;

	private static RDFSTerminology instance;

	public static RDFSTerminology getInstance() {
		if (instance == null) {
			instance = new RDFSTerminology();
		}
		return instance;
	}

	public RDFSTerminology() {
		// TODO: implement singleton properly
		instance = this;
		terms = ResourceBundle.getBundle("RDFS-terminology");
	}

	@Override
	public String[] getTermNames() {
		return terms.keySet().toArray(new String[terms.keySet().size()]);
	}

	public URI getURIForTerm(String term) {
		if (terms.containsKey(term)) {

				return Rdf2GoCore.getInstance().createURI(terms.getString(term));
		}
		else {
			return null;
		}
	}

}
