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
package de.knowwe.kdom.manchester.types;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;

/**
 * Simple {@link AbstractType} to wrap {@link Annotations} in a ontology.
 *
 * @author Stefan Mark
 * @created 13.08.2011
 */
public class Annotations extends AbstractType {

	public final static String KEYWORD = "Annotations[:]?";

	public final static String KEYWORDS = "("
			+ Annotation.KEYWORD_COMMENT + "|"
			+ Annotation.KEYWORD_LABEL
			+ "|\\z)";

	/**
	 *
	 * @param String frame The keywords that appear in the current frame
	 */
	public Annotations(String frame) {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(frame, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		Keyword key = new Keyword(KEYWORD);
		this.addChildType(key);

		Annotation a = new Annotation();

		ListItem list = new ListItem();
		list.addChildType(a);
		this.addChildType(list);

		this.addChildType(a);
	}
}
