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
package de.knowwe.termbrowser;

import java.util.Collection;
import java.util.List;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.termbrowser.util.Tree;
import de.knowwe.termbrowser.util.Tree.Node;

/**
 * 
 * @author jochenreutelshofer
 * @created 10.12.2012
 */
public class TermBrowserRender {

	public static final int THRESHOLD_MAX_TERM_NUMBER = 15;
	private static boolean zebra = false;

	private final UserContext user;
	private final de.knowwe.core.utils.LinkToTermDefinitionProvider linkProvider;
	private final String master;
	private TermBrowserHierarchy hierarchy = null;

	/**
	 * 
	 */
	public TermBrowserRender(UserContext user, de.knowwe.core.utils.LinkToTermDefinitionProvider linkProvider, String master) {
		this.user = user;
		this.linkProvider = linkProvider;
		this.master = master;
		RecommendationSet recommendationSet = TermRecommender.getInstance().getRecommendationSet(
				user);
		if (recommendationSet != null) {
			hierarchy = recommendationSet.getHierarchy();
		}
	}

	public String renderTermBrowser() {
		RenderResult string = new RenderResult(user);
		string.appendHtml("<div class='termbrowserframe'>");

		{
			string.appendHtml("<div class='termbrowserheader'>");

			// show collapse button, headline and clear button
			String toolTipCollapse = "Liste der Begriffe verbergen";
			String toolTipOpen = "Liste der Begriffe aufklappen";
			String toolTipClear = "Liste der Begriffe leeren";
			boolean collapsed = TermRecommender.getInstance().listIsCollapsed(user);
			String minusStyle = "";
			String plusStyle = "";
			String clearStyle = "float:right;";
			if (collapsed) {
				minusStyle = "display:none;";
				clearStyle += "display:none;";
			}
			else {
				plusStyle = "display:none;";
			}
			string.appendHtml("<span title='" + toolTipOpen + "' style='float:left;" + plusStyle
					+ "' class='ui-icon ui-icon-triangle-1-e showList hoverAction'></span>");
			string.appendHtml("<span title='" + toolTipCollapse + "' style='float:left;"
					+ minusStyle
					+ "' class='ui-icon ui-icon-triangle-1-s hideList hoverAction'></span>");
			string.appendHtml("<span class='toggleList'>Benutzte Begriffe:</span>");
			string.appendHtml("<span title='" + toolTipClear + "' style='float:left;" + clearStyle
					+ "' class='ui-icon ui-icon-minus clearList hoverAction'></span>");
			string.appendHtml("</div>");

			// render term list
			Tree<RatedTerm> ratedTermTreeTop = TermRecommender.getInstance().getRatedTermTreeTop(
						user,
						THRESHOLD_MAX_TERM_NUMBER);

			renderTermTree(string, ratedTermTreeTop, collapsed, linkProvider, master);

		}
		string.appendHtml("</div>");
		return string.toStringRaw();
	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param string
	 * @param subList
	 */
	private void renderTermTree(RenderResult string, Tree<RatedTerm> tree, boolean collapsed, LinkToTermDefinitionProvider linkProvider, String master) {

		String style = "";
		if (collapsed) {
			style = "display:none;";
		}
		string.appendHtml("<div style='" + style + "' class='termlist'>");

		Node<RatedTerm> root = tree.getRoot();
		List<Node<RatedTerm>> roots = root.getChildrenSorted();
		if (roots.size() == 0) {
			string.appendHtml("<span style='padding-left: 75px;'>-keine-</span>");
		}
		for (Node<RatedTerm> rootConcept : roots) {

			renderConceptSubTree(rootConcept, 0, string, linkProvider, master);
		}
		string.appendHtml("</div>");

	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param rootConcept
	 */
	private void renderConceptSubTree(Node<RatedTerm> rootConcept, int level, RenderResult string, LinkToTermDefinitionProvider linkProvider, String master) {
		string.append("\n"); // append newline into html-code from time to time
								// to avoid jspwiki bug
		if (!rootConcept.getData().equals(RatedTerm.ROOT)) {
			renderConcept(rootConcept, level, string, linkProvider, master);
		}
		level += 1;
		List<de.knowwe.termbrowser.util.Tree.Node<RatedTerm>> childrenSorted = rootConcept.getChildrenSorted();
		for (Node<RatedTerm> node : childrenSorted) {
			renderConceptSubTree(node, level, string, linkProvider, master);
		}

	}

	private void renderConcept(Node<RatedTerm> t, int depth, RenderResult string, LinkToTermDefinitionProvider linkProvider, String master) {
		String term = t.getData().getTerm();
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

		String name = Strings.encodeURL(topic);
		String url = linkProvider.getLinkToTermDefinition(name, null);
		// baseUrl + name;
		String divStyle = "";
		string.appendHtml("<div id='draggable' style='"
				+ "'  class='termline "
				+ lineStyleClass
				+ "'>");
		{
			// table with 2 columns: term name and action buttons (visible hover
			// only)
			string.appendHtml("<table style='table-layout:fixed'>");
			{
				string.appendHtml("<tr height='23px'>");

				// calculate indents div for hierarchy visualization
				string.appendHtml(createIndent(depth));

				// append html code for the actions that can be performed for
				// each term
				{
					string.appendHtml("<td style='min-width: 16px; padding:0px;'>");
					insertActionButtonsPre(string, url, divStyle, t, master);
					string.appendHtml("</td>");
				}

				// render actual line content
				{
					string.appendHtml("<td style='width:90%;padding-left:2px;' class='termbrowser'>");

					// using different font style depending on current hierarchy
					// depth
					string.appendHtml("<a href='" + url + "'>");
					string.appendHtml("<div class='termname' style='display:inline;"
							+ createStyle(depth)
							+ "'>");

					// insert term name
					string.appendHtml(term.replaceAll("_", "_<wbr>"));
					string.appendHtml("</div>");
					string.appendHtml("</a>");
					string.appendHtml("</td>");
				}

				// append html code for the actions that can be performed for
				// each term
				{
					string.appendHtml("<td style='min-width: 32px;max-width: 32px;padding:0px;'>");
					insertActionButtonsPost(string, url, divStyle, term, depth, master);
					string.appendHtml("</td>");
				}
				string.appendHtml("</tr>");
			}
			string.appendHtml("</table>");
		}
		string.appendHtml("</div>");
	}

	/**
	 * 
	 * @created 19.04.2013
	 * @param string
	 * @param url
	 * @param divStyle
	 */
	private void insertActionButtonsPost(RenderResult string, String url, String divStyle, String term, int level, String master) {
		string.appendHtml("<table style='table-layout:fixed'>");
		string.appendHtml("<tr>");
		{

			insertAddParentButton(string, divStyle, term, level, master);
			insertRemoveButton(string, divStyle);

		}
		string.appendHtml("</tr></table>");
	}

	/**
	 * 
	 * @created 19.04.2013
	 * @param string
	 * @param url
	 * @param divStyle
	 */
	private void insertActionButtonsPre(RenderResult string, String url, String divStyle, Node<RatedTerm> term, String master) {
		boolean allChildrenShown = allChildrenShown(term, master);

		string.appendHtml("<table style='padding:0px;table-layout:fixed'>");
		string.appendHtml("<tr>");
		{
			if (allChildrenShown) {
				if (!(hierarchy.getChildren(term.getData().getTerm()).size() == 0)) {
					insertCollapseButton(string, divStyle);
				}
			}
			else {
				insertExpandButton(string, divStyle);
			}
		}
		string.appendHtml("</tr></table>");
	}

	/**
	 * 
	 * @created 03.05.2013
	 * @param term
	 * @return
	 */
	private boolean allChildrenShown(Node<RatedTerm> term, String master) {
		List<String> childrenConcepts = hierarchy.getChildren(term.getData().getTerm());
		for (String childTerm : childrenConcepts) {
			if (!term.getChildren().contains(new Node<RatedTerm>(new RatedTerm(childTerm)))) {
				return false;
			}
		}
		return true;
	}

	private static void insertCollapseButton(RenderResult string, String divStyle) {
		// expand concept, i.e. add all children to this list
		string.appendHtml("<td style='"
				+ divStyle
				+ "' class='termbrowser'><span class='ui-icon ui-icon-minus collapseConcept hoverAction' title='Unterbegriffe aus Liste entfernen' style='display:none;'></span></td>");
	}

	/**
	 * 
	 * @created 03.05.2013
	 * @param string
	 * @param divStyle
	 */
	private void insertExpandButton(RenderResult string, String divStyle) {
		// expand concept, i.e. add all children to this list
		string.appendHtml("<td style='"
				+ divStyle
				+ "' class='termbrowser'><span class='ui-icon ui-icon-plus expandConcept' title='Unterbegriff in diese Liste aufnehmen'></span></td>");
	}

	/**
	 * 
	 * @created 03.05.2013
	 * @param string
	 * @param divStyle
	 */
	private void insertRemoveButton(RenderResult string, String divStyle) {
		// delete concept from list
		string.appendHtml("<td style='"
				+ divStyle
				+ "' class='termbrowser'><span class='ui-icon ui-icon-circle-close removeConcept hoverAction' title='Begriff aus dieser Liste herausnehmen' style='display:none;'></span></td>");
	}

	/**
	 * 
	 * @created 03.05.2013
	 * @param string
	 * @param divStyle
	 */
	private void insertAddParentButton(RenderResult string, String divStyle, String term, int level, String master) {
		if (level == 0) {

			List<String> parentConcepts = hierarchy.getParents(term);
			if (parentConcepts.size() > 0) {

				// add parent concept to list
				string.appendHtml("<td style='"
						+ divStyle
						+ "' class='termbrowser'><span class='ui-icon ui-icon-arrowreturnthick-1-n addParentConcept hoverAction' title='Oberbegriff dieser Liste hinzufügen' style='display:none;'></span></td>");
			}
			else {
				// to have a fixed layout of the remaining button
				addDummyTableCell(string, 16);
			}
		}
		else {
			// to have a fixed layout of the remaining button
			addDummyTableCell(string, 16);
		}
	}

	/**
	 * 
	 * @created 03.06.2013
	 * @param i
	 */
	private static void addDummyTableCell(RenderResult string, int i) {

		string.appendHtml("<td style='' class='termbrowser'><span style='background-position: -96px -224px;' class='ui-icon' style='display:none;'></span></td>");

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

	private static String createIndent(int count) {
		int width = count * 15;
		String padding = "padding:0.01em;display:block;float:left;min-width:" + width + "px;";

		String result = "<td style='" + padding + "'></td>";

		return result;
	}

}
