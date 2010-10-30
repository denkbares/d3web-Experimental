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

package de.d3web.we.kdom.table;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.AbstractKnowWEObjectType;
import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.ISectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.wikiConnector.KnowWEUserContext;

/**
 * TableCellStart.
 * 
 * This class represents the start of a <code>TableCell</code>. Therefore it
 * handles the rendering and the sectioning of the <code>TableCell</code> start
 * markup.
 * 
 * @author smark
 * 
 * @see AbstractKnowWEObjectType
 * @see TableCell
 */
public class TableCellStart extends DefaultAbstractKnowWEObjectType {

	public TableCellStart() {
		sectionFinder = new TableCellStartSectionFinder();
	}

	public class TableCellStartSectionFinder implements ISectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, KnowWEObjectType type) {

			if (!text.startsWith("|")) {
				return null;
			}

			List<SectionFinderResult> result = new ArrayList<SectionFinderResult>();
			result.add(new SectionFinderResult(0, 1));
			return result;
		}
	}

	/**
	 * Returns the renderer for the <code>TableCellStart</code>.
	 * 
	 * This renderer actually renders nothing, but it hides the ugly "||".
	 */
	@Override
	public KnowWEDomRenderer<TableCellStart> getRenderer() {

		class TableCellStartRenderer extends KnowWEDomRenderer<TableCellStart> {

			@Override
			public void render(KnowWEArticle article, Section<TableCellStart> sec, KnowWEUserContext user, StringBuilder string) {
				string.append("");
			}
		}

		return new TableCellStartRenderer();
	}
}
