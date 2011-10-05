package de.knowwe.taghandler;

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
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
 * @author Stefan Mark
 * @created 20.09.2011
 */
public class OWLApiDebugTagHandler extends AbstractHTMLTagHandler {

	private static final String NAME = "owlapi.debug";

	public OWLApiDebugTagHandler() {
		super(NAME);
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {

		StringBuilder html = new StringBuilder();
		html.append("<div style=\"background: none repeat scroll 0 0 #F5F5F5;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");

		if (parameters.get("help") != null) {
			html.append(getDescription(user));
			html.append("</div>");
			return html.toString();
		}

		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLReasoner reasoner = connector.getReasoner();
		OWLOntology ontology = connector.getOntology();

		// reasoner.precomputeInferences();

		// The method isConsistent is poorly supported by the implementations,
		// so check instead if owl:thing is a subclass of owl:nothing
		boolean consistent = reasoner.isConsistent();
		consistent = reasoner.isSatisfiable(connector.getManager().getOWLDataFactory().getOWLThing());
		OWLOntologyID ontologyID = ontology.getOntologyID();
		html.append("Ontology : " + ontologyID + "<br />");
		html.append("Ontology IRI: " + ontologyID.getOntologyIRI() + "<br />");
		html.append("Ontology Version IRI: " + ontologyID.getVersionIRI() + "<br />");
		html.append("Anonymous Ontology: " + ontologyID.isAnonymous() + "<br />");
		html.append("Format : " + connector.getManager().getOntologyFormat(ontology) + "<br />");
		html.append("Consistent: " + consistent + "<br />");
		html.append("<br />");

		Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();

		// remove owl:Nothing node and every class equivalent to owl:Nothing
		html.append("Unsatisfiable classes:<br /><dl>");
		Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
		if (!unsatisfiable.isEmpty()) {

			for (OWLClass clazz : unsatisfiable) {

				html.append("<dt>" + OWLApiTagHandlerUtil.labelClass(clazz) + "</dt>");

				Set<Set<OWLAxiom>> explanations =
						OWLApiTagHandlerUtil.getExplanations(clazz);
				for (Set<OWLAxiom> explanation : explanations) {
					for (OWLAxiom causingAxiom : explanation) {
						html.append("<dd>");
						html.append(OWLApiTagHandlerUtil.verbalizeToManchesterSyntax(causingAxiom));
						html.append("</dd>");
					}
				}

				// below returns fewer explanations, but why?
				// BlackBoxOWLDebugger debugger = new
				// BlackBoxOWLDebugger(connector.getManager(),
				// ontology,
				// connector.getFactory());
				// try {
				// Set<OWLAxiom> axioms =
				// debugger.getSOSForIncosistentClass(clazz);
				//
				// for (OWLAxiom a : axioms) {
				// // html.append(OWLApiTagHandlerUtil.getExplanation(a));
				// html.append("<dd>");
				// OWLAxiom axiom = a.getAxiomWithoutAnnotations();
				// html.append(OWLApiTagHandlerUtil.verbalizeToManchesterSyntax(axiom));
				// html.append("</dd>");
				// }
				// }
				// catch (OWLException e) {
				// e.printStackTrace();
				// }
			}
			html.append("</dl>");
		}
		else {
			html.append("<dt>none</dt><dd></dd></dl>");
		}
		html.append("</div>");
		return html.toString();
	}

	/**
	 * Returns an example usage string
	 *
	 * @return A example usage string
	 */
	@Override
	public String getExampleString() {
		StringBuilder example = new StringBuilder();
		example.append("[{KnowWEPlugin " + NAME + " [");
		example.append(", help ]");
		example.append("}])\n ");
		example.append("The parameters in [ ] are optional.");
		return example.toString();
	}

	/**
	 * Appends a simple how to use message to the output if the
	 * {@link TagHandler} was used incorrectly.
	 *
	 * @created 20.09.2011
	 * @return String The how to use message
	 */
	@Override
	public String getDescription(UserContext user) {

		StringBuilder help = new StringBuilder();
		help.append("<dl>");

		help.append("<dt><strong>NAME</strong></dt>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ "}] - show default information about the OWL ontology.</dd>");

		help.append("<dt><strong>SYNOPSIS</strong></dt>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ "}] - Prints default information about the local OWL ontology.</dd>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ " , help}] - Show a how to use message for this taghandler.</dd>");

		help.append("<dt><strong>DESCRIPTION</strong></dt>");
		help.append("<dd>The OWLApiDebugTagHandler shows default informations about the OWL ontology like 'isconsistent' and much more.</dd>");

		help.append("</dl>");

		return help.toString();
	}
}