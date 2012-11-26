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

import java.util.ArrayList;
import java.util.Collection;

import de.knowwe.compile.utils.CompileUtils;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;

public abstract class AbstractKnowledgeUnitCompileScript<T extends Type> implements KnowledgeUnitCompileScript<T> {

	@Override
	public Collection<Section<? extends SimpleTerm>> getAllReferencesOfKnowledgeUnit(
			Section<? extends KnowledgeUnit> section) {

		/*
		 * this is a default behaviour, working for all markups where
		 * knowledge-units dont overlap
		 */

		Collection<Section<SimpleTerm>> allReferencesOfCompilationUnit = CompileUtils.getAllLocalReferencesOfCompilationUnit(section);

		// some evil workaround because of generics problem
		Collection<Section<? extends SimpleTerm>> result = new ArrayList<Section<? extends SimpleTerm>>();
		for (Section<SimpleTerm> ref : allReferencesOfCompilationUnit) {
			result.add(ref);
		}

		result.addAll(getExternalReferencesOfKnowledgeUnit(section));

		return result;

	}

	public abstract Collection<Section<? extends SimpleTerm>> getExternalReferencesOfKnowledgeUnit(Section<? extends KnowledgeUnit> section);

}
