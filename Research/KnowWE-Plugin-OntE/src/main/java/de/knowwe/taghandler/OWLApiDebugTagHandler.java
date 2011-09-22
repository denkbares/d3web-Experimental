package de.knowwe.taghandler;

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.taghandler.TagHandler;
import de.d3web.we.user.UserContext;
import de.knowwe.owlapi.OWLAPIConnector;

/**
 * The {@link OWLApiDebugTagHandler} prints out some useful information about
 * the local ontology. For example: if the ontology is consistent or some of the
 * unsatisfiable classes.
 *
 * TODO: Specify language in properties file
 *
 * @author Stefan Mark
 * @created 20.09.2011
 */
public class OWLApiDebugTagHandler extends AbstractHTMLTagHandler {

	public OWLApiDebugTagHandler() {
		super("owlapidebug");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {

		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLReasoner reasoner = connector.getReasoner();
		OWLOntology ontology = connector.getOntology();

		reasoner.precomputeInferences();
		boolean consistent = reasoner.isConsistent();

		StringBuilder html = new StringBuilder();
		html.append("<div style=\"background: none repeat scroll 0 0 #F5F5F5;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");

		if (parameters.get("help") != null) {
			html.append(helpMessage());
			html.append("</div>");
			return html.toString();
		}

		html.append("Ontology : " + ontology.getOntologyID() + "<br />");
		html.append("Format : " + connector.getManager().getOntologyFormat(ontology) + "<br />");
		html.append("Consistent: " + consistent + "<br />");
		html.append("<br />");

		Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();

		// remove owl:Nothing node and every class equivalent to owl:Nothing
		Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
		if (!unsatisfiable.isEmpty()) {
			html.append("Unfortunately I found unsatisfiable classes. I will print out a list asap :(.");
			html.append("<br />");
			for (OWLClass clazz : unsatisfiable) {
				html.append(" " + OWLApiTagHandlerUtil.labelClass(clazz));
			}
		}
		else {
			html.append("I could not find any unsatisfiable classes. :)");
		}
		html.append("</div>");
		return html.toString();
	}

	/**
	 * Appends a simple how to use message to the output if the
	 * {@link TagHandler} was used incorrectly.
	 *
	 * @created 20.09.2011
	 * @return String The how to use message
	 */
	private String helpMessage() {

		StringBuilder help = new StringBuilder();
		help.append("<dl>");

		help.append("<dt><strong>NAME</strong></dt>");
		help.append("<dd>[{KnowWEPlugin owlapidebug}] - show default information about the OWL ontology.</dd>");

		help.append("<dt><strong>SYNOPSIS</strong></dt>");
		help.append("<dd>[{KnowWEPlugin owlapidebug}] - Prints default information about the local OWL ontology.</dd>");
		help.append("<dd>[{KnowWEPlugin owlapidebug , help}] - Show a how to use message for this taghandler.</dd>");

		help.append("<dt><strong>DESCRIPTION</strong></dt>");
		help.append("<dd>The OWLApiDebugTagHandler shows default informations about the OWL ontology like 'isconsistent' and much more.</dd>");

		help.append("</dl>");

		return help.toString();
	}
}
