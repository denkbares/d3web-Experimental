package de.knowwe.d3web.scoreTable.renderer;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class TableLineRenderer extends KnowWEDomRenderer {

	@Override
	public void render(KnowWEArticle article, Section section,
			UserContext user, StringBuilder string) {

		string.append(KnowWEUtils.maskHTML("<tr>"));
		DelegateRenderer.getInstance().render(article, section, user, string);
		string.append(KnowWEUtils.maskHTML("</tr>"));
		
	}

}
