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
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.wikiConnector.WikiAttachment;

/**
 * 
 * @author Reinhard Hatko
 * @created 26.10.2012
 */
public class BaselineRenderer implements Renderer {

	private static DateFormat FORMAT = DateFormat.getDateInstance();

	@Override
	public void render(Section<?> section, UserContext user, RenderResult string) {
		StringBuilder bob = new StringBuilder();
		bob.append("<div class='baselineParent'>");

		BaselineDiff diff = CompareBaselinesAction.getDiff(section, user);
		renderSelection(user, bob, section, diff);

		bob.append("<div class='baselineCompareResult'>");
		if (diff != null) {
			bob.append(CompareBaselinesAction.createDiffHtml(diff));
		}

		bob.append("</div>");
		bob.append("</div>");
		string.appendHTML(bob.toString());
	}

	private static void renderSelection(UserContext user, StringBuilder bob, Section<?> section, BaselineDiff diff) {
		List<Baseline> baselines = loadBaselines(user.getTitle());

		bob.append("<div class='baselineSelection'>");
		if (baselines.isEmpty()) {
			bob.append("There is no baseline to compare against. Please create one first.");
			bob.append("</div>");
			return;
		}
		String selection1 = diff != null ? diff.getBase1().getName() : "";
		String selection2 = diff != null ? diff.getBase2().getName() : "";

		renderDropdown("Differences between", baselines, selection1, bob);
		baselines.add(0, CreateBaselineAction.createCurrentBaseline(user));
		renderDropdown("and", baselines, selection2, bob);
		bob.append("<input type=\"hidden\" value=\"" + section.getID() + "\">");
		bob.append("</div>");
	}

	/**
	 * 
	 * @created 27.10.2012
	 * @param title
	 * @param baselines
	 * @param selection
	 * @param bob
	 */
	public static void renderDropdown(String title, Collection<Baseline> baselines, String selection, StringBuilder bob) {
		bob.append(title);
		bob.append("<select class=\"baselineSelect\">");
		for (Baseline baseline : baselines) {
			bob.append("<option value=\"");
			String name = baseline.getName();
			bob.append(name);
			bob.append("\"");
			if (name.equals(selection)) {
				bob.append(" selected=\"selected\"");
			}
			bob.append(">");
			bob.append(name);
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
