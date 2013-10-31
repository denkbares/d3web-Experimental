/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.annotation.type.list;

import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.compile.object.renderer.ReferenceSurroundingRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.kdom.render.CompositeRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.06.2013
 */
public class ListObjectIdentifier extends IncrementalTermReference {

	public ListObjectIdentifier(Renderer r) {
		super(String.class);
		this.setSectionFinder(new AllTextFinderTrimmed());
		CompositeRenderer renderer = new CompositeRenderer(r,
				new ReferenceSurroundingRenderer());
		this.setRenderer(new ToolMenuDecoratingRenderer(renderer));
	}

}
