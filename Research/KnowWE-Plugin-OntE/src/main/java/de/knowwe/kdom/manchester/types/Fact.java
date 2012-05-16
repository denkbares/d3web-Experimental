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

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;

/**
 * <p>
 * {@link AbstractType} for a Facts in the Manchester OWL Syntax.
 * </p>
 * <p>
 * fact ::= [ 'not' ] (objectPropertyFact | dataPropertyFact)
 * </p>
 * 
 * @author Stefan Mark
 * @created 30.09.2011
 */
public class Fact extends AbstractType {

	public static final String PATTERN = "(" + PropertyExpression.PATTERN + ")\\s+("
			+ Literal.PATTERN + "|"
			+ OWLTermReferenceManchester.PATTERN_NON_CAP + ")";

	public Fact() {

		// this.setSectionFinder(new RegexSectionFinder(p));
		AllTextFinderTrimmed sectionFinder1 = new AllTextFinderTrimmed();
		this.setSectionFinder(sectionFinder1);

		// TODO: colon should be captured by keyword already..
		AnonymousType colon = new AnonymousType("colon");
		colon.setSectionFinder(new RegexSectionFinder(":"));
		this.addChildType(colon);

		// blanks as delimiter
		AnonymousType blank = new AnonymousType("whitespace");
		blank.setSectionFinder(new RegexSectionFinder("\\s"));
		this.addChildType(blank);

		// create sectionFinder for PropertyExpression
		AllTextFinderTrimmed sectionFinder2 = new AllTextFinderTrimmed();
		ConstraintSectionFinder csf = new ConstraintSectionFinder(sectionFinder2);
		csf.addConstraint(SingleChildConstraint.getInstance());
		csf.addConstraint(AtMostOneFindingConstraint.getInstance());
		PropertyExpression ope = new PropertyExpression();
		ope.setSectionFinder(csf);

		this.addChildType(ope);
		// this.addChildType(ref);

		this.addChildType(new Literal());
		this.addChildType(new OWLTermReferenceManchester(OWLTermReferenceManchester.PATTERN_NON_CAP));

	}
}