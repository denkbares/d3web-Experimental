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

package de.knowwe.example;

import java.util.Map;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;

/**
 * @author Alex Legler
 */
public class HelloWorldTagHandler extends AbstractTagHandler {

	public HelloWorldTagHandler() {
		super("helloworld");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> values, RenderResult result) {
		String count = values.get("count");

		int number = 1;

		if (count != null) {
			try {
				number = Integer.parseInt(count);
			}
			catch (NumberFormatException e) {
				// not a valid number
			}

			if (number < 0) {
				number = 1;
			}
		}

		result.appendHTML(" <b>Hello World!</b>");

		for (int i = 0; i < number; i++) {
			result.append("<img src=\"KnowWEExtension/images/helloworld.jpg\" alt=\":)\"/>");
		}

	}

}
