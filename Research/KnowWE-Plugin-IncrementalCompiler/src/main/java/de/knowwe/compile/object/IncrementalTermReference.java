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

import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.rendering.Renderer;

public abstract class IncrementalTermReference extends SimpleReference {

	public IncrementalTermReference(Class<?> termObjectClass) {
		super(TermRegistrationScope.GLOBAL, termObjectClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.knowwe.core.kdom.AbstractType#setRenderer(de.knowwe.core.kdom.rendering
	 * .Renderer)
	 * 
	 * 
	 * makes sure that the ReferenceRenderer for the error-messages is actually
	 * installed and called and does not get overridden
	 */
	@Override
	public void setRenderer(Renderer renderer) {
		if (this.getRenderer() != null) {
			if (this.getRenderer() instanceof ReferenceRenderer) {
				((ReferenceRenderer) this.getRenderer()).setRenderer(renderer);
			}
		}
		else {
			super.setRenderer(renderer);
		}
	}

}
