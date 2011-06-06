/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.casetrain.type.general;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMNotice;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.casetrain.util.Utils;


/**
 * 
 * @author Johannes Dienst
 * @created 06.06.2011
 */
public class BlockMarkupTypeRenderer extends KnowWEDomRenderer<BlockMarkupType> {

	@SuppressWarnings("unchecked")
	@Override
	public void render(KnowWEArticle article, Section<BlockMarkupType> sec, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<div class='"
				+ sec.get().getCSSClass()
				+ "'>"));
		Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
				article,
				sec,
				KDOMError.class), string);

		Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
				article,
				sec,
				KDOMWarning.class), string);

		Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
				article,
				sec,
				KDOMNotice.class), string);

		// TODO Delegation renders PlainText around collapsebox!
		Section<BlockMarkupContent> con =
			Sections.findSuccessor(sec, BlockMarkupContent.class);
		BlockMarkupContentRenderer.getInstance().render(article, con, user, string);
		string.append(KnowWEUtils.maskHTML("</div>"));

	}

}
