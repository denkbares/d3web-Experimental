package de.knowwe.kdom.renderer;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.Strings;

public class IRITypeRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder string) {
		string.append(Strings.maskHTML(KnowWEUtils.escapeHTML(sec.getText())));
	}
}
