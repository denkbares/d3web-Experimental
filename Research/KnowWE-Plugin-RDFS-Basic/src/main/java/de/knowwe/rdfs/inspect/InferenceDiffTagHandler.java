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
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.util.SparqlResultSetRenderer;
import de.knowwe.tools.Tool;

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
public class InferenceDiffTagHandler extends AbstractTagHandler {

	private static DefaultMarkupRenderer<DefaultMarkupType> defaultMarkupRenderer =
			new DefaultMarkupRenderer<DefaultMarkupType>();

	/**
	 * Constructor for the StatementImportanceTagHandler. Defines the used
	 * TagHandler string in the article page.
	 */
	public InferenceDiffTagHandler() {
		super("inferenceDiff");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext user, Map<String, String> parameters) {

		Map<String, String> urlParameters = user.getParameters();
		String sectionID = urlParameters.get("section");

		if (sectionID == null)
			return "Could not calculate any difference. No statement given!";

		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(
				user.getWeb());

		Section<?> statementSection = Sections.getSection(sectionID);
		StringBuilder html = new StringBuilder();
		if (statementSection != null) {

			Collection<Statement> diff = Rdf2GoCore.getInstance().generateStatementDiffForSection(
					statementSection);

			// render the difference

			html.append(KnowWEUtils.maskHTML("<h3>"));
			html.append("Diff for: \"" + statementSection.getOriginalText() + "\"");
			html.append(KnowWEUtils.maskHTML("</h3>"));
			html.append(KnowWEUtils.maskHTML("<table>"));
			html.append(renderDiff(diff));
			html.append(KnowWEUtils.maskHTML("</table>"));
		}

		StringBuilder buffer = new StringBuilder();
		String cssClassName = "type_" + section.get().getName();
		defaultMarkupRenderer.renderDefaultMarkupStyled(
				getTagName(), html.toString(), sectionID, cssClassName, new Tool[] {},
				user,
				buffer);
		KnowWEUtils.maskJSPWikiMarkup(buffer);
		return buffer.toString();

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

		result.append(KnowWEUtils.maskHTML("<tr>"));
		result.append(KnowWEUtils.maskHTML("<th>"));
		result.append("x");
		result.append(KnowWEUtils.maskHTML("</th>"));
		result.append(KnowWEUtils.maskHTML("<th>"));
		result.append("y");
		result.append(KnowWEUtils.maskHTML("</th>"));
		result.append(KnowWEUtils.maskHTML("<th>"));
		result.append("z");
		result.append(KnowWEUtils.maskHTML("</th>"));
		result.append(KnowWEUtils.maskHTML("</tr>"));

		Iterator<Statement> itr = set.iterator();
		while (itr.hasNext()) {
			Statement row = itr.next();
			// x y z (variables names of the query, see above)

			result.append(KnowWEUtils.maskHTML("<tr>"));
			result.append(KnowWEUtils.maskHTML("<td>"));
			result.append(SparqlResultSetRenderer.renderNode(true, row.getSubject()));
			result.append(KnowWEUtils.maskHTML("</td>"));
			result.append(KnowWEUtils.maskHTML("<td>"));
			result.append(SparqlResultSetRenderer.renderNode(true, row.getPredicate()));
			result.append(KnowWEUtils.maskHTML("</td>"));
			result.append(KnowWEUtils.maskHTML("<td>"));
			result.append(SparqlResultSetRenderer.renderNode(true, row.getObject()));
			result.append(KnowWEUtils.maskHTML("</td>"));
			result.append(KnowWEUtils.maskHTML("</tr>"));
		}
		return result.toString();
	}
}
