package de.d3web.we.kdom.dashTree.subclassing;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class PreRendererWithoutTilde extends KnowWEDomRenderer {
	
	
	@Override
	public void render(KnowWEArticle article, Section sec, KnowWEUserContext user, StringBuilder string) {
		string.append("{{{");
		StringBuilder dashtreeContent = new StringBuilder();
		DelegateRenderer.getInstance().render(article, sec,
				user, dashtreeContent);
		String withOutTilde = dashtreeContent.toString().replaceAll("~-",
				"-");
		string.append(withOutTilde);

		string.append("}}}");
	}
}


