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
package de.knowwe.casetrain.renderer;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * Renders a span with css-classes around some text.
 * 
 * @author Johannes Dienst
 * @created 03.06.2011
 */
public class SpanClassRenderer implements Renderer {

	public static final String META_KEY = "metakeyword";

	private String clazz;
	private final List<String> cssClasses;

	public SpanClassRenderer() {
		cssClasses = new ArrayList<String>();
		clazz = "classdummy";
	}

	public SpanClassRenderer(String s) {
		cssClasses = new ArrayList<String>();
		clazz = s;
	}

	public SpanClassRenderer(List<String> classes) {
		clazz = "classdummy";
		if (!classes.isEmpty()) clazz = classes.remove(0);
		cssClasses = new ArrayList<String>();
		cssClasses.addAll(classes);
	}

	@Override
	public void render(Section<?> sec, UserContext user,
			StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<span class=\"" + clazz));

		for (String clazz : cssClasses)
			string.append(" " + clazz);
		string.append(KnowWEUtils.maskHTML("\">"));

		MouseOverTitleRenderer.getInstance().render(sec, user, string);
		string.append(KnowWEUtils.maskHTML("</span>"));
	}

}
