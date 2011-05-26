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

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;


/**
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class AttributeName extends AbstractType {

	public AttributeName() {
		this.setSectionFinder(new SectionFinder(){

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
				int ende = text.indexOf(":");
				List<SectionFinderResult> res = new ArrayList<SectionFinderResult>();
				if (ende != -1) {
					res.add(new SectionFinderResult(0, ende));
				}
				return res;
			}

		});
		this.setCustomRenderer(new KnowWEDomRenderer<MetaLine>() {

			@Override
			public void render(KnowWEArticle article, Section<MetaLine> sec, UserContext user, StringBuilder string) {
				string.append(KnowWEUtils.maskHTML("<td>"));
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</td>"));

			}
		});
	}
}
