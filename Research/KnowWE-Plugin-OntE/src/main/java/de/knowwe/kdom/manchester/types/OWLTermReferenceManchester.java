/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.kdom.manchester.types;

import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.rdfs.IRITermRef;
import de.knowwe.termObject.IRITermReference;

/**
 * Simple {@link IRITermReference} that represents OWL classes and OWL
 * individuals.
 *
 * @author Stefan Mark
 * @created 18.05.2011
 */
public class OWLTermReferenceManchester extends IRITermRef {

	public static final String PATTERN = "\\b([A-Z]|owl)[A-Za-z0-9:]+\\b";

	public OWLTermReferenceManchester() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN));
	}
}
