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


/**
 * 
 * TODO how to render this?
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class Audio extends MultimediaItem {

	public static String KEY_AUDIO= "Audio:";

	private static String REGEX = "\\{" + KEY_AUDIO + "(.*?)\\}";

	public Audio() {
		super(REGEX);

		//		this.setCustomRenderer(new KnowWEDomRenderer<Audio>() {
		//
		//			@Override
		//			public void render(KnowWEArticle article, Section<Link> sec, UserContext user, StringBuilder string) {
		//				Section<MultimediaItemContent> linkURL = Sections.findChildOfType(sec,
		//						MultimediaItemContent.class);
		//				string.append(KnowWEUtils.maskHTML("<img height='70' src='"));
		//				string.append("attach/" + sec.getArticle().getTitle() + "/");
		//				string.append(linkURL.getOriginalText().trim());
		//				string.append(KnowWEUtils.maskHTML("'></img>"));
		//			}
		//		});

	}

}