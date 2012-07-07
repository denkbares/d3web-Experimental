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

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

/**
 * 
 * @author Stefan Plehn
 * @created 10.05.2011
 */
public class BoldType extends AbstractType {

	// @Override
	// public List<SectionFinderResult> lookForSections(String text, Section<?>
	// father, Type type) {
	// // TODO Auto-generated method stub // RegEXS
	// // RegexSectionFinder;
	// return null;
	// }
	public BoldType() {

		this.setSectionFinder(new RegexSectionFinder("__.*?__"));
		this.addChildType(new WikiTextType());
	}

}