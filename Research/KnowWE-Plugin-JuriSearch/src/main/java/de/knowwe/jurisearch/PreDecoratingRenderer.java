package de.knowwe.jurisearch;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class PreDecoratingRenderer extends KnowWEDomRenderer {

	KnowWEDomRenderer renderer = null;

	public PreDecoratingRenderer(KnowWEDomRenderer r) {
		this.renderer = r;
	}

	@Override
	public void render(KnowWEArticle article, Section section, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<pre>"));

		renderer.render(article, section, user, string);

		string.append(KnowWEUtils.maskHTML("</pre>"));

	}

}
