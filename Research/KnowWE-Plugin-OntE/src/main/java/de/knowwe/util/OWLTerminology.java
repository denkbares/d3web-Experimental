/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.util;

import java.util.ResourceBundle;

import de.d3web.core.knowledge.TerminologyManager;
import de.knowwe.core.compile.TerminologyExtension;

/**
 * Loads predefined OWL Terms into the {@link TerminologyManager}.
 *
 * @author Stefan Mark
 * @created 22.11.2011
 */
public class OWLTerminology implements TerminologyExtension {

	private static ResourceBundle terms;

	static {
		terms = ResourceBundle.getBundle("OWL-Terminology");
	}

	@Override
	public String[] getTermNames() {
		return terms.keySet().toArray(new String[terms.keySet().size()]);
	}
}