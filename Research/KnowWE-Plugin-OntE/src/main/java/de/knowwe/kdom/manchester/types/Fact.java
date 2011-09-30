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
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;

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

	public static final String PATTERN = "(" + ObjectPropertyExpression.PATTERN + ")\\s+("
			+ OWLTermReferenceManchester.PATTERN + ")";

	public Fact() {

		Pattern p = Pattern.compile(PATTERN);

		this.setSectionFinder(new RegexSectionFinder(p));

		ObjectPropertyExpression ope = new ObjectPropertyExpression();
		ope.setSectionFinder(new RegexSectionFinder(p, 1));

		OWLTermReferenceManchester ref = new OWLTermReferenceManchester();

		this.addChildType(ope);
		this.addChildType(ref);
	}
}