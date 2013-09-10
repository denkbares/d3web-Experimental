/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.kdom.manchester;

import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

/**
 * 
 * @author jochenreutelshofer
 * @created 26.10.2012
 */
public class OWLTermRef extends IncrementalTermReference {

	final Renderer REF_RENDERER =
			new ToolMenuDecoratingRenderer(new StyleRenderer(
					"color:rgb(25, 180, 120)"));

	public OWLTermRef() {
		super(String.class);
		this.setRenderer(REF_RENDERER);
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
