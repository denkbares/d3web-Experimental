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

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.AllTextFinderTrimSpaces;
import de.knowwe.kdom.sectionFinder.ConditionalSectionFinder;


/**
 * 
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class TableLine extends AbstractType {

	public TableLine() {

		this.setSectionFinder(new ConditionalSectionFinder(new AllTextFinderTrimSpaces()) {

			// hack to allow for comment after last relation
			// TODO: find better way
			@Override
			protected boolean condition(String text, Section<?> father) {
				// if starts as a comment and there is no next line, there
				// is TableLine in it
				if (text.trim().startsWith("//") && !(text.contains("\n"))) {
					return false;
				}
				return true;
			}
		});

		//		this.addSubtreeHandler(Priority.LOW, new CreateXCLRelationHandler());
		//		this.setCustomRenderer(new CoveringRelationRenderer());

		// here also a comment might occur:
		AnonymousType relationComment = new AnonymousType("comment");
		relationComment.setSectionFinder(new RegexSectionFinder("[\\t ]*"
				+ "//[^\r\n]*+" + "\\r?\\n"));
		relationComment.setCustomRenderer(StyleRenderer.COMMENT);
		this.addChildType(relationComment);

		this.sectionFinder = new RegexSectionFinder("([^\\|]+\\|)+[^\\|]+");

		// divide the line in delimiters and cells
		AnonymousType delimiter = new AnonymousType("delimiter");
		delimiter.setSectionFinder(new RegexSectionFinder("\\|"));
		delimiter.setCustomRenderer(StyleRenderer.COMMENT); // TODO Just a quick-shot
		this.addChildType(delimiter);

		this.addChildType(new TableCell());
	}

}
