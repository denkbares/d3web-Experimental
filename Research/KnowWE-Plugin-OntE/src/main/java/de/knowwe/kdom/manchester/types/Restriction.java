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
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;

/**
 *
 *
 * @author Stefan Mark
 * @created 16.05.2011
 */
public class Restriction extends AbstractType {

	public static final String BEFORE_REGEX = "(.+?)(?:\\s)+";

	public static final String AFTER_REGEX = "(?:\\s)+(.+)";

	public static final String AFTER_INTEGER = "(?:\\s)+(\\d+)\\s+(.+)?";

	private static Restriction instance = null;

	/**
	 * Constructor
	 */
	protected Restriction() {

		this.setSectionFinder(new AllTextFinderTrimmed());

		SomeRestriction some = new SomeRestriction();
		this.addChildType(some);

		OnlyRestriction only = new OnlyRestriction();
		this.addChildType(only);

		CardinalityRestriction i = new CardinalityRestriction(this);
		this.addChildType(i);

		ValueRestriction value = new ValueRestriction();
		this.addChildType(value);

		Fact fact = new Fact();
		this.addChildType(fact);

		OWLTermReferenceManchester def = new OWLTermReferenceManchester();
		this.addChildType(def);

		ObjectPropertyExpression ope = new ObjectPropertyExpression();
		this.addChildType(ope);
	}

	public static synchronized Restriction getInstance() {
		if (instance == null) {
			instance = new Restriction();
		}
		return instance;
	}

	/**
	 * Check whether the current {@link Restriction} is a
	 * {@link SomeRestriction} section.
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isSomeRestriction(Section<Restriction> section) {
		return Sections.findChildOfType(section, SomeRestriction.class) != null;
	}

	/**
	 * Retrieves a {@link SomeRestriction} section
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return The found {@link OWLTermReferenceManchester} sections
	 */
	public Section<SomeRestriction> getSomeRestriction(Section<Restriction> section) {
		return Sections.findChildOfType(section, SomeRestriction.class);
	}

	/**
	 * Check whether the current {@link Restriction} is a
	 * {@link OnlyRestriction} section.
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isOnlyRestriction(Section<Restriction> section) {
		return Sections.findChildOfType(section, OnlyRestriction.class) != null;
	}

	/**
	 * Retrieves a {@link OnlyRestriction} section
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return The found {@link OWLTermReferenceManchester} sections
	 */
	public Section<OnlyRestriction> getOnlyRestriction(Section<Restriction> section) {
		return Sections.findChildOfType(section, OnlyRestriction.class);
	}

	/**
	 * Check whether the current {@link Restriction} is a
	 * {@link CardinalityRestriction} section.
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isCardinalityRestriction(Section<Restriction> section) {
		return Sections.findChildOfType(section, CardinalityRestriction.class) != null;
	}

	/**
	 * Retrieves a {@link CardinalityRestriction} section
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return The found {@link OWLTermReferenceManchester} sections
	 */
	public Section<CardinalityRestriction> getCardinalityRestriction(Section<Restriction> section) {
		return Sections.findChildOfType(section, CardinalityRestriction.class);
	}

	/**
	 * Check whether the current {@link Restriction} is a
	 * {@link ValueRestriction} section.
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isValueRestriction(Section<Restriction> section) {
		return Sections.findChildOfType(section, ValueRestriction.class) != null;
	}

	/**
	 * Retrieves a {@link ValueRestriction} section
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return The found {@link OWLTermReferenceManchester} sections
	 */
	public Section<ValueRestriction> getValueRestriction(Section<Restriction> section) {
		return Sections.findChildOfType(section, ValueRestriction.class);
	}

	/**
	 * Check whether the current {@link Restriction} is a
	 * {@link OWLTermReferenceManchester} section.
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isTermReference(Section<Restriction> section) {
		return Sections.findChildOfType(section, OWLTermReferenceManchester.class) != null;
	}

	/**
	 * Retrieves a {@link OWLTermReferenceManchester} section
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return The found {@link OWLTermReferenceManchester} sections
	 */
	public Section<OWLTermReferenceManchester> getTermReference(Section<Restriction> section) {
		return Sections.findChildOfType(section, OWLTermReferenceManchester.class);
	}

	/**
	 * Check whether the current {@link Restriction} is a
	 * {@link ObjectPropertyExpression} section.
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isObjectProperty(Section<Restriction> section) {
		return Sections.findChildOfType(section, ObjectPropertyExpression.class) != null;
	}

	/**
	 * Retrieves a {@link ObjectPropertyExpression} section
	 *
	 * @param Section<Restriction> a A {@link Restriction} section
	 * @return The found {@link ObjectPropertyExpression} sections
	 */
	public Section<ObjectPropertyExpression> getObjectProperty(Section<Restriction> section) {
		return Sections.findChildOfType(section, ObjectPropertyExpression.class);
	}

}