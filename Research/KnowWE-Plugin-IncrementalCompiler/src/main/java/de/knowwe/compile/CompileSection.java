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
package de.knowwe.compile;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;

/**
 * Wrapper class for sections allowing equals to compare only the text-content.
 * The compilation algorithm presumes, that identical text-statements also
 * result in the same entities in the target representation when being
 * translated
 * 
 * @author Jochen
 * @created 04.03.2012
 */
public class CompileSection<T extends Type> {

	T type;

	public T get() {
		return type;
	}

	public Section<T> getSection() {
		return section;
	}

	private Section<T> section = null;

	public static <T extends Type> CompileSection<T> create(Section<T> s) {
		return new CompileSection<T>(s);
	}

	public CompileSection(Section<T> s) {
		this.section = s;
		type = s.get();
	}

	@Override
	public int hashCode() {
		return section.getText().hashCode();
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof CompileSection) {
			CompileSection<?> other = (CompileSection<?>) arg0;
			if (!(other.section.getText().equals(this.section.getText()))) {
				return false;
			}
			else {
				if (Sections.createTypePathToRoot(other.section).equals(
						Sections.createTypePathToRoot(this.section))) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return section.toString();
	}

}
