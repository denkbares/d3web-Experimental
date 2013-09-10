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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.termbrowser.InterestingTermDetector;
import de.knowwe.wisskont.ConceptMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 03.06.2013
 */
public class WissassTermDetector implements InterestingTermDetector {

	private static final double WEIGHT_REFERENCE = 0.5;
	private static final double WEIGHT_DEFINITION = 1.0;

	@Override
	public Map<String, Double> getWeightedTermsOfInterest(Article article) {
		Map<String, Double> interestingTerms = new HashMap<String, Double>();

		List<Section<SimpleDefinition>> definitions = Sections.findSuccessorsOfType(
				article.getRootSection(), SimpleDefinition.class);
		for (Section<SimpleDefinition> def : definitions) {
			String termname = def.get().getTermName(def);
			// only those defined by by concept markups are added to the
			// term recommender
			if (Sections.findAncestorOfType(def, ConceptMarkup.class) != null) {
				interestingTerms.put(termname, WEIGHT_DEFINITION);
			}
		}

		List<Section<SimpleReference>> references = Sections.findSuccessorsOfType(
				article.getRootSection(), SimpleReference.class);
		for (Section<SimpleReference> ref : references) {
			String termname = ref.get().getTermName(ref);
			Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
					ref.get().getTermIdentifier(ref));
			if (termDefinitions.size() > 0) {
				Section<? extends SimpleDefinition> def = termDefinitions.iterator().next();
				// only those defined by concept markups are added to the
				// term recommender
				if (Sections.findAncestorOfType(def, ConceptMarkup.class) != null) {
					interestingTerms.put(termname, WEIGHT_REFERENCE);
				}
			}
		}

		return interestingTerms;
	}

}
