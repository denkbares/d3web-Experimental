/*
 * Copyright (C) 2013 denkbares GmbH
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

package de.knowwe.ontology.turtle.edit;

import java.util.Set;

import de.d3web.strings.Identifier;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.ontology.turtle.lazyRef.LazyReferenceManager;
import de.knowwe.ontology.turtle.lazyRef.LazyURIReference;

/**
 * Created by jochenreutelshofer on 25.03.14.
 */
public class TermLazyDragRenderer extends TermDragRenderer {

	protected String getIdentifierString(Section<?> section) {
		Section<LazyURIReference> successor = Sections.successor(section, LazyURIReference.class);
		Set<Identifier> potentialMatches = LazyReferenceManager.getInstance()
				.getPotentialMatches(Compilers.getCompiler(section, OntologyCompiler.class), successor.getText());
		if (potentialMatches != null && potentialMatches.size() > 0) {
			return potentialMatches.iterator().next().toExternalForm();
		}
		return null;
	}
}
