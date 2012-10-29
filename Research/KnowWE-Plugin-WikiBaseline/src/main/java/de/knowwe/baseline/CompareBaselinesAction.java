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
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.knowwe.baseline.BaselineDiff.ArticleVersion;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.wikiConnector.WikiAttachment;
import de.knowwe.core.wikiConnector.WikiConnector;

/**
 * 
 * @author Reinhard Hatko
 * @created 26.10.2012
 */
public class CompareBaselinesAction extends AbstractAction {

	private static final String[] CHANGED_HEADER = {
			"Title", "Edits", "Date", "Author", "" };
	private static final String[] ADDED_HEADER = {
			"Title", "Version", "Date", "Author", "" };
	private static final String BASELINE1 = "baseline1";
	private static final String BASELINE2 = "baseline2";

	static class ChangeInfo {

		private final String author;
		private final Date date;
		private final String changeNote;

		public ChangeInfo(String author, Date date, String changeNote) {
			this.author = author;
			this.date = date;
			this.changeNote = changeNote;
		}

		public String getAuthor() {
			return author;
		}

		public Date getDate() {
			return date;
		}

		public String getChangeNote() {
			return changeNote;
		}

	}

	@Override
	public void execute(UserActionContext context) throws IOException {
		String basename1 = context.getParameter(BASELINE1);
		String basename2 = context.getParameter(BASELINE2);

		if (basename1 == null || basename2 == null) {
			// TODO errorhandling
			return;
		}

		Baseline baseline1 = loadBaseline(context, basename1);
		Baseline baseline2 = loadBaseline(context, basename2);

		if (basename1 == null || basename2 == null) {
			// TODO errorhandling
			return;
		}

		BaselineDiff diff = new BaselineDiff(baseline1, baseline2);

		StringBuilder bob = new StringBuilder();
		printDiff(diff, bob);
		context.setContentType("text/html");
		context.getWriter().write(bob.toString());

	}

	/**
	 * 
	 * @created 27.10.2012
	 * @param diff
	 * @param bob
	 */
	private static void printDiff(BaselineDiff diff, StringBuilder bob) {
		if (diff.isEmpty()) {
			bob.append("No changes");
			return;
		}

		printChangedArticles(diff, bob);
		printAddedArticles(diff, bob);
		printRemovedArticles(diff, bob);

	}

	private static void printRemovedArticles(BaselineDiff diff, StringBuilder bob) {
		List<String> removedArticles = new LinkedList<String>(diff.getRemovedArticles());
		if (removedArticles.isEmpty()) return;
		Collections.sort(removedArticles);
		bob.append("<div class=\"baselineRemoved\">");
		bob.append("<span>Deleted articles</span><br>");
		for (String title : removedArticles) {
			bob.append(title);
			bob.append(", ");
		}
		bob.replace(bob.length() - 2, bob.length(), "");
		bob.append("</div>");
		bob.append("\n");

	}

	/**
	 * 
	 * @created 27.10.2012
	 * @param diff
	 * @param bob
	 */
	private static void printAddedArticles(BaselineDiff diff, StringBuilder bob) {
		Collection<ArticleVersion> addedArticles = diff.getAddedArticles();
		if (addedArticles.isEmpty()) return;
		bob.append("<div class=\"baselineAdded\">");
		createTable(bob, "New articles");
		insertHeaders(bob, ADDED_HEADER);
		bob.append("<tbody>");
		for (ArticleVersion info : addedArticles) {
			ArticleVersion oldInfo = BaselineDiff.createArticleInfo(info.getTitle(), 1);
			appendInfoRow(oldInfo, info, bob, true);
			appendDetailsRow(oldInfo, info, bob, true);

		}
		bob.append("</tbody>");
		bob.append("</table>");
		bob.append("</div>");
		bob.append("\n");

	}

	private static void printChangedArticles(BaselineDiff diff, StringBuilder bob) {
		Map<String, ArticleVersion[]> articles = diff.getChangedArticles();
		if (articles.isEmpty()) return;
		bob.append("<div class=\"baselineChanged\">");
		createTable(bob, "Changed articles");
		insertHeaders(bob, CHANGED_HEADER);
		bob.append("<tbody>");
		for (String title : articles.keySet()) {
			ArticleVersion[] infos = articles.get(title);
			appendInfoRow(infos[0], infos[1], bob, false);
			appendDetailsRow(infos[0], infos[1], bob, false);

		}

		bob.append("</tbody>");
		bob.append("</table>");
		bob.append("</div>");
		bob.append("\n");

	}



	/**
	 * Adds a table row for one changed article
	 */
	private static void appendInfoRow(ArticleVersion info1, ArticleVersion info2, StringBuilder bob, boolean includeFirst) {
		bob.append("<tr class=\"infoRow\">");
		appendDataCells(bob, createLink(info1.getTitle()));
		int changes = info2.getVersion() - info1.getVersion();
		// for new articles, we also want to count the first version
		if (includeFirst) changes++;
		appendDataCells(bob, String.valueOf(changes));
		appendDataCells(bob, format(info2.getLastModDate()));
		appendDataCells(bob, info2.getLastEditor());
		bob.append("<td><div class=\"detailsArrow\" alt=\"Show details\"></div></td>");
		bob.append("</tr>\n");
	}

