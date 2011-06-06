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
import de.knowwe.termObject.IRIEntityType.IRIDeclarationType;

public class ObjectPropertyIRIDefinition extends AbstractIRITermDefinition {
	
	public static final StyleRenderer PROPERTY_RENDERER = new StyleRenderer("color:rgb(40, 40, 160)");
	
	public ObjectPropertyIRIDefinition() {
		this.setCustomRenderer(PROPERTY_RENDERER);
	}

	@Override
	public String getTermIdentifier(Section<? extends KnowWETerm<IRIEntityType>> s) {
		return s.getOriginalText();
	}

	@Override
	protected IRIDeclarationType getIRIDeclarationType() {
		return IRIDeclarationType.OBJECT_PROPERTY;
	}
}


