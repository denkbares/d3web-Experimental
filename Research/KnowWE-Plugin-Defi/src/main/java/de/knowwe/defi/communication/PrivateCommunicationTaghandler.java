package de.knowwe.defi.communication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import de.knowwe.comment.forum.Forum;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.defi.forum.ForumMenuTagHandler;


public class PrivateCommunicationTaghandler extends AbstractTagHandler {

	public PrivateCommunicationTaghandler() {
		super("privatecom");
	}

	@Override
	public String render(Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder html = new StringBuilder();
		String to;
		ResourceBundle rb = ResourceBundle.getBundle("KnowWE_Defi_config");
		
		if (parameters.containsKey("to")) to = parameters.get("to");
		else to = rb.getString("defi.berater");
		
		html.append(renderPrivateCommunicationFrame(userContext, to));
		
		return Strings.maskHTML(html.toString());
	}

	public String renderPrivateCommunicationFrame(UserContext context, String to) {
		StringBuilder html = new StringBuilder();
		String[] names = {
				context.getUserName(), to };
		Arrays.sort(names);
		String forumPagename = "Persoenliche Nachrichten(" + names[0] + "," + names[1] + ")";
		ForumMenuTagHandler fth = new ForumMenuTagHandler();
		HashMap<String, String> log = fth.checkLog(context);
		String lastVisit = log.get(forumPagename);
		int numberOfNewEntries;
		Section<? extends Forum> forum = null;

		if (Environment.getInstance().getWikiConnector().doesArticleExist(forumPagename)) {
			Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
					forumPagename);
			forum = Sections.findSuccessor(article.getRootSection(),
					Forum.class);
		}

		if (forum == null) numberOfNewEntries = 0;
		else {

			if (lastVisit == null) {
				lastVisit = "01.01.1900 00:00";
				// Benutzer = Autor?
				if (context.getUserName().equals(fth.getAuthor(forum.getText()))) {
					// Gibt es mehr als den eigenen Eintrag?
					numberOfNewEntries = fth.getNumberOfNewEntries(forum.getText(),
							lastVisit) - 1;
				}
				else {
					numberOfNewEntries = fth.getNumberOfNewEntries(forum.getText(),
							lastVisit);
				}
			}
			else {
				numberOfNewEntries = fth.getNumberOfNewEntries(forum.getText(),
						lastVisit);
			}
		}

		if (numberOfNewEntries > 0) html.append("<div class='privatecom' style='border-right: 30px solid green'>");
		else html.append("<div class='privatecom' style='border-right: 30px solid grey'>");

		html.append("<span>Private Kommunikation mit " + to + "</span>");
		html.append("<input type='button' value='Pers&ouml;nliche Nachricht' onclick='newChat(\""
				+ names[0] + "\", \"" + names[1]
				+ "\");return false' />");

		html.append("</div>");

		return html.toString();
	}

}
