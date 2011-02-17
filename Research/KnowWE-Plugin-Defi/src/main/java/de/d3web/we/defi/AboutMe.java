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
package de.d3web.we.defi;

import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;


/**
 * 
 * @author smark
 * @created 25.01.2011
 */
public class AboutMe extends DefaultMarkupType {

	private static DefaultMarkup MARKUP = null;

	static {
		MARKUP = new DefaultMarkup("aboutme");
		MARKUP.addAnnotation("avatar", true);
		MARKUP.addAnnotation("about", true);
	}

	/**
	 * @param markup
	 */
	public AboutMe() {
		super(MARKUP);
		this.setCustomRenderer(this.getDefaultRenderer());
		this.setIgnorePackageCompile(true);
	}

	@Override
	protected KnowWEDomRenderer<?> getDefaultRenderer() {
		return new AboutMeRenderer<KnowWEObjectType>();
	}
}
