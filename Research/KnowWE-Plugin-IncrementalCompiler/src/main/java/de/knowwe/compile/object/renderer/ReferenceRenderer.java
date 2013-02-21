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
package de.knowwe.compile.object.renderer;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

/**
 * 
 * @author jochenreutelshofer
 * @created 27.11.2012
 */
/**
 * 
 * 
 * @author Jochen
 * @created 09.06.2011
 */
public class ReferenceRenderer implements Renderer {

	final Renderer REF_RENDERER =
			new ToolMenuDecoratingRenderer(new StyleRenderer(
					"color:rgb(25, 180, 120)"));

	final Renderer PREDEFINDED_TERM_RENDERER =
			new ToolMenuDecoratingRenderer(new StyleRenderer(
					"font-weight:bold;font-color:black"));

	public Renderer getRenderer() {
		return r;
	}

	public void setRenderer(Renderer r) {
		this.r = r;
	}

	private Renderer r = null;

	/**
	 * 
	 */
	public ReferenceRenderer() {
		r = new ToolMenuDecoratingRenderer(new DelegateRenderer());
	}

	public ReferenceRenderer(Renderer renderer) {
		if (renderer != null) {
			r = new ToolMenuDecoratingRenderer(renderer);
		}
		else {
			r = new ToolMenuDecoratingRenderer(new DelegateRenderer());
		}
	}

	@Override
	public void render(Section<?> section, UserContext user, RenderResult string) {

		@SuppressWarnings("unchecked")
		Section<? extends SimpleTerm> reference = (Section<? extends SimpleTerm>) section;

		if (IncrementalCompiler.getInstance().getTerminology().isPredefinedObject(
				reference.get().getTermIdentifier(reference))) {
			PREDEFINDED_TERM_RENDERER.render(reference, user, string);
		}
		else if (IncrementalCompiler.getInstance().getTerminology().isImportedObject(
				reference.get().getTermIdentifier(reference))) {
			REF_RENDERER.render(reference, user, string);
		}
		else {
			string.appendHtml("<a name='" + reference.getID() + "'>");
			string.appendHtml("</a>");
			r.render(reference, user, string);
		}

	}
}
