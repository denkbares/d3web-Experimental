/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.module;

import de.d3web.we.kdom.rendering.PageAppendHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class ValidationPageAppendHandler implements PageAppendHandler {

	@Override
	public String getDataToAppend(String topic, String web,
			UserContext user) {

		if (user.userIsAdmin()) {
			// KnowWEArticle article =
			// KnowWEEnvironment.getInstance().getArticle(web, topic);
			// boolean valid =
			// KDOMValidator.getTagHandlerInstance().validateArticle(article);
			// String header =
			// "<div id=\"validator-panel\" class=\"panel\"><h3>"
			// + KnowWEEnvironment.getInstance().getKwikiBundle(user).getString(
			// "KnowWE.ValidatorHandler.header")
			// + "</h3><div><ul>";
			// return valid ? "" : KnowWEUtils.maskHTML(header
			// + KDOMValidator.getTagHandlerInstance().getBuilder().toString()
			// + "</ul></div></div>");
			return KnowWEUtils.maskHTML("<span class='warning'>Currently the KDOM is not validated due to code refactoring!</span>");
		}
		else {
			return "";
		}
	}

	@Override
	public boolean isPre() {
		return false;
	}

}
