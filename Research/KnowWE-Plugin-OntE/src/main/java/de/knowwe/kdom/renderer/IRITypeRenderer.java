package de.knowwe.kdom.renderer;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class IRITypeRenderer<T extends AbstractType> extends KnowWEDomRenderer<T> {

	@Override
	public void render(KnowWEArticle article, Section<T> sec, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML(KnowWEUtils.html_escape(sec.getOriginalText())));
	}
}
