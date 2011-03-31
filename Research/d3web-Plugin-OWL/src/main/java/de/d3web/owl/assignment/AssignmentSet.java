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
package de.d3web.owl.assignment;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;

/**
 *
 * @author Sebastian Furth
 * @created Mar 29, 2011
 */
public class AssignmentSet implements KnowledgeSlice {

	public final static KnowledgeKind<AssignmentSet> KNOWLEDGE_KIND =
			new KnowledgeKind<AssignmentSet>("AssignmentSet", AssignmentSet.class);

	private final Collection<Assignment> assignments = new HashSet<Assignment>();

	public boolean removeAssignment(Assignment assignment) {
		if (assignment == null) {
			throw new NullPointerException("null can't be removed from the set.");
		}
		return assignments.remove(assignment);
	}

	public boolean addAssignment(Assignment assignment) {
		if (assignment == null) {
			throw new NullPointerException("null can't be added to the set.");
		}
		return assignments.add(assignment);
	}

	public Collection<Assignment> getAssignments() {
		return Collections.unmodifiableCollection(assignments);
	}

	@Override
	public String toString() {
		return "AssignmentSet [assignments=" + assignments + "]";
	}

}
