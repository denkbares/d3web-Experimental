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

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.casetrain.type.MetaData;


/**
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class BlockMarkupContent extends AbstractType {

	private final String css;

	public BlockMarkupContent(String cssClass) {
		this.css = cssClass;
		this.setCustomRenderer(new KnowWEDomRenderer<SubblockMarkup>() {

			@Override
			public void render(KnowWEArticle article, Section<SubblockMarkup> sec, UserContext user, StringBuilder string) {

				boolean isMeta = false;
				if (Sections.findAncestorOfType(sec, MetaData.class) != null) {isMeta = true;}
				if (!isMeta) {
					string.append(KnowWEUtils.maskHTML("%%collapsebox-closed \r\n"));
					string.append(
							KnowWEUtils.maskHTML("! "
									+ Sections.findSuccessor(sec, Title.class).getOriginalText().trim()
									+ "\r\n"));
				}

				string.append(KnowWEUtils.maskHTML("<div class='"
						+ css
						+ "'>"));
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</div>\r\n"));

				if (!isMeta) {string.append(KnowWEUtils.maskHTML("/%\r\n"));}
			}
		});

	}

}
