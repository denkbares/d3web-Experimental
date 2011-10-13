package de.knowwe.owlapi.query;

import java.util.Set;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.taghandler.TagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.owlapi.OWLAPIConnector;

/**
 *
 *
 * @author Stefan Mark
 * @created 04.10.2011
 * @param <T>
 */
public class OWLApiQueryRenderer<T extends AbstractType> extends KnowWEDomRenderer<T> {

	public static OWLAPIConnector connector = null;

	@Override
	public void render(KnowWEArticle article, Section<T> sec, UserContext user, StringBuilder string) {

		String query = DefaultMarkupType.getAnnotation(sec, "query");
		String show = DefaultMarkupType.getAnnotation(sec, "show");

		if (show == null) {
			show = "";
		}

		StringBuilder html = new StringBuilder();
		html.append("<div style=\"background: none repeat scroll 0 0 #FFFE9D;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");


		if (query != null && !query.trim().isEmpty()) {
			ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
			OWLApiQueryEngine engine = new OWLApiQueryEngine(shortFormProvider);
			query = query.replace(",", "");
			html.append("<dl><dt>Query: " + query + "</dt>");

			try {

				if (show.equals("*") || show.equals("ind")) {
					Set<OWLNamedIndividual> individuals = engine.getIndividuals(query, true);
					html.append("<dd>Individuals:</dd><dd>");
					printEntities(individuals, html, shortFormProvider);
					html.append("</dd>");
				}

				if (show.equals("*") || show.equals("sub")) {
					Set<OWLClass> subClasses = engine.getSubClasses(query, true);
					html.append("<dd>SubClasses:</dd><dd>");
					printEntities(subClasses, html, shortFormProvider);
					html.append("</dd>");
				}
				if (show.equals("*") || show.equals("sup")) {
					Set<OWLClass> superClasses = engine.getSuperClasses(query, true);
					html.append("<dd>SuperClasses:</dd><dd>");
					printEntities(superClasses, html, shortFormProvider);
					html.append("</dd>");
				}
				if (show.equals("*") || show.equals("equ")) {
					Set<OWLClass> equivalentClasses = engine.getEquivalentClasses(query);
					html.append("<dd>Equivalent:</dd><dd>");
					printEntities(equivalentClasses, html, shortFormProvider);
					html.append("</dd>");
				}
				html.append("</dl>");
			}
			catch (ParserException e) {
				html.append(e.getMessage());
			}
		}
		else {
			html.append(getDescription(user));
		}
		html.append("</div>");
		string.append(KnowWEUtils.maskHTML(html.toString()));
	}

	private void printEntities(Set<? extends OWLEntity> entities, StringBuilder string, ShortFormProvider shortFormProvider) {

		if (!entities.isEmpty()) {
			for (OWLEntity entity : entities) {
				string.append("....");
				string.append(shortFormProvider.getShortForm(entity));
				string.append("<br />");
			}
			string.append("<br />");
		}
		else {
			string.append("NONE FOUND<br /><br />");
		}
	}

	/**
	 * Appends a simple how to use message to the output if the
	 * {@link TagHandler} was used incorrectly.
	 *
	 * @created 20.09.2011
	 * @return String The how to use message
	 */
	private String getDescription(UserContext user) {

		StringBuilder help = new StringBuilder();
		help.append("<dl>");

		help.append("<dt><strong>NAME</strong></dt>");
		help.append("<dd>&#37;&#37;owlapi.query<br />@query=some kind of query<br />&#37;<br />"
				+ " - Prints results of a query given in Manchester OWL syntax.</dd>");

		help.append("<dt><strong>SYNOPSIS</strong></dt>");
		help.append("<dd>&#37;&#37;owlapi.query"
				+ " - Prints results of a query given in Manchester OWL syntax.</dd>");

		help.append("<dt><strong>DESCRIPTION</strong></dt>");
		help.append("<dd>The OWLApiQuery Markup prints results of a query verbalized in the Manchester OWL syntax.</dd>");

		help.append("</dl>");

		return help.toString();
	}
}
