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
 * Simple {@link AbstractType} to wrap {@link Annotations} in a Manchester OWL
 * Syntax ontology.
 *
 * @author Stefan Mark
 * @created 13.08.2011
 */
public class Annotations extends AbstractType {

	public final static String KEYWORD = "Annotations[:]?";

	public final static String INSIDE_PATTERN = "(?m)" +
			"(" + KEYWORD + ".+$" +
			"(\r\n?|\n)" +
			"((^.*$)(\r\n?|\n))" +
			"+?)" +
			"((^\\s*$)){1}";

	/**
	 * Empty Constructor. Just for nested support of Annotations
	 */
	public Annotations() {
		this(null);
	}

	/**
	 * Constructor for the {@link Annotations} description of an element in the
	 * Manchester OWL Syntax. The Constructor needs to know the frame the
	 * annotation is inside for correct sectioning.
	 *
	 * @param String frame The keywords that appear in the current frame
	 */
	public Annotations(String frame) {

		Pattern p = null;
		if (frame != null && !frame.isEmpty()) {
			p = ManchesterSyntaxUtil.getDescriptionPattern(frame, KEYWORD);
		}
		else {
			p = Pattern.compile(INSIDE_PATTERN);
		}
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		this.addChildType(new Keyword(KEYWORD));

		// List occurrence of annotations ...
		NonTerminalList list = new NonTerminalList();
		NonTerminalListContent listContent = new NonTerminalListContent();
		listContent.addChildType(new Annotation());
		list.addChildType(listContent);
		this.addChildType(list);

		// ... or non list occurrence of a annotation
		this.addChildType(new Annotation());
	}
}
