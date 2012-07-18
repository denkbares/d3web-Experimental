package de.knowwe.rdf2go.sparql;

import de.knowwe.core.Environment;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class ArticleLinkNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable) {
		boolean foundArticle = false;
		String[] statements = text.split(" ");
		String[] articleLinks = new String[statements.length];
		for (int i = 0; i < statements.length; i++) {
			String statement = statements[i];
			if (statement.isEmpty()) continue;
			String title = Rdf2GoUtils.trimNamespace(statement);
			if (Environment.getInstance().getWikiConnector().doesArticleExist(title)) {
				foundArticle = true;
				articleLinks[i] = Strings.maskHTML(KnowWEUtils.getURLLinkHTMLToArticle(title));
			}
			else {
				articleLinks[i] = Rdf2GoUtils.reduceNamespace(statement);
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
