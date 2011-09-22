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
package de.knowwe.kdom.manchester;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.frames.clazz.ClassFrame;
import de.knowwe.kdom.manchester.frames.datatype.DataTypeFrame;
import de.knowwe.kdom.manchester.frames.individual.IndividualFrame;
import de.knowwe.kdom.manchester.frames.misc.MiscFrame;
import de.knowwe.kdom.manchester.frames.objectproperty.ObjectPropertyFrame;

/**
 *
 *
 * @author smark
 * @created 24.05.2011
 */
public class ManchesterMarkup extends AbstractType {

	public final static int FLAGS = Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE;

	/**
	 * Put here all the frames that are possible in the Manchester Syntax. Only
	 * those added here are considered when parsing the page.
	 */
	private final static String FRAME_KEYWORDS = "("
			+ ClassFrame.KEYWORD + "|"
			+ IndividualFrame.KEYWORD + "|"
			+ ObjectPropertyFrame.KEYWORD + "|"
			+ DataTypeFrame.KEYWORD + "|"
			+ MiscFrame.FRAME_KEYWORDS + ")";

	/**
	 *
	 */
	public ManchesterMarkup() {
		Pattern pattern = ManchesterSyntaxUtil.getFramePattern(FRAME_KEYWORDS);
		this.setSectionFinder(new RegexSectionFinder(pattern, 0));
		this.setCustomRenderer(new ManchesterSyntaxRenderer());
		this.addChildType(new ManchesterMarkupContentType());

	}
	/**
	 * @author smark
	 * @created 24.05.2011
	 */
	public static class ManchesterMarkupContentType extends AbstractType {

		private static ManchesterMarkupContentType instance = null;

		protected ManchesterMarkupContentType() {
			this.setSectionFinder(new AllTextSectionFinder());
			this.addSubtreeHandler(new ManchesterSubtreeHandler());

			this.addChildType(new ClassFrame());
			this.addChildType(new IndividualFrame());
			this.addChildType(new ObjectPropertyFrame());

			this.addChildType(new MiscFrame());
		}

		public static synchronized ManchesterMarkupContentType getInstance() {
			if (instance == null) {
				instance = new ManchesterMarkupContentType();
			}
			return instance;
		}
	}
}
