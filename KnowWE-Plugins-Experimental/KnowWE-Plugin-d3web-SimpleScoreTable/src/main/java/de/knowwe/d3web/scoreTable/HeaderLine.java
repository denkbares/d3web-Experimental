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

/* THIS FILE IS GENERATED. DO NOT EDIT */

package de.knowwe.d3web.scoreTable;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.d3web.scoreTable.renderer.TableLineRenderer;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;

public class HeaderLine extends AbstractType {

	public HeaderLine() {
		AnonymousType before = new AnonymousType("Before");
		before.setSectionFinder(new RegexSectionFinder("\\s*"));
		this.addChildType(before);
		AnonymousType after = new AnonymousType("After");
		after.setSectionFinder(new RegexSectionFinder("\\r?\\n"));
		this.addChildType(after);
		this.addChildType(new CornerCell());
		this.addChildType(new SolutionCell());
		this.addChildType(new Bar());
		ConstraintSectionFinder c = new ConstraintSectionFinder(new RegexSectionFinder("\\s*(\\|{1,2}.*?)\\r?\\n",Pattern.DOTALL|Pattern.MULTILINE,0));
		setSectionFinder(c);
		c.addConstraint(AtMostOneFindingConstraint.getInstance());
		//setCustomRenderer(new GenericHTMLRenderer<HeaderLine>("span", new String[] {"style", "color: blue;", "title", "HeaderLine"}));
		setRenderer(new TableLineRenderer());
	}

}