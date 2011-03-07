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

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.termObject.URIObject.URIObjectType;

public class LocalConceptDefinition extends URITermDefinition {
	

	public static final StyleRenderer CLASS_RENDERER = new StyleRenderer(
			"color:rgb(152, 180, 12)");

	public static final String LOCAL_KEY = "this";
	
	public LocalConceptDefinition() {
		this.setSectionFinder(new RegexSectionFinder("def\\s*?" + LOCAL_KEY + "\\s*?"));
		this.setCustomRenderer(CLASS_RENDERER);
	}

	@Override
	protected URIObjectType getURIObjectType() {
		return URIObjectType.unspecified;
	}

	@Override
	public String getTermName(Section<? extends KnowWETerm<URIObject>> s) {
		return s.getArticle().getTitle();
	}
	
	

}
