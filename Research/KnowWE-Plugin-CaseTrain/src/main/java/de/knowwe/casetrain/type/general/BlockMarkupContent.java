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

import java.util.List;
import java.util.ResourceBundle;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.casetrain.info.Info;
import de.knowwe.casetrain.type.MetaData;


/**
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class BlockMarkupContent extends AbstractType {

	private static final ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");;

	private final String css;

	public BlockMarkupContent(String cssClass) {
		this.css = cssClass;
		this.setCustomRenderer(new KnowWEDomRenderer<SubblockMarkup>() {

			@SuppressWarnings("unchecked")
			@Override
			public void render(KnowWEArticle article, Section<SubblockMarkup> sec,
					UserContext user, StringBuilder string) {

				boolean isMeta = false;
				if (Sections.findAncestorOfType(sec, MetaData.class) != null) {isMeta = true;}
				boolean isInfo = false;
				if (Sections.findAncestorOfType(sec, Info.class) != null) {isInfo = true;}

				if (!isMeta) {
					string.append(KnowWEUtils.maskHTML("%%collapsebox-closed \r\n"));

					String tString = "";
					if (isInfo) {
						Section<Title> titl = Sections.findSuccessor(sec, Title.class);
						if (titl != null)
							tString = titl.getOriginalText().trim();
					} else {
						tString = bundle.getString(sec.getFather().get().getName());
					}

					string.append(
							KnowWEUtils.maskHTML("! "
									+ tString
									+ "\r\n"));
				}

				//				string.append(KnowWEUtils.maskHTML("<div class='"
				//						+ css
				//						+ "'>"));
				Class<?>[] array = {Title.class};
				List<Section<?>> secsWithoutTitle = Sections.getChildrenExceptExactType(sec, array);

				// Dont ask why I use this hack here!
				// DelegateRenderer should find the SubblockMarkupRender, but in fact
				// it does NOT! Johannes
				for (Section<?> s :secsWithoutTitle) {
					s.get().getRenderer().render(article, s, user, string);
				}
				//				string.append(KnowWEUtils.maskHTML("</div>\r\n"));

				if (!isMeta) {string.append(KnowWEUtils.maskHTML("/%\r\n"));}
			}
		});

	}

}
