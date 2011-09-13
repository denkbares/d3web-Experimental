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
package de.knowwe.kdom.manchester.frames.individual;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.termObject.NamedIndividualIRIDefinition;

/**
 *
 *
 * @author smark
 * @created 24.06.2011
 */
public class IndividualFrame extends AbstractType {

	public static final String KEYWORD = "Individual[:]?";

	public static final String KEYWORDS = "("
			+ Types.KEYWORD + "|"
			+ SameAs.KEYWORD + "|"
			+ Facts.KEYWORD
			+ "|\\z)";

	public IndividualFrame() {

		Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p));
		this.addChildType(new IndividualContentType());
	}
	/**
	 * Bundle the content within the Default Markup in a separate content type.
	 *
	 * @author smark
	 * @created 24.05.2011
	 */
	public static class IndividualContentType extends AbstractType {

		public IndividualContentType() {
			this.setSectionFinder(new AllTextFinderTrimmed());

			this.addChildType(new IndividualDefinition());
			this.addChildType(new Types());
			this.addChildType(new SameAs());
			this.addChildType(new Facts());
		}
	}

	/**
	 *
	 * @author smark
	 * @created 24.06.2011
	 */
	public static class IndividualDefinition extends AbstractType {

		public static String PATTERN = IndividualFrame.KEYWORD + "\\p{Blank}+(.+)";

		public IndividualDefinition() {

			Pattern p = Pattern.compile(PATTERN);
			SectionFinder sf = new RegexSectionFinder(p, 0);
			this.setSectionFinder(sf);

			Keyword key = new Keyword(IndividualFrame.KEYWORD);
			this.addChildType(key);

			Individual individual = new Individual();
			individual.setSectionFinder(new AllTextFinderTrimmed());
			this.addChildType(individual);
		}
	}

	/**
	 * 
	 * 
	 * @author smark
	 * @created 24.06.2011
	 */
	public static class Individual extends NamedIndividualIRIDefinition {

		public Individual() {

		}
	}
}

