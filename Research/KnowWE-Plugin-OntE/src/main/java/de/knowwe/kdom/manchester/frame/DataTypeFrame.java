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
package de.knowwe.kdom.manchester.frame;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.termObject.DatatypePropertyIRIDefinition;

/**
 * 
 * 
 * @author Stefan Mark
 * @created 24.05.2011
 */
public class DataTypeFrame extends AbstractType {

	public static final String KEYWORD = "Datatype[:]?";

	public static final String PATTERN = "^\\p{Blank}*" + KEYWORD + "\\p{Blank}+(.+)$" +
											"(.*)" +
											"^${2}";

	public DataTypeFrame() {

		this.setSectionFinder(new AllTextFinderTrimmed());

		DatatypeDefinition dt = new DatatypeDefinition();
		this.addChildType(dt);

	}
}

/**
 *
 * @author smark
 * @created 24.05.2011
 */
class DatatypeDefinition extends AbstractType {

	public static String PATTERN = DataTypeFrame.KEYWORD + "\\p{Blank}+(.+)";

	public DatatypeDefinition() {

		Pattern p = Pattern.compile(PATTERN);
		SectionFinder sf = new RegexSectionFinder(p, 0);
		this.setSectionFinder(sf);

		Keyword key = new Keyword(DataTypeFrame.KEYWORD);
		this.addChildType(key);

		Datatype owl = new Datatype();
		owl.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(owl);
	}

	/**
	 *
	 *
	 * @author smark
	 * @created 06.06.2011
	 */
	class Datatype extends DatatypePropertyIRIDefinition {

		public Datatype() {

		}
	}
}
