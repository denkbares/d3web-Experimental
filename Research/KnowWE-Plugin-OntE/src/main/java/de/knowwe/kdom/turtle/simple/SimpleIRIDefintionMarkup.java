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
package de.knowwe.kdom.turtle.simple;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.KnowWETerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.termObject.AbstractIRITermDefinition;
import de.knowwe.termObject.IRIEntityType;
import de.knowwe.termObject.IRIEntityType.IRIDeclarationType;

public class SimpleIRIDefintionMarkup extends AbstractType {

	private static final String REGEX = "^def\\s(.+)$";

	public SimpleIRIDefintionMarkup() {
		this.setSectionFinder(new RegexSectionFinder(REGEX,
				Pattern.MULTILINE));

		this.addChildType(new SimpleIRIDefType());
	}

	class SimpleIRIDefType extends AbstractIRITermDefinition {

		final StyleRenderer CLASS_RENDERER = new StyleRenderer(
				"color:rgb(125, 80, 102)");

		public SimpleIRIDefType() {
			this.setSectionFinder(new
					RegexSectionFinder(Pattern.compile("def\\s(.+)"),
							1));
			this.setCustomRenderer(CLASS_RENDERER);
		}

		@Override
		protected IRIDeclarationType getIRIDeclarationType() {
			return IRIDeclarationType.UNSPECIFIED;
		}

		@Override
		public String getTermIdentifier(Section<? extends KnowWETerm<IRIEntityType>> s) {
			return s.getOriginalText().trim();
		}

	}

}
