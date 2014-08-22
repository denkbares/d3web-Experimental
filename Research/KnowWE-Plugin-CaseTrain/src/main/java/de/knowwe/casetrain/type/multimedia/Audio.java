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

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;

/**
 * 
 * TODO how to render this?
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class Audio extends MultimediaItem {

	public static String KEY_AUDIO = "Audio:";

	private static String REGEX = "\\{" + KEY_AUDIO + "(.*?)\\}";

	public Audio() {
		super(REGEX);

		this.setRenderer(new Renderer() {

			@Override
			public void render(Section<?> sec, UserContext user, RenderResult string) {
				Section<MultimediaItemContent> linkURL = Sections.child(sec,
						MultimediaItemContent.class);
				string.appendHtml("<span title='Audio'>");
				string.append(linkURL.getText().trim());
				string.appendHtml("'</span>");
			}
		});

	}

}
