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

package de.d3web.we.ci4ke.handling;

import de.d3web.we.ci4ke.build.CIBuildPersistenceHandler;
import de.d3web.we.ci4ke.build.CIBuilder.CIBuildTriggers;
import de.d3web.we.core.KnowWERessourceLoader;
import de.d3web.we.kdom.Section;
import de.d3web.we.utils.KnowWEUtils;

public class CIDashboard {

	private final CIConfig config;

	private final CIBuildPersistenceHandler persistenceHandler;

	public CIDashboard(Section<CIDashboardType> section) {
		this.config = (CIConfig) KnowWEUtils.getStoredObject(section,
				CIConfig.CICONFIG_STORE_KEY);
		persistenceHandler = new CIBuildPersistenceHandler(
				this.config.getDashboardID());
	}

	public String render() {

		KnowWERessourceLoader.getInstance().add("CI4KE.css",
				KnowWERessourceLoader.RESOURCE_STYLESHEET);
		KnowWERessourceLoader.getInstance().add("CIPlugin.js",
				KnowWERessourceLoader.RESOURCE_SCRIPT);

		StringBuffer html = new StringBuffer();

		html.append("<div id='" + config.getDashboardID() + "-ci-dashboard' class='panel'>");
		html.append(renderDashboardContents());
		html.append("</div>");

		return html.toString();
	}

	public String renderDashboardContents() {

		StringBuffer html = new StringBuffer();

		html.append("<h3>");
		html.append(persistenceHandler.renderCurrentBuildStatus() + "&nbsp;&nbsp;&nbsp;&nbsp;");
		html.append(persistenceHandler.renderBuildHealthReport() + "&nbsp;&nbsp;&nbsp;&nbsp;");
		html.append(config.getMonitoredArticleTitle());

		html.append("</h3>\n");

		html.append("<div id='ci-content-wrapper'>");// Main content wrapper

		// Left Column: Lists all the knowledge-base Builds of the targeted
		// article
		html.append("<div id='" + config.getDashboardID() + "-column-left' class='ci-column-left'>");

		html.append("<h3 style=\"background-color: #CCCCCC;\">Build History");

		if (config.getTrigger().equals(CIBuildTriggers.onDemand)) {
			html.append("<a href=\"#\" onclick=\"fctExecuteNewBuild('" + config.getDashboardID()
					+ "');\"");
			html.append("<img border=\"0\" align=\"right\" src='KnowWEExtension/ci4ke/images/22x22/clock.png' "
					+ "alt='Schedule a build' title='Schedule a build'></a>");
		}
		html.append("</h3>");
		// render Builds

		html.append("<div id=\"" + config.getDashboardID() + "-build-table\">\n");
		html.append(persistenceHandler.renderNewestBuilds(5));
		html.append("</div>");

		html.append("</div>");

		html.append("<div id='" + config.getDashboardID()
				+ "-build-details-wrapper' class='ci-build-details-wrapper'>");

		html.append(CIDashboardType.renderBuildDetails(this.config.getDashboardID(),
				persistenceHandler.getCurrentBuildNumber()));

		html.append("</div></div>");

		return html.toString();
	}
}
