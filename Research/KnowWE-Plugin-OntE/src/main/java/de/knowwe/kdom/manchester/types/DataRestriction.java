/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 *
 *
 * @author Stefan Mark
 * @created 16.05.2011
 */
public class DataRestriction extends AbstractType {

	public static final String KEYWORDS = "(" + ManchesterSyntaxKeywords.SOME_.getKeyword() + "|"
			+ ManchesterSyntaxKeywords.ONLY_.getKeyword() + "|"
			+ ManchesterSyntaxKeywords.VALUE_.getKeyword()
			+ ")";

	private static DataRestriction instance = null;

	/**
	 * Constructor
	 */
	protected DataRestriction() {

		this.setSectionFinder(new RegexSectionFinder(Restriction.BEFORE_REGEX + KEYWORDS
				+ Restriction.AFTER_REGEX));

		this.addChildType(new SomeRestriction(ManchesterSyntaxKeywords.SOME_.getKeyword(), true));
		this.addChildType(new OnlyRestriction(ManchesterSyntaxKeywords.ONLY_.getKeyword(), true));
		this.addChildType(new ValueRestriction(ManchesterSyntaxKeywords.VALUE_.getKeyword(), true));

		CardinalityRestriction i = new CardinalityRestriction(this);
		this.addChildType(i);

		this.addChildType(new Delimiter());
	}

	public static synchronized DataRestriction getInstance() {
		if (instance == null) {
			instance = new DataRestriction();
		}
		return instance;
	}

	/**
	 * Check whether the current {@link DataRestriction} is a
	 * {@link SomeRestriction} section.
	 *
	 * @param Section<Restriction> a A {@link DataRestriction} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isSomeRestriction(Section<DataRestriction> section) {
		return Sections.findChildOfType(section, SomeRestriction.class) != null;
	}

	/**
	 * Retrieves a {@link SomeRestriction} section
	 *
	 * @param Section<Restriction> a A {@link DataRestriction} section
	 * @return The found {@link OWLTermReferenceManchester} sections
	 */
	public Section<SomeRestriction> getSomeRestriction(Section<DataRestriction> section) {
		return Sections.findChildOfType(section, SomeRestriction.class);
	}

	/**
	 * Check whether the current {@link DataRestriction} is a
	 * {@link OnlyRestriction} section.
	 *
	 * @param Section<Restriction> a A {@link DataRestriction} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isOnlyRestriction(Section<DataRestriction> section) {
		return Sections.findChildOfType(section, OnlyRestriction.class) != null;
	}

	/**
	 * Retrieves a {@link OnlyRestriction} section
	 *
	 * @param Section<Restriction> a A {@link DataRestriction} section
	 * @return The found {@link OWLTermReferenceManchester} sections
	 */
	public Section<OnlyRestriction> getOnlyRestriction(Section<DataRestriction> section) {
		return Sections.findChildOfType(section, OnlyRestriction.class);
	}

	/**
	 * Check whether the current {@link DataRestriction} is a
	 * {@link CardinalityRestriction} section.
	 *
	 * @param Section<Restriction> a A {@link DataRestriction} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isCardinalityRestriction(Section<DataRestriction> section) {
		return Sections.findChildOfType(section, CardinalityRestriction.class) != null;
	}

	/**
	 * Retrieves a {@link CardinalityRestriction} section
	 *
	 * @param Section<Restriction> a A {@link DataRestriction} section
	 * @return The found {@link OWLTermReferenceManchester} sections
	 */
	public Section<CardinalityRestriction> getCardinalityRestriction(Section<DataRestriction> section) {
		return Sections.findChildOfType(section, CardinalityRestriction.class);
	}

	/**
	 * Check whether the current {@link DataRestriction} is a
	 * {@link ValueRestriction} section.
	 *
	 * @param Section<Restriction> a A {@link DataRestriction} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isValueRestriction(Section<DataRestriction> section) {
		return Sections.findChildOfType(section, ValueRestriction.class) != null;
	}

	/**
	 * Retrieves a {@link ValueRestriction} section
	 *
	 * @param Section<Restriction> a A {@link DataRestriction} section
	 * @return The found {@link OWLTermReferenceManchester} sections
	 */
	public Section<ValueRestriction> getValueRestriction(Section<DataRestriction> section) {
		return Sections.findChildOfType(section, ValueRestriction.class);
	}
}