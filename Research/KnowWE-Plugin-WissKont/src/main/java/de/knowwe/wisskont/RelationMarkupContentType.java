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
package de.knowwe.wisskont;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.renderer.CompositeRenderer;
import de.knowwe.kdom.renderer.SurroundingRenderer;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 30.11.2012
 */
public class RelationMarkupContentType extends AbstractType {

	public RelationMarkupContentType(String regex) {
		this.setSectionFinder(new RegexSectionFinder(regex, Pattern.MULTILINE
				| Pattern.DOTALL,
				2));
		this.addChildType(new ConceptListContent());
		this.setRenderer(new CompositeRenderer(new SurroundingRenderer() {

			@Override
			public void renderPre(Section<?> section, UserContext user, RenderResult string) {
				string.appendHtml("<span class='relationMarkupContent clearfix' id='"
						+ section.getID() + "' style='display:inline' >");
			}

			@Override
			public void renderPost(Section<?> section, UserContext user, RenderResult string) {
				string.appendHtml("</span>");

			}
		}));
	}

}
