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
		StringBuilder html = new StringBuilder();

		if (query != null && !query.trim().isEmpty()) {
			ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
			OWLApiQueryEngine engine = new OWLApiQueryEngine(shortFormProvider);
			try {
				Set<OWLNamedIndividual> individuals = engine.getIndividuals(query, true);
				html.append("Individuals:<br />");
				printEntities(individuals, html, shortFormProvider);

				Set<OWLClass> subClasses = engine.getSubClasses(query, true);
				html.append("SubClasses:<br />");
				printEntities(subClasses, html, shortFormProvider);

				Set<OWLClass> superClasses = engine.getSuperClasses(query, true);
				html.append("SuperClasses:<br />");
				printEntities(superClasses, html, shortFormProvider);

				Set<OWLClass> equivalentClasses = engine.getEquivalentClasses(query);
				html.append("Equivalent:<br />");
				printEntities(equivalentClasses, html, shortFormProvider);
			}
			catch (ParserException e) {
				html.append(e.getMessage());
			}
		}
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
}
