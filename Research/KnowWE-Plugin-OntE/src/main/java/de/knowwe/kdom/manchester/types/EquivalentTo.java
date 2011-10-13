package de.knowwe.kdom.manchester.types;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.frame.DefaultDescription;

/**
 *
 *
 * @author Stefan Mark
 * @created 28.09.2011
 */
public class EquivalentTo extends DefaultDescription {

	public static final String KEYWORD = "EquivalentTo[:]?";

	public EquivalentTo(String description) {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(description, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		this.addChildType(new Keyword(KEYWORD));
	}
}
