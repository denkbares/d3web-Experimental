package de.knowwe.kdom.manchester.types;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;

/**
 *
 *
 * @author Stefan Mark
 * @created 28.09.2011
 */
public class SubPropertyOf extends AbstractType {

	public static final String KEYWORD = "SubPropertyOf[:]?";

	public SubPropertyOf(String description) {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(description, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		Keyword key = new Keyword(KEYWORD);
		this.addChildType(key);
	}
}
