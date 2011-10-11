/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.taghandler;

import java.util.Map;

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class ValidatorHandler extends AbstractHTMLTagHandler {

	public ValidatorHandler() {
		super("validateKDOM");
	}

	@Override
	public String getDescription(UserContext user) {
		return KnowWEEnvironment.getInstance().getKwikiBundle(user).getString(
				"KnowWE.ValidatorHandler.description");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> values, String web) {
		// KnowWEArticle article =
		// KnowWEEnvironment.getInstance().getArticle(web, topic);
		// KDOMValidator.getTagHandlerInstance().validateArticle(article);
		// String header = "<div id=\"validator-panel\" class=\"panel\"><h3>"
		// + KnowWEEnvironment.getInstance().getKwikiBundle(user).getString(
		// "KnowWE.ValidatorHandler.header")
		// + "</h3><div><ul>";
		// return header +
		// KDOMValidator.getTagHandlerInstance().getBuilder().toString()
		// + "</ul></div></div>";
		return KnowWEUtils.maskHTML("<span class='warning'>Currently the KDOM is not validated due to code refactoring!</span>");
	}

}