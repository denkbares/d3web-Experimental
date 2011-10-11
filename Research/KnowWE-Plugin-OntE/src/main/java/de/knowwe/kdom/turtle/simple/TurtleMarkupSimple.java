/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.knowwe.kdom.turtle.simple;

import java.util.regex.Pattern;

import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.turtle.TurtleRDF2GoCompiler;
import de.knowwe.termObject.IRITermReference;

public class TurtleMarkupSimple extends AbstractType{

	public TurtleMarkupSimple() {

		this.setSectionFinder(new RegexSectionFinder("\\{(.*?::.*?)\\}", Pattern.DOTALL,1));

		this.addChildType(new SimpleTurtlePredicate());
	
		this.addChildType(new SimpleTurtleSubject());

		this.addChildType(new SimpleTurtleObject());

		this.addSubtreeHandler(Priority.LOWEST, new TurtleRDF2GoCompiler());

	}
	
	class SimpleTurtlePredicate extends IRITermReference {
		public SimpleTurtlePredicate (){
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new RegexSectionFinder("\\b([^\\s]*)::", Pattern.DOTALL, 1));
			c.addConstraint(SingleChildConstraint.getInstance());
			c.addConstraint(AtMostOneFindingConstraint.getInstance());
			this.setSectionFinder(c);
		}
	}
	
	class SimpleTurtleSubject extends IRITermReference {
		public SimpleTurtleSubject() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			c.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(c);
		}
		
	}
	
	class SimpleTurtleObject extends IRITermReference {
		public SimpleTurtleObject() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new RegexSectionFinder("::\\s(.*)",Pattern.DOTALL,1));
			c.addConstraint(SingleChildConstraint.getInstance());
			//c.addConstraint(new NonEmptyConstraint());
			this.setSectionFinder(c);
		}
	}
}
