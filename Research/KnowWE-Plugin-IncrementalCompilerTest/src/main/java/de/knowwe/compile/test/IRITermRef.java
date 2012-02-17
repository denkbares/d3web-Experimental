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

package de.knowwe.compile.test;

import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWERenderer;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

public class IRITermRef extends IncrementalTermReference {

	@SuppressWarnings("unchecked")
	final KnowWERenderer<IRITermRef> REF_RENDERER =
			new ToolMenuDecoratingRenderer<IRITermRef>(new StyleRenderer(
					"color:rgb(25, 180, 120)"));

	public IRITermRef() {
		super(String.class);
		this.setRenderer(REF_RENDERER);
	}

	@Override
	public String getTermIdentifier(Section<? extends SimpleTerm> s) {
		// dirty hack for colons '::'
		// TODO: fix
		if (s.getText().endsWith("::")) return s.getText().substring(0,
				s.getText().length() - 2);

		return s.getText();

	}

}
