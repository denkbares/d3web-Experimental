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
package de.knowwe.wisskont.browser;

import java.util.Collection;
import java.util.List;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.wisskont.util.Tree;
import de.knowwe.wisskont.util.Tree.Node;

/**
 * 
 * @author jochenreutelshofer
 * @created 10.12.2012
 */
public class TermBrowserRenderUtils {

	public static final int THRESHOLD_MAX_TERM_NUMBER = 20;
	private static boolean zebra = false;

	public static String renderTermBrowser(UserContext user, String searchFieldContent) {
		RenderResult string = new RenderResult(user);
		string.appendHtml("<div class='termbrowserframe'>");
		string.appendHtml("<div class='termbrowserheader'>Benutzte Begriffe:</div>");
		string.appendHtml("<div class='ui-widget'><table><tr><td><label for='conceptSearch' style='font-weight:normal;padding-right:0;font: 83%/140% Verdana,Arial,Helvetica,sans-serif;;'>Suche: </label></td><td><input id='conceptSearch' size='15' value='' /></td></tr></table></div>");
		string.appendHtml("<script>" +
				"jq$(document).ready(function() {" +
				// "$(function() {" +
				" var availableTags = [" +
				generateTermnames() +
				"];" +
				"jq$( \"#conceptSearch\" ).autocomplete({" +
				"source: availableTags," +
				"select: function( event, ui ) {" +
				"updateTermBrowser(event,ui);" +
				"}," +
				"});" +
				"});" +
				"</script>");

		Tree<RatedTerm> ratedTermTreeTop = TermRecommender.getInstance().getRatedTermTreeTop(user,
				THRESHOLD_MAX_TERM_NUMBER);

		renderTermTree(string, ratedTermTreeTop);
		string.appendHtml("</div>");
		string.appendHtml("</div>");
		return string.toStringRaw();
	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param string
	 * @param subList
	 */
	private static void renderTermTree(RenderResult string, Tree<RatedTerm> tree) {
		string.appendHtml("<div class='termlist'>");

		Node<RatedTerm> root = tree.getRoot();
		List<Node<RatedTerm>> roots = root.getChildrenSorted();
		for (Node<RatedTerm> rootConcept : roots) {

			renderConceptSubTree(rootConcept, 0, string);
		}

	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param rootConcept
	 */
	private static void renderConceptSubTree(Node<RatedTerm> rootConcept, int level, RenderResult string) {
		if (!rootConcept.getData().equals(RatedTerm.ROOT)) {
			renderConcept(rootConcept.getData(), level, string);
		}
		level += 1;
		List<Node<RatedTerm>> childrenSorted = rootConcept.getChildrenSorted();
		for (Node<RatedTerm> node : childrenSorted) {
			renderConceptSubTree(node, level, string);
		}

	}

	private static void renderConcept(RatedTerm t, int depth, RenderResult string) {
		String term = t.getTerm();
		String topic = term;
		Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				new Identifier(term));
		if (termDefinitions.size() > 0) {
			Section<? extends SimpleDefinition> def = termDefinitions.iterator().next();
			topic = def.getTitle();
		}

		String lineStyleClass = "zebraline";
		if (!zebra) {
			zebra = true;
		}
		else {
			lineStyleClass = "zebraline-white";
			zebra = false;
		}

		String baseUrl = Rdf2GoCore.getInstance().getLocalNamespace();
		String name = Strings.encodeURL(topic);
		String url = baseUrl + name;
		// String divStyle = "display:inline; float:left;";
		String divStyle = "";
		string.appendHtml("<div id='draggable' style='"
				+ "'  class='termline "
				+ lineStyleClass
				+ "'>"
				+
				"<table style='table-layout:fixed"
				+ "'>"
				+
				// "<col width='80'/><col width='16'/><col width='16'/><col width='16'/>"+
				"<tr height='23px'>"
				+
				createDashes(depth)
				+
				"<td style='width:80%' class='termbrowser'>"
				+ "<div class='termname' style='display:inline;"
				+ createStyle(depth)
				+ "'>"
				+ term.replaceAll("_", "_<wbr>")
				+ "</div></td>"
				+ "<td style='min-width: 48px;width:20%;'>"
				+ "<table style='table-layout:fixed'><tr>"
				+ "<td style='"
				+ divStyle
				+ "' class='termbrowser'><a href='"
				+ url
				+ "'><span class='ui-icon ui-icon-arrowreturnthick-1-e openConcept' title='Seite zu diesem Konzept öffnen' style='display:none;'></span></a></td>"
				+
				"<td style='"
				+ divStyle
				+ "' class='termbrowser'><span class='ui-icon ui-icon-circle-close removeConcept' title='Konzept aus dieser Liste herausnehmen' style='display:none;'></span></td>"
				+
				"<td style='"
				+ divStyle
				+ "' class='termbrowser'><span class='ui-icon ui-icon-arrow-4-diag expandConcept' title='Unterkonzepte in diese Liste aufnehmen' style='display:none;'></span></td>"
				+
				"</tr></table></td></tr></table></div>");
	}

	/**
	 * 
	 * @created 15.04.2013
	 * @param depth
	 * @return
	 */
	private static String createStyle(int depth) {

		String result = "";

		if (depth == 0) {
			result += "font-weight:bold;";
		}
		if (depth == 1) {
			result += "font-size:100%;";
		}
		if (depth == 2) {
			result += "font-size:80%;";
		}
		if (depth == 3) {
			result += "font-size:60%;";
		}
		if (depth == 4) {
			result += "font-size:50%;";
		}

		return result;
	}

	private static String createDashes(int count) {
		int width = count * 15;
		String padding = "padding:0.01em;display:block;float:left;min-width:" + width + "px;";

		String result = "<td style='" + padding + "'></td>";

		return result;
	}

	/**
	 * 
	 * @created 10.12.2012
	 * @return
	 */
	private static String generateTermnames() {
		String result = "";
		Collection<Section<? extends SimpleDefinition>> allTermDefinitions = IncrementalCompiler.getInstance().getTerminology().getAllTermDefinitions();

		for (Section<? extends SimpleDefinition> def : allTermDefinitions) {
			result += "\"" + def.get().getTermName(def) + "\"" + ",\n";
		}

		return result;
	}
}
