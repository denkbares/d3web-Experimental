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

import de.knowwe.compile.utils.IncrementalCompilerLinkToTermDefinitionProvider;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.append.PageAppendHandler;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.vis.OntoGraphDataBuilder;
import de.knowwe.termbrowser.TermSetManager;
import de.knowwe.visualization.GraphDataBuilder;
import de.knowwe.wisskont.ConceptMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 29.11.2012
 */
public class ShowConceptRelationsAppendHandler implements PageAppendHandler {

	@Override
	public void append(String web, String title, UserContext user, RenderResult result) {
		ArticleManager articleManager = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);
		Article article = articleManager.getArticle(title);
		Section<?> section = article.getRootSection();
		List<Section<ConceptMarkup>> conceptMarkups = Sections.findSuccessorsOfType(
				article.getRootSection(), ConceptMarkup.class);
		if (conceptMarkups.size() == 1) {
			Section<SimpleDefinition> def = Sections.findSuccessor(conceptMarkups.get(0),
					SimpleDefinition.class);
			Map<String, String> parameterMap = new HashMap<String, String>();

			// parameterMap.put(OntoGraphDataBuilder.GRAPH_SIZE,
			// OntoVisType.getAnnotation(section,
			// OntoVisType.ANNOTATION_SIZE));

			String format = "svg";
			parameterMap.put(OntoGraphDataBuilder.FORMAT, format);

			parameterMap.put(OntoGraphDataBuilder.CONCEPT, def.get().getTermName(def));

			parameterMap.put(OntoGraphDataBuilder.EXCLUDED_RELATIONS, "label");

			parameterMap.put(OntoGraphDataBuilder.EXCLUDED_NODES, "rdfs:Resource,WissassConcept");

			parameterMap.put(OntoGraphDataBuilder.GRAPH_SIZE, "690");

			parameterMap.put(OntoGraphDataBuilder.RANK_DIRECTION, "RL");

			parameterMap.put(OntoGraphDataBuilder.LINK_MODE, OntoGraphDataBuilder.LINK_MODE_BROWSE);

			parameterMap.put(OntoGraphDataBuilder.SHOW_OUTGOING_EDGES, "false");

			parameterMap.put(OntoGraphDataBuilder.SHOW_CLASSES, "false");

			parameterMap.put(OntoGraphDataBuilder.SHOW_SCROLLBAR, "false");

			// which kind of visualization ???
			// parameterMap.put(OntoGraphDataBuilder.RENDERER,
			// OntoVisType.Renderer.d3.name());
			// parameterMap.put(OntoGraphDataBuilder.VISUALIZATION,
			// OntoVisType.Visualizations.force.name());

			parameterMap.put(OntoGraphDataBuilder.RENDERER, GraphDataBuilder.Renderer.dot.name());

			String colorCodes = "";
			colorCodes += "kann: #009900;"; // green
			colorCodes += "muss: red;";
			colorCodes += "temporalGraph: #FFCC00;"; // dark yellow
			colorCodes += "assoziation: blue;";
			colorCodes += "cave: purple";

			parameterMap.put(OntoGraphDataBuilder.RELATION_COLOR_CODES, colorCodes);

			boolean collapsed = TermSetManager.getInstance().graphIsCollapsed(user);
			String stylePlus = "";
			String styleMinus = "";
			String styleContent = "";
			if (collapsed) {
				styleContent = "display:none;";
				styleMinus = "display:none;";
			}
			else {
				stylePlus = "display:none;";
			}

			parameterMap.put(OntoGraphDataBuilder.REQUESTED_DEPTH, "1");
			parameterMap.put(OntoGraphDataBuilder.REQUESTED_HEIGHT, "1");
			OntoGraphDataBuilder OntoGraphDataBuilder = new OntoGraphDataBuilder(
					user.getServletContext().getRealPath(""), section, parameterMap,
					new IncrementalCompilerLinkToTermDefinitionProvider(), Rdf2GoCore.getInstance());
			result.appendHtml("<div class='termgraph'>");
			result.appendHtml("<div class='termgraphHeader'>");
			result.appendHtml("<span style='float:left;" + stylePlus
					+ "' class='ui-icon ui-icon-triangle-1-e showGraph'></span>");
			result.appendHtml("<span style='float:left;" + styleMinus
					+ "' class='ui-icon ui-icon-triangle-1-s hideGraph'></span>");
			result.appendHtml("<div class='toggleGraph' style='font-weight:bold;'>Übersicht über Begriffsverknüpfungen</div>");
			result.appendHtml("</div>");
			result.appendHtml("<div style='" + styleContent + "' class='termgraphcontent'>");
			OntoGraphDataBuilder.render(result);
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
