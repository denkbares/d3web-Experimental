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

package de.knowwe.hermes.kdom.event.renderer;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.hermes.TimeStamp;

public class TimeEventDateRenderer implements Renderer {

	private static TimeEventDateRenderer instance;

	public static TimeEventDateRenderer getInstance() {
		if (instance == null) {
			instance = new TimeEventDateRenderer();
		}
		return instance;
	}

	@Override
	public void render(Section<?> sec, UserContext user,
			RenderResult result) {
		String date = "no date found";
		if (result.charAt(result.length() - 1) == '\n') {
			result.deleteCharAt(result.length() - 1);
		}
		if (sec != null) date = sec.getText();
		if (date.startsWith("\r\n")) date = date.substring(2);

		String dateDecoded = TimeStamp.decode(date);
		result.appendHtml("   :   " + dateDecoded + "</h4>\\\\");
	}
}
