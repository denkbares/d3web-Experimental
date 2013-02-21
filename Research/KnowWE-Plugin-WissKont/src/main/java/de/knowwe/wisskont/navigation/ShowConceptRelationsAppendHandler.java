/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.wisskont.navigation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.append.PageAppendHandler;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.rdfs.vis.RenderingCore;
import de.knowwe.wisskont.ConceptMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 29.11.2012
 */
public class ShowConceptRelationsAppendHandler implements PageAppendHandler {

	@Override
	public void append(String web, String topic, UserContext user, RenderResult result) {
		ArticleManager articleManager = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);
		Article article = articleManager.getArticle(topic);
		Section<?> section = article.getRootSection();
		List<Section<ConceptMarkup>> conceptMarkups = Sections.findSuccessorsOfType(
				article.getRootSection(), ConceptMarkup.class);
		if (conceptMarkups.size() == 1) {
			Section<SimpleDefinition> def = Sections.findSuccessor(conceptMarkups.get(0),
					SimpleDefinition.class);
			Map<String, String> parameterMap = new HashMap<String, String>();

			// parameterMap.put(RenderingCore.GRAPH_SIZE,
			// OntoVisType.getAnnotation(section,
			// OntoVisType.ANNOTATION_SIZE));

			String format = "svg";
			parameterMap.put(RenderingCore.FORMAT, format);

			parameterMap.put(RenderingCore.CONCEPT, def.get().getTermName(def));

			parameterMap.put(RenderingCore.EXCLUDED_RELATIONS, "label");

			parameterMap.put(RenderingCore.EXCLUDED_NODES, "Resource");

			parameterMap.put(RenderingCore.GRAPH_SIZE, "690");

			parameterMap.put(RenderingCore.RANK_DIRECTION, "RL");

			parameterMap.put(RenderingCore.LINK_MODE, RenderingCore.LINK_MODE_BROWSE);

			parameterMap.put(RenderingCore.SHOW_OUTGOING_EDGES, "false");

			parameterMap.put(RenderingCore.SHOW_CLASSES, "false");

			parameterMap.put(RenderingCore.SHOW_SCROLLBAR, "false");

			String colorCodes = "";
			colorCodes += "kann: #009900;"; // green
			colorCodes += "muss: red;";
			colorCodes += "temporalBevor: #FFCC00;"; // dark yellow
			colorCodes += "assoziation: blue;";

			parameterMap.put(RenderingCore.RELATION_COLOR_CODES, colorCodes);

			//
			// String props = OntoVisType.getAnnotation(section,
			// OntoVisType.ANNOTATION_SHOWPROPERTIES);
			// parameterMap.put(RenderingCore.SHOW_PROPERTIES, props);

			parameterMap.put(RenderingCore.REQUESTED_DEPTH, "1");
			parameterMap.put(RenderingCore.REQUESTED_HEIGHT, "1");

			RenderingCore renderingCore = new RenderingCore(
					user.getServletContext().getRealPath(""), section, parameterMap);
			result.appendHtml("<div class='termgraph'>");
			result.appendHtml("<div class='termgraphHeader'>");
			result.appendHtml("<span style='float:left;' class='ui-icon ui-icon-circle-plus showGraph'></span>");
			result.appendHtml("<span style='float:left;display:none;' class='ui-icon ui-icon-circle-minus hideGraph'></span>");
			result.appendHtml("<div style=''>Übersicht über Begriffsverknüpfungen</div>");
			result.appendHtml("</div>");
			result.appendHtml("<div style='display:none;' class='termgraphcontent'>");
			renderingCore.render(result);
			result.appendHtml("</div>");
			result.appendHtml("</div>");
		}
		else {
			if ((conceptMarkups.size() > 1)) {
				// TODO: show warning
			}
		}
	}

	@Override
	public boolean isPre() {
		return false;
	}

}
