package de.knowwe.kdom.renderer;

import de.d3web.strings.Strings;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;

public class IRITypeRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user, RenderResult string) {
		string.appendHtml(Strings.encodeHtml(sec.getText()));
	}
}
