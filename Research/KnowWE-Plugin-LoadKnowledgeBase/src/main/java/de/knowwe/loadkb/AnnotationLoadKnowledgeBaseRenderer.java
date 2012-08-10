package de.knowwe.loadkb;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;

public final class AnnotationLoadKnowledgeBaseRenderer implements Renderer {

	public AnnotationLoadKnowledgeBaseRenderer() {
		// TODO Auto-generated constructor stub

	}

	@Override
	public void render(Section<?> section, UserContext user, StringBuilder buffer) {
		// TODO Auto-generated method stub
		String load = section.getText();
		load = load.replaceFirst("@", "");
		buffer.append("\n");
		buffer.append(Strings.maskHTML("<img src='KnowWEExtension/images/ofo.gif'></img> "));

		buffer.append(load).append("\n");
	}

}
