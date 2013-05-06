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
 * 
 * @author adm_rieder
 * @created 07.11.2012
 */
public class Visualization {

					   
	public static void visualise(String concept, RenderResult result){
			appendHtmlWrap("<script> createKnoten() </script>", result);
	}
	public static void visualiseWheel(String concept, RenderResult result){
		appendHtmlWrap(" <script> createWheel() </script>", result);
}
	public static void visualiseTree(String concept, RenderResult result){
		appendHtmlWrap(" <script> createTree() </script>", result);
}
	public static void visualiseForce(String concept, RenderResult result){
		appendHtmlWrap(" <script> createForce() </script>", result);
	}

	public static void visualiseBubble(String concept, RenderResult result){
				appendHtmlWrap(" <script> createBubble() </script>", result);
		
	}
	/**
	 * Adds some of the Basic Websitefeatures with are common in all the
	 * Graphvisualisations
	 * 
	 */
	
	public static void appendHtmlWrap(String jsCommands, RenderResult result) {
		String context = Environment.getInstance().getWikiConnector().getServletContext().getContextPath();
		result.appendHtml(
				"	<link rel=\"stylesheet\" href=\""
						+ context
						+ "/KnowWEExtension/css/visualisation.css\">"
						+
						"	<link rel=\"stylesheet\" href=\""
						+ context
						+ "/KnowWEExtension/css/toolbar.css\">"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/d3.js \"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/d3Config.js \"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/jquery-1.9.1.js \"></script>\r\n"+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/OphtoToolbar.js \"></script>\r\n"+
						"<script type='text/javascript' src='KnowWEExtension/scripts/jquery-autosize.min.js'>"
						+
						"</script></script><script type='text/javascript' src='KnowWEExtension/scripts/jquery-compatibility.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-helper.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-notification.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/quicki.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/overviewGraph.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/correction.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/drag.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/jquery.treeTable.js'></script>"
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
						"<script type='text/javascript' src='KnowWEExtension/scripts/toolsMenuDecorator.js'></script>"
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
						+ "/KnowWEExtension/scripts/jquery-ui.js \"></script>\r\n"+
						"<div id=headerwrap class=fixed>\r\n" + 
						
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/scripts/jquery-1.9.1.js \"></script>\r\n"+
					
						"<div id=header>\r\n" + 
						"<ul id=\"entry-list\" class=\"list\">\r\n" + 
						"<li class=\"active menue\">Bubble </li>\r\n" + 
						"<li class=\"menue\">Wheel </li>\r\n" + 
						"<li class=\"menue\"> Tree </li>\r\n" + 
						"<li class=\"editor\">Editor▼</li>\r\n" + 
						"</ul>\r\n" + 
						"</div>\r\n" + 
						"<div class=\"editorDiv hidden\">\r\n" + 
						"<div class=\"dropzone\" id=\"source\" >1</div>\r\n" + 
						"\r\n" + 
						"<div class=\"choosa\">\r\n" + 
						"<select id=\"relSelect\" data-placeholder=\"Relation...\" class=\"chzn-select\" style=\"width:150px\"  tabindex=\"-1\">\r\n" + 
						" <!--clear value option-->\r\n" + 
						" <option value=\"\"></option>\r\n" + 
						"  <option value=\"unterkonzept\">unterkonzept</option>\r\n" + 
						"   <option value=\"\">kann</option>\r\n" + 
						"    <option value=\"\">muss</option>\r\n" + 
						"</select>\r\n" + 
						"</div>\r\n" + 
						"\r\n" + 
						"\r\n" + 
						"\r\n" + 
						"<div class=\"dropzone\" id=\"target\" >2</div>\r\n" + 
						"\r\n" + 
						"\r\n" + 
						"<div class=\"buttonBar\" >\r\n" + 
						"<input type=\"button\" value=\"OK\" ></input>\r\n" + 
						"<input type=\"button\" value=\"DEL\"></input>\r\n" + 
						"</div>"+
						"</div>"
						+
						"<div id =vis></div>"
						+
					
						jsCommands
				
						
						
				);
	}

	
	
	

	
}
