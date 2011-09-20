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
package de.knowwe.casetrain.type.multimedia;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * 
 * Represents a link.
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class Link extends MultimediaItem {

	public static String KEY_LINK = "Link:";

	private static String REGEX = "\\{" + KEY_LINK + "(.*?)\\}(\\{.*?})?";

	public Link() {
		super(REGEX);

		this.clearSubtreeHandlers();
		this.addChildType(new Url());

		this.setCustomRenderer(new KnowWEDomRenderer<Link>() {

			@Override
			public void render(KnowWEArticle article, Section<Link> sec,
					UserContext user, StringBuilder string) {
				Section<MultimediaItemContent> linkURL = Sections.findChildOfType(sec,
						MultimediaItemContent.class);
				string.append(KnowWEUtils.maskHTML(
						"<span title=\"Link\" class=\"casetrainlink\">"));
				Section<Url> url = Sections.findChildOfType(sec, Url.class);
				string.append(KnowWEUtils.maskHTML("<a href=\""));
				String linkString = linkURL.getOriginalText().trim();
				if (!linkString.startsWith("http://"))
				;
				string.append("http://");
				string.append(linkString);
				string.append(KnowWEUtils.maskHTML("\">"));
				if (url == null)
					string.append(linkURL.getOriginalText().trim());
					else
					string.append(url.getOriginalText().
							substring(1, url.getOriginalText().length() - 1).trim());
					string.append(KnowWEUtils.maskHTML("</a>"));
					string.append(KnowWEUtils.maskHTML("</span>"));
				}
		});

	}

	public class Url extends AbstractType {

		private final String regex = "\\{.*?\\}";

		public Url() {
			this.setSectionFinder(new RegexSectionFinder(regex));
		}
	}

}
