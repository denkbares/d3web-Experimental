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
package de.knowwe.termbrowser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.knowwe.core.Environment;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 02.10.2013
 */
public class DefaultTermDetector extends AbstractTermDetector {

	@Override
	protected Collection<Section<? extends TermDefinition>> getDefs(Section<? extends TermReference> ref, UserContext user) {
		Set<Section<? extends TermDefinition>> result = new HashSet<Section<? extends TermDefinition>>();
		final Collection<TerminologyManager> terminologyManagers = TermBrowserMarkup.getTerminologyManager(user);
		for (TerminologyManager terminologyManager : terminologyManagers) {
			Section<?> termDefiningSection = terminologyManager.getTermDefiningSection(ref.get().getTermIdentifier(
					ref));
			if (termDefiningSection == null) {
				return result;
			}
			if (termDefiningSection.get() instanceof TermDefinition) {
				Section<? extends TermDefinition> def = Sections.cast(termDefiningSection,
						TermDefinition.class);
				result.add(def);
			}
		}
		return result;
	}

	public static Collection<Section<? extends TermDefinition>> getDefinitions(Section<? extends TermReference> ref, UserContext user) {
		return new DefaultTermDetector().getDefs(ref, user);
	}
}
