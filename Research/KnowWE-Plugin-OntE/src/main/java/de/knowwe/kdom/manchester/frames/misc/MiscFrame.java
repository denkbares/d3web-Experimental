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
package de.knowwe.kdom.manchester.frames.misc;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.manchester.types.ListItem;
import de.knowwe.kdom.manchester.types.OWLTermReferenceManchester;
import de.knowwe.kdom.manchester.types.ObjectPropertyExpression;

/**
 * Simple {@link AbstractType} for the {@link MiscFrame} Manchester OWL syntax
 * frame.
 *
 * @author Stefan Mark
 * @created 22.09.2011
 */
public class MiscFrame extends AbstractType {

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

		Pattern p = ManchesterSyntaxUtil.getFramePattern(FRAME_KEYWORDS);
		this.setSectionFinder(new RegexSectionFinder(p));

		this.addChildType(new Keyword(FRAME_KEYWORDS));

		// FIXME add annotations

		// must have two items
		ListItem list = new ListItem();
		list.addChildType(new ObjectPropertyExpression());
		list.addChildType(new OWLTermReferenceManchester());
		this.addChildType(list);
	}

	/**
	 * Check whether the current {@link MiscFrame} is a DisjointClasses frame.
	 *
	 * @created 21.09.2011
	 * @param Section<MiscFrame> a A {@link MiscFrame} section
	 * @return TRUE if EquivalentClasses, FALSE otherwise
	 */
	public boolean isEquivalentClasses(Section<MiscFrame> section) {

		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getOriginalText().matches(KEYWORD_EQUIVALENT_CLASSES)) {
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
	public boolean isDisjointClasses(Section<MiscFrame> section) {
		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getOriginalText().matches(KEYWORD_DISJOINT_CLASSES)) {
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
	public boolean isSameIndividuals(Section<MiscFrame> section) {

		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getOriginalText().matches(KEYWORD_SAME_INDIVIDUAL)) {
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
	public boolean isDifferentIndividuals(Section<MiscFrame> section) {
		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getOriginalText().matches(KEYWORD_DIFFERENT_INDIVIDUAL)) {
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
	public boolean isEquivalentProperties(Section<MiscFrame> section) {

		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getOriginalText().matches(KEYWORD_EQUIVALENT_PROPERTIES)) {
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
	public boolean isDisjointProperties(Section<MiscFrame> section) {
		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getOriginalText().matches(KEYWORD_DISJOINT_PROPERTIES)) {
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
	public boolean isEquivalentDataProperties(Section<MiscFrame> section) {

		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getOriginalText().matches(KEYWORD_EQUIVALENT_PROPERTIES)) {
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
	public boolean isDisjointDataProperties(Section<MiscFrame> section) {
		Section<Keyword> keyword = Sections.findSuccessor(section, Keyword.class);
		if (keyword != null) {
			if (keyword.getOriginalText().matches(KEYWORD_DISJOINT_PROPERTIES)) {
				return true;
			}
		}
		return false;
	}
}
