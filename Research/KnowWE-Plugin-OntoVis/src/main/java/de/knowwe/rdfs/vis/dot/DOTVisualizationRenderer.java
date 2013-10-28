/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.rdfs.vis.dot;

import java.util.Map;

import de.knowwe.rdfs.vis.GraphVisualizationRenderer;
import de.knowwe.rdfs.vis.RenderingCore;
import de.knowwe.rdfs.vis.SubGraphData;
import de.knowwe.rdfs.vis.util.FileUtils;

/**
 * A GraphVisualizationRenderer using DOT/GraphViz for the visualization of the
 * SubGraphData.
 * 
 * @author jochenreutelshofer
 * @created 27.05.2013
 */
public class DOTVisualizationRenderer implements GraphVisualizationRenderer {

	private final SubGraphData data;
	private final Map<String, String> parameters;
	private String source = null;

	/**
	 * 
	 */
	public DOTVisualizationRenderer(SubGraphData data, Map<String, String> parameters) {
		this.data = data;
		this.parameters = parameters;
	}

	@Override
	public String generateSource() {
		source = DotRenderer.createDotSources(data, parameters);
		DotRenderer.createAndwriteDOTFiles(parameters.get(RenderingCore.SECTION_ID), source,
				parameters.get(RenderingCore.REAL_PATH),
				parameters.get(RenderingCore.DOT_APP));
		return source;
	}

	@Override
	public String getHTMLIncludeSnipplet() {
		return createHTMLOutput();
	}

	@Override
	public String getSource() {
		if (source == null) {
			return generateSource();
		}
		return source;
	}

	/**
	 * 
	 * @created 03.09.2012
	 * @param StringBuilder
	 */
	private String createHTMLOutput() {
		StringBuffer html = new StringBuffer();
		String style = "max-height:1000px; ";
		if (parameters.get(RenderingCore.SHOW_SCROLLBAR) != null
				&& parameters.get(RenderingCore.SHOW_SCROLLBAR).equals("false")) {
			// no scroll-bars
		}
		else {
			style += "overflow:scroll";
		}
		String div_open = "<div style=\"" + style + "\">";
		String div_close = "</div>";
		String sectionID = parameters.get(RenderingCore.SECTION_ID);
		String tmpPath = FileUtils.KNOWWEEXTENSION_FOLDER + FileUtils.TOMCAT_PATH_SEPARATOR
				+ FileUtils.TMP_FOLDER
				+ FileUtils.TOMCAT_PATH_SEPARATOR;

		String png_default = div_open + "<img alt='graph' src='"
				+ tmpPath + "graph" + sectionID + ".png'>" + div_close;
		String svg = div_open + "<object data='" + tmpPath
				+ "graph" + sectionID + ".svg' type=\"image/svg+xml\">" + png_default
				+ "</object>" + div_close;
		String format = parameters.get(RenderingCore.FORMAT);
		if (format == null) {
			html.append(png_default);
		}
		else if (format.equals("svg")) {
			html.append(svg);
		}
		else {
			html.append(png_default);
		}
		return html.toString();
	}

}
