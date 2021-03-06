/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.rdfs.wikiObjectModel.types;

import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.sectionFinder.OneOfStringFinderExact;
import de.knowwe.rdfs.IRITermRef;
import de.knowwe.rdfs.wikiObjectModel.Utils;

/**
 * 'this' keyword allows to reference on the local WikiContent element (section
 * or page).
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 09.07.2012
 */
public class LocalConceptReference extends IRITermRef {

	private static final String[] THIS_KEYS = new String[] { "this" };

	/**
	 * 
	 */
	public LocalConceptReference() {
		this.setSectionFinder(new OneOfStringFinderExact(THIS_KEYS));
	}

	@Override
	public String getTermName(Section<? extends Term> s) {
		return Utils.findContextConceptTermname(s);
	}

}
