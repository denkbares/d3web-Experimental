package de.knowwe.rdf2go.sparql;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class ArticleLinkNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable, UserContext user, Rdf2GoCore core) {
		boolean foundArticle = false;
		String lns = Rdf2GoCore.getInstance().getLocalNamespace();

		String[] statements;
		// We are only interested in statements from the local name space.
		// Other name spaces or no name space (simple string) probably is not
		// representing an article in the wiki.
		if (text.startsWith(lns)) {
			statements = text.split(" ");
		}
		else {
			return text;
		}

		String[] articleLinks = new String[statements.length];
		for (int i = 0; i < statements.length; i++) {
			String statement = statements[i];
			statement = Strings.decodeURL(statement);
			if (statement.isEmpty()) continue;
			String title = Rdf2GoUtils.trimNamespace(core, statement);
			if (Environment.getInstance().getArticleManager(Environment.DEFAULT_WEB)
					.getTitles().contains(title)) {
				foundArticle = true;
				articleLinks[i] = new RenderResult(user).appendHtml(
						KnowWEUtils.getLinkHTMLToArticle(title)).toStringRaw();
			}
			else {
				articleLinks[i] = Rdf2GoUtils.reduceNamespace(core, statement);
			}
		}
		if (foundArticle) {
			boolean first = true;
			StringBuilder links = new StringBuilder();
			for (String articleLink : articleLinks) {
				if (first) {
					first = false;
				}
				else {
					links.append(", ");
				}
				links.append(articleLink);
			}
			return links.toString();
		}
		return text;
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return false;
	}

}
