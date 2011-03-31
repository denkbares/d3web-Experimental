/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3web.owl.assignment;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.AllTextFinderDivCorrectTrimmed;
import de.knowwe.core.CommentLineType;

/**
 *
 * @author Sebastian Furth
 * @created Mar 30, 2011
 */
public class AssignmentContentType extends AbstractType {

	public AssignmentContentType() {
		/* We take almost the whole content */
		this.sectionFinder = new AllTextFinderDivCorrectTrimmed();
		/* ChildrenTypes */
		this.childrenTypes.add(new CommentLineType());
		this.childrenTypes.add(new ChoiceValueAssignmentType());
		this.childrenTypes.add(new YesNoAssignmentType());
		this.childrenTypes.add(new RatingAssignmentType());
		this.childrenTypes.add(new ActionAssignmentType());
	}

}
