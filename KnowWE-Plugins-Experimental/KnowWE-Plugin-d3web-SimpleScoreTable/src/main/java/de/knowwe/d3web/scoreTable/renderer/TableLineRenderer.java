package de.knowwe.d3web.scoreTable.renderer;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;

public class TableLineRenderer implements Renderer {

	@Override
	public void render(Section<?> section, UserContext user,
			RenderResult string) {

		string.appendHtml("<tr>");
		DelegateRenderer.getInstance().render(section, user, string);
		string.appendHtml("</tr>");

	}

}
