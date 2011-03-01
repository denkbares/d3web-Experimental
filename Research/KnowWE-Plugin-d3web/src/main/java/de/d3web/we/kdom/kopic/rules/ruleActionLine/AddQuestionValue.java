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

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.Annotation.FindingQuestion;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;

/**
 * @author Johannes Dienst
 * 
 */
public class AddQuestionValue extends AbstractType {

	@Override
	public void init() {
		this.sectionFinder = new AddQuestionValueSectionFinder();
		PlusEqual plusEqual = new PlusEqual();
		this.childrenTypes.add(plusEqual);
		this.childrenTypes.add(new FindingQuestion(plusEqual));
		AddedValue aA = new AddedValue();
		aA.setSectionFinder(new AllTextFinderTrimmed()); // TODO more specificly
															// parsing
		this.childrenTypes.add(aA);
	}

	private class AddQuestionValueSectionFinder extends SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section father, Type type) {

			if (text.contains("+=")) {

				int start = 0;
				int end = text.length();
				while (text.charAt(start) == ' ' || text.charAt(start) == '"') {
					start++;
					if (start >= end) return null;
				}
				while (text.charAt(end - 1) == ' ' || text.charAt(end - 1) == '"') {
					end--;
					if (start >= end) return null;
				}

				List<SectionFinderResult> result = new ArrayList<SectionFinderResult>();
				result.add(new SectionFinderResult(start, end));
				return result;
			}

			return null;
		}
	}

	private class AddingValueSectionFinder extends SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section father, Type type) {

			if (text.contains("(") && text.contains(")")) {
				List<SectionFinderResult> result = new ArrayList<SectionFinderResult>();
				result.add(new SectionFinderResult(text.indexOf("(") + 1,
									text.lastIndexOf(")")));
				return result;
			}
			return null;
		}
	}
}
