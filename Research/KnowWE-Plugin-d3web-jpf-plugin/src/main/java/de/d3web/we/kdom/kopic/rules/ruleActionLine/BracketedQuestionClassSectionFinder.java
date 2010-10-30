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
package de.d3web.we.kdom.kopic.rules.ruleActionLine;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;

/**
 * The Question class in InstantIndication and SuppressedQuestion is bracketed.
 * So it needs this SectionFinder to find it.
 * 
 * @author Johannes Dienst
 */
public class BracketedQuestionClassSectionFinder extends SectionFinder {

	@Override
	public List<SectionFinderResult> lookForSections(String text, Section father, KnowWEObjectType type) {

		// TODO can there be Brackets inside Brackets
		if (text.contains("[") && text.contains("]")) {
			List<SectionFinderResult> result = new ArrayList<SectionFinderResult>();
			result.add(new SectionFinderResult(text.indexOf("[") + 1,
								text.indexOf("]")));
			return result;
		}
		return null;
	}
}
