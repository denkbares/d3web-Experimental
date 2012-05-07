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
package de.d3web.knowwe.renderer;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.knowwe.type.AnnotatedString;
import de.d3web.knowwe.type.AnnotationObject;
import de.d3web.knowwe.type.AnnotationProperty;
import de.d3web.knowwe.type.SimpleAnnotation;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.contexts.AnnotationContext;
import de.knowwe.core.contexts.ContextManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.Strings;

/**
 * 
 * @author Johannes Dienst
 * @created 09.06.2011
 */
public class AnnotationInlineAnswerRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder string) {

		Section<?> prop = Sections.findSuccessor(sec, AnnotationProperty.class);
		if (prop == null || !prop.getText().contains("asks")) return;

		String question = null;
		Section<?> qAChild = Sections.findSuccessor(sec, SimpleAnnotation.class);

		if (qAChild == null) qAChild = Sections.findSuccessor(sec, AnnotationObject.class);

		if (qAChild != null) question = qAChild.getText().trim();

		if (question == null) {
			Section<?> findChildOfType = Sections.findSuccessor(sec, SimpleAnnotation.class);
			if (findChildOfType != null) {
				question = findChildOfType.getText();
			}
		}

		String text = "ERROR!!";
		try {
			text = Sections.findSuccessor(sec, AnnotatedString.class).getText();
		}
		catch (NullPointerException e) {
			Logger.getLogger(
					AnnotationInlineAnswerRenderer.class.getName()).
					log(Level.INFO, "AnnotatedString not found");
			// new StandardAnnotationRenderer().render(article, sec, user,
			// string);
		}

		String web = sec.getWeb();
		String title = sec.getTitle();
		KnowledgeBase service = D3webUtils.getKnowledgeBase(web, title);
		String name = service.getName();
		if (name == null) {
			KnowledgeBase base = D3webUtils.getFirstKnowledgeBase(web);
			if (base != null) service = base;
		}

		String middle = renderline(sec, user.getUserName(), question, text, service);

		if (middle != null) string.append(middle);
		// else {
		// new StandardAnnotationRenderer().render(article, sec, user, string);
		// }
	}

	private String renderline(Section<?> sec, String user, String question,
			String text, KnowledgeBase kb) {
		if (kb != null && question != null) {
			question = question.trim();
			Question q = kb.getManager().searchQuestion(question);
			if (q != null) {
				AnnotationContext context = (AnnotationContext) ContextManager
						.getInstance().getContext(sec, AnnotationContext.CID);
				String op = "";
				if (context != null) op = context.getAnnotationproperty();
				// UpperOntology2 uo = UpperOntology2.getInstance();
				// if (!uo.knownConcept(op)) {
				// return KnowWEUtils.maskHTML(DefaultTextType
				// .getErrorUnknownConcept(op, text));
				// }
				String s = "<a href=\"#" + sec.getID() + "\"></a>"
						+ getRenderedInput(q.getName(), q.getName(),
								kb.getId(), user, "Annotation", text, op);
				String masked = Strings.maskHTML(s);
				return masked;
			}
			else {
				return Strings.maskHTML(KnowWEUtils.getErrorQ404(
						question, text));
			}
		}
		return null;
	}

	private static String getRenderedInput(String questionid, String question,
			String namespace, String userName, String title, String text,
			String type) {
		question = Strings.encodeURL(question);
		// text=URLEncoder.encode(text);

		String rendering = "<span class=\"semLink\" "
				+ "rel=\"{type: '" + type + "', objectID: '" + questionid
				+ "', termName: '" + text + "', user:'" + userName + "'}\">"
				+ text + "</span>";
		return rendering;
	}

}