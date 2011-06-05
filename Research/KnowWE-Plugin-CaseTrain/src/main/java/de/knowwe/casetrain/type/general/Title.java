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
package de.knowwe.casetrain.type.general;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.ExactlyOneFindingConstraint;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;

/**
 * 
 * Belongs to BlockMarkupType. Represents the rest of the first line after the
 * keyword.
 * 
 * TODO Finds an empty title when the line looks like this
 *      MetaDaten:\r\n
 *      This is wrong. It should find no title at all.
 * 
 * @author Jochen
 * @created 06.04.2011
 */
public class Title extends AbstractType {

	public static final String TITLE = "Titel";

	public Title() {
		ConstraintSectionFinder cf = new ConstraintSectionFinder(new LineSectionFinder());
		cf.addConstraint(ExactlyOneFindingConstraint.getInstance());
		this.setSectionFinder(cf);

		//		this.setCustomRenderer(new DivStyleClassRenderer(TITLE, null));

	}

}
