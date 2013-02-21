package de.knowwe.loadkb;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;

public final class AnnotationLoadKnowledgeBaseRenderer implements Renderer {

	public AnnotationLoadKnowledgeBaseRenderer() {
		// TODO Auto-generated constructor stub

	}

	@Override
	public void render(Section<?> section, UserContext user, RenderResult buffer) {
		// TODO Auto-generated method stub
		String load = section.getText();
		load = load.replaceFirst("@", "");
		buffer.append("\n");
		buffer.appendHTML("<img src='KnowWEExtension/images/ofo.gif'></img> ");
		buffer.append(load);
	}

}
