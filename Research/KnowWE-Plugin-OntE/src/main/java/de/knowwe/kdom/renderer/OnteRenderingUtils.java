package de.knowwe.kdom.renderer;

import java.util.Collection;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;

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
	public static boolean isKnownTerm(String term) {

		ReferenceManager manager = IncrementalCompiler.getInstance().getTerminology();
		return manager.isValid(term);
	}

	/**
	 * Renders a nice hyperlink to the article a concept is defined. used for
	 * Inter-Wiki linking of the ontology definitions.
	 *
	 * @created 22.11.2011
	 * @param concept
	 * @return
	 */
	public static String renderHyperlink(Section<? extends Type> term) {

		if (term == null) {
			return "";
		}

		StringBuilder s = new StringBuilder();

		s.append("<span style=\"font-size:9px;padding-left:30px;\">(Defined in: ");
		s.append("<a href=\"Wiki.jsp?page=" + term.getOriginalText()
				+ "\" title=\"Goto definition article\">");
		s.append(term.getTitle());
		s.append("</a>");
		s.append(")</span>");

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
	public static String renderHyperlink(String term) {

		Collection<Section<? extends TermDefinition>> termDefs = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				term);

		// only one definition allowed in the onte plugin, so simply use the
		// first result
		if (!termDefs.isEmpty()) {
			return renderHyperlink(termDefs.iterator().next());
		}
		return "";
	}
}
