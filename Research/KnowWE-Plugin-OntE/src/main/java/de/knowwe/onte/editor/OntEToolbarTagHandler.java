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
package de.knowwe.onte.editor;

import java.util.Map;

import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.taghandler.TagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.toolbar.ToolbarButton;
import de.knowwe.toolbar.ToolbarUtils;

/**
 * A small flexible toolbar for often used ontology tasks. The buttons of the
 * toolbar can easily be set as an extension point in the plugin.xml.
 * 
 * @author Stefan Mark
 * @created 12.10.2011
 */
public class OntEToolbarTagHandler extends AbstractHTMLTagHandler {

	private static final String NAME = "onte.toolbar";

	public OntEToolbarTagHandler() {
		super(NAME);
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {

		StringBuilder html = new StringBuilder();
		if (parameters.get("help") != null) { // print help message
			html.append(getDescription(user));
			return html.toString();
		}

		KnowWEArticleManager mgr =
				KnowWEEnvironment.getInstance().getArticleManager(web);
		KnowWEArticle article = mgr.getArticle(user.getTitle());

		ToolbarButton[] buttons = ToolbarUtils.getButtons(article, user);

		html.append("<div class=\"onte-buttons onte-buttonbar\" style=\"width: 100%; height: 44px;\">");
		for (ToolbarButton button : buttons) {
			html.append(ToolbarUtils.getButtonHTML(button));

		}
		html.append("</div>");

		return html.toString();
	}

	/**
	 * Returns an example usage string
	 *
	 * @created 20.09.2011
	 * @return A example usage string
	 */
	@Override
	public String getExampleString() {
		StringBuilder example = new StringBuilder();
		example.append("[{KnowWEPlugin " + NAME + " [");
		example.append(", help ]");
		example.append("}])\n ");
		example.append("The parameters in [ ] are optional.");
		return example.toString();
	}

	/**
	 * Appends a simple how to use message to the output if the
	 * {@link TagHandler} was used incorrectly.
	 *
	 * @created 20.09.2011
	 * @return String The how to use message
	 */
	@Override
	public String getDescription(UserContext user) {

		StringBuilder help = new StringBuilder();
		help.append("<dl>");

		help.append("<dt><strong>NAME</strong></dt>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ "}] - shows a toolbar for interaction with the ontology. Such actions are export, import of an ontology, consistency checking, etc.</dd>");

		help.append("<dt><strong>SYNOPSIS</strong></dt>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ "}] - shos a toolbar for interaction with the ontology.</dd>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ " , help}] - Show a how to use message for this taghandler.</dd>");

		help.append("<dt><strong>DESCRIPTION</strong></dt>");
		help.append("<dd>The OntEToolbarTagHandler renders a toolbar for some ontology actions.</dd>");

		help.append("</dl>");

		return help.toString();
	}

}
