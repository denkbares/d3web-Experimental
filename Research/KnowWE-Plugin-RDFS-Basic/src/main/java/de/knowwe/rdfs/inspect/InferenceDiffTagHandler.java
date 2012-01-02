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
package de.knowwe.rdfs.inspect;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.ontoware.rdf2go.model.Statement;

import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;
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
public class InferenceDiffTagHandler extends AbstractHTMLTagHandler {

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
	public InferenceDiffTagHandler() {
		super("inferenceDiff");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {

		Map<String, String> urlParameters = user.getParameters();
		String sectionID = urlParameters.get("section");
		String title = urlParameters.get("topic");

		if (sectionID == null)
			return "Could not calculate any difference. No statement given!";

		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);
		KnowWEArticle rdfArticle = mgr.getArticle(title);

		Section<?> statementSection = Sections.getSection(sectionID);
		StringBuilder html = new StringBuilder();
		if (statementSection != null) {

			Collection<Statement> diff = Rdf2GoCore.getInstance().generateStatementDiffForSection(
					statementSection);

			// render the difference

			html.append("<dl>");
			html.append("<dt>Removed: " + statementSection.getOriginalText() + "</dt>");
			html.append(renderDiff(diff));
			html.append("</dl>");
		}
		return html.toString();
	}

	/**
	 * Renders the difference of two HashSets as a table to the user.
	 * 
	 * @created 01.06.2011
	 * @param set
	 *            A set containing the statements that differ.
	 * @return HTML string
	 */
	private String renderDiff(Collection<Statement> set) {
		StringBuilder result = new StringBuilder();

		Iterator<Statement> itr = set.iterator();
		while (itr.hasNext()) {
			Statement row = itr.next();
			// x y z (variables names of the query, see above)

			result.append("<dd>");

			result.append(row.getSubject().toString());
			result.append(" ");
			result.append(row.getPredicate().toString());
			result.append(" ");
			result.append(row.getObject().toString());

			result.append("</dd>");
		}
		return result.toString();
	}

}
