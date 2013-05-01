/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.revisions.manager.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * 
 * @author grotheer
 * @created 21.04.2013
 */
public class SimpleTextDiff extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	private String perform(UserActionContext context) throws IOException {
		Map<String, String> params = context.getParameters();
		if (params.containsKey("title") && params.containsKey("version")) {
			String title = params.get("title");
			int version = Integer.parseInt(params.get("version"));

			String t1 = Environment.getInstance().getWikiConnector().getVersion(title, version);
			String t2 = Environment.getInstance().getWikiConnector().getVersion(title, -1);

			String header = "<h4>Text-Diff for page '" + title + "':</h4>";
			String diff = getTextDiff(title, version, -1, t1, t2, "\n");
			if (!diff.isEmpty()) {
				return header + "<pre>" + diff + "</pre>";
			}
			else {
				return "<p class=\"box error\">No differences to current page version.";
			}
		}
		return "<p class=\"box error\">Error while getting text diff.";
	}

	public static String getTextDiff(String title, int version1, int version2, String text1, String text2, String linebreak) {
		String versionString1 = "Version " + version1 + " of  page '" + title + "'";
		String versionString2 = "Version " + version2 + " of  page '" + title + "'";
		if (version2 == -1) {
			versionString2 = "Current version of page '" + title + "'";
		}
		if (version1 == 0) {
			versionString1 = "Uploaded Version of page '" + title + "'";
		}
		List<String> lines1 = Arrays.asList(text1.split("\n"));
		List<String> lines2 = Arrays.asList(text2.split("\n"));
		Patch patch = DiffUtils.diff(lines1, lines2);

		StringBuilder builder = new StringBuilder();
		List<String> result = DiffUtils.generateUnifiedDiff(versionString1, versionString2, lines1,
				patch, 5);

		// TODO make this line splitting better, to not break the screen
		int limit = 100;
		for (String line : result) {
			if (line.length() <= limit) {
				builder.append(line + linebreak);
			}
			else {
				String cuttedline = line;
				while (cuttedline.length() > limit) {
					builder.append(cuttedline.substring(0, limit - 1) + linebreak);
					cuttedline = cuttedline.substring(limit, cuttedline.length() - 1);
				}
			}
		}
		return builder.toString();
	}
}
