/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3web.debugger.renderer;

import de.d3web.core.inference.Rule;
import de.d3web.core.session.Session;
import de.d3web.we.kdom.rules.RuleContentType;
import de.d3web.we.kdom.rules.action.RuleAction;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.d3web.debugger.inference.DebugAction;
import de.knowwe.d3web.debugger.inference.DebugCondition;


/**
 * 
 * @author dupke
 */
public class DebuggerRuleRenderer implements Renderer{


	@Override
	public void render(Section<?> sec, UserContext user,
			StringBuilder string) {
		KnowWEArticle article = KnowWEUtils.getCompilingArticles(sec).iterator().next();
		Session session = D3webUtils.getSession(article.getTitle(), user,
				article.getWeb());
		Section<RuleAction> ruleAction = Sections.findSuccessor(sec,
				RuleAction.class);
		Rule r = null;
		if (ruleAction != null) {
			r = (Rule) KnowWEUtils.getStoredObject(article, ruleAction,
					RuleContentType.ruleStoreKey);
		}
		
		StringBuffer buffer = new StringBuffer();

		DebugCondition dc = new DebugCondition(r.getCondition());
		DebugAction da = new DebugAction(r.getAction());
		if (r.hasFired(session)) buffer.append("<div class='ruleContentFired' ruleid='"
				+ r.hashCode() + "'>");
		else
			buffer.append("<div class='ruleContent' ruleid='" + r.hashCode() + "'>");

		buffer.append("IF "
				+ dc.render(session, KnowWEEnvironment.DEFAULT_WEB, sec.getTitle(), false)
				+ "<br />");
		;
		buffer.append("THEN " + da.render());
		buffer.append("</div>");

		string.append(buffer.toString());
	}
}
