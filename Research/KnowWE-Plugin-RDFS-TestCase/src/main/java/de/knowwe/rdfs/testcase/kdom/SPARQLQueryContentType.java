/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.rdfs.testcase.kdom;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.n3.TurtleObjectBlankNode;
import de.knowwe.kdom.n3.TurtlePredicate;
import de.knowwe.rdfs.IRITermRef;
import de.knowwe.rdfs.literal.TurtleObjectLiteral;

/**
 * 
 * @author Sebastian Furth
 * @created 02.03.2012
 */
public class SPARQLQueryContentType extends AbstractType {

	public SPARQLQueryContentType() {
		// variables
		addChildType(new VariableType());

		// punctuation
		AnonymousType punctuation = new AnonymousType("Punctuation");
		punctuation.setSectionFinder(new RegexSectionFinder("[.,;]"));
		addChildType(punctuation);

		// literals, blank nodes, numbers
		addChildType(new TurtleObjectLiteral());
		addChildType(new TurtleObjectBlankNode());
		addChildType(new Number());

		// predicates
		addChildType(new TurtlePredicate());
		addChildType(new Colon());

		// TODO: Add support for advanced SPARQL (FILTER etc.)

		// term references (subjects and objects)
		IRITermRef iri = new IRITermRef();
		iri.setSectionFinder(new AllTextFinderTrimmed());
		addChildType(iri);

		setSectionFinder(new AllTextSectionFinder());
	}

}
