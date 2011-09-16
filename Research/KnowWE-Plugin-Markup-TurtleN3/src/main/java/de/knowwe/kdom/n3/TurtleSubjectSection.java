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

package de.knowwe.kdom.n3;

import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.AtMostOneFindingConstraint;
import java.util.regex.Pattern;
import de.knowwe.core.renderer.GenericHTMLRenderer;
import de.knowwe.kdom.n3.TurtleSubject;
import de.d3web.we.kdom.type.AnonymousType;

public class TurtleSubjectSection extends AbstractType {

	public TurtleSubjectSection() {
		AnonymousType after = new AnonymousType("After");
		after.setSectionFinder(new RegexSectionFinder("\\b[^\\s]*::"));
		childrenTypes.add(after);
		childrenTypes.add(new TurtleSubject());
		ConstraintSectionFinder c = new ConstraintSectionFinder(new RegexSectionFinder("(.*?)\\b[^\\s]*::",Pattern.DOTALL|Pattern.MULTILINE,1));
		setSectionFinder(c);
		c.addConstraint(AtMostOneFindingConstraint.getInstance());
		setCustomRenderer(new GenericHTMLRenderer<TurtleSubjectSection>("span", new String[] {"style", "color: red;", "title", "TurtleSubjectSection"}));
	}

}