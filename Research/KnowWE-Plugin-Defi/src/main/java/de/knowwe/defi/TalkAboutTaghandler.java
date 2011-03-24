package de.knowwe.defi;

import java.util.Map;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * The TalkAboutTaghandler adds a link into any Wiki article. It links to a page
 * containing a forum. For more information on the forum see the
 * KnowWE-Plugin-Comment. Please use the following syntax:
 * 
 * <blockquote> [{KnowWEPlugin talkabout , title=LinkName}] </blockquote>
 * 
 * The title attribute is optional. Only used for the HTML title attribute.
 * 
 * @author smark
 * @created 24.03.2011
 */
public class TalkAboutTaghandler extends AbstractTagHandler {

	public TalkAboutTaghandler() {
		super("talkabout");
	}


	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {

		StringBuilder talkAbout = new StringBuilder();

		String talkPage = userContext.getUserName() + "_comment_therapist";

		String title = parameters.get("title");

		if (title == null ) {
			title = " &raquo; " + talkPage;
		}

		talkAbout.append("<a href=\"Wiki.jsp?page=");
		talkAbout.append(KnowWEUtils.urlencode(talkPage.trim()));
		talkAbout.append("\" title=\"Title:");
		talkAbout.append(title);
		talkAbout.append("\" rel=\"nofollow\">");
		talkAbout.append("Mit Therapeuten dar&uuml;ber sprechen");
		talkAbout.append("</a>");

		return KnowWEUtils.maskHTML(talkAbout.toString());
	}
}
