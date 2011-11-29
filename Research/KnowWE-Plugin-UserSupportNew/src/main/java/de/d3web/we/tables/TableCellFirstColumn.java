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
package de.d3web.we.tables;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.condition.CompositeCondition;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;


/**
 * 
 * Representing the cells for column 0.
 * Only there can be Objects like Questions, Answers, Solutions etc.
 * 
 * TODO CompositeCondition right?
 * 
 * @author Johannes Dienst
 * @created 28.11.2011
 */
public class TableCellFirstColumn extends TableCell {

	@SuppressWarnings("unchecked")
	public static final KnowWEDomRenderer<TableCellFirstColumn> INDIVIDUAL_RENDERER =
	new ToolMenuDecoratingRenderer<TableCellFirstColumn>(new StyleRenderer(
			"color:rgb(152, 180, 12)"));

	// TODO Insert the right hierarchy here
	public TableCellFirstColumn() {
		super();
		this.customRenderer = INDIVIDUAL_RENDERER;
		this.sectionFinder = new TableCellFirstColumnSectionFinder();
		this.addChildType(new CompositeCondition());
	}

	private class TableCellFirstColumnSectionFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

			Section<TableCellFirstColumn> first = Sections.findChildOfType(father, TableCellFirstColumn.class);

			if ( first != null ) return null;

			SectionFinder regex = new TableCellSectionFinder();
			List<SectionFinderResult> results = regex.lookForSections(text, father, type);
			List<SectionFinderResult> toReturn = new ArrayList<SectionFinderResult>();
			if (!results.isEmpty()) toReturn.add(results.get(0));
			return results;
		}

	}
}
