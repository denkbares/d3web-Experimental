/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.compile;

import java.util.Collection;

import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;

/**
 * A hazard filter helps the incremental compiler to not remove and subsequently
 * insert the same piece of knowledge in one compile step.
 * 
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 09.07.2012
 */
public interface HazardFilter {

	/**
	 * Elements that are equally (text-equal) appearing in both collections are
	 * sorted out from both collections evenly.
	 * 
	 * @created 09.07.2012
	 * @param insert
	 * @param remove
	 */
	public void filter(Collection<Section<? extends KnowledgeUnit>> insert, Collection<Section<? extends KnowledgeUnit>> remove);

	public void filterDefs(Collection<Section<SimpleDefinition>> insert, Collection<Section<SimpleDefinition>> remove);
}
