/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.onte.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.util.OWLEntityComparator;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.renderer.ManchesterOWLSyntaxHTMLColorRenderer;
import de.knowwe.owlapi.query.OWLApiQueryEngine;
import de.knowwe.taghandler.OWLApiTagHandlerUtil;

/**
 *
 *
 * @author Stefan Mark
 * @created 13.10.2011
 */
public class OWLApiClassExpressionQueryAction extends AbstractAction {

	private static ShortFormProvider shortFormProvider = new SimpleShortFormProvider();

	private OWLClassExpression expression = null;

	private ManchesterOWLSyntaxHTMLColorRenderer renderer = null;

	@Override
	public void execute(UserActionContext context) throws IOException {

		StringBuilder html = new StringBuilder();
		String query = context.getParameter("query");
		String option = context.getParameter("options");

		renderer = new ManchesterOWLSyntaxHTMLColorRenderer(
				KnowWEUtils.getTerminologyHandler(context.getWeb()));

		if (query != null && !query.trim().isEmpty()) {
			query = query.replace(",", "");
			OWLApiQueryEngine engine = new OWLApiQueryEngine(shortFormProvider);
			String[] selectedOptions = (option != null) ? option.split("::") : new String[0];

			Map<OWLEntity, Set<OWLAxiom>> queryResults = new HashMap<OWLEntity, Set<OWLAxiom>>();
			String tmp = "";

			try {
				expression = engine.getOWLClassExpression(query);

				// Query for individual results ...
				Set<OWLNamedIndividual> individuals = engine.getIndividuals(query, true);
				for (OWLNamedIndividual owlNamedIndividual : individuals) {
					Set<OWLAxiom> explanations = engine.getIndividualExplanations(expression,
							owlNamedIndividual);
					queryResults.put(owlNamedIndividual, explanations);
				}
				tmp = printQueryResults("Individuals", queryResults, isVisible(selectedOptions,
						"Individuals"));
				html.append(tmp);
				queryResults.clear();

				// ... or query for sub classes ...
				Set<OWLClass> classes = engine.getSubClasses(query, true);
				for (OWLClass cls : classes) {
					Set<OWLAxiom> explanations = engine.getSubClassesExplanations(expression, cls);
					queryResults.put(cls, explanations);
				}
				tmp = printQueryResults("Subclasses", queryResults, isVisible(selectedOptions,
						"Subclasses"));
				html.append(tmp);
				queryResults.clear();

				// ... or query for super classes ...
				classes = engine.getSuperClasses(query, true);
				for (OWLClass cls : classes) {
					Set<OWLAxiom> explanations = engine.getSuperClassesExplanations(expression, cls);
					queryResults.put(cls, explanations);
				}
				tmp = printQueryResults("Super classes", queryResults, isVisible(selectedOptions,
						"Super classes"));
				html.append(tmp);
				queryResults.clear();

				// ... or query for equivalent classes ...
				classes = engine.getEquivalentClasses(query);
				for (OWLClass cls : classes) {
					Set<OWLAxiom> explanations = engine.getEquivalentClassesExplanations(
							expression, cls);
					queryResults.put(cls, explanations);
				}
				tmp = printQueryResults("Equivalent classes", queryResults, isVisible(
						selectedOptions, "Equivalent classes"));
				queryResults.clear();
				html.append(tmp);
			}
			catch (ParserException e) {
				html.append(e.getMessage());
			}
		}

		html.append("</div>");
		context.getWriter().write(html.toString());
	}

	/**
	 * Checks weather a certain option from the query window is checked. If TRUE
	 * the according information is visible to the user, if FALSE, the
	 * information will be hidden until the checkbox is checked.
	 *
	 * @created 10.10.2011
	 * @param selectedOptions
	 * @param option
	 * @return
	 */
	private boolean isVisible(String[] selectedOptions, String option) {
		String className = option.toLowerCase().replace(" ", "");
		for (String s : selectedOptions) {
			if (s.equals("onte-option-" + className)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 *
	 * @created 10.10.2011
	 * @param option
	 * @param queryResults
	 * @param isVisible
	 * @return
	 */
	private String printQueryResults(String option, Map<OWLEntity, Set<OWLAxiom>> queryResults, boolean isVisible) {

		StringBuilder results = new StringBuilder();

		// sort the results alphabetically
		List<OWLEntity> list = new ArrayList<OWLEntity>(queryResults.keySet());
		Collections.sort(list, new OWLEntityComparator(shortFormProvider));

		String className = option.toLowerCase().replace(" ", "");
		String style = "style=\"display:none; visibility:hidden\"";

		if(isVisible) {
			style = "";
		}

		results.append("<dl id=\"onte-option-" + className + "\" " + style + "><dt>"
				+ option
				+ " ("
				+ list.size() + ")</dt>");

		if (!list.isEmpty()) {
			for (OWLEntity entity : list) {
				results.append("<dd class=\"onte-result\" onmouseout=\"this.className='onte-result';return true;\" onmouseover=\"this.className='onte-result hover';return true;\">");
				results.append(shortFormProvider.getShortForm(entity));
				results.append("<div class=\"onte-explanation-details onte-box\">");

				for (OWLAxiom axiom : queryResults.get(entity)) {
					results.append("<p>");
					String verbalized = OWLApiTagHandlerUtil.verbalizeToManchesterSyntax(axiom);
					renderer.colorize(verbalized, results, axiom);
					results.append("</p>");
				}
				results.append("</div>");
				results.append("</dd>");
			}
		}
		else {
			results.append("<dd>...</dd>");
		}
		results.append("</dl>");
		return results.toString();
	}
}
