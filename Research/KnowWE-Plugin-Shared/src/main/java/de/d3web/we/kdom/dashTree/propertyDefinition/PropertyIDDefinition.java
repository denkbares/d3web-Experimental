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

package de.d3web.we.kdom.dashTree.propertyDefinition;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * @author Jochen
 * 
 *         Type for an (OWL-) property-definition
 * 
 */
public class PropertyIDDefinition extends AbstractType {

	private static PropertyIDDefinition defaultInstance = null;

	public static PropertyIDDefinition getDefaultInstance() {
		if (defaultInstance == null) {
			defaultInstance = new PropertyIDDefinition();

		}

		return defaultInstance;
	}

	@Override
	protected void init() {
		this.sectionFinder = new AllTextFinderTrimmed();
		this.setCustomRenderer(new PropertyIDRenderer());
	}

	class PropertyIDRenderer extends KnowWEDomRenderer {

		@Override
		public void render(KnowWEArticle article, Section sec,
				UserContext user, StringBuilder string) {

			string.append(KnowWEUtils.maskHTML("<span title=\"ObjectProperty Definition\">"));
			StyleRenderer.PROPERTY.render(article, sec, user,
					string);
			string.append(KnowWEUtils.maskHTML("</span>"));
		}

	}
}
