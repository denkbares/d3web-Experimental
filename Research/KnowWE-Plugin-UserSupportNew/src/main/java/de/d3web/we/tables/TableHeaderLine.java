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

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.renderer.StyleRenderer;


/**
 * 
 * @author Johannes Dienst
 * @created 28.11.2011
 */
public class TableHeaderLine extends AbstractType {

	public TableHeaderLine() {

		// here also a comment might occur:
		AnonymousType relationComment = new AnonymousType("comment");
		relationComment.setSectionFinder(new RegexSectionFinder("[\\t ]*"
				+ "//[^\r\n]*+" + "\\r?\\n"));
		relationComment.setRenderer(StyleRenderer.COMMENT);
		this.addChildType(relationComment);

		//		this.sectionFinder = new RegexSectionFinder("([^\\|]+\\|)+[^\\|]+");
		//		ConstraintSectionFinder csf = new ConstraintSectionFinder(
		//				new RegexSectionFinder("([^\\|]+\\|)+[^\\|]+"));
		//		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
		//		this.sectionFinder = csf;
		this.sectionFinder = new TableHeaderSectionFinder();

		// divide the line in delimiters and cells
		AnonymousType delimiter = new AnonymousType("delimiter");
		delimiter.setSectionFinder(new RegexSectionFinder("\\|"));
		delimiter.setRenderer(StyleRenderer.COMMENT); // TODO Just a quick-shot
		this.addChildType(delimiter);

		this.addChildType(new TableHeaderCell());
	}

	private class TableHeaderSectionFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

			Section<TableHeaderLine> header = Sections.findChildOfType(father, TableHeaderLine.class);

			if ( header != null ) return null;

			SectionFinder regex = new RegexSectionFinder("([^\\|]+\\|)+[^\\|]+");
			List<SectionFinderResult> results = regex.lookForSections(text, father, type);
			List<SectionFinderResult> toReturn = new ArrayList<SectionFinderResult>();
			if (!results.isEmpty()) toReturn.add(results.get(0));
			return results;
		}

	}

}
