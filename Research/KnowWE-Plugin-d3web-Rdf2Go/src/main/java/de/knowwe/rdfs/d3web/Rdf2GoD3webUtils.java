package de.knowwe.rdfs.d3web;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.core.session.Session;
import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.dashtree.DashTreeUtils;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;

public class Rdf2GoD3webUtils {

	public static URI getSessionIdURI(Rdf2GoCore core, Session session) {
		return core.createlocalURI(Strings.encodeURL(session.getId()));
	}

	public static URI getFactURI(Rdf2GoCore core) {
		return core.createlocalURI("Fact");
	}

	public static URI getHasFactURI(Rdf2GoCore core) {
		return core.createlocalURI("hasFact");
	}

	public static URI getHasValueURI(Rdf2GoCore core) {
		return core.createlocalURI("hasValue");
	}

	public static URI getHasTerminologyObjectURI(Rdf2GoCore core) {
		return core.createlocalURI("hasTerminologyObject");
	}

	public static boolean hasParentDashTreeElement(OntologyCompiler compiler, Identifier parentIdentifier) {
		boolean hasParent = false;
		Collection<Section<?>> termDefiningSections = compiler.getTerminologyManager()
				.getTermDefiningSections(parentIdentifier);
		for (Section<?> termDefiningSection : termDefiningSections) {
			Section<? extends DashTreeElement> fatherDashTreeElement = DashTreeUtils.getParentDashTreeElement(termDefiningSection);
			if (fatherDashTreeElement != null) {
				hasParent = true;
				break;
			}
		}
		return hasParent;
	}

}
