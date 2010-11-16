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

package de.d3web.we.hermes.kdom.event.renderer;

import de.d3web.we.hermes.TimeStamp;
import de.d3web.we.hermes.kdom.event.TimeEventNew;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class TimeEventDateRenderer extends KnowWEDomRenderer<TimeEventNew> {

	private static TimeEventDateRenderer instance;

	public static TimeEventDateRenderer getInstance() {
		if (instance == null) {
			instance = new TimeEventDateRenderer();
		}
		return instance;
	}

	@Override
	public void render(KnowWEArticle article, Section<TimeEventNew> sec,
			KnowWEUserContext user, StringBuilder result) {
		String date = "no date found";
		if (result.charAt(result.length() - 1) == '\n') {
			result.deleteCharAt(result.length() - 1);
		}
		if (sec != null)
			date = sec.getOriginalText();
		if (date.startsWith("\r\n"))
			date = date.substring(2);
		
		String dateDecoded = TimeStamp.decode(date);
		result.append(KnowWEUtils.maskHTML("   :   " + dateDecoded + "</h4>\\\\"));
	}
}
