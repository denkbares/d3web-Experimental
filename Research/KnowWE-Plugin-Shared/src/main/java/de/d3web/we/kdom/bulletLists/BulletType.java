/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.kdom.bulletLists;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;

public class BulletType extends AbstractType {

	@Override
	public void init() {
		this.sectionFinder = new BulletFinder();
	}

	class BulletFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text,
				Section<?> father, Type type) {

			int index = text.indexOf('*');
			char c = text.charAt(index + 1);
			while (c == ' ') {
				index++;
				c = text.charAt(index);
			}
			ArrayList<SectionFinderResult> resultRegex = new ArrayList<SectionFinderResult>();
			resultRegex.add(new SectionFinderResult(0, index));
			return resultRegex;
		}

	}
}
