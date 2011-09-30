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
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.manchester.types.NonTerminalList;
import de.knowwe.kdom.manchester.types.NonTerminalListContent;

/**
 *
 *
 * @author Stefan Mark
 * @created 24.06.2011
 */
public class Characteristics extends AbstractType {

	public static final String KEYWORD = "Characteristics[:]?";

	public Characteristics() {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(ObjectPropertyFrame.KEYWORDS,
				KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		Keyword key = new Keyword(KEYWORD);
		this.addChildType(key);

		NonTerminalList list = new NonTerminalList();
		NonTerminalListContent listContent = new NonTerminalListContent();
		listContent.addChildType(new CharacteristicsTerm());
		list.addChildType(listContent);
		this.addChildType(list);

		this.addChildType(new CharacteristicsTerm());
	}

	/**
	 *
	 *
	 * @author Stefan Mark
	 * @created 13.08.2011
	 */
	public static class CharacteristicsTerm extends AbstractType {

		// private static String TERMS =
		// "InverseFunctional|Functional|Irreflexive|Reflexive|Asymmetric|Symmetric|Transitive";

		public static final StyleRenderer CLASS_RENDERER = new StyleRenderer(
				"color:rgb(115, 0, 70)");

		public CharacteristicsTerm() {

			StringBuilder t = new StringBuilder();
			for (CharacteristicTypes c : CharacteristicTypes.values()) {
				t.append(c.getType());
				t.append("|");
			}

			this.setCustomRenderer(CLASS_RENDERER);
			Pattern p = Pattern.compile(t.toString().substring(0, t.toString().length() - 1));
			this.setSectionFinder(new RegexSectionFinder(p));
		}
	}
}
