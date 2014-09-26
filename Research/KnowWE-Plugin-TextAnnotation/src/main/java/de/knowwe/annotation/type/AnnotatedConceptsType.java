/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.annotation.type;

import java.util.regex.Pattern;

import de.knowwe.annotation.type.list.AnnotatedConceptListContent;
import de.knowwe.annotation.type.list.KeyType;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 21.06.2013
 */
public class AnnotatedConceptsType extends AbstractType {

	public static final String CONCEPT_KEY = "BEGRIFFE:";
	private static final String PATTERN = CONCEPT_KEY + "(.*?)$";
	public static final String LIST_SEPARATOR = ",";

	/**
	 * 
	 */
	public AnnotatedConceptsType() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN,
				Pattern.MULTILINE | Pattern.DOTALL));
		this.addChildType(new AnnotatedConceptListContent(PATTERN));

		this.addChildType(new KeyType(CONCEPT_KEY));
	}

}
