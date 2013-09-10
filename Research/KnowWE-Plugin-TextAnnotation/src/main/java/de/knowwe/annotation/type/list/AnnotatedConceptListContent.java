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
package de.knowwe.annotation.type.list;

import java.util.regex.Pattern;

import de.knowwe.annotation.type.AnnotatedConceptsType;
import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.sectionFinder.SplitSectionFinderUnquotedNonEmpty;

/**
 * 
 * @author jochenreutelshofer
 * @created 23.06.2013
 */
public class AnnotatedConceptListContent extends AbstractType {

	/**
	 * 
	 */
	public AnnotatedConceptListContent(String pattern) {
		this.setSectionFinder(new RegexSectionFinder(pattern,
				Pattern.MULTILINE | Pattern.DOTALL, 1));
		this.addChildType(new ObjectSegment());
		this.addChildType(new ListSeparatorType());
	}

	class ListSeparatorType extends AbstractType {

		/**
		 * 
		 */
		public ListSeparatorType() {
			this.setSectionFinder(new AllTextSectionFinder());
		}
	}

	class ObjectSegment extends AbstractKnowledgeUnitType<ObjectSegment> {

		public ObjectSegment() {
			this.setSectionFinder(new SplitSectionFinderUnquotedNonEmpty(
					AnnotatedConceptsType.LIST_SEPARATOR));
			this.addChildType(new ListObjectIdentifier(new OILinkRenderer()));
		}
	}
}
