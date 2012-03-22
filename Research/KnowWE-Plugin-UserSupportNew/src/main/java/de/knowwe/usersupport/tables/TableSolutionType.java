/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.usersupport.tables;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.ExactlyOneFindingConstraint;
import de.knowwe.kdom.sectionFinder.NonEmptyLineSectionFinder;
import de.knowwe.kdom.sectionFinder.StringSectionFinderUnquoted;


/**
 * 
 * @author Johannes Dienst
 * @created 11.03.2012
 */
public class TableSolutionType extends AbstractType
{

	public TableSolutionType()
	{
		ConstraintSectionFinder solutionFinder = new ConstraintSectionFinder(
				new NonEmptyLineSectionFinder());
		solutionFinder.addConstraint(ExactlyOneFindingConstraint.getInstance());
		this.setSectionFinder(solutionFinder);

		// cut the optional '{'
		AnonymousType closing = new AnonymousType("bracket");
		closing.setSectionFinder(new StringSectionFinderUnquoted("{"));
		this.addChildType(closing);

		TableSolutionDefinition solDef = new TableSolutionDefinition();
		ConstraintSectionFinder allFinder = new ConstraintSectionFinder(new AllTextFinderTrimmed());
		allFinder.addConstraint(ExactlyOneFindingConstraint.getInstance());
		solDef.setSectionFinder(allFinder);
		this.addChildType(solDef);
	}
}
