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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.manchester.types.NonTerminalList;
import de.knowwe.kdom.manchester.types.NonTerminalListContent;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 *
 *
 * @author Stefan Mark
 * @created 24.06.2011
 */
public class Characteristics extends AbstractType {

	public static final String KEYWORD = "Characteristics[:]?";

	public Characteristics(boolean isObject, String description) {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(description, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		Keyword key = new Keyword(KEYWORD);
		this.addChildType(key);

		NonTerminalList list = new NonTerminalList();
		NonTerminalListContent listContent = new NonTerminalListContent();

		if (isObject) {
			listContent.addChildType(new ObjectPropertyCharacteristic());
		}
		else {
			listContent.addChildType(new DataPropertyCharacteristic());
		}

		list.addChildType(listContent);
		this.addChildType(list);

		this.addChildType(new ObjectPropertyCharacteristic());
	}

	/**
	 * Returns the {@link ObjectPropertyCharacteristic} sections for further handling.
	 *
	 * @created 27.09.2011
	 * @param Section<Characteristics> section
	 * @return The found section
	 */
	public List<Section<? extends Type>> getCharacteristics(Section<Characteristics> section) {
		List<Section<? extends Type>> list = new ArrayList<Section<? extends Type>>();
		list.addAll(Sections.findSuccessorsOfType(section, ObjectPropertyCharacteristic.class));
		return list;
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 13.08.2011
 */
class ObjectPropertyCharacteristic extends AbstractType {

	// private static String TERMS =
	// "InverseFunctional|Functional|Irreflexive|Reflexive|Asymmetric|Symmetric|Transitive";

	public static final StyleRenderer CLASS_RENDERER = new StyleRenderer(
			"color:rgb(115, 0, 70)");

	public ObjectPropertyCharacteristic() {

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

/**
 * The {@link DataPropertyCharacteristic} only allows 'Functional' as value.
 * 
 * @author Stefan Mark
 * @created 13.08.2011
 */
class DataPropertyCharacteristic extends AbstractType {

	public static final StyleRenderer CLASS_RENDERER = new StyleRenderer(
			"color:rgb(115, 0, 70)");

	public DataPropertyCharacteristic() {

		Pattern p = Pattern.compile(CharacteristicTypes.FUNCTIONAL.getType());
		this.setSectionFinder(new RegexSectionFinder(p));
		this.setCustomRenderer(CLASS_RENDERER);
	}
}
