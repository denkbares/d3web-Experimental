/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.compile.object;

import java.util.Collection;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;

/**
 * 
 * Marker Interface for a complex definition, i.e. a definition that depends on
 * other referenced terms to be valid.
 * 
 * @author jochenreutelshofer
 * @created 09.05.2012
 */
public interface ComplexDefinition extends Type {

	/**
	 * Delivers the terms that this ComplexDefinition depends on.
	 * 
	 * @created 11.06.2012
	 * @param section
	 * @return
	 */
	public Collection<Section<SimpleReference>> getAllReferences(
			Section<? extends ComplexDefinition> section);

}
