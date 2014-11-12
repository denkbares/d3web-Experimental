/*
 * Copyright (C) 2014 think-further.de
 */
package de.knowwe.defi.table;

import java.util.Map;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;

/**
 * @author Sebastian Furth (think-further.de)
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
