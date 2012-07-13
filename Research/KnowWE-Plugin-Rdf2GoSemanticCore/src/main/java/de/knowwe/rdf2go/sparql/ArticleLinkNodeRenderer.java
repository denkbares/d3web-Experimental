package de.knowwe.rdf2go.sparql;

import de.knowwe.core.Environment;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;

public class ArticleLinkNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable) {
		String title = Rdf2GoCore.getInstance().trimNamespace(text);
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
