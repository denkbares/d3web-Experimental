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
package de.knowwe.jurisearch.tree;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.object.QuestionReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.dashtree.DashTreeUtils;

/**
 * 
 * @author boehler
 * @created 19.01.2012
 */
public class QuestionIdentifier extends QuestionReference {

	public QuestionIdentifier() {
		AllTextFinderTrimmed allTextFinderTrimmed = new AllTextFinderTrimmed();
		ConstraintSectionFinder csf = new ConstraintSectionFinder(allTextFinderTrimmed);
		csf.addConstraint(SingleChildConstraint.getInstance());

		this.setSectionFinder(csf);
	}

	public Section<QuestionIdentifier> getFatherQuestion(Section<QuestionIdentifier> s) {
		Section<DashTreeElement> element = Sections.findAncestorOfType(s, DashTreeElement.class);
		if (element == null) return null;
		Section<? extends DashTreeElement> fatherDashTreeElement = DashTreeUtils.getFatherDashTreeElement(element);
		if (fatherDashTreeElement == null) return null;
		Section<QuestionIdentifier> fatherQuestionID = Sections.findSuccessor(
				fatherDashTreeElement, QuestionIdentifier.class);
		return fatherQuestionID;
	}

	public List<Section<QuestionIdentifier>> getChildrenQuestion(Section<QuestionIdentifier> s) {
		Section<DashTreeElement> element = Sections.findAncestorOfType(s, DashTreeElement.class);
		List<Section<DashTreeElement>> childrenDashtreeElements = DashTreeUtils.findChildrenDashtreeElements(element);

		List<Section<QuestionIdentifier>> result = new ArrayList<Section<QuestionIdentifier>>();
		for (Section<DashTreeElement> child : childrenDashtreeElements) {

			Section<QuestionIdentifier> childID = Sections.findSuccessor(child,
					QuestionIdentifier.class);
			result.add(childID);
		}
		return result;
	}


}
