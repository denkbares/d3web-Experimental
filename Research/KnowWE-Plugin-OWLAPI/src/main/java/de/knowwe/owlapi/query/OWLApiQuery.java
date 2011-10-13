/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.owlapi.query;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 *
 * @author Stefan Mark
 * @created 04.10.2011
 */
public class OWLApiQuery extends DefaultMarkupType {

	private static DefaultMarkup MARKUP = null;

	static {
		MARKUP = new DefaultMarkup("owlapi.query");
		MARKUP.addAnnotation("query", true);
		MARKUP.addAnnotation("show", false);
	}

	/**
	 * @param markup
	 */
	public OWLApiQuery() {
		super(MARKUP);
		this.setCustomRenderer(this.getDefaultRenderer());
		this.setIgnorePackageCompile(true);
	}

	@Override
	protected KnowWEDomRenderer<?> getDefaultRenderer() {
		return new OWLApiQueryRenderer<AbstractType>();
	}
}
