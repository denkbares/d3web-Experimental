package de.knowwe.rdfs.util;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.Node;

import de.d3web.strings.Identifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.utils.CompileUtils;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.ontology.sparql.RenderMode;
import de.knowwe.ontology.sparql.SparqlResultNodeRenderer;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class TermLinkNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(Node node, String text, String variable, UserContext user, Rdf2GoCore core, RenderMode mode) {
		String termName = Rdf2GoUtils.trimNamespace(core, text);
		Identifier identifier = new Identifier(termName);
		Collection<Section<? extends SimpleDefinition>> termDefinitions =
				IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
						identifier);
		if (termDefinitions != null && !termDefinitions.isEmpty()) {
			if (mode == RenderMode.HTML) {
				return CompileUtils.createLinkToDefinition(identifier, user);
			}
			else {
				return identifier.toExternalForm();
			}
		}
		return text;
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return false;
	}

}
