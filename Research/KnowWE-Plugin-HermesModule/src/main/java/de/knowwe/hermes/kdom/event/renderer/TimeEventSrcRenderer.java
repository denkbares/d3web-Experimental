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

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.hermes.kdom.event.TimeEventNew;
import de.knowwe.hermes.kdom.event.TimeEventNew.Source;

public class TimeEventSrcRenderer extends KnowWEDomRenderer<TimeEventNew> {

	private static TimeEventSrcRenderer instance;

	public static TimeEventSrcRenderer getInstance() {
		if (instance == null) {
			instance = new TimeEventSrcRenderer();
		}
		return instance;
	}

	@Override
	public void render(KnowWEArticle article, Section<TimeEventNew> sec,
			UserContext user, StringBuilder result) {
		if (sec.equals(Sections.findChildOfType(sec.getFather(), Source.class))) {
			result.append(KnowWEUtils.maskHTML("\\\\__Quellen:__\\\\"));
		}
		String source = "no source found";
		if (sec != null)
			source = sec.getOriginalText();
		String key = "QUELLE:";
		if (source.startsWith(key)) {
			source = source.substring(key.length());
		}
		source += "\\\\";
		result.append(source);
	}
}
