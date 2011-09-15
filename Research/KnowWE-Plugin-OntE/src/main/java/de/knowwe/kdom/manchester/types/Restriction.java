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

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;

/**
 *
 *
 * @author smark
 * @created 16.05.2011
 */
public class Restriction extends AbstractType {

	public static final String BEFORE_REGEX = "(.+?)(?:\\s)+";

	public static final String AFTER_REGEX = "(?:\\s)+(.+)";

	public static final String AFTER_INTEGER = "(?:\\s)+(\\d+)";

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

		MinRestriction min = new MinRestriction();
		this.addChildType(min);

		MaxRestriction max = new MaxRestriction();
		this.addChildType(max);

		ExactlyRestriction exactly = new ExactlyRestriction();
		this.addChildType(exactly);

		ValueRestriction value = new ValueRestriction();
		this.addChildType(value);

		OWLTermReferenceManchester def = new OWLTermReferenceManchester();
		this.addChildType(def);

		// TODO oneOf restriction missing
	}

	public static synchronized Restriction getInstance() {
		if (instance == null) {
			instance = new Restriction();
		}
		return instance;
	}
}