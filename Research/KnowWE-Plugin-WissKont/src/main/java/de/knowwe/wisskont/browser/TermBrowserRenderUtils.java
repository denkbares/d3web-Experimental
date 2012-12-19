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

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * 
 * @author jochenreutelshofer
 * @created 10.12.2012
 */
public class TermBrowserRenderUtils {

	public static String renderTermBrowser(UserContext user, String searchFieldContent) {
		StringBuilder string = new StringBuilder();
		string.append(Strings.maskHTML("<div class='termbrowserframe'>"));
		string.append(Strings.maskHTML("<div class='termbrowserheader'>Konzepte:</div>"));
		string.append(Strings.maskHTML("<div class='ui-widget'><table><tr><td><label for='conceptSearch' style='font-weight:normal;padding-right:0;font: 83%/140% Verdana,Arial,Helvetica,sans-serif;;'>Suche: </label></td><td><input id='conceptSearch' size='15' value='' /></td></tr></table></div>"));
		string.append(Strings.maskHTML("<script>" +
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
				"</script>"));
		List<String> rankedTermList = TermRecommender.getInstance().getRankedTermList(user);

		string.append(Strings.maskHTML("<div class='termlist'>"));
		boolean zebra = false;
		for (int i = 0; i < 10; i++) {

			if (i >= rankedTermList.size()) break;
			String term = rankedTermList.get(i);
			String topic = term;
			Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
					new TermIdentifier(term));
			if (termDefinitions.size() > 0) {
				Section<? extends SimpleDefinition> def = termDefinitions.iterator().next();
				topic = def.getTitle();
			}

			String lineStyle = "";
			if (!zebra) {
				zebra = true;
			}
			else {
				lineStyle += "background-color:white;";
				zebra = false;
			}
			String baseUrl = Rdf2GoCore.getInstance().getLocalNamespace();
			String name = Strings.encodeURL(topic);
			String url = baseUrl + name;
			// String divStyle = "display:inline; float:left;";
			String divStyle = "";
			string.append(Strings.maskHTML("<div id='draggable' style='"
					+ lineStyle
					+ "'  class='termline'>"
					+
					"<table style='table-layout:fixed'>"
					+
					// "<col width='80'/><col width='16'/><col width='16'/><col width='16'/>"+
					"<tr height='23px'>"
					+
					"<td style='width:80%' class='termbrowser'><div class='termname'>"
					+ term.replaceAll("_", Strings.maskHTML("_<wbr>"))
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
					"</tr></table></td></tr></table></div>"));
		}

		string.append(Strings.maskHTML("</div>"));
		string.append(Strings.maskHTML("</div>"));

		return string.toString();
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
