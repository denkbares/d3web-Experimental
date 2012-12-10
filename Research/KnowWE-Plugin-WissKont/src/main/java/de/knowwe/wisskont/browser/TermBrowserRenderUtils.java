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
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;

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
		string.append(Strings.maskHTML("<div class='ui-widget'><table><tr><td><label for='conceptSearch' style='font-weight:normal;padding-right:0;font: 83%/140% Verdana,Arial,Helvetica,sans-serif;;'>Suche: </label></td><td><input id='conceptSearch' size='15' value='"
				+ searchFieldContent + "' /></td></tr></table></div>"));
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
		for (int i = 0; i < 10; i++) {
			if (i >= rankedTermList.size()) break;
			String term = rankedTermList.get(i);
			string.append(Strings.maskHTML("<div id='draggable' class='termline'><div class='termname'>"
					+ term + "</div></div>"));
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
