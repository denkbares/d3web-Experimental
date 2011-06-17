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
package de.d3web.we.testcase;

import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.SectionFinderConstraint;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;

/**
 * 
 * @author Reinhard Hatko
 * @created 21.01.2011
 */
final class UnchangedType extends AbstractType {

	private final String regex = "\\s*" + GetNewQuickEditAnswersAction.UNCHANGED_VALUE_STRING
			+ "\\s*";

	@Override
	protected void init() {
		// setSectionFinder(new RegexSectionFinder(Pattern.compile(regex)));
		setSectionFinder(new ConstraintSectionFinder(
				new RegexSectionFinder(Pattern.compile(regex)), new SectionFinderConstraint() {

					@Override
					public <T extends Type> boolean satisfiesConstraint(List<SectionFinderResult> found, Section<?> father, Class<T> type, String text) {
						return found != null ? found.size() == 1 : true;
					}

					@Override
					public <T extends Type> void filterCorrectResults(List<SectionFinderResult> found, Section<?> father, Class<T> type, String text) {
						if (found != null)
							found.clear();
						}
				}));
	}
}