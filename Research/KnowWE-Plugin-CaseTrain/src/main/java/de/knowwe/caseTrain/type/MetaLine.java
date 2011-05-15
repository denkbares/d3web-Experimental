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
package de.knowwe.caseTrain.type;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.rendering.NothingRenderer;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;


/**
 * 
 * A Line of the MetaDaten table.
 * The Delimiter ":" is not rendered into the table.
 * 
 * @author Jochen
 * @created 6.04.2011
 */
public class MetaLine extends AbstractType {

	public MetaLine() {
		this.setSectionFinder(new LineSectionFinder());
		this.addChildType(new AttributeName());
		this.addChildType(new Delimiter());
		this.addChildType(new AttributeContent());

		this.setCustomRenderer(new KnowWEDomRenderer<MetaLine>() {

			@Override
			public void render(KnowWEArticle article, Section<MetaLine> sec, UserContext user, StringBuilder string) {
				string.append(KnowWEUtils.maskHTML("<tr>"));
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</tr>"));
			}
		});


	}

	class Delimiter extends AbstractType {

		public Delimiter() {
			this.setSectionFinder(new RegexSectionFinder(":"));
			this.setCustomRenderer(NothingRenderer.getInstance());
		}

	}

}
