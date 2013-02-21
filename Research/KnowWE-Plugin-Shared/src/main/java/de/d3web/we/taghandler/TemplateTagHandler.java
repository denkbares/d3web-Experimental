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
package de.d3web.we.taghandler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.ResourceBundle;

import de.d3web.we.action.TemplateGenerationAction;
import de.d3web.we.kdom.wikiTemplate.Template;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.report.Messages;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.xml.AbstractXMLType;

/**
 * @author Johannes Dienst
 * 
 *         Used to Generate WikiPages out of a TemplateType {@link TemplateType}
 *         {@link TemplateGenerationAction}
 */
public class TemplateTagHandler extends AbstractHTMLTagHandler {

	public TemplateTagHandler() {
		super("copytemplate");
	}

	@Override
	public String getDescription(UserContext user) {
		return Messages.getMessageBundle(user).
				getString("KnowWE.TemplateTagHandler.description");
	}

	@Override
	public void renderHTML(String web, String topic,
			UserContext user, Map<String, String> values, RenderResult result) {

		ResourceBundle rb = Messages
				.getMessageBundle(user);

		List<Section<Template>> templates = getTemplateTypes(Environment
				.getInstance().getArticle(web, topic));

		StringBuffer html = new StringBuffer();

		html.append("<div id=\"TemplateTagHandler\" class=\"panel\"><h3>"
				+ rb.getString("KnowWE.TemplateTagHandler.topic") + "</h3>");
		html.append("<form method='post' action=''>");
		html.append("<fieldset>");

		if ((templates != null) && (!templates.isEmpty())) {

			int i = 0;
			String secName = "";
			for (ListIterator<Section<Template>> it = templates.listIterator(); it
					.hasNext(); i++) {

				Section<Template> temp = it.next();
				secName = AbstractXMLType.getAttributeMapFor(temp).get(
						"name");

				html.append("<div>");
				html.append("<p><img src='KnowWEExtension/images/arrow_right.png' border='0'/> "
						+ rb
								.getString("KnowWE.TemplateTagHandler.copy")
						+ " " + secName + "</p>");
				html.append("<label for='" + "Template" + i + "'>"
						+ rb.getString("KnowWE.TemplateTagHandler.newpage")
						+ "</label>");
				html.append("<input id='"
						+ "Template"
						+ i
						+ "' type='text' name='templateTerm' class='field' title=''/>");

				html.append("<input type='button' value='"
						+ rb.getString("KnowWE.TemplateTagHandler.copyButton")
						+ "' name='generate' class='button generate-template' "
						+ "title='' rel='{jar : \"Template"
						+ i + "\"}'/>");

				html.append("</div> \n"); // \n only to avoid hmtl-code being
				// cut by JspWiki (String.length >
				// 10000)

			}
		}
		else {
			html.append("<div>");
			html.append("<p class='info box'>"
					+ rb.getString("KnowWE.TemplateTagHandler.noTemplate")
					+ "</p>");
			html.append("</div>");

		}

		// div for generating info
		html.append("<div id ='TemplateGeneratingInfo'>");
		html.append("</div>");

		html.append("</fieldset> ");

		html.append("</form>");

		html.append("</div>");

		result.appendHTML(html.toString());
	}

	/**
	 * Finds all TemplateTypes in an article.
	 * 
	 * @param article
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Section<Template>> getTemplateTypes(Article article) {
		List<Section<?>> found = Sections.getSubtreePreOrder(article.getRootSection());
		// article.getSection()
		// Sections.findSuccessorsOfType(TemplateType.class, found);
		ArrayList<Section<Template>> cleaned = new ArrayList<Section<Template>>();
		for (Section<? extends Type> s : found) {
			if (s.get() instanceof Template) cleaned.add((Section<Template>) s);
		}
		return cleaned;
	}
}
