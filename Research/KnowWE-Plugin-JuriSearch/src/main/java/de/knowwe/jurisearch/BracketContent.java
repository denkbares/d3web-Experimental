package de.knowwe.jurisearch;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.EmbracedContentFinder;

public class BracketContent extends AbstractType {

	public static final char BRACKET_OPEN_CHAR = '[';
	public static final char BRACKET_CLOSE_CHAR = ']';

	public static final String BRACKET_OPEN = "\\" + BRACKET_OPEN_CHAR;
	public static final String BRACKET_CLOSE = "\\" + BRACKET_CLOSE_CHAR;

	public BracketContent() {
		this.setSectionFinder(new EmbracedContentFinder(BRACKET_OPEN_CHAR,
				BRACKET_CLOSE_CHAR, true));
		this.setRenderer(new StyleRenderer("font-weight:bold"));
	}
}