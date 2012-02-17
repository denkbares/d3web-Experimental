package de.knowwe.jurisearch;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class PreDecoratingRenderer implements Renderer {

	Renderer renderer = null;

	public PreDecoratingRenderer(Renderer r) {
		this.renderer = r;
	}

	@Override
	public void render(Section<?> section, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<pre>"));

		renderer.render(section, user, string);

		string.append(KnowWEUtils.maskHTML("</pre>"));

	}

}
