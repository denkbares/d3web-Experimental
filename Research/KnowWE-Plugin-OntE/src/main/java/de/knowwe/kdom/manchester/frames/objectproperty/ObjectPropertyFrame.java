/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.kdom.manchester.frames.objectproperty;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.termObject.ObjectPropertyIRIDefinition;

/**
 *
 *
 * @author smark
 * @created 24.05.2011
 */
public class ObjectPropertyFrame extends AbstractType {

	public static final String KEYWORD = "ObjectProperty[:]?";

	public static final String KEYWORDS = "("
			+ SubPropertyOf.KEYWORD + "|"
			+ Characteristics.KEYWORD + "|"
			+ Range.KEYWORD + "|"
			+ Domain.KEYWORD + "|"
			+ InverseOf.KEYWORD
			+ "|\\z)";

	public ObjectPropertyFrame() {

		Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p));

		ObjectPropertyDefinition dt = new ObjectPropertyDefinition();
		this.addChildType(dt);

		this.addChildType(new Annotations(KEYWORDS));
		this.addChildType(new SubPropertyOf());
		this.addChildType(new Characteristics());
		this.addChildType(new Domain());
		this.addChildType(new Range());
		this.addChildType(new InverseOf());
	}

	/**
	 * 
	 * @author smark
	 * @created 24.05.2011
	 */
	public static class ObjectPropertyDefinition extends AbstractType {

		public static String PATTERN = ObjectPropertyFrame.KEYWORD + "\\p{Blank}+(.+)";

		public ObjectPropertyDefinition() {

			Pattern p = Pattern.compile(PATTERN);
			SectionFinder sf = new RegexSectionFinder(p, 0);
			this.setSectionFinder(sf);

			Keyword key = new Keyword(ObjectPropertyFrame.KEYWORD);
			this.addChildType(key);

			ObjectProperty owl = new ObjectProperty();
			owl.setSectionFinder(new AllTextFinderTrimmed());
			this.addChildType(owl);
		}
	}
	/**
	 *
	 *
	 * @author smark
	 * @created 06.06.2011
	 */
	public static class ObjectProperty extends ObjectPropertyIRIDefinition {

		public ObjectProperty() {

		}
	}

}
