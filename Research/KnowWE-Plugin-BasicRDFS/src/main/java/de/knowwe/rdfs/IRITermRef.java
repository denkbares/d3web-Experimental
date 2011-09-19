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

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.tools.ToolMenuDecoratingRenderer;
import de.knowwe.compile.object.IncrementalTermReference;

public class IRITermRef extends IncrementalTermReference<String> {

	@SuppressWarnings("unchecked")
	final KnowWEDomRenderer<IRITermRef> REF_RENDERER =
			new ToolMenuDecoratingRenderer<IRITermRef>(new StyleRenderer(
					"color:rgb(25, 180, 120)"));

	public IRITermRef() {
		super(String.class);
		this.setCustomRenderer(REF_RENDERER);
	}

	@Override
	public String getTermIdentifier(Section<? extends KnowWETerm<String>> s) {
		// dirty hack for colons '::'
		// TODO: fix
		if (s.getOriginalText().endsWith("::")) return s.getOriginalText().substring(0,
				s.getOriginalText().length() - 2);

		return s.getOriginalText();

	}

	@Override
	public String getTermObjectDisplayName() {
		return "URI";
	}

}
