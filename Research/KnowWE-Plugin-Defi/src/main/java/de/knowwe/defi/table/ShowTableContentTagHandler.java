/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.defi.table;

import java.util.Map;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;

/**
 * @author Sebastian Furth (think-further)
 * @created 03.11.14
 */
public class ShowTableContentTagHandler extends AbstractTagHandler {

	public static final String VERSION_KEY = ShowTableTagHandler.VERSION_KEY;

	private static final String PARAM_ID = "id";
	private static final int DEFAULT_INPUT_NUMBER = 0;

	public ShowTableContentTagHandler() {
		super("Textausgabe");
	}

	@Override
	public void render(Section<?> section, UserContext user,
					   Map<String, String> parameters, RenderResult result) {

		// number of the input
		int inputNumber = DEFAULT_INPUT_NUMBER;

		// id of the table
		String id = parameters.get(PARAM_ID);
		if (id == null) {
			result.append("Error: The id attribute is mandatory!");
			return;
		}

		// version
		String versionString = user.getParameter(VERSION_KEY);
		int version = 0;
		if (versionString != null) {
			version = Integer.parseInt(versionString);
		}

		// get content from the <username>_data page and render it
		String content = TableUtils.getStoredContentString(inputNumber, id, version, user.getUserName());
		result.append(content);
	}

}
