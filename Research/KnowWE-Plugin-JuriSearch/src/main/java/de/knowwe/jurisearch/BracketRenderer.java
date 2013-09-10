package de.knowwe.jurisearch;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;

public class BracketRenderer implements Renderer {

	@Override
	public void render(Section<?> section, UserContext user, StringBuilder string) {
		string.append("~");
		DelegateRenderer.getInstance().render(section, user, string);
	}

}