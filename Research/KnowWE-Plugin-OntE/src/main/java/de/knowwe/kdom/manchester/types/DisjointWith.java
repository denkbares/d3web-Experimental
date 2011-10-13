package de.knowwe.kdom.manchester.types;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;

/**
 *
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
public class DisjointWith extends AbstractType {

	public static final String KEYWORD = "DisjointWith[:]?";

	public DisjointWith(String description) {

		if (description == null || description.isEmpty()) {
			throw new IllegalArgumentException(
					"Empty keywords are not allowed for the DisjointWith description!");
		}

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(description, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		this.addChildType(new Keyword(KEYWORD));
	}
}
