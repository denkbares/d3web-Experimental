/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.knowwe.hermes;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 *
 * @author grotheer
 * @created 11.11.2010
 */
public class TreeViewType extends AbstractType {

	public static final String START_TAG = "%TreeStart";
	public static final String END_TAG = "%TreeEnd";

	@Override
	protected void init() {
		sectionFinder = new RegexSectionFinder(START_TAG + "[\\w|\\W]*?"
				+ END_TAG);
		this.setCustomRenderer(new TreeViewRenderer());
	}

	public static class TreeViewRenderer extends KnowWEDomRenderer<TreeViewType> {

		@Override
		public void render(KnowWEArticle article, Section<TreeViewType> sec, UserContext user, StringBuilder string) {
			long id = System.currentTimeMillis();

			String pre = ""
					+ "<!-- YUI CSS files: -->"
					+ "<link rel='stylesheet' type='text/css' href='KnowWEExtension/css/yui2/treeview/assets/skins/sam/treeview.css'>"
					+ "<!-- YUI JS files: -->"
					+ "<script type='text/javascript' src='KnowWEExtension/scripts/yui2/yahoo-dom-event/yahoo-dom-event.js'></script>"
					+ "<script type='text/javascript' src='KnowWEExtension/scripts/yui2/treeview/treeview-min.js'></script>"

					+ "<div id='tree" + id + "' class=\"hermes-tree-view\">";

			String post = ""
					+ "</div>"
					+ "<script type='text/javascript'>"
					+ "var tree" + id + ";\n"
					+ "function treeInit" + id + "() {\n"
					+ "tree" + id + " = new YAHOO.widget.TreeView('tree" + id + "');\n"
					// + "tree" + id + ".expandAll();" // keep 'em collpased
					+ "tree" + id + ".render();\n}\n"
					+ "treeInit" + id + "();"
					+ "</script>";

			string.append(KnowWEUtils.maskHTML(pre));
			DelegateRenderer.getInstance().render(article, sec, user, string);
			string.append(KnowWEUtils.maskHTML(post));
		}
	}
}
