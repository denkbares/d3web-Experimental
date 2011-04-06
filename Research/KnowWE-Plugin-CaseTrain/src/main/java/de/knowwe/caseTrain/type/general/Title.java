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
package de.knowwe.caseTrain.type.general;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.ExactlyOneFindingConstraint;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * Belongs to BlockMarkupType. Represents the rest of the first line after the
 * keyword.
 * 
 * 
 * @author Jochen
 * @created 06.04.2011
 */
public class Title extends AbstractType {

	public Title() {
		ConstraintSectionFinder cf = new ConstraintSectionFinder(new LineSectionFinder());
		cf.addConstraint(ExactlyOneFindingConstraint.getInstance());
		this.setSectionFinder(cf);

		this.setCustomRenderer(new KnowWEDomRenderer<Title>() {

			// TODO: use existing renderer class
			@Override
			public void render(KnowWEArticle article, Section<Title> sec, UserContext user, StringBuilder string) {
				if (sec.getOriginalText().trim().equals("")) return; // if empty
																		// do
																		// nothing
				Section<? extends Type> father = sec.getFather().getFather();
				String classPrefix = father.get().getClass().getSimpleName();

				string.append(KnowWEUtils.maskHTML("<div class='" +
							"Titel"
						+ "'>"));
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</div>"));

			}
		});

	}

}
