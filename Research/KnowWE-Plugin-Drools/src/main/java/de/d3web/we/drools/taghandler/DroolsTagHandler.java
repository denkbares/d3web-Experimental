/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.drools.taghandler;

import java.util.Map;

import de.d3web.we.core.KnowWERessourceLoader;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * The TagHandler which adds the command line for the Drools Plugin 
 * [{KnowWEPlugin Drools}]
 * @author Florian Ziegler
 */
public class DroolsTagHandler extends AbstractTagHandler {

	public DroolsTagHandler() {
		super("drools");
	}

	@Override
	/**
	 * adds the command line
	 */
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {

		KnowWERessourceLoader rl = KnowWERessourceLoader.getInstance();
		rl.add("drools.css", KnowWERessourceLoader.RESOURCE_STYLESHEET);

		StringBuilder bob = new StringBuilder();
		bob.append("<div class=\"panel\"><h3>Drools Shell</h3>");
		bob.append("<form id=\"DroolsCommandLine\" name=\"DroolsCommandLine\" method=\"get\" onKeyPress=\"return submitenter(this,event)\">");
		bob.append("<input type=\"text\" id=\"droolsField\" class=\"droolsField\" name=\"fact\" size=\"50\" />");
		//bob.append("<div id=\"ac_container\" style=\"position:relative;\"></div>");
		bob.append("<input type=\"button\" id=\"droolsCreate\" class=\"droolsButton\" value=\"Execute\" onclick=\"return Drools.createFact()\" />");
		bob.append("&nbsp;&nbsp;&nbsp;");
		bob.append("<input type=\"button\" id=\"droolsExpand\" class=\"droolsButton\" value=\"(-)\" onclick=\"return Drools.showConsole()\" />");
		bob.append("&nbsp;&nbsp;&nbsp;");
		bob.append("<input type=\"button\" id=\"droolsTextAreaButton\" class=\"droolsButton\" onclick=\"return Drools.switchToTextarea()\" value=\"Batch\" />");
		bob.append("<div id=\"drools-console\"></div></div>");
		bob.append("</form>");
		bob.append("<script type=\"text/javascript\">");
		bob.append("new Autocomplete('droolsField', { serviceUrl:'lol', wikiPageTitle:'"
				+ article.getTitle() + "', width:512 });");
		bob.append("</script>");
		
		return KnowWEUtils.maskHTML(bob.toString());
	}

	
	

}
