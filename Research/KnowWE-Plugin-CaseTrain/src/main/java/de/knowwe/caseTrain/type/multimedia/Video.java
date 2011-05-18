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
package de.knowwe.caseTrain.type.multimedia;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;


/**
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class Video extends MultimediaItem {

	public static String KEY_VIDEO = "Video:";

	private static String REGEX = "\\{" + KEY_VIDEO + "(.*?)\\}";

	public Video() {
		super(REGEX);

		// TODO this is not for Video.
		this.setCustomRenderer(new KnowWEDomRenderer<Video>() {

			@Override
			public void render(KnowWEArticle article, Section<Video> sec, UserContext user, StringBuilder string) {
				Section<MultimediaItemContent> bildURL = Sections.findChildOfType(sec,
						MultimediaItemContent.class);
				string.append(KnowWEUtils.maskHTML("<img height='70' src='"));
				string.append("attach/" + sec.getArticle().getTitle() + "/");
				string.append(bildURL.getOriginalText().trim());
				string.append(KnowWEUtils.maskHTML("'></img>"));
			}
		});

	}

}