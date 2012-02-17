package de.knowwe.kdom.renderer;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWERenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class IRITypeRenderer<T extends AbstractType> implements KnowWERenderer<T> {

	@Override
	public void render(Section<T> sec, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML(KnowWEUtils.escapeHTML(sec.getText())));
	}
}
