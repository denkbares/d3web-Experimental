/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.ophtovisD3;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.rendering.RenderResult;

/**
 * @author adm_rieder
 * @created 07.11.2012
 */
public class Visualization {

	public static void visualise(String concept, RenderResult result) {
		appendHtmlWrap("<script> createKnoten() </script>", result);
	}

	public static void visualiseWheel(String concept, RenderResult result) {
		appendHtmlWrap(" <script> createWheel() </script>", result);
	}

	public static void visualiseTree(String concept, RenderResult result) {
		appendHtmlWrap(" <script> createTree() </script>", result);
	}

	public static void visualiseForce(String concept, RenderResult result) {
		appendHtmlWrap(" <script> createForce() </script>", result);
	}

	public static void visualiseBubble(String concept, RenderResult result) {
		appendHtmlWrap(" <script> createBubble('" + concept + "') </script>", result);

	}

	/**
	 * Adds some of the Basic Websitefeatures with are common in all the Graphvisualisations
	 */

	private static void appendHtmlWrap(String jsCommands, RenderResult result) {
		String context = Environment.getInstance().getWikiConnector().getServletContext().getContextPath();
		result.appendHtml(
				"	<link rel=\"stylesheet\" href=\""
						+ context
						+ "/KnowWEExtension/css/visualisation.css\">"
						+
						// base style
						"	<link rel=\"stylesheet\" href=\""
						+ context
						+ "/KnowWEExtension/css/base_style_vis.css\">"
						+
						// "	<link rel=\"stylesheet\" href=\""
						// + context
						// + "/KnowWEExtension/css/toolbar.css\">"
						// +
						// visCollapsableTree
						// base style
						"	<link rel=\"stylesheet\" href=\""
						+ context
						+ "/KnowWEExtension/css/scoreStyle.css\">"
						+
						"	<link rel=\"stylesheet\" href=\""
						+ context
						+ "/KnowWEExtension/css/visCollapsableTreeStyle.css\">"
						//
						+
						// new navigation menu css
						"	<link rel=\"stylesheet\" href=\""
						+ context
						+ "/KnowWEExtension/css/nav_menu.css\">"
						//
						+
						// breadcrumbs.css
						"	<link rel=\"stylesheet\" href=\""
						+ context
						+ "/KnowWEExtension/css/breadcrumbs.css\">"
						//
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/d3.js \"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/searchbarVis.js \"></script>\r\n"
						+
						// Breadcrumbs import
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/breadcrumb.js \"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/d3Config.js \"></script>\r\n"
						+
						// visCollapsableTree
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/visCollapsableTree.js \"></script>\r\n"
						+
						//
						//
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/jquery-1.9.1.js \"></script>\r\n" +
						"<script type='text/javascript' src='KnowWEExtension/scripts/jquery-compatibility.js'></script>"
						+
						"<script type='text/javascript' src='scripts/mootools.js'></script>"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/OphtoToolbar.js \"></script>\r\n"
						+
						// new menu javascript
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/nav_menu_jquery.js \"></script>\r\n"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-helper.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-notification.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/quicki.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/correction.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-Rdf2GoSemanticCore.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-d3web-basic.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-CI4KE.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/loadQuicki.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/saveQuicki.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-Core.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/DefaultTableEditTool.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/DefaultEditTool.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/TextArea.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-EditCommons.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/TableEditTool.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/restoreActionScript.js'></script>"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/jquery-ui.js \"></script>\r\n" +
						//
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/infobox.js \"></script>\r\n"
						+
						//
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/jquery-1.9.1.js \"></script>\r\n" +
						// old menu
						// "<div id=headerwrap class=fixed>\r\n" +
						// "<div id=header>\r\n" +
						// "<ul id=\"entry-list\" class=\"list\">\r\n" +
						// "<li class=\"active menue\" >Visualisierungstypen :   </li>\r\n"
						// +
						// "<li class=\"active menue\" onclick=\"createBubble()\">Bubble </li>\r\n"
						// +
						// "<li class=\"menue\" onclick=\"createWheel()\">Wheel </li>\r\n"
						// +
						// "<li class=\"menue\" onclick=\"createReingold()\"> ReingoldTree </li>\r\n"
						// +
						// "<li class=\"menue\" onclick=\"createReingoldCollapsible()\"> ReingoldColl </li>\r\n"
						// +
						// //
						// "<li class=\"menue\" onclick=\"createTreeDiagonal()\"> Tree diagonal </li>\r\n"
						// // +
						// "<li class=\"menue\" onclick=\"createTreeCollapsable()\"> Tree Collapsable </li>\r\n"
						// +
						// "<li class=\"menue\" onclick=\"createCollForce()\"> ColForceLabeled </li>\r\n"
						// +
						// "<li class=\"menue\" onclick=\"createTreeDiagonal()\"> DiagonalTree </li>\r\n"
						// +
						// "<li class=\"menue\" onclick=\"createFixRootTree()\"> FixedRootTree </li>\r\n"
						// +
						// "<li class=\"editor\">Editor▼</li>\r\n"
						// +
						// "</ul>\r\n" +
						// "</div>\r\n" +
						// "</div>\r\n" +
						// old menu
						// main container start
						"<div id=\"container\">"
						+ "<div id=\"left-container\">"
						+ "</div>"
						+ "<div id=\"center-container\">"
						+ // Breadcrumbs
						// "<div id=\"breadcrumb\" class=\"breadcrumbs\">"
						// +
						"<div id=\"crumb-container\" class=\"cr-cont\">"
						+
						"<div id=\"breadcrumb\" class=\"crumbs\">"
						+ "<a href=\"#home\"> </a>"
						+ "</div>"
						+ "</div>"// breadcrumbs end
						// -------------DISCO div with d3js MAGIC
						+
						"<div id=\"vis\"></div>"
						+ "</div>"
						+ "<div id=\"right-container\">"
						+ "</div>"
						// new navigation menu
						// Navigation Bar div
						+ "<div id=\"cssmenu\">"// -------------Navigation
						+
						"<ul>"// ul main
						+
						"<li class=\"active\"><a href=\"index.html\"><span>Visualisierungen</span></a></li>"// li
						+
						"<li class=\"has-sub\" onclick=\"createBubble()\"><a href=\"#\"><span>Circle Packing</span></a></li>"
						+
						"<li class=\"has-sub\" onclick=\"createWheel()\"><a href=\"#\"><span>Radial Treemap</span></a></li>"
						+
						"<li class=\"has-sub\" onclick=\"createReingold()\"><a href=\"#\"><span>Reingold-Tilford Tree</span></a></li>"
						+
						"<li class=\"has-sub\" onclick=\"createTreeCollapsable()\"><a href=\"#\"><span>Collapsible Tree</span></a></li>"
						// +
						// "<li class=\"has-sub\" onclick=\"createCollForce()\"><a href=\"#\"><span>Force Graph Labeled</span></a></li>"
						+
						"<li class=\"has-sub\"><a href=\"#\"><span>Experimental I</span></a>"
						+ "<ul>"
						 +
						"<li onclick=\"createPerformanceTree()\"><a href=\"#\"><span>Performance Tree</span></a></li>"//
						+
						// "<li onclick=\"createTreeDiagonal()\"><a href=\"#\"><span>Experiment TreeDiag</span></a></li>"//
						// li
						// +
						// "<li onclick=\"createCollForceTest()\"><a href=\"#\"><span>Coll Force Test</span></a></li>"//
						// li
						// +
						// "<li class=\"last\"><a href=\"#\"><span>Experiment 3</span></a></li>"//
						// li
						// +
						// "<li class=\"last\" onclick=\"createCollForceTest()\"><a href=\"#\"><span>Experiment JIT</span></a></li>"//
						// li
						// + "</ul>"
						// +
						// "<li class=\"has-sub\"><a href=\"#\"><span>Experimental II</span></a>"
						// + "<ul>"
						// +
						// "<li><a href=\"#\"><span>Experiment 1</span></a></li>"//
						// li
						// +
						// "<li><a href=\"#\"><span>Experiment 2</span></a></li>"//
						// li
						// +
						// "<li class=\"last\"><a href=\"#\"><span>Experiment 3</span></a></li>"//
						// li
						"</ul>"
						+ "<li class=\"has-sub\"><a href=\"javascript:history.back()\"><span>Back to WIKI</span></a></li>"
						+
						"</ul>"// ul main
						// searchbar start
						+
						"<div id=\"tfheader\">\r\n"
						+
						"<form id=\"tfnewsearch\">\r\n"
						+
						"<input type=\"text\" id=\"tfq2b\" class=\"tftextinput2\" name=\"q\" size=\"21\" maxlength=\"120\" value=\"Search\" onkeyup=\"searchInVis(this.value)\" onclick=\"this.value=''\"> \r\n"
						+
						"</form>\r\n"
						+
						"<div class=\"tfclear\"></div>\r\n"
						+
						"	</div>"
						// + "<div class=\"items\">"
						// + "<ul>"
						// +
						// "<li onclick=\"updateBreadcrumbs\"><a href=\"#test1\">Test 1</a></li>"
						// + "<li><a href=\"#test2\">Test 2</a></li>"
						// + "</ul>"
						// + "</div>"// items end
						+ "</div>"// -------------Navigation end
						// new navigation menu
						// "<div class=\"editorDiv hidden\">\r\n" +
						// "<div class=\"dropzone\" id=\"source\" >1</div>\r\n"
						// +
						// "\r\n" +
						// "<div class=\"choosa\">\r\n" +
						// "<select id=\"relSelect\" data-placeholder=\"Relation...\" class=\"chzn-select\" style=\"width:150px\"  tabindex=\"-1\">\r\n"
						// +
						// " <!--clear value option-->\r\n" +
						// " <option value=\"\"></option>\r\n" +
						// "  <option value=\"unterkonzept\">unterkonzept</option>\r\n"
						// +
						// "   <option value=\"\">kann</option>\r\n" +
						// "    <option value=\"\">muss</option>\r\n" +
						// "</select>\r\n" +
						// "</div>\r\n" +
						// "\r\n" +
						// "\r\n" +
						// "\r\n" +
						// "<div class=\"dropzone\" id=\"target\" >2</div>\r\n"
						// +
						// "\r\n" +
						// "\r\n" +
						// "<div class=\"buttonBar\" >\r\n" +
						// "<input type=\"button\" value=\"OK\" ></input>\r\n" +
						// "<input type=\"button\" value=\"DEL\"></input>\r\n" +
						// "</div>"+
						// "</div>"
						// +
						// //
						// "<div class=\"hidden\" id=\"infolist\" ></div>\r\n"
						// +
						// main container end
						+
						"</div>"
						+
						jsCommands
		);
	}

}
