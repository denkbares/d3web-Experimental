package de.knowwe.kdom.manchester.types;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

/**
 *
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
public class Facet extends AbstractType {

	public static final String PATTERN = "length|minLength|maxLength|pattern|langRange|<=|<|>=|>";

	public Facet() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN));
	}

	public boolean isMaxInclusive(Section<Facet> facet) {
		return facet.getOriginalText().equals("<=");
	}

	public boolean isMaxExclusive(Section<Facet> facet) {
		return facet.getOriginalText().equals("<");
	}

	public boolean isMinExclusive(Section<Facet> facet) {
		return facet.getOriginalText().equals(">");
	}

	public boolean isMinInclusive(Section<Facet> facet) {
		return facet.getOriginalText().equals(">=");
	}

	public boolean isMaxLength(Section<Facet> facet) {
		return facet.getOriginalText().equals("maxLength");
	}

	public boolean isMinLength(Section<Facet> facet) {
		return facet.getOriginalText().equals("minLength");
	}
	public boolean isLength(Section<Facet> facet) {
		return facet.getOriginalText().equals("length");
	}

	public boolean isPattern(Section<Facet> facet) {
		return facet.getOriginalText().equals("pattern");
	}

	public boolean isLangRange(Section<Facet> facet) {
		return facet.getOriginalText().equals("langRange");
	}
}
