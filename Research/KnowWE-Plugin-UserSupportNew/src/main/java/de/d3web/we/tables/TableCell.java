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
import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;


/**
 * 
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class TableCell extends AbstractType {

	@SuppressWarnings("unchecked")
	public static final KnowWEDomRenderer<TableCell> INDIVIDUAL_RENDERER =
	new ToolMenuDecoratingRenderer<TableCell>(new StyleRenderer(
			"color:rgb(152, 180, 12)"));

	public TableCell() {

		// finds everything except an empty cell
		//		this.sectionFinder = new RegexSectionFinder("[^\\|\\s]+");

		this.sectionFinder = new SectionFinder() {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
				List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();

				Pattern p = Pattern.compile("\\S");
				if (p.matcher(text).find())
					results.addAll(new AllTextFinderTrimmed().lookForSections(text, father, type));
				else
					results.add(new SectionFinderResult(text.length()-1, text.length()-1));

				return results;
			}

		};

		//		this.setCustomRenderer(INDIVIDUAL_RENDERER);
		//		this.setCustomRenderer(new ToolMenuDecoratingRenderer(StyleRenderer.CHOICE));
		//		customRenderer = new ToolMenuDecoratingRenderer(StyleRenderer.CHOICE);
		//		this.setCustomRenderer(new ObjectInfoLinkRenderer(StyleRenderer.CHOICE));
	}

}
