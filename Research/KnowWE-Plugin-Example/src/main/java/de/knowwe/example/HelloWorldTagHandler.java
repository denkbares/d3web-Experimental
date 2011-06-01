/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.knowwe.example;

import java.util.Map;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * @author Alex Legler
 */
public class HelloWorldTagHandler extends AbstractTagHandler {

	public HelloWorldTagHandler() {
		super("helloworld");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> values) {
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

		String result = " <b>Hello World!</b>";

		for (int i = 0; i < number; i++) {
			result += ("<img src=\"KnowWEExtension/images/helloworld.png\" alt=\":)\"/>");
		}

		return KnowWEUtils.maskHTML(result);
	}

}
