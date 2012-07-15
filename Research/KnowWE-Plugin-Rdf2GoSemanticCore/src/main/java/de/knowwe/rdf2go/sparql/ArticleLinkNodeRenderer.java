package de.knowwe.rdf2go.sparql;

import de.knowwe.core.Environment;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class ArticleLinkNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable) {
		String title = Rdf2GoUtils.trimNamespace(text);
		if (Environment.getInstance().getWikiConnector().doesArticleExist(title)) {
			return Strings.maskHTML(KnowWEUtils.getURLLinkHTMLToArticle(title));
		}
		return text;
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return false;
	}

}
