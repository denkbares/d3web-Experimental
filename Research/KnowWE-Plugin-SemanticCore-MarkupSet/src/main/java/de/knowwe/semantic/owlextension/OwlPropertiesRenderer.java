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

/**
 *
 */
package de.knowwe.semantic.owlextension;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;

/**
 * @author kazamatzuri
 * 
 */
public class OwlPropertiesRenderer implements Renderer {

	private static OwlPropertiesRenderer instance;

	public static synchronized OwlPropertiesRenderer getInstance() {
		if (instance == null) instance = new OwlPropertiesRenderer();
		return instance;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private OwlPropertiesRenderer() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.we.dom.renderer.KnowWEDomRenderer#render(de.d3web.we.dom.Section
	 * , java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder string) {
		String text = sec.getText();
		for (String cur : text.split("\r\n|\r|\n")) {
			if (cur.trim().length() > 0) string.append(cur.trim() + "\\\\");
		}
	}
}
