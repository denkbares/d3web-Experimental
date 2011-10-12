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

package de.knowwe.kdom;

import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.renderer.GenericHTMLRenderer;
import de.knowwe.kdom.TestChildren1;
import de.knowwe.kdom.TestChildren2;
import de.knowwe.kdom.TestChildren3;

public class TestType extends TermDefinition {

	public TestType() {

		childrenTypes.add(new TestChildren1());
		childrenTypes.add(new TestChildren2());
		childrenTypes.add(new TestChildren3());
		setSectionFinder(new RegexSectionFinder(".*"));

		setCustomRenderer(new GenericHTMLRenderer<TestType>("span", new String[] {"style", "color: red;", "title", "TestType"}));
	}

}