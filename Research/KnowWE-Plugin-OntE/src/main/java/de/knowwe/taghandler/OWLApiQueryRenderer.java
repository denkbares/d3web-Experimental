package de.knowwe.taghandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.util.OWLEntityComparator;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.taghandler.TagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.renderer.OnteRenderingUtils;
import de.knowwe.owlapi.query.OWLApiQueryEngine;

/**
 * 
 * 
 * @author Stefan Mark
 * @created 04.10.2011
 * @param <T>
 */
public class OWLApiQueryRenderer implements Renderer {

	public static OWLApiQueryEngine engine = null;
	public static ShortFormProvider shortFormProvider = null;

	static {
		shortFormProvider = new SimpleShortFormProvider();
		engine = new OWLApiQueryEngine(shortFormProvider);
	}

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder string) {

		String query = DefaultMarkupType.getAnnotation(sec, "query");
		String show = DefaultMarkupType.getAnnotation(sec, "show");
		String view = DefaultMarkupType.getAnnotation(sec, "view");

		if (show == null) {
			show = "";
		}

		StringBuilder html = new StringBuilder();

		if (query != null && !query.trim().isEmpty()) {
			query = query.replace(",", "");
			if (view != null && view.equals("raw")) {
				printRawView(html, query, show);
			}
			else {
				printDefaultView(html, query, show);
			}
		}
		else {
			html.append("<div style=\"background: none repeat scroll 0 0 #FFFE9D;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");
			html.append(getDescription(user));
			html.append("</div>");
		}
		string.append(Strings.maskHTML(html.toString()));
	}

	/**
	 * Prints a raw list without the query string and the result type. This raw
	 * view is mainly used to print a navigation etc.
	 * 
	 * @created 12.11.2011
	 */
	private void printRawView(StringBuilder html, String query, String show) {
		try {
			html.append("<ul>");
			if (show.equals("*") || show.equals("ind")) {
				OWLClassExpression exp = engine.getOWLClassExpression(query);
				Set<OWLNamedIndividual> individuals = engine.getIndividuals(query, true);
				printEntities(individuals, html, exp);
			}

			if (show.equals("*") || show.equals("sub")) {
				Set<OWLClass> subClasses = engine.getSubClasses(query, true);
				printEntities(subClasses, html, null);
			}
			if (show.equals("*") || show.equals("sup")) {
				Set<OWLClass> superClasses = engine.getSuperClasses(query, true);
				printEntities(superClasses, html, null);
			}
			if (show.equals("*") || show.equals("equ")) {
				Set<OWLClass> equivalentClasses = engine.getEquivalentClasses(query);
				printEntities(equivalentClasses, html, null);
			}
			html.append("</ul>");
		}
		catch (ParserException e) {
			html.append(e.getMessage());
		}
	}

	private void printDefaultView(StringBuilder html, String query, String show) {
		html.append("<div style=\"background: none repeat scroll 0 0 #FFFE9D;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");
		html.append("<dl><dt>Query: ").append(query).append("</dt>");
		try {

			if (show.equals("*") || show.equals("ind")) {
				OWLClassExpression exp = engine.getOWLClassExpression(query);
				Set<OWLNamedIndividual> individuals = engine.getIndividuals(query, true);
				html.append("<dd>Individuals:</dd><dd><ul>");
				printEntities(individuals, html, exp);
				html.append("</ul></dd>");
			}

			if (show.equals("*") || show.equals("sub")) {
				Set<OWLClass> subClasses = engine.getSubClasses(query, true);
				html.append("<dd>SubClasses:</dd><dd><ul>");
				printEntities(subClasses, html, null);
				html.append("</ul></dd>");
			}
			if (show.equals("*") || show.equals("sup")) {
				Set<OWLClass> superClasses = engine.getSuperClasses(query, true);
				html.append("<dd>SuperClasses:</dd><dd><ul>");
				printEntities(superClasses, html, null);
				html.append("</ul></dd>");
			}
			if (show.equals("*") || show.equals("equ")) {
				Set<OWLClass> equivalentClasses = engine.getEquivalentClasses(query);
				html.append("<dd>Equivalent:</dd><dd><ul>");
				printEntities(equivalentClasses, html, null);
				html.append("</ul></dd>");
			}
			html.append("</dl>");
		}
		catch (ParserException e) {
			html.append(e.getMessage());
		}
		html.append("</div>");
	}

	/**
	 * Prints the results found from the {@link OWLApiQueryEngine}.
	 * 
	 * @created 12.11.2011
	 * @param entities
	 * @param string
	 * @param shortFormProvider
	 */
	private void printEntities(Set<? extends OWLEntity> entities, StringBuilder string, OWLClassExpression exp) {

		List<OWLEntity> list = new ArrayList<OWLEntity>(entities);
		Collections.sort(list, new OWLEntityComparator(shortFormProvider));

		if (!list.isEmpty()) {
			for (OWLEntity entity : list) {

				string.append("<li>");
				String conceptName = OnteRenderingUtils.getDisplayName(entity);

				String href = OnteRenderingUtils.renderHyperlink(conceptName, true);
				string.append(href);

				string.append("</li>");
			}
		}
		else {
			string.append("<li>NONE FOUND</li>");
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
		help.append("<dd>&#37;&#37;owlapi.query<br />@query=some kind of query<br />@show=the result type<br />@view=raw or styled output<br />&#37;<br />"
				+ " - Prints results of a query given in Manchester OWL syntax. The results can be filtered for subclass, superclass, individual and equivalent classes relationships.</dd>");

		help.append("<dt><strong>SYNOPSIS</strong></dt>");
		help.append("<dd>&#37;&#37;owlapi.query"
				+ " - Prints results of a query given in Manchester OWL syntax. The results can be filtered for subclass, superclass, individual and equivalent classes relationships. Possible values for the result type are as defined:<br />"
				+ "sub: only show sub classes<br />"
				+ "sup: only show super classes<br />"
				+ "equ: only show equivalent classes<br />"
				+ "ind: only show individuals<br />"
				+ "</dd>");

		help.append("<dt><strong>DESCRIPTION</strong></dt>");
		help.append("<dd>The OWLApiQuery Markup prints results of a query verbalized in the Manchester OWL syntax. Each result is rendered as a link to jump directly to the page the concept has been defined. Allows simple and fast navigation.</dd>");

		help.append("</dl>");

		return help.toString();
	}
}
