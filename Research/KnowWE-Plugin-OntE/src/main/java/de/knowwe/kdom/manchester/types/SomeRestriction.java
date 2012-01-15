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
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.ManchesterClassExpression.OWLClassContentType;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 *
 * @author Stefan Mark
 * @created 18.05.2011
 */
public class SomeRestriction extends AbstractType {

	/**
	 * Keyword for the <code>some</code> restriction.
	 */
	public static final String KEY = ManchesterSyntaxKeywords.SOME.getKeyword();

	/**
	 *
	 */
	public SomeRestriction(String keyword, boolean isData) {

		String REGEX = Restriction.BEFORE_REGEX + keyword + Restriction.AFTER_REGEX;

		SectionFinder sf = new RegexSectionFinder(REGEX, Pattern.DOTALL);
		this.setSectionFinder(sf);

		Pattern p = Pattern.compile(REGEX, Pattern.DOTALL);
		ConstraintSectionFinder csf = new ConstraintSectionFinder(new RegexSectionFinder(p, 1));
		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());

		PropertyExpression ope = new PropertyExpression();
		ope.setSectionFinder(csf);
		this.addChildType(ope);

		Keyword key = new Keyword(keyword);
		this.addChildType(key);

		if (isData) {
			this.addChildType(ManchesterSyntaxUtil.getDataRangeExpression());
		}
		else {
			this.addChildType(OWLClassContentType.getCompositeCondition());
		}
	}

	public boolean isObjectPropertyExpression(Section<SomeRestriction> section) {

		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			return keyword.getOriginalText().equals(ManchesterSyntaxKeywords.SOME.getKeyword());
		}
		return false;
	}

	public boolean isDataPropertyExpression(Section<SomeRestriction> section) {

		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			return keyword.getOriginalText().equals(ManchesterSyntaxKeywords.SOME_.getKeyword());
		}
		return false;
	}

	public Section<PropertyExpression> getObjectProperty(Section<SomeRestriction> section) {
		return Sections.findChildOfType(section, PropertyExpression.class);
	}

	public Section<ManchesterClassExpression> getManchesterClassExpression(Section<SomeRestriction> section) {
		return Sections.findChildOfType(section, ManchesterClassExpression.class);
	}

	public Section<DataRangeExpression> getDataRangeExpression(Section<SomeRestriction> section) {
		return Sections.findChildOfType(section, DataRangeExpression.class);
	}

}