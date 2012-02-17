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
package de.knowwe.casetrain.type;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWERenderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;


/**
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class AttributeContent extends AbstractType {

	public AttributeContent() {
		this.setSectionFinder(new AllTextFinderTrimmed());
		this.setRenderer(new KnowWERenderer<MetaLine>() {

			@Override
			public void render(Section<MetaLine> sec, UserContext user, StringBuilder string) {

				string.append(KnowWEUtils.maskHTML("<td>"));
				DelegateRenderer.getInstance().render(sec, user, string);
				string.append(KnowWEUtils.maskHTML("</td>"));

			}
		});
	}
}
