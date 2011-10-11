/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.kdom.xcl;

import java.util.List;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.we.utils.D3webUtils;
import de.d3web.xcl.XCLModel;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.renderer.ObjectInfoLinkRenderer;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * 
 * @author Johannes Dienst
 * 
 *         Highlights the Solutions in CoveringList according to state. Also
 *         Includes the ObjectInfoLinkRenderer.
 * 
 */
public class SolutionIDHighlightingRenderer extends KnowWEDomRenderer {

	private static SolutionIDHighlightingRenderer instance;

	@Override
	public void render(KnowWEArticle article, Section sec,
			UserContext user, StringBuilder string) {
		String solution = sec.getOriginalText().replace("\"", "").trim();

		Session session = D3webUtils.getSession(article.getTitle(), user,
				article.getWeb());

		String spanStart = KnowWEUtils
				.maskHTML("<span style=\"background-color: rgb(");
		String spanStartEnd = KnowWEUtils.maskHTML(";\">");
		String spanEnd = KnowWEUtils.maskHTML("</span>");

		if (session != null) {

			List<Solution> diags = session.getKnowledgeBase().getManager().getSolutions();

			for (Solution d : diags) {

				if (d.getName().equals(solution)) {
					Rating state;
					XCLModel diagModel = d.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);

					if (diagModel == null) state = new Rating(State.UNCLEAR);
					else state = diagModel.getState(session);

					if (state.hasState(State.ESTABLISHED)) {
						string
								.append(spanStart + "51, 255, 51)"
										+ spanStartEnd);
					}

					if (state.hasState(State.EXCLUDED)) {
						string
								.append(spanStart + "255, 153, 0)"
										+ spanStartEnd);
					}

					if (state.hasState(State.SUGGESTED)) {
						string.append(spanStart + "220, 200, 11)"
								+ spanStartEnd);
					}

					if (state.hasState(State.UNCLEAR)) {
						string.append(spanStart + ")" + spanStartEnd);
					}
				}
			}
		}
		else {
			string.append("");
		}

		new ObjectInfoLinkRenderer(StyleRenderer.SOLUTION).render(article, sec,
				user, string);
		string.append(spanEnd);
	}

	/**
	 * Singleton.
	 * 
	 * @return
	 */
	public static SolutionIDHighlightingRenderer getInstance() {
		if (instance == null) instance = new SolutionIDHighlightingRenderer();

		return instance;
	}

}
