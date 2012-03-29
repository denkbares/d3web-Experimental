package de.knowwe.jurisearch;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.EmbracedContentFinder;

public class EmbracedContent extends AbstractType {

	public static final char BRACKET_OPEN = '[';
	public static final char BRACKET_CLOSE = ']';

	public static final String BRACKET_OPEN_REGEX = "\\" + BRACKET_OPEN;
	public static final String BRACKET_CLOSE_REGEX = "\\" + BRACKET_CLOSE;

	public EmbracedContent() {
		this.setSectionFinder(new EmbracedContentFinder(BRACKET_OPEN,
				BRACKET_CLOSE, true));
		this.setRenderer(new StyleRenderer("font-weight:bold"));
	}
}