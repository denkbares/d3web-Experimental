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

package de.d3web.we.kdom.kopic.renderer;

import java.util.Collection;

import de.d3web.report.Message;
import de.knowwe.core.compile.packaging.PackageRenderUtils;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class SetCoveringListSectionRenderer extends KnowWEDomRenderer {

	@Override
	public void render(KnowWEArticle article, Section sec, UserContext user, StringBuilder string) {

		// string.append("%%collapsebox-closed \n");

		// String title = "";
		// if(sec.get() instanceof AbstractXMLObjectType) {
		// title =
		// ((AbstractXMLObjectType)sec.get()).getXMLTagName()+" ";
		// }

		// string.append("! " +title + " \n");
		StringBuilder compile = new StringBuilder();
		article = PackageRenderUtils.checkArticlesCompiling(article, sec, compile);

		if (sec.get() instanceof AbstractType) {
			Type type = sec.get();
			Collection<Message> messages = KnowWEUtils.getMessages(article, sec, Message.class);
			if (messages != null && !messages.isEmpty()) {
				string.append("{{{");
				for (Message m : messages) {
					string.append(m.getMessageType() + ": " + m.getMessageText() + " Line: "
							+ m.getLineNo() + KnowWEUtils.maskHTML("<br>"));
					if (m.getMessageType().equals(Message.ERROR)) {
						insertErrorRenderer(sec, m, user.getUserName());
					}
				}
				string.append("}}}");
			}
		}

		// string.append(KnowWEUtils.maskHTML(
		// "<div id=\"" + sec.getID()
		// + "\"><pre class=\"ReRenderSectionMarker\" id=\"" + sec.getID()
		// + "-pre\" rel=\"{id:'" + sec.getID() + "'}\">"));

		StringBuilder b = new StringBuilder();
		DelegateRenderer.getInstance().render(article, sec, user, b);
		string.append("{{{" + compile + b.toString() + "}}}");

		// string.append(KnowWEUtils.maskHTML("</pre></div>"));

		// string.append("/%\n");
	}

	protected void insertErrorRenderer(Section<?> sec, Message m, String user) {
		String text = m.getLine();
		if (text == null || text.length() == 0) return;
		Section<?> errorSec = Sections.findSmallestNodeContaining(sec, text);
		((AbstractType) errorSec.get()).setCustomRenderer(ErrorRenderer.getInstance());

	}

}