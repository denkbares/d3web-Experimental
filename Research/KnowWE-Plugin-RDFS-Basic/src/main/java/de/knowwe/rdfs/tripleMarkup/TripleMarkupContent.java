/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.rdfs.tripleMarkup;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.TypeRestrictedReference;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.rdfs.IRITermRef;
import de.knowwe.rdfs.RDFSTermCategory;
import de.knowwe.rdfs.literal.TurtleObjectLiteral;

class TripleMarkupContent extends AbstractType {

	public TripleMarkupContent() {
		this.setSectionFinder(new RegexSectionFinder(TripleMarkup.TRIPLE_REGEX,
				Pattern.DOTALL | Pattern.MULTILINE, 1));
		this.addChildType(new SimpleTurtlePredicate());
		this.addChildType(new SimpleTurtleSubjectSection());
		this.addChildType(new SimpleTurtleObjectSection());

		this.setRenderer(new RangeCheckRenderer());
	}

	class SimpleTurtlePredicate extends IRITermRef implements TypeRestrictedReference {

		public SimpleTurtlePredicate() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new RegexSectionFinder("\\b([^\\s]*)::", Pattern.DOTALL, 1));
			c.addConstraint(SingleChildConstraint.getInstance());
			c.addConstraint(AtMostOneFindingConstraint.getInstance());
			this.setSectionFinder(c);
		}

		@Override
		public boolean checkTypeConstraints(Section<? extends Term> s) {
			Object info = IncrementalCompiler.getInstance().getTerminology().getDefinitionInformationForValidTerm(
					s.get().getTermIdentifier(s));
			if (info != null) {
				if (info instanceof Map) {
					Map<String, ? extends Object> map = (Map<String, ? extends Object>) info;
					Set<String> keyset = map.keySet();
					for (Object key : keyset) {
						if (map.get(key) instanceof RDFSTermCategory) {
							RDFSTermCategory rdfsTermCategory = (RDFSTermCategory) map.get(key);
							if (rdfsTermCategory.equals(RDFSTermCategory.Class)
									|| rdfsTermCategory.equals(RDFSTermCategory.Individual)) {
								return false;
							}
						}
					}
				}

			}
			return true;
		}

		@Override
		public String getMessageForConstraintViolation(Section<? extends Term> s) {
			return "only properties allowed here";
		}
	}

}

class SimpleTurtleObjectSection extends AbstractType {

	public SimpleTurtleObjectSection() {
		ConstraintSectionFinder c = new ConstraintSectionFinder(
				new RegexSectionFinder("::(.*)", Pattern.DOTALL, 1));
		c.addConstraint(SingleChildConstraint.getInstance());
		this.setSectionFinder(c);

		this.addChildType(new TurtleObjectLiteral());
		this.addChildType(new SimpleTurtleObjectRef());

	}
}

class SimpleTurtleObjectRef extends IRITermRef {

	public SimpleTurtleObjectRef() {
		this.setSectionFinder(new AllTextFinderTrimmed());
	}
}

class SimpleTurtleSubjectSection extends AbstractType {

	/**
	 * 
	 */
	public SimpleTurtleSubjectSection() {
		ConstraintSectionFinder c = new ConstraintSectionFinder(
				new AllTextFinderTrimmed());
		c.addConstraint(SingleChildConstraint.getInstance());
		this.setSectionFinder(c);

		this.addChildType(new SimpleTurtleSubject());
	}

}

class SimpleTurtleSubject extends IRITermRef {

	public SimpleTurtleSubject() {
		this.setSectionFinder(new AllTextFinderTrimmed());
	}

}
