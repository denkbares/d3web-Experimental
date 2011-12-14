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

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.AnonymousType;

public class TurtleMarkupN3 extends AbstractType {

	public TurtleMarkupN3() {
		AnonymousType before = new AnonymousType("Before");
		before.setSectionFinder(new RegexSectionFinder("ttl:?"));
		childrenTypes.add(before);
		AnonymousType after = new AnonymousType("After");
		after.setSectionFinder(new RegexSectionFinder("\\."));
		childrenTypes.add(after);
		childrenTypes.add(new TurtleMarkupN3Content());
		setSectionFinder(new RegexSectionFinder("ttl:?(.*?)\\.", Pattern.DOTALL
				| Pattern.MULTILINE, 1));

	}

}