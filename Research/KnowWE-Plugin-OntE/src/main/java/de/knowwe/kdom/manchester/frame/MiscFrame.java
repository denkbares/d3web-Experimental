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

import java.util.ArrayList;
import java.util.List;

import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.manchester.compile.MiscFrameCompileScript;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.Delimiter;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.manchester.types.NonTerminalList;
import de.knowwe.kdom.manchester.types.NonTerminalListContent;
import de.knowwe.kdom.manchester.types.OWLTermReferenceManchester;
import de.knowwe.kdom.manchester.types.ObjectPropertyExpression;

/**
 * <p>
 * Simple {@link AbstractType} for the {@link MiscFrame} Manchester OWL syntax
 * frame. It follows an excerpt from the Manchester OWL syntax definition from
 * the W3C.
 * </p>
 * <code>
 * misc ::= 'EquivalentClasses:' annotations description2List<br />
 * | 'DisjointClasses:' annotations description2List<br />
 * | 'EquivalentProperties:' annotations objectProperty2List<br />
 * | 'DisjointProperties:' annotations objectProperty2List<br />
 * | 'EquivalentProperties:' annotations dataProperty2List<br />
 * | 'DisjointProperties:' annotations dataProperty2List<br />
 * | 'SameIndividual:' annotations individual2List<br />
 * | 'DifferentIndividuals:' annotations individual2List
 * </code>
 * 
 * @author Stefan Mark
 * @created 22.09.2011
 */
public class MiscFrame extends DefaultFrame implements KnowledgeUnit {

	public static final String KEYWORD_SAME_INDIVIDUAL = "SameIndividual[:]?";
	public static final String KEYWORD_DIFFERENT_INDIVIDUAL = "DifferentIndividuals[:]?";

	public static final String KEYWORD_EQUIVALENT_CLASSES = "EquivalentClasses[:]?";
	public static final String KEYWORD_DISJOINT_CLASSES = "DisjointClasses[:]?";

	public static final String KEYWORD_EQUIVALENT_PROPERTIES = "EquivalentProperties[:]?";
	public static final String KEYWORD_DISJOINT_PROPERTIES = "DisjointProperties[:]?";

	public static final String FRAME_KEYWORDS = "("
			+ KEYWORD_DIFFERENT_INDIVIDUAL + "|"
			+ KEYWORD_SAME_INDIVIDUAL + "|"
			+ KEYWORD_DISJOINT_CLASSES + "|"
			+ KEYWORD_EQUIVALENT_CLASSES + "|"
			+ KEYWORD_DISJOINT_PROPERTIES + "|"
			+ KEYWORD_EQUIVALENT_PROPERTIES
				+ ")";

	/**
	 * Constructor for the {@link MiscFrame}. Adds the necessary
	 * {@link AbstractType}s needed for correct mapping in the KDOM of KnowWE.
	 */
	public MiscFrame() {

		super(FRAME_KEYWORDS);

		List<Type> types = new ArrayList<Type>();

		types.add(new Keyword(FRAME_KEYWORDS));
		types.add(new Annotations());

		NonTerminalList list = new NonTerminalList();
		NonTerminalListContent listContent = new NonTerminalListContent();
		listContent.addChildType(new ObjectPropertyExpression());
		listContent.addChildType(new OWLTermReferenceManchester());
		list.addChildType(listContent);
		types.add(list);

		types.add(new Delimiter());
		this.setKnownDescriptions(types);
	}

	/**
	 * Check whether the current {@link MiscFrame} is a DisjointClasses frame.
	 * 
	 * @created 21.09.2011
	 * @param Section<MiscFrame> a A {@link MiscFrame} section
	 * @return TRUE if EquivalentClasses, FALSE otherwise
	 */
	public boolean isEquivalentClasses(Section<? extends DefaultFrame> section) {

		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getText().matches(KEYWORD_EQUIVALENT_CLASSES)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the current {@link MiscFrame} is a DisjointClasses frame.
	 * 
	 * @created 21.09.2011
	 * @param Section<MiscFrame> a A {@link MiscFrame} section
	 * @return TRUE if DisjointClasses, FALSE otherwise
	 */
	public boolean isDisjointClasses(Section<? extends DefaultFrame> section) {
		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getText().matches(KEYWORD_DISJOINT_CLASSES)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the current {@link MiscFrame} is a SameIndividual frame.
	 * 
	 * @created 21.09.2011
	 * @param Section<MiscFrame> a A {@link MiscFrame} section
	 * @return TRUE if SameIndividual, FALSE otherwise
	 */
	public boolean isSameIndividuals(Section<? extends DefaultFrame> section) {

		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getText().matches(KEYWORD_SAME_INDIVIDUAL)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the current {@link MiscFrame} is a DifferentIndividuals
	 * frame.
	 * 
	 * @created 21.09.2011
	 * @param Section<MiscFrame> a A {@link MiscFrame} section
	 * @return TRUE if DifferentIndividuals, FALSE otherwise
	 */
	public boolean isDifferentIndividuals(Section<? extends DefaultFrame> section) {
		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getText().matches(KEYWORD_DIFFERENT_INDIVIDUAL)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the current {@link MiscFrame} is a EquivalentProperties
	 * frame.
	 * 
	 * @created 21.09.2011
	 * @param Section<MiscFrame> a A {@link MiscFrame} section
	 * @return TRUE if EquivalentProperties, FALSE otherwise
	 */
	public boolean isEquivalentProperties(Section<? extends DefaultFrame> section) {

		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getText().matches(KEYWORD_EQUIVALENT_PROPERTIES)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the current {@link MiscFrame} is a DisjointProperties
	 * frame.
	 * 
	 * @created 21.09.2011
	 * @param Section<MiscFrame> a A {@link MiscFrame} section
	 * @return TRUE if DisjointProperties, FALSE otherwise
	 */
	public boolean isDisjointProperties(Section<? extends DefaultFrame> section) {
		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getText().matches(KEYWORD_DISJOINT_PROPERTIES)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the current {@link MiscFrame} is a EquivalentProperties
	 * frame.
	 * 
	 * @created 21.09.2011
	 * @param Section<MiscFrame> a A {@link MiscFrame} section
	 * @return TRUE if EquivalentProperties, FALSE otherwise
	 */
	public boolean isEquivalentDataProperties(Section<? extends DefaultFrame> section) {

		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getText().matches(KEYWORD_EQUIVALENT_PROPERTIES)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the current {@link MiscFrame} is a DisjointProperties
	 * frame.
	 * 
	 * @created 21.09.2011
	 * @param Section<MiscFrame> a A {@link MiscFrame} section
	 * @return TRUE if DisjointProperties, FALSE otherwise
	 */
	public boolean isDisjointDataProperties(Section<? extends DefaultFrame> section) {
		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getText().matches(KEYWORD_DISJOINT_PROPERTIES)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public KnowledgeUnitCompileScript getCompileScript() {
		return new MiscFrameCompileScript();
	}
}
