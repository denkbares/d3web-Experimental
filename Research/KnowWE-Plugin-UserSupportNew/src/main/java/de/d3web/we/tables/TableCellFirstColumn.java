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
package de.d3web.we.tables;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.Finding;
import de.d3web.we.renderer.TableCellFirstColumnRenderer;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

/**
 * 
 * Representing the cells for column 0. Only there can be Objects like
 * Questions, Answers, Solutions etc.
 * 
 * TODO CompositeCondition right?
 * 
 * @author Johannes Dienst
 * @created 28.11.2011
 */
public class TableCellFirstColumn extends TableCell
{

	public static final Renderer INDIVIDUAL_RENDERER =
			new ToolMenuDecoratingRenderer(new TableCellFirstColumnRenderer());

	// TODO Insert the right hierarchy for CompositeCondition here
	public TableCellFirstColumn()
	{
		super();
		this.setRenderer(INDIVIDUAL_RENDERER);
		this.sectionFinder = new TableCellFirstColumnSectionFinder();

		// Add the possible TerminalConditions here
		CompositeCondition cc = new CompositeCondition();
		List<Type> types = new ArrayList<Type>();
		types.add(new Finding());

		cc.setAllowedTerminalConditions(types);
		this.addChildType(cc);
	}

	private class TableCellFirstColumnSectionFinder implements SectionFinder
	{

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

			Section<TableCellFirstColumn> first = Sections.findChildOfType(father, TableCellFirstColumn.class);
			if (first != null) return null;

			Section<TableNormalCell> normalCell = Sections.findChildOfType(father, TableNormalCell.class);
			if (normalCell != null) return null;

			// Check if it is a HeuristicDiagnosisTable
			// no FirstColumnTableCell here
			Section<HeuristicDiagnosisTable> hTable = Sections.findAncestorOfType(father, HeuristicDiagnosisTable.class);
			if (hTable != null)
			{
				List<Section<TableLine>> lines = Sections.findSuccessorsOfType(hTable, TableLine.class);
				if (lines.size() == 1) return null;
			}

			// Check if it is a DecisionTable
			// 1. TableLine with Actions has no TableCellFirstColumn
			// 2. All FirstTableCells after Actions should be SetQuestionValues
			Section<DecisionTable> dTable = Sections.findAncestorOfType(father, DecisionTable.class);
			if (dTable != null)
			{
				if (text.trim().equals("Actions")) return null; // 1. Condition

				// 2. condition: check if a tableline before has "Actions" as first cell text
				List<Section<TableLine>> lines = Sections.findSuccessorsOfType(dTable, TableLine.class);
				for (Section<TableLine> line : lines)
				{
					List<Section<TableNormalCell>> cells = Sections.findSuccessorsOfType(line, TableNormalCell.class);
					if (!cells.isEmpty())
					{
						if (cells.get(0).getText().trim().equals("Actions")) return null;
					}
				}
			}


			SectionFinder regex = new TableCellSectionFinder();
			List<SectionFinderResult> results = regex.lookForSections(text, father, type);
			List<SectionFinderResult> toReturn = new ArrayList<SectionFinderResult>();
			if (!results.isEmpty()) toReturn.add(results.get(0));
			return results;
		}

	}
}
