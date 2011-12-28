/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.knowwe.rdf2go.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;

import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.event.ArticleUpdatesFinishedEvent;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * The StatementImportanceTagHandler shows to a given RDFS statement how
 * important it is in the knowledge base. Therefore the difference between the
 * knowledge with and without the statement is compared and the result shown to
 * the user.
 * 
 * Please insert the following code snippet in an article page:
 * <code>[{KnowWEPlugin statementImportance}]</code>
 * 
 * @author Stefan Mark
 * @created 01.06.2011
 */
public class StatementImportanceTagHandler extends AbstractHTMLTagHandler {

	/**
	 * An instance of the RDF2Go object. Used to connect to the triple store.
	 */
	private static final Rdf2GoCore rdf2goCore = Rdf2GoCore.getInstance();

	private static final String[] vars = new String[] {
			"x", "y", "z" };
	/**
	 * Constructor for the StatementImportanceTagHandler. Defines the used
	 * TagHandler string in the article page.
	 */
	public StatementImportanceTagHandler() {
		super("statementImportance");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {

		Map<String, String> urlParameters = user.getParameters();
		String sectionID = urlParameters.get("section");
		String title = urlParameters.get("topic");
		String query = getQuery();

		if (sectionID == null) return "Could not calculate any difference. No statement given!";

		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);
		KnowWEArticle rdfArticle = mgr.getArticle(title);

		Section<?> statementSection = Sections.getSection(sectionID);
		StringBuilder html = new StringBuilder();
		if (statementSection != null) {

			List<Statement> statements = rdf2goCore.getSectionStatementsRecursive(statementSection);
			HashSet<String> previous = modelToHashSet(query);
			rdf2goCore.removeSectionStatementsRecursive(statementSection);
			rdf2goCore.notify(new ArticleUpdatesFinishedEvent());

			HashSet<String> after = modelToHashSet(query);
			HashSet<String> diff = modelDiff(previous, after);

			// re-add the deleted statement
			if (statements.size() > 0) {
				rdf2goCore.addStatement(statements.get(0), statementSection);
				rdf2goCore.notify(new ArticleUpdatesFinishedEvent());
			}

			// render the difference

			html.append("<dl>");
			html.append("<dt>Removed: " + statementSection.getOriginalText() + "</dt>");
			html.append(renderDiff(diff));
			html.append("</dl>");
		}
		return html.toString();
	}
	/**
	 * Creates out of the defined variables in <code>{@link vars}</code>
	 *
	 * @created 01.06.2011
	 * @return
	 */
	private String getQuery() {
		String variables = StringUtils.join(vars, " ?");
		return "SELECT ?" + variables + "{ ?" + variables + ".}";
	}
	/**
	 * Queries the triple store through the rdf2go API and stores all found
	 * statements into a HashSet. This set is later used to calculate the
	 * difference between the state of the knowledge with and without a
	 * statement.
	 *
	 * @created 01.06.2011
	 * @param String query A SPARQL query used to ask the triple store.
	 * @return HashSet<QueryRow> The resulting statements of the query in a
	 *         HashSet.
	 */
	private HashSet<String> modelToHashSet(String query) {
		QueryResultTable qrt = rdf2goCore.sparqlSelect(query);

		ClosableIterator<QueryRow> itr = qrt.iterator();

		HashSet<String> shell = new HashSet<String>();

		while (itr.hasNext()) {
			QueryRow row = itr.next();
			shell.add(beatifyQueryRow(row));
		}
		return shell;
	}
	/**
	 * Calculates the difference of two {@link HashSet}s containing RDF
	 * statements.
	 *
	 * @created 01.06.2011
	 * @param previous The statements before the delete operation.
	 * @param after The statements after the delete operation.
	 * @return The difference of the two HashSets
	 */
	private HashSet<String> modelDiff(HashSet<String> previous, HashSet<String> after) {
		HashSet<String> diff = new HashSet<String>();

		Iterator<String> itr = previous.iterator();
		while (itr.hasNext()) {
			String row = itr.next();
			if (!after.contains(row)) {
				diff.add(row);
			}
		}
		return diff;
	}
	/**
	 * Renders the difference of two HashSets as a table to the user.
	 *
	 * @created 01.06.2011
	 * @param set A set containing the statements that differ.
	 * @return HTML string
	 */
	private String renderDiff(HashSet<String> set) {
		StringBuilder result = new StringBuilder();

		Iterator<String> itr = set.iterator();
		while (itr.hasNext()) {
			String row = itr.next();
			// x y z (variables names of the query, see above)

			result.append("<dd>");
			result.append(row);
			// result.append(rdf2goCore.reduceNamespace(row.getValue("x").toString()));
			// result.append(rdf2goCore.reduceNamespace(row.getValue("y").toString()));
			// result.append(rdf2goCore.reduceNamespace(row.getValue("z").toString()));
			result.append("</dd>");
		}
		return result.toString();
	}

	/**
	 * Just some formatting of the triples for the eye.
	 *
	 * @created 01.06.2011
	 * @param row
	 * @return
	 */
	private String beatifyQueryRow(QueryRow row) {

		StringBuilder statement = new StringBuilder();
		for(int i = 0; i < vars.length; i++) {
			String ressource = rdf2goCore.reduceNamespace(row.getValue(vars[i]).toString());
			statement.append(ressource);

			if(i < vars.length){
				statement.append(" | ");
			}
		}
		return statement.toString();
	}
}
