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
package de.knowwe.rdfs.testcase.kdom;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.basicType.PlainText;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.sectionFinder.AllBeforeTypeSectionFinder;

/**
 * 
 * @author Sebastian Furth
 * @created 20.12.2011
 */
public class RDFSTestCaseContentType extends AbstractType {

	public static final String SEPARATOR = "<=>";

	public RDFSTestCaseContentType() {
		// specify and configure types
		ExpectedBindingsType expectedBindings = new ExpectedBindingsType();
		SPARQLQueryType sparqlQuery = new SPARQLQueryType();
		PlainText arrow = new PlainText();
		arrow.setSectionFinder(new RegexSectionFinder(SEPARATOR));
		sparqlQuery.setSectionFinder(new AllBeforeTypeSectionFinder(arrow));

		// add them as children
		addChildType(expectedBindings);
		addChildType(arrow);
		addChildType(sparqlQuery);

		// add subtree handler
		addSubtreeHandler(new RDFSTestCaseHandler());

		// set section finder
		setSectionFinder(new AllTextSectionFinder());
	}

}
