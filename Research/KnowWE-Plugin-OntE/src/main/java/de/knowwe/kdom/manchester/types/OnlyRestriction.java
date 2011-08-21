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

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.ExactlyOneFindingConstraint;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.knowwe.kdom.manchester.ManchesterClassExpression.OWLClassContentType;

/**
 * 
 * @author smark
 * @created 18.05.2011
 */
public class OnlyRestriction extends AbstractType {

	/**
	 * Keyword for the <code>only</code> restriction.
	 */
	public static final String KEY = "ONLY";

	public static final String REGEX = Restriction.BEFORE_REGEX + KEY + Restriction.AFTER_REGEX;

	/**
	 *
	 */
	public OnlyRestriction() {

		SectionFinder sf = new RegexSectionFinder(REGEX, Pattern.DOTALL);
		this.setSectionFinder(sf);

		Pattern p = Pattern.compile(REGEX, Pattern.DOTALL);
		ConstraintSectionFinder csf = new ConstraintSectionFinder(new RegexSectionFinder(p, 1));
		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());

		ObjectPropertyExpression ope = new ObjectPropertyExpression();
		ope.setSectionFinder(csf);
		this.addChildType(ope);

		Keyword key = new Keyword(KEY);
		this.addChildType(key);

		this.addChildType(OWLClassContentType.getCompositeCondition());
	}
}