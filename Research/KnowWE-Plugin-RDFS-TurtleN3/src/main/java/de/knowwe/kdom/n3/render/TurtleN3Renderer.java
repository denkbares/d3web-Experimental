package de.knowwe.kdom.n3.render;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.n3.TurtleMarkupN3;

public class TurtleN3Renderer extends KnowWEDomRenderer<TurtleMarkupN3> {

	
	@Override
	public void render(KnowWEArticle article, Section<TurtleMarkupN3> sec, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<pre>"));
		DelegateRenderer.getInstance().render(article, sec, user, string);
		string.append(KnowWEUtils.maskHTML("</pre>"));
		
	}
	
	

}
