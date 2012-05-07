package de.knowwe.d3web.scoreTable.renderer;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;

public class TableLineRenderer implements Renderer {

	@Override
	public void render(Section<?> section, UserContext user,
			StringBuilder string) {

		string.append(Strings.maskHTML("<tr>"));
		DelegateRenderer.getInstance().render(section, user, string);
		string.append(Strings.maskHTML("</tr>"));

	}

}
