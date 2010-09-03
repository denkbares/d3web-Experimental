/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.kdom.constraint;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;

public class SingleChildConstraint implements SectionFinderConstraint {

	private static SingleChildConstraint instance = null;

	public static SingleChildConstraint getInstance() {
		if (instance == null) {
			instance = new SingleChildConstraint();

		}

		return instance;
	}

	@Override
	public void filterCorrectResults(
			List<SectionFinderResult> found, Section father, KnowWEObjectType type) {

		if (!satisfiesConstraint(found, father, type)) {
			found.clear();
		}
		else {
			List<SectionFinderResult> result = new ArrayList<SectionFinderResult>();
			SectionFinderResult e = found.get(0);
			result.clear();
			result.add(e);
		}
	}

	@Override
	public boolean satisfiesConstraint(List<SectionFinderResult> found,
			Section father, KnowWEObjectType type) {
		List<Section<? extends KnowWEObjectType>> findChildrenOfType = father.findChildrenOfType(type.getClass());
		if (findChildrenOfType != null && findChildrenOfType.size() > 0) {
			return false;
		}

		return true;
	}

}
