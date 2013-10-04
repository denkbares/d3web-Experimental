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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;

/**
 * 
 * @author jochenreutelshofer
 * @created 02.10.2013
 */
public abstract class AbstractTermDetector implements InterestingTermDetector {

	@Override
	public Map<String, Double> getWeightedTermsOfInterest(Article article, String master) {
		Map<String, Double> interestingTerms = new HashMap<String, Double>();

		List<Section<TermDefinition>> definitions = Sections.findSuccessorsOfType(
				article.getRootSection(), TermDefinition.class);
		for (Section<TermDefinition> def : definitions) {
			String termname = def.get().getTermIdentifier(def).toExternalForm().replaceAll("\"", "");
			// if (termname.contains(":")) {
			// termname = termname.substring(termname.indexOf(":") + 1);
			// }
			interestingTerms.put(termname, WEIGHT_DEFINITION);
		}

		List<Section<TermReference>> references = Sections.findSuccessorsOfType(
				article.getRootSection(), TermReference.class);
		for (Section<TermReference> ref : references) {
			// String termname = ref.get().getTermName(ref);
			Collection<Section<? extends TermDefinition>> termDefinitions = getDefs(ref, master);
			if (termDefinitions.size() > 0) {
				String termname = ref.get().getTermIdentifier(ref).toExternalForm().replaceAll(
						"\"", "");
				// if (termname.contains(":")) {
				// termname = termname.substring(termname.indexOf(":") + 1);
				// }
				interestingTerms.put(termname, WEIGHT_REFERENCE);
			}
		}

		return interestingTerms;
	}

	/**
	 * 
	 * @created 02.10.2013
	 * @param ref
	 * @return
	 */
	protected abstract Collection<Section<? extends TermDefinition>> getDefs(Section<? extends TermReference> ref, String master);
}
