/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.user.UserContext;

/**
 * Utility class for the Toolbar.
 *
 * @author Stefan Mark
 * @created 04.12.2011
 */
public class ToolbarUtils {

	public static String PARAM_ORDER = "order";

	public static Collection<ToolbarButtonProvider> getProviders() {
		Extension[] extensions = PluginManager.getInstance().getExtensions(
				"KnowWE-Plugin-OntE",
				"ToolbarButtonProvider");

		Collection<ToolbarButtonProvider> providers = new ArrayList<ToolbarButtonProvider>(
				extensions.length);

		Arrays.sort(extensions, new ExtensionComparator());
		for (int i = 0; i < extensions.length; i++) {
			Extension extension = extensions[i];
			providers.add((ToolbarButtonProvider) extension.getSingleton());
		}
		return providers;
	}

	/**
	 * Loads the buttons specified as an Extension Point.
	 *
	 * @created 04.12.2011
	 * @param article
	 * @param userContext
	 * @return
	 */
	public static ToolbarButton[] getButtons(Article article, UserContext userContext) {
		List<ToolbarButton> tools = new LinkedList<ToolbarButton>();
		for (ToolbarButtonProvider provider : getProviders()) {
			Collections.addAll(tools, provider.getButtons(article, userContext));
		}
		return tools.toArray(new ToolbarButton[tools.size()]);
	}

	/**
	 * Returns the button layout for a button within the toolbar
	 *
	 * @created 04.12.2011
	 * @param ToolbarButton button
	 * @return String HTML string of the button layout
	 */
	public static String getButtonHTML(ToolbarButton button) {
		StringBuilder html = new StringBuilder();

		html.append("<a href=\"javascript:");
		html.append(button.getJSAction());
		html.append(";void(0);\" ");
		html.append("\" title=\"");
		html.append(button.getDescription());
		html.append("\" class=\"onte-button left small\">");
		html.append("<img src=\"KnowWEExtension/images/onte/transparent.png\" style=\"");
		html.append("background: url('").append(button.getIconPath()).append(
				"') no-repeat scroll center 6px transparent; height: 22px;width: 16px;");
		html.append("\" /></a>");

		return html.toString();
	}
}

/**
 *
 * @author Stefan Mark
 *
 */
class ExtensionComparator implements Comparator<Extension> {

	@Override
	public int compare(Extension o1, Extension o2) {
		int order1 = Integer.parseInt(o1.getParameter(ToolbarUtils.PARAM_ORDER));
		int order2 = Integer.parseInt(o2.getParameter(ToolbarUtils.PARAM_ORDER));

		if (order1 > order2) {
			return 1;
		}
		return -1;
	}
}
