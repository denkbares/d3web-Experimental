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

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.sparql.RenderMode;
import de.knowwe.rdf2go.sparql.SparqlResultRenderer;
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

	private static DefaultMarkupRenderer defaultMarkupRenderer =
			new DefaultMarkupRenderer();

	/**
	 * Constructor for the StatementImportanceTagHandler. Defines the used
	 * TagHandler string in the article page.
	 */
	public InferenceDiffTagHandler() {
		super("inferenceDiff");
	}

	@Override
	public void render(Section<?> section, UserContext user, Map<String, String> parameters, RenderResult result) {

		Map<String, String> urlParameters = user.getParameters();
		String sectionID = urlParameters.get("section");

		if (sectionID == null) {
			result.append("Could not calculate any difference. No statement given!");
			return;
		}

		RenderResult html = new RenderResult(result);
		Section<?> statementSection = Sections.getSection(sectionID);
		if (statementSection != null) {

			Collection<Statement> diff = Rdf2GoCore.getInstance().generateStatementDiffForSection(
					statementSection);
			// render the difference
			html.appendHtml("<h3>");
			html.append("Diff for: \"" + statementSection.getText() + "\"");
			html.appendHtml("</h3>");
			html.appendHtml("<table>");
			html.append(renderDiff(diff, user));
			html.appendHtml("</table>");
		}

		RenderResult buffer = new RenderResult(user);
		String cssClassName = "type_" + section.get().getName();
		defaultMarkupRenderer.renderDefaultMarkupStyled(
				getTagName(), html.toStringRaw(), sectionID, cssClassName, new Tool[] {},
				user,
				buffer);
		result.appendJSPWikiMarkup(buffer);

	}

	/**
	 * Renders the difference of two HashSets as a table to the user.
	 * 
	 * @created 01.06.2011
	 * @param set A set containing the statements that differ.
	 * @return HTML string
	 */
	private String renderDiff(Collection<Statement> set, UserContext user) {
		Rdf2GoCore core = Rdf2GoCore.getInstance();
		RenderResult result = new RenderResult(user);

		result.appendHtml("<tr>");
		result.appendHtml("<th>");
		result.append("x");
		result.appendHtml("</th>");
		result.appendHtml("<th>");
		result.append("y");
		result.appendHtml("</th>");
		result.appendHtml("<th>");
		result.append("z");
		result.appendHtml("</th>");
		result.appendHtml("</tr>");

		Iterator<Statement> itr = set.iterator();
		while (itr.hasNext()) {
			Statement row = itr.next();
			// x y z (variables names of the query, see above)

			result.appendHtml("<tr>");
			result.appendHtml("<td>");
			result.append(SparqlResultRenderer.getInstance().renderNode(row.getSubject(), null,
					false, user, core, RenderMode.HTML));
			result.appendHtml("</td>");
			result.appendHtml("<td>");
			result.append(SparqlResultRenderer.getInstance().renderNode(row.getPredicate(), null,
					false, user, core, RenderMode.HTML));
			result.appendHtml("</td>");
			result.appendHtml("<td>");
			result.append(SparqlResultRenderer.getInstance().renderNode(row.getObject(), null,
					false, user, core, RenderMode.HTML));
			result.appendHtml("</td>");
			result.appendHtml("</tr>");
		}
		return result.toString();
	}
}
