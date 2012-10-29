/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.baseline;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.core.wikiConnector.WikiAttachment;

/**
 * 
 * @author Reinhard Hatko
 * @created 26.10.2012
 */
public class BaselineRenderer implements Renderer {

	private static DateFormat FORMAT = DateFormat.getDateInstance();

	@Override
	public void render(Section<?> section, UserContext user, StringBuilder string) {
		StringBuilder bob = new StringBuilder();
		bob.append("<div class='baselineParent'>");
		renderSelection(user, bob);
		bob.append("<div class='baselineCompareResult'>");

		bob.append("</div>");
		bob.append("</div>");
		string.append(Strings.maskHTML(bob.toString()));
	}

	private static void renderSelection(UserContext user, StringBuilder bob) {
		List<Baseline> baselines = loadBaselines(user.getTitle());

		bob.append("<div class='baselineSelection'>");
		if (baselines.isEmpty()) {
			bob.append("There are no baselines to compare against. Please create one first.");
			bob.append("</div>");
			return;
		}

		renderDropdown("First baseline:", baselines, bob);
		baselines.add(0, CreateBaselineAction.createCurrentBaseline(user));
		renderDropdown("Second baseline:", baselines, bob);

		bob.append("</div>");
	}

	/**
	 * 
	 * @created 27.10.2012
	 * @param title
	 * @param baselines
	 * @param bob
	 */
	public static void renderDropdown(String title, Collection<Baseline> baselines, StringBuilder bob) {
		bob.append(title);
		bob.append("<select>");
		for (Baseline baseline : baselines) {
			bob.append("<option value=\"");
			bob.append(baseline.getName());
			bob.append("\">");
			bob.append(baseline.getName());
			bob.append(" (");
			bob.append(FORMAT.format(new Date(baseline.getDate())));
			bob.append(")");
			bob.append("</option>");
		}
		bob.append("</select>");
	}

	/**
	 * 
	 * @created 27.10.2012
	 * @param title
	 * @return
	 */
	private static List<Baseline> loadBaselines(String title) {
		List<Baseline> result = new LinkedList<Baseline>();
		try {
			Collection<WikiAttachment> attachments = getBaselineAttachments(title);
			for (WikiAttachment wikiAttachment : attachments) {
				result.add(CompareBaselinesAction.loadBaseline(wikiAttachment));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		Collections.sort(result);
		return result;
	}

	private static Collection<WikiAttachment> getBaselineAttachments(String title) throws IOException {
		Collection<WikiAttachment> bases = new LinkedList<WikiAttachment>();
		List<WikiAttachment> attachments = Environment.getInstance().getWikiConnector().getAttachments(
				title);
		for (WikiAttachment wikiAttachment : attachments) {
			if (wikiAttachment.getFileName().endsWith(CreateBaselineAction.BASELINE_SUFFIX)) {
				bases.add(wikiAttachment);
			}
		}

		return bases;

	}

}
