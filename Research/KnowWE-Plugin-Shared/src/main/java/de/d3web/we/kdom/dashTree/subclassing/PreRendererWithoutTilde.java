package de.d3web.we.kdom.dashTree.subclassing;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWERenderer;
import de.knowwe.core.user.UserContext;

public class PreRendererWithoutTilde implements KnowWERenderer {

	@Override
	public void render(Section sec, UserContext user, StringBuilder string) {
		string.append("{{{");
		StringBuilder dashtreeContent = new StringBuilder();
		DelegateRenderer.getInstance().render(sec, user,
				dashtreeContent);
		String withOutTilde = dashtreeContent.toString().replaceAll("~-",
				"-");
		string.append(withOutTilde);

		string.append("}}}");
	}
}
