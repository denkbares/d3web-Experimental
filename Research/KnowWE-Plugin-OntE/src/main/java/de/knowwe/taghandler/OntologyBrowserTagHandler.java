package de.knowwe.taghandler;

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;

import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.taghandler.TagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.owlapi.OWLAPIConnector;


public class OntologyBrowserTagHandler extends AbstractHTMLTagHandler {

	private static final String NAME = "onte.browser";

	public OntologyBrowserTagHandler() {
		super(NAME);
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {

		StringBuilder html = new StringBuilder();

		if (parameters.get("help") != null) {
			html.append("<div style=\"background: none repeat scroll 0 0 #F5F5F5;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");
			html.append(getDescription(user));
			html.append("</div>");
			return html.toString();
		}

		html.append("<div id=\"onte-ontology-browser\" class=\"onte-box\">")
				.append("<div id=\"oob-header\"><h2>") // begin header
				.append("<img src=\"KnowWEExtension/images/owl_class_24.png\" width=\"24\" />")
				.append("Ontology Browser")
				.append(loadOntologyInformation())
				.append("</h2></div>") // end header
				.append("<div id=\"oob-navigation\" class=\"simpletab oob-bar\">") // start
																					// navigation
				.append("<ul class=\"simpletabs\">")
				.append(
						"<li data-simpletab=\"oob-class-hierarchy\" data-type=\"OWLClass\">Classes</li>")
				.append(
						"<li data-simpletab=\"oob-individual-hierarchy\" data-type=\"OWLIndividual\">Individuals</li>")
				.append(
						"<li data-simpletab=\"oob-object-hierarchy\" data-type=\"OWLObjectProperty\">Object properties</li>")
				.append(
						"<li data-simpletab=\"oob-data-hierarchy\" data-type=\"OWLDataProperty\">Data properties</li>")
				.append("</ul>")
				.append("</div>") // end navigation

				.append("<div style=\"clear:both;padding-top:10px;\" class=\"oob-bar\"></div>") // clearer

				// start adding buttons for the tree
				.append("<div id=\"oob-buttons\" class=\"oob-box\">")
				.append("<a class=\"onte-button left small\" href=\"#\">")
				.append(
						"<img class=\"tree-expand\" src=\"KnowWEExtension/images/onte/magnifier-zoom-in.png\"></a>")
				.append("<a class=\"onte-button left small\" href=\"#\">")
				.append(
						"<img class=\"tree-collapse\" src=\"KnowWEExtension/images/onte/magifier-zoom-out.png\"></a>")
				.append("<a class=\"onte-button left small\" href=\"#\">")
				.append(
						"<img id=\"oob-search\" class=\"tree-search\" src=\"KnowWEExtension/images/onte/magnifier.png\"></a>")
				.append("</div>") // end buttons


				.append("<div id=\"oob-hierarchy\">") // start hierarchy
				.append(
						"<div id=\"oob-class-hierarchy\" class=\"oob-box\" style=\"overflow:auto; height:400px;\"></div>")
				.append(
						"<div id=\"oob-individual-hierarchy\" class=\"oob-box\" style=\"overflow:auto; height:400px;\"></div>")
				.append(
						"<div id=\"oob-object-hierarchy\" class=\"oob-box\" style=\"overflow:auto; height:400px;\"></div>")
				.append(
						"<div id=\"oob-data-hierarchy\" class=\"oob-box\" style=\"overflow:auto; height:400px;\"></div>")

				// .append("<div id=\"onte-browser-tree\" class=\"oob-box\" style=\"overflow:auto; height:400px;\"></div>")

				.append("</div>") // end hierarchy

				// start information element
				.append("<div id=\"oob-information\" class=\"oob-box\">")
				.append("</div>")
				// end information element

				.append("</div>"); // end browser

		html.append("<br /><br /><br />");
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
		example.append(", help ");
		example.append(", tree=[class|individual|object|data]");
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
				+ "}] - prints a hierarchie for the given entity type of the local ontology.</dd>");

		help.append("<dt><strong>SYNOPSIS</strong></dt>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ "}] - prints a hierarchie for the given entity type of the local ontology.</dd>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ " , help}] - Show a how to use message for this taghandler.</dd>");

		help.append("<dt><strong>DESCRIPTION</strong></dt>");
		help.append("<dd>The OntEOntologyTreeOverviewTagHandler ...</dd>");

		help.append("</dl>");

		return help.toString();
	}

	private String loadOntologyInformation() {

		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLOntology ontology = connector.getOntology();
		StringBuilder html = new StringBuilder();

		OWLOntologyID ontologyID = ontology.getOntologyID();

		Set<OWLDeclarationAxiom> decs = ontology.getAxioms(AxiomType.DECLARATION);

		int objectPropertyCount = 0;
		int dataPropertyCount = 0;
		int classCount = 0;
		int datatypeCount = 0;

		for (OWLDeclarationAxiom owlDeclarationAxiom : decs) {
			if (!owlDeclarationAxiom.getObjectPropertiesInSignature().isEmpty()) {
				objectPropertyCount++;
			}
			if (!owlDeclarationAxiom.getDataPropertiesInSignature().isEmpty()) {
				dataPropertyCount++;
			}
			if (!owlDeclarationAxiom.getClassesInSignature().isEmpty()) {
				classCount++;
			}
			if (!owlDeclarationAxiom.getDatatypesInSignature().isEmpty()) {
				datatypeCount++;
			}
		}

		html.append(
				"<span style=\"float:right;font-size:10px;font-weight:normal;width:50%;\"><span>Document IRI:")
				.append(ontologyID.getOntologyIRI()).append("</span>");
		html.append("<span style=\"float:left;padding-right:10px;\"> Class declaration count: ")
				.append(classCount).append("</span>");

		html.append("<span style=\"float:left;padding-right:10px;\"> Individual count: ").append(
				ontology.getAxiomCount(AxiomType.CLASS_ASSERTION)).append("</span>");

		html.append("<span style=\"float:left;padding-right:10px;\"> Data Property count: ")
				.append(dataPropertyCount).append("</span>");

		html.append("<span style=\"float:left;padding-right:10px;\"> Object Property count: ")
				.append(objectPropertyCount).append("</span>");

		html.append("<span style=\"float:left;padding-right:10px;\"> Datatype definition count: ")
				.append(datatypeCount).append("</span>");
		html.append("</span>");

		return html.toString();
	}
}
