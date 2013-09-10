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

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.basicType.EmbracedType;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.n3.TurtlePredicate;
import de.knowwe.rdfs.IRITermRef;

/**
 * 
 * @author Sebastian Furth
 * @created 10.02.2012
 */
public class SPARQLQueryType extends AbstractType {

	private static final String SELECT = "SELECT";
	private static final String WHERE = "WHERE";

	public SPARQLQueryType() {
		// SELECT
		AnonymousType select = new AnonymousType(SELECT);
		select.setSectionFinder(new RegexSectionFinder(SELECT, Pattern.CASE_INSENSITIVE));
		addChildType(select);

		// WHERE
		AnonymousType where = new AnonymousType(WHERE);
		where.setSectionFinder(new RegexSectionFinder(WHERE, Pattern.CASE_INSENSITIVE));
		addChildType(where);

		// Triples
		EmbracedType triples = new EmbracedType(new SPARQLQueryContentType(), "{", "}");
		addChildType(triples);

		// Variables
		addChildType(new VariableType());
	}

	public static String getSPARQLQuery(Section<SPARQLQueryType> sparqlSection) {
		StringBuilder query = new StringBuilder();
		appendRecursively(sparqlSection, query);
		return query.toString();
	}

	private static void appendRecursively(Section<?> section, StringBuilder query) {
		for (Section<?> child : section.getChildren()) {
			if (child.getChildren().size() > 0 && !(child.get() instanceof TurtlePredicate)) {
				appendRecursively(child, query);
			}
			else if (child.get() instanceof IRITermRef || child.get() instanceof TurtlePredicate) {
				IRITermRef ref = (IRITermRef) child.get();
				@SuppressWarnings("unchecked")
				String iri = "lns:" + ref.getTermIdentifier((Section<? extends Term>) child);
				query.append(iri);
			}
			else if (child.get() instanceof Colon) {
				continue;
			}
			else {
				query.append(child.getText());
			}
		}

	}
}
