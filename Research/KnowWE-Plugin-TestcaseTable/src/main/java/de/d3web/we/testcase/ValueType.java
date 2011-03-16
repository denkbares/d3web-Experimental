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

import java.util.LinkedList;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.condition.Number;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.kdom.table.TableLine;

/**
 * 
 * @author Reinhard Hatko
 * @created 21.01.2011
 */
public class ValueType extends AbstractType {

	@Override
	protected void init() {
		setSectionFinder(new AllTextSectionFinder());
		addChildType(new Number());
		CellAnswerRef aRef = new CellAnswerRef();
		addChildType(aRef);

		aRef.setSectionFinder(new SectionFinder() {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
				Section<TestcaseTable> table = Sections.findAncestorOfExactType(father,
						TestcaseTable.class);
				List<Section<TableLine>> lines = new LinkedList<Section<TableLine>>();
				Sections.findSuccessorsOfType(table, TableLine.class, lines);

				if (lines.size() > 1) {
					if (text.length() > 0) {
						return SectionFinderResult.createSingleItemList(new SectionFinderResult(0,
								text.length()));
					}// no text to match
					else {
						return null;
					}
				}// in the first line of the table, there are no values
				else {
					return null;
				}

			}
		});
	}

}
