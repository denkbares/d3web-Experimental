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

package de.knowwe.rdfs;

import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.compile.object.renderer.ReferenceRenderer;
import de.knowwe.compile.object.renderer.ReferenceSurroundingRenderer;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.render.CompositeRenderer;
import de.knowwe.kdom.renderer.StyleRenderer;

public class IRITermRef extends IncrementalTermReference {

	public IRITermRef() {
		super(String.class);
		this.setRenderer(new CompositeRenderer(new ReferenceRenderer(new StyleRenderer(
				"color:rgb(25, 180, 120)")),
				new ReferenceSurroundingRenderer()));
	}

	@Override
	public String getTermName(Section<? extends Term> s) {
		// dirty hack for colons '::'
		// TODO: fix
		String identifierString = s.getText();
		if (s.getText().endsWith("::")) identifierString = s.getText().substring(0,
				s.getText().length() - 2);

		return identifierString;

	}

}
