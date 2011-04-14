/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.hermes.kdom;

import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;
import de.d3web.we.kdom.semanticAnnotation.SemanticAnnotation;
import de.d3web.we.kdom.semanticAnnotation.SemanticAnnotationContent;
import de.knowwe.hermes.kdom.conceptMining.LocationOccurrence;
import de.knowwe.hermes.kdom.conceptMining.PersonOccurrence;

public class TimeEventDescriptionType extends AbstractType {

	@Override
	protected void init() {

		SemanticAnnotation semanticAnnotation = new SemanticAnnotation();

		// first grab annotated concepts
		this.childrenTypes.add(semanticAnnotation);

		// then search for un-annotated concepts
		this.childrenTypes.add(new PersonOccurrence());
		this.childrenTypes.add(new LocationOccurrence());

		sectionFinder = new AllTextSectionFinder();
	}

	private Type findContentType(SemanticAnnotation semanticAnnotation) {
		List<Type> annoChildren = semanticAnnotation.getAllowedChildrenTypes();
		for (Type Type : annoChildren) {
			if (Type instanceof SemanticAnnotationContent) {
				return Type;
			}
		}
		return null;

	}

}
