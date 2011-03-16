/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.kdom.table.TableCellContent;
import de.d3web.we.kdom.table.TableLine;
import de.d3web.we.object.QuestionReference;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 21.01.2011
 */
public class HeaderCellContent extends TableCellContent {

	@Override
	protected void init() {
		setCustomRenderer(new TestcaseTableCellContentRenderer());
		childrenTypes.add(new UnchangedType());

		QuestionReference qref = new QuestionReference();
		qref.setSectionFinder(new SectionFinder() {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
				Section<TableLine> line = Sections.findAncestorOfType(father, TableLine.class);

				// first two columns are no QRefs, but name and time
				if (line.getChildren().size() < 3) {
					return null;
				}
				else {
					if (text.length() > 0) {
						return SectionFinderResult.createSingleItemList(new SectionFinderResult(0,
								text.length()));
					}
					else {
						return null;
					}
				}

			}
		});

		childrenTypes.add(qref);

	}

}
