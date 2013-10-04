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
package de.knowwe.wisskont.browser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.termbrowser.AbstractTermDetector;

/**
 * 
 * @author jochenreutelshofer
 * @created 03.06.2013
 */
public class IncrementalTermDetector extends AbstractTermDetector {

	/**
	 * 
	 * @created 02.10.2013
	 * @param ref
	 * @return
	 */
	@Override
	protected Collection<Section<? extends TermDefinition>> getDefs(Section<TermReference> ref, String master) {
		Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				ref.get().getTermIdentifier(ref));
		Set<Section<? extends TermDefinition>> result = new HashSet<Section<? extends TermDefinition>>();
		for (Section<? extends SimpleDefinition> section : termDefinitions) {
			result.add(section);
		}
		return result;
	}

}