	private static void appendDetailsRow(ArticleVersion info1, ArticleVersion info2, StringBuilder bob, boolean includeFirst) {

		bob.append("<tr class=\"detailsRow\">");
		bob.append("<td colspan=\"" + CHANGED_HEADER.length + "\">");
		bob.append("<span>Detailed history:</span>");
		bob.append("<ul class=\"changeList\">");
		Map<Integer, ChangeInfo> changes = getAllChanges(info1.getTitle(), info1.getVersion(),
				info2.getVersion(), includeFirst);
		for (Integer v : changes.keySet()) {
			bob.append("<li>");
			ChangeInfo change = changes.get(v);
			bob.append(v);
			bob.append(". ");
			bob.append(createDiffLink(info1.getTitle(), v.intValue() - 1, v.intValue(),
					format(change.getDate())));
			bob.append(" ");
			bob.append(change.getAuthor());
			bob.append(": ");

			String note = change.getChangeNote();
			if (!note.isEmpty()) {
				bob.append(note);
			}
			else {
				bob.append("(no change note)");

			}
			bob.append("</li>");
		}

		bob.append("</ul>");
		bob.append("</td>");
		bob.append("</tr>\n");
	}

	public static Map<Integer, ChangeInfo> getAllChanges(String title, int v1, int v2, boolean includeFirst) {
		Map<Integer, ChangeInfo> result = new LinkedHashMap<Integer, ChangeInfo>();
		WikiConnector connector = Environment.getInstance().getWikiConnector();
		int last = includeFirst ? v1 - 1 : v1;
		for (int i = v2; i > last; i--) {
			String note = connector.getChangeNote(title, i);
			Date date = connector.getLastModifiedDate(title, i);
			String author = connector.getAuthor(title, i);
			result.put(Integer.valueOf(i), new ChangeInfo(author, date, note));
		}

		return result;
	}


	public static String format(Date date) {
		return DateFormat.getDateTimeInstance().format(date);
	}

	/**
	 * 
	 * @created 27.10.2012
	 * @param title
	 * @param version
	 * @return
	 */
	private static String createLink(String title, int version, String description) {
		return "<a href='" + KnowWEUtils.getURLLink(title, version) + "' >" + description + "</a>";
	}

	private static String createLink(String title) {
		return createLink(title, -1, title);
	}

	private static String createDiffLink(String title, int v1, int v2, String description) {
		if (v1 == 0) {
			return description;
		}
		else {
			return "<a href='" + KnowWEUtils.getDiffURLLink(title, v1, v2) + "' >" + description
					+ "</a>";
		}
	}

	private static void insertHeaders(StringBuilder bob, String[] headers) {
		bob.append("<thead>");
		bob.append("<tr>");
		appendHeaderCells(bob, headers);
		bob.append("</tr>");
		bob.append("</thead>");
		bob.append("\n");
	}


	private static void appendDataCells(StringBuilder bob, String... cells) {
		appendCell(bob, "td", cells);
	}

	private static void appendHeaderCells(StringBuilder bob, String... cells) {
		appendCell(bob, "th", cells);
	}

	private static void appendCell(StringBuilder bob, String type, String... cells) {
		for (String cell : cells) {
			bob.append("<");
			bob.append(type);
			bob.append(">");
			bob.append(cell);
			bob.append("</");
			bob.append(type);
			bob.append(">");
		}
	}

	private static void createTable(StringBuilder bob, String caption) {
		bob.append("<table class=\"wikitable changeTable\">");
		bob.append("<caption>");
		bob.append(caption);
		bob.append("</caption>");
		bob.append("\n");

	}

	/**
	 * 
	 * @created 26.10.2012
	 * @param context
	 * @param baseName
	 * @return
	 * @throws IOException
	 */
	private static Baseline loadBaseline(UserActionContext context, String baseName) throws IOException {
		if (baseName.equals(CreateBaselineAction.NAME_CURRENT)) {
			return CreateBaselineAction.createNewBaseline(CreateBaselineAction.NAME_CURRENT, "",
					context.getWeb());
		}

		WikiAttachment attachment = Environment.getInstance().getWikiConnector().getAttachment(
				context.getTitle() + "/" + baseName + CreateBaselineAction.BASELINE_SUFFIX);
		return loadBaseline(attachment);

	}

	/**
	 * 
	 * @created 27.10.2012
	 * @param attachment
	 * @return
	 * @throws IOException
	 */
	public static Baseline loadBaseline(WikiAttachment attachment) throws IOException {
		InputStream stream = attachment.getInputStream();

		Baseline baseline;
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
			NamedNodeMap attributes = doc.getElementsByTagName("baseline").item(0).getAttributes();
			String name = attributes.getNamedItem("name").getNodeValue();
			long date = Long.parseLong(attributes.getNamedItem("date").getNodeValue());
			String username = attributes.getNamedItem("user").getNodeValue();

			baseline = new Baseline(name, date, username);

			NodeList articles = doc.getElementsByTagName("article");

			for (int i = 0, size = articles.getLength(); i < size; i++) {
				Node item = articles.item(i);
				int version = Integer.parseInt(item.getAttributes().getNamedItem("version").getNodeValue());
				String title = item.getFirstChild().getNodeValue();
				baseline.addArticle(title, version);
			}

			return baseline;

		}
		catch (Exception e) {
			throw new IOException(e);
		}
		finally {
			stream.close();
		}
	}

}
