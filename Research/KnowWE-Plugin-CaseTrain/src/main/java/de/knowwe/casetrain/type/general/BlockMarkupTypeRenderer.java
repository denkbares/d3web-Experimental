/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.casetrain.type.general;

import de.knowwe.casetrain.util.Utils;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * @author Johannes Dienst
 * @created 06.06.2011
 */
public class BlockMarkupTypeRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder string) {

		string.append(KnowWEUtils.maskHTML("<div class='"
				+ ((BlockMarkupType) sec.get()).getCSSClass()
				+ "'>"));
		KnowWEArticle article = KnowWEUtils.getCompilingArticles(sec).iterator().next();
		Utils.renderKDOMReportMessageBlock(Messages.getErrors(Messages.getMessagesFromSubtree(
				article, sec)), string);

		Utils.renderKDOMReportMessageBlock(Messages.getWarnings(Messages.getMessagesFromSubtree(
				article, sec)), string);

		Utils.renderKDOMReportMessageBlock(Messages.getNotices(Messages.getMessagesFromSubtree(
				article, sec)), string);

		// TODO Delegation renders PlainText around collapsebox!
		// string.append(KnowWEUtils.maskHTML("<pre id=\""
		// + sec.getID()
		// + "\"style=\"white-space:pre-wrap;" +
		// "background: none repeat scroll 0 0 #F5F5F5;" +
		// "border: 1px solid #E5E5E5;position:relative;margin:0px\">"));
		// string.append(KnowWEUtils.maskHTML("<div style=\"position:relative;top:0px;"
		// +
		// "right:0px;border-bottom: 1px solid #E5E5E5;" +
		// "border-left: 1px solid #E5E5E5;padding:5px;" +
		// "float:right;\">"
		// // + getFrameName(sec)
		// // + getEditorIcon(sec)
		// + Utils.renderTools(article, sec, user)
		// // + getLink(sec)
		// + "</div>"));
		Section<BlockMarkupContent> con =
				Sections.findSuccessor(sec, BlockMarkupContent.class);
		BlockMarkupContentRenderer.getInstance().render(con, user, string);
		string.append(KnowWEUtils.maskHTML("</div>"));

		// string.append(KnowWEUtils.maskHTML("</pre>"));
	}

}
