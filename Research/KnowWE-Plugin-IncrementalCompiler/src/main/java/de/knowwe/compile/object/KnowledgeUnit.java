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

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.objects.TermReference;
/**
 * 
 * Interface for types that have to be compiled to a knowledge repository
 * creating one or multiple knowledge slices
 * 
 * 
 * @author Jochen
 * @created 08.06.2011
 */
public interface KnowledgeUnit<T extends Type> extends Type{
	
	public Collection<Section<TermReference>> getAllReferences(Section<? extends KnowledgeUnit<T>> section);
	
	
	
	public void insertIntoRepository(Section<T> section);

	public void deleteFromRepository(Section<T> section);

}
