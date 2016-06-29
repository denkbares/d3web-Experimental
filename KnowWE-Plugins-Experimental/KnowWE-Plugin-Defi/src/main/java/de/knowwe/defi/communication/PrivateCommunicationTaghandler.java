package de.knowwe.defi.communication;

import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;

import de.knowwe.comment.forum.Forum;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.defi.forum.DiscussionUtils;

public class PrivateCommunicationTaghandler extends AbstractTagHandler {

	public PrivateCommunicationTaghandler() {
		super("privatecom");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
		StringBuilder html = new StringBuilder();
		String to;
		ResourceBundle rb = ResourceBundle.getBundle("KnowWE_Defi_config");
		if (parameters.containsKey("to")) to = parameters.get("to");
		else to = rb.getString("defi.berater");
		html.append(renderPrivateCommunicationFrame(userContext, to));
		result.appendHtml(html.toString());
	}

	public String renderPrivateCommunicationFrame(UserContext context, String to) {
		StringBuilder html = new StringBuilder();
		String[] names = {
				context.getUserName(), to };
		Arrays.sort(names);
		String forumPagename = "Persoenliche Nachrichten(" + names[0] + "," + names[1] + ")";
		int numberOfNewEntries;
		Section<? extends Forum> forum = null;

		if (Environment.getInstance().getWikiConnector().doesArticleExist(forumPagename)) {
			Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
					forumPagename);
			forum = Sections.successor(article.getRootSection(),
					Forum.class);
		}

		if (forum == null) numberOfNewEntries = 0;
		else numberOfNewEntries = DiscussionUtils.getNumberOfNewEntriesInForum(forum,
				context.getUserName());

		if (numberOfNewEntries != 0) html.append("<div class='privatecom' style='border-right: 30px solid green'>");
		else html.append("<div class='privatecom' style='border-right: 30px solid grey'>");

		html.append("<span>Private Kommunikation mit ").append(to).append("</span>");
		html.append("<input type='button' value='Pers&ouml;nliche Nachricht' onclick='newChat(\"")
				.append(names[0])
				.append("\", \"")
				.append(names[1])
				.append("\");return false' />");
		html.append("</div>");

		return html.toString();
	}
}
