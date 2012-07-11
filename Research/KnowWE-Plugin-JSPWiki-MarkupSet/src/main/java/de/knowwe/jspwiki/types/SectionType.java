/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.knowwe.jspwiki.types;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

/**
 * 
 * @author Lukas Brehl, Jochen
 * @created 25.05.2012
 */

public class SectionType extends AbstractType {

	/**
	 * for the plugin framework to call
	 */
	public SectionType() {
		this(3);
	}

	/*
	 * A SectionType can have a SectionHeaderType and a SectionContentType as
	 * children.
	 */
	public SectionType(int count) {
		System.out.println("creating SectionType: " + count);
		// this.setSectionFinder(new WikiBookSectionFinder('!', true));
		this.setSectionFinder(new SectionBlockFinder(createMarker(count)));
		this.addChildType(new SectionHeaderType());
		this.addChildType(new SectionContentType(count));
	}

	/**
	 * 
	 * @created 11.07.2012
	 * @param count
	 * @return
	 */
	private static String createMarker(int count) {
		String marker = "";
		for (int i = 0; i < count; i++) {
			marker += "!";
		}
		return marker;
	}

	class SectionBlockFinder extends RegexSectionFinder {

		public SectionBlockFinder(String marker) {
			// The regex for the major level looks like: "^!!!.*?((?=^!!!)|\\z)"
			super("^" + marker + // the marker
					".*?" // any content
					+ "((?=^" + marker + ")" + // look ahead for the next one

					"|\\z)" // or end of content
			, Pattern.DOTALL
					| Pattern.MULTILINE, 0);
		}
	}

}
