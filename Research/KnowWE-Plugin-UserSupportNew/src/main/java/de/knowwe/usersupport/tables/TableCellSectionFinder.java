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
package de.knowwe.usersupport.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;


/**
 * 
 * @author Johannes Dienst
 * @created 28.11.2011
 */
public class TableCellSectionFinder implements SectionFinder
{

	@Override
	public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
		List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();

		//		Pattern p = Pattern.compile("\\S");
		Pattern p = Pattern.compile("[^\\|\\s]+");

		if (p.matcher(text).find())
			results.addAll(new AllTextFinderTrimmed().lookForSections(text, father, type));
		else
			results.add(new SectionFinderResult(text.length()-1, text.length()-1));

		// sort out empty TableCells
		List<SectionFinderResult> toReturn = new ArrayList<SectionFinderResult>();
		if (!type.isAssignableFromType(TableHeaderCell.class)) {
			for (SectionFinderResult res : results) {
				Section<ColumnDelimiter> fatherSuccs = Sections.findChildOfType(father, ColumnDelimiter.class);

				if ((res.getStart() != res.getEnd())) { toReturn.add(res);}
				//				else if (cell != null && !cell.get().isAssignableFromType(TableCellFirstColumn.class)) {
				else if ( fatherSuccs != null) {toReturn.add(res);}
			}
		} else
		{
			toReturn = results;
		}

		return toReturn;
	}

}
