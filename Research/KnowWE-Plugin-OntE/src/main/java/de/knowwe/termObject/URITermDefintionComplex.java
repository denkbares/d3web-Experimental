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

package de.knowwe.termObject;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermReference;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;

public abstract class URITermDefintionComplex extends URITermDefinition{

	
	@Override
	protected boolean checkDependencies(Section<URITermDefinition> s) {
		Section<?> father = s.getFather();
		List<Section<TermReference>> refs = new ArrayList<Section<TermReference>>();
		Sections.findSuccessorsOfType(father, TermReference.class, refs);
		TerminologyHandler tHandler = KnowWEUtils.getTerminologyHandler(s.getArticle().getWeb());
		for (Section<TermReference> section : refs) {
			boolean valid = tHandler.isDefinedTerm(s.getArticle(), section.get().getTermName(section),KnowWETerm.GLOBAL);
			if(!valid) return false;
		}
		
		return true;
	}
}
