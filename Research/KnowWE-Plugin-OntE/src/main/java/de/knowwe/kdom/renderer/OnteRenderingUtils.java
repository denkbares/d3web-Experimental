package de.knowwe.kdom.renderer;

import java.util.Collection;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import de.knowwe.compile.ImportManager;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;
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
	public static boolean isKnownTerm(TermIdentifier termIdendifier) {
		ReferenceManager manager = IncrementalCompiler.getInstance().getTerminology();
		return manager.isValid(termIdendifier);
	}

	public static String determineTypeOfTerm(String term) {

		OWLApiQueryParser parser = new OWLApiQueryParser(new SimpleShortFormProvider());
		if (parser.isClassName(term)) {
			return "OWLClass";
		}
		else if (parser.isObjectPropertyName(term)) {
			return "OWLObjectProperty";
		}
		else if (parser.isDataPropertyName(term)) {
			return "OWLDataProperty";
		}
		else if (parser.isIndividualName(term)) {
			return "OWLIndividual";
		}
		return null;
	}

	public static String getHyperlink(TermIdentifier termIdentifier) {

		if (IncrementalCompiler.getInstance().getTerminology().isImportedObject(termIdentifier)) {
			Section<? extends AbstractType> importSection = ImportManager.resolveImportSection(termIdentifier);
			return "Wiki.jsp?page=" + importSection.getTitle();
		}
		else {
			Collection<Section<? extends SimpleDefinition>> termDefs = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
					termIdentifier);
			if (!termDefs.isEmpty()) {

				Section<?> next = termDefs.iterator().next();
				if (next.getArticle() != null) {
					return "Wiki.jsp?page=" + next.getTitle();
				}
			}
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

		String href = getHyperlink(KnowWEUtils.getTermIdentifier(term));
		if (!href.isEmpty()) {
			s.append("<a href=\"").append(href);
			s.append("\" title=\"Goto definition article\">");
			s.append(term.getText());
			s.append("</a>");
		}
		else {
			// predefined
			s.append(term.getText());
		}

		// boolean isPredefined =
		// IncrementalCompiler.getInstance().getTerminology().isPredefinedObject(
		// term.getOriginalText());
		//
		// if (isPredefined) {
		// s.append(term.getOriginalText());
		// }
		// else {
		// s.append("<a href=\"Wiki.jsp?page=");
		//
		// boolean isImported =
		// IncrementalCompiler.getInstance().getTerminology().isImportedObject(
		// term.getOriginalText());
		// if (isImported) {
		// Section<?> importSection =
		// ImportManager.resolveImportSection(term.getOriginalText());
		// s.append(importSection.getTitle());
		// }
		// else {
		// s.append(term.getTitle());
		// }
		// s.append("\" title=\"Goto definition article\">");
		// s.append(term.getOriginalText());
		// s.append("</a>");
		// }

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

		term = term.replace("\"", "").trim();
		Collection<Section<? extends SimpleDefinition>> termDefs =
				IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
						new TermIdentifier(term));

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

	/**
	 * 
	 * 
	 * @created 21.12.2011
	 * @param owlEntity
	 * @return
	 */
	public static String getDisplayName(OWLEntity owlEntity) {

		IRI iri = owlEntity.getIRI();

		if (iri.getFragment() != null) {
			return iri.getFragment();
		}

		String path = owlEntity.getIRI().toURI().getPath();
		return path.substring(path.lastIndexOf("/") + 1);
	}
}
