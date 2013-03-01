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
package de.knowwe.ontology.kdom.relation;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.utils.Patterns;
import de.knowwe.core.utils.Strings;

public class LiteralType extends AbstractType {

	public static final String XSD_PATTERN = "(?:\\^\\^xsd:[\\w])";

	/**
	 * Either single quoted word and optionally xsd type or normal quote and
	 * mandatory xsd type.
	 */
	private static final String LITERAL_PATTERN =
			Patterns.SINGLE_QUOTED + XSD_PATTERN + "?"
					+ "|" + Patterns.QUOTED + XSD_PATTERN;

	public LiteralType() {
		this.setSectionFinder(new RegexSectionFinder(
				LITERAL_PATTERN));
	}

	public String getLiteral(Section<LiteralType> section) {
		return Strings.unquote(section.getText(), '\'');
	}
}