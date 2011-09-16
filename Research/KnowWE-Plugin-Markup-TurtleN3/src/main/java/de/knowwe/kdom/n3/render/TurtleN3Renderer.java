package de.knowwe.kdom.n3.render;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.kdom.n3.TurtleMarkupN3;

public class TurtleN3Renderer extends KnowWEDomRenderer<TurtleMarkupN3> {

	@Override
	public void render(KnowWEArticle article, Section<TurtleMarkupN3> sec, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<pre>"));
		DelegateRenderer.getInstance().render(article, sec, user, string);
		string.append(KnowWEUtils.maskHTML("</pre>"));
		
	}
	
	

}
