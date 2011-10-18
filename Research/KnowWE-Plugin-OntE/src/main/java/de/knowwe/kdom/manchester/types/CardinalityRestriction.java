/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.kdom.manchester.types;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.ExactlyOneFindingConstraint;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 *
 * @author Stefan Mark
 * @created 18.09.2011
 */
public class CardinalityRestriction extends AbstractType {

	public static final String MIN = ManchesterSyntaxKeywords.MIN.getKeyword();
	public static final String MAX = ManchesterSyntaxKeywords.MAX.getKeyword();
	public static final String EXACTLY = ManchesterSyntaxKeywords.EXACTLY.getKeyword();

	public static final String KEYWORD_PATTERN = "("
			+ MIN + "|"
			+ MAX + "|"
			+ EXACTLY
			+ ")";

	public static final String REGEX = Restriction.BEFORE_REGEX
			+ KEYWORD_PATTERN
			+ Restriction.AFTER_INTEGER;

	/**
	 *
	 */
	public CardinalityRestriction(AbstractType type) {

		SectionFinder sf = new RegexSectionFinder(REGEX);
		this.setSectionFinder(sf);

		Pattern p = Pattern.compile(REGEX);
		ConstraintSectionFinder csf = new ConstraintSectionFinder(new RegexSectionFinder(p, 1));
		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
		ObjectPropertyExpression ope = new ObjectPropertyExpression();
		ope.setSectionFinder(csf);
		this.addChildType(ope);

		Keyword key = new Keyword(KEYWORD_PATTERN);
		this.addChildType(key);

		NonNegativeInteger nni = new NonNegativeInteger();
		p = Pattern.compile(Restriction.AFTER_INTEGER);
		nni.setSectionFinder(new RegexSectionFinder(p, 1));
		this.addChildType(nni);

		if (type != null) {
			this.addChildType(type);
		}
	}

	public Section<ObjectPropertyExpression> getObjectProperty(Section<CardinalityRestriction> section) {
		return Sections.findChildOfType(section, ObjectPropertyExpression.class);
	}

	public Integer getDigit(Section<CardinalityRestriction> section) {
		Section<NonNegativeInteger> digit = Sections.findChildOfType(section,
				NonNegativeInteger.class);
		if (digit != null) {
			return Integer.parseInt(digit.getOriginalText());
		}
		return 0;
	}

	public boolean hasOptionalRestriction(Section<CardinalityRestriction> section) {
		return Sections.findChildOfType(section, Restriction.class) != null;
	}

	public Section<Restriction> getOptionalRestriction(Section<CardinalityRestriction> section) {
		return Sections.findChildOfType(section, Restriction.class);
	}

	public boolean isMinRestriction(Section<CardinalityRestriction> section) {
		Section<Keyword> keyword = Sections.findChildOfType(section, Keyword.class);
		if (keyword != null) {
			if (keyword.get().getKeyword(keyword).equals(MIN)) {
				return true;
			}
		}
		return false;
	}

	public boolean isMaxRestriction(Section<CardinalityRestriction> section) {
		Section<Keyword> keyword = Sections.findChildOfType(section, Keyword.class);
		if (keyword != null) {
			if (keyword.get().getKeyword(keyword).equals(MAX)) {
				return true;
			}
		}
		return false;
	}

	public boolean isExactlyRestriction(Section<CardinalityRestriction> section) {
		Section<Keyword> keyword = Sections.findChildOfType(section, Keyword.class);
		if (keyword != null) {
			if (keyword.get().getKeyword(keyword).equals(EXACTLY)) {
				return true;
			}
		}
		return false;
	}
}
