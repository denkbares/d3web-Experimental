package de.knowwe.kdom.renderer;

import java.util.Collection;

import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.owlapi.query.OWLApiQueryParser;

/**
 * The OnteRenderingUtils class contains methods used in the rendering process
 * during the components in the OntE-Plugin.
 *
 * @author Stefan Mark
 * @created 22.11.2011
 */
public class OnteRenderingUtils {

	/**
	 * Checks weather a term identifier is known to the terminology.
	 *
	 * @created 22.11.2011
	 * @param term
	 * @return
	 */
	public static boolean isKnownTerm(String termIdendifier) {
		ReferenceManager manager = IncrementalCompiler.getInstance().getTerminology();
		return manager.isValid(termIdendifier);
	}

	public static String determineTypeOfTermIdentifier(String termIdendifier) {

		OWLApiQueryParser parser = new OWLApiQueryParser(new SimpleShortFormProvider());
		if (parser.isClassName(termIdendifier)) {
			return "OWLClass";
		}
		else if (parser.isObjectPropertyName(termIdendifier)) {
			return "OWLObjectProperty";
		}
		else if (parser.isDataPropertyName(termIdendifier)) {
			return "OWLDataProperty";
		}
		else if (parser.isIndividualName(termIdendifier)) {
			return "OWLIndividual";
		}
		return null;
	}

	public static String getHyperlink(String term) {
		Collection<Section<? extends TermDefinition>> termDefs = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				term);
		if (!termDefs.isEmpty()) {
			return "Wiki.jsp?page=" + termDefs.iterator().next().getOriginalText();
		}
		return "";
	}

	/**
	 * Create the HTML of a button of the toolbar.
	 *
	 * @created 12.10.2011
	 * @param title
	 * @param action
	 * @param image
	 * @return
	 */
	public static String getButton(String title, String action, String imageClass) {
		return "<a href=\"javascript:"
				+ action
				+ ";void(0);\" jsaction=\""
				+ action
				+ "\" title=\""
				+ title
				+ "\" class=\"onte-button left small\">"
				+ "<img src=\"KnowWEExtension/images/onte/transparent.png\" class=\""
				+ imageClass + "\" /></a>";
	}

	/**
	 * Renders a nice hyperlink to the article a concept is defined. used for
	 * Inter-Wiki linking of the ontology definitions.
	 *
	 * @created 22.11.2011
	 * @param concept
	 * @return
	 */
	public static String renderHyperlink(Section<? extends Type> term, boolean raw) {

		if (term == null) {
			return "";
		}

		StringBuilder s = new StringBuilder();

		if (!raw) {
			s.append("<span style=\"font-size:9px;padding-left:10px;\">(Defined in: ");
		}
		s.append("<a href=\"Wiki.jsp?page=" + term.getTitle()
				+ "\" title=\"Goto definition article\">");
		s.append(term.getTitle());
		s.append("</a>");

		if (!raw) {
			s.append(")</span>");
		}

		return s.toString();
	}

	/**
	 * Renders a nice hyperlink to the article a concept is defined. used for
	 * Inter-Wiki linking of the ontology definitions.
	 *
	 * @created 22.11.2011
	 * @param concept
	 * @return
	 */
	public static String renderHyperlink(String term, boolean raw) {

		Collection<Section<? extends TermDefinition>> termDefs = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				term);

		// only one definition allowed in the onte plugin, so simply use the
		// first result
		if (!termDefs.isEmpty()) {
			return renderHyperlink(termDefs.iterator().next(), raw);
		}
		return "";
	}

	public static String renderHyperlink(String term) {
		return renderHyperlink(term, false);
	}
}
