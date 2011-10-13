package de.knowwe.kdom.manchester.types;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;

/**
 *
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
public class Domain extends AbstractType {

	public static final String KEYWORD = "Domain[:]?";

	public Domain(String description) {
		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(description, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		Keyword key = new Keyword(KEYWORD);
		this.addChildType(key);

		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}
