/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.diaflux.coverageCity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.diaflux.FlowchartUtils;
import de.knowwe.diaflux.type.FlowchartType;

/**
 * 
 * @author Reinhard Hatko
 * @created 14.03.2013
 */
public class CoverageCityRenderer implements Renderer {

	@Override
	public void render(Section<?> section, UserContext user, RenderResult bob) {

		String id = section.getID();
		bob.appendHtml("<div class=\"diafluxCity\">");
		bob.appendHtml("<script type=\"text/javascript\" src=\"KnowWEExtension/scripts/city.js\"></script>");
		bob.appendHtml("<script type=\"text/javascript\" src=\"KnowWEExtension/scripts/scenejs.js\"></script>");
		bob.appendHtml("<script type=\"text/javascript\" src=\"KnowWEExtension/scripts/d3webviz.js\"></script>");
		// bob.appendHtml("<link href=\"KnowWEExtension/css/d3webviz.css\" type=\"text/css\" rel=\"stylesheet\">");
		bob.appendHtml("<canvas id=\"diafluxCity" + id + "\" width=\"1000\" height=\"900\">");
		bob.appendHtml("</canvas>\n");

		bob.appendHtml("<script type=\"text/javascript\">KNOWWE.d3webViz.createDiaFluxCity(\"" + id
				+ "\", 'CoverageCityAction', KNOWWE.d3webViz.coveragePick);</script>");
		bob.appendHtml("<div id='pickresult" + id + "'></div>\n");
		bob.appendHtml("<div id='nodeId" + id + "'></div>\n");

		// TODO error handling
		Set<String> articles = getCompilingArticles(section);

		String art = articles.iterator().next();
		KnowledgeBase base = D3webUtils.getKnowledgeBase(section.getWeb(), art);

		List<StartNode> nodes = DiaFluxUtils.getAutostartNodes(base);

		if (nodes.isEmpty()) {

			bob.appendHtml("No Autostart node found.");
			bob.appendHtml("</div>\n");
			return;
		}
		Section<FlowchartType> flow = FlowchartUtils.findFlowchartSection(section.getWeb(),
				nodes.get(0).getFlow().getName());

		bob.append(FlowchartUtils.prepareFlowchartRenderer(user, "flowDisplay" + id, flow.getID(),
				PathCoverageHighlight.COVERAGE_CITY_SCOPE, true));

		bob.appendHtml("</div>\n");

	}

	private static Set<String> getCompilingArticles(Section<?> section) {
		Set<String> compilingArticles = new HashSet<String>();
		PackageManager packageManager = KnowWEUtils.getPackageManager(section.getArticle().getWeb());
		if (section != null) {
			for (String packageName : section.getPackageNames()) {
				compilingArticles.addAll(packageManager.getCompilingArticles(packageName));
			}
		}
		if (compilingArticles.isEmpty()) {
			compilingArticles.addAll(packageManager.getCompilingArticles());
		}
		return compilingArticles;
	}

}
