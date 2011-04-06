package de.knowwe.caseTrain;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class DivStyleClassRenderer extends KnowWEDomRenderer<Type> {
	
	String cssClass;
	
	public DivStyleClassRenderer(String s) {
		this.cssClass = s;
	}
	
	@Override
	public void render(KnowWEArticle article, Section<Type> sec, UserContext user, StringBuilder string) {
	string.append(KnowWEUtils.maskHTML("<div class='"
				+ cssClass
			+ "'>"));
	DelegateRenderer.getInstance().render(article, sec, user, string);
	string.append(KnowWEUtils.maskHTML("</div>"));

	}


}
