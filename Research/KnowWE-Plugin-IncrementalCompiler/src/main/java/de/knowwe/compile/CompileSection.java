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

/**
 * Wrapper class for sections allowing equals to compare only the text-content.
 * The compilation algorithm presumes, that identical text-statements also
 * result in the same entities in the target representation when being
 * translated
 * 
 * @author Jochen
 * @created 04.03.2012
 */
public class CompileSection {

	public Section<? extends Type> getSection() {
		return section;
	}

	private Section<? extends Type> section = null;

	public CompileSection(Section<? extends Type> s) {
		this.section = s;
	}

	@Override
	public int hashCode() {
		return section.getText().hashCode();
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof CompileSection) {
			CompileSection other = (CompileSection) arg0;
			return other.section.getText().equals(this.section.getText());
		}
		return false;
	}

}
