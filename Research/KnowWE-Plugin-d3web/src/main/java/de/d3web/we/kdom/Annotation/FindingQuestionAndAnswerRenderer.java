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
package de.d3web.we.kdom.Annotation;

import java.util.Collection;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.renderer.ObjectInfoLinkRenderer;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * Renders red background for Findings and questions when there was an error
 * when creating the knowledge. Error is contained in tooltip.
 * 
 * @author Reinhard Hatko Created on: 12.11.2009
 */
public class FindingQuestionAndAnswerRenderer extends KnowWEDomRenderer {

	private final KnowWEDomRenderer delegate;

	public FindingQuestionAndAnswerRenderer(String foregroundColor) {

		delegate = new ObjectInfoLinkRenderer(
				new StyleRenderer(foregroundColor));

	}

	@Override
	public void render(KnowWEArticle article, Section sec, UserContext user, StringBuilder string) {

		Collection<KDOMReportMessage> messages = KDOMReportMessage.getMessages(sec, article);

		if (messages.isEmpty() || messages.iterator().next().getVerbalization().equals("")) {
			delegate.render(article, sec, user, string);
		}
		else {

			// TODO: atm just the first is used, rest ignored
			KDOMReportMessage message = messages.iterator().next();

			string.append("<span class='error_highlight' title=\"" + message + "\">");

			renderer.render(article, sec, user,
					string);

			string.append("</span>");
		}

	}

	private static StyleRenderer renderer = new StyleRenderer("color:rgb(0, 0, 255)");

}
