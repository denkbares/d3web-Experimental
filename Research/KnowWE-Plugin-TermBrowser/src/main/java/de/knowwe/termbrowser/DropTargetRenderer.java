package de.knowwe.termbrowser;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;

public class DropTargetRenderer implements Renderer {

	public void renderPre(Section<?> section, UserContext user, RenderResult string) {
		if (section.getText().trim().length() > 3) {
			string.appendHtml("<div style='display:inline;' dragdropid='" + section.getID()
					+ "' class='dropTargetMarkup'>");

		}
	}

	public void renderPost(Section<?> section, UserContext user, RenderResult string) {
		if (section.getText().trim().length() > 3) {
			string.appendHtml("</div>");
		}
	}

	@Override
	public void render(Section<?> section, UserContext user, RenderResult result) {
		renderPre(section, user, result);

		// call original (and potentially additional plugged) renderers
		Renderer nextRendererForType = Environment.getInstance().getNextRendererForType(
				section.get(), this);
		if (nextRendererForType != null) {
			nextRendererForType.render(section, user, result);
		}

		renderPost(section, user, result);

	}

}
