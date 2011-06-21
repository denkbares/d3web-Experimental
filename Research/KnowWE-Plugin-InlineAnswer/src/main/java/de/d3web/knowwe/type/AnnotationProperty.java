/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.knowwe.type;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.type.AnonymousType;


/**
 * 
 * This property can only be <b>asks</b>
 * 
 * @author Johannes Dienst
 * @created 09.06.2011
 */
public class AnnotationProperty extends AbstractType {

	public AnnotationProperty() {
		this.sectionFinder = new RegexSectionFinder("[(\\w:)?\\w]*::");
		AnonymousType at = new AnonymousType("PropertyDelimiter");
		at.setSectionFinder(new RegexSectionFinder("::"));
		this.childrenTypes.add(at);
		this.childrenTypes.add(new AnnotationPropertyName());
	}

}
