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

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.user.UserContext;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 23.06.2013
 */
public class KeyType extends AbstractType {

	public KeyType(String keyRegex) {
		this.setRenderer(new Renderer() {

			@Override
			public void render(Section<?> section, UserContext user, RenderResult string) {
				string.appendHtml("<span title='Für diesen Textabschnitt relevante Begriffe' style='font-style:italic;'>");
				string.append(section.getText());
				string.appendHtml("</span>");
			}
		});
		this.setSectionFinder(new RegexSectionFinder("(?i)" + keyRegex,
				1));
	}
}