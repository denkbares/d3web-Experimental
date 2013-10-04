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
package de.knowwe.ontology.browser;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.ontology.kdom.resource.AbbreviatedResourceDefinition;
import de.knowwe.ontology.kdom.resource.AbbreviatedResourceReference;
import de.knowwe.termbrowser.DefaultTermDetector;
import de.knowwe.termbrowser.InterestingTermDetector;

/**
 * 
 * @author jochenreutelshofer
 * @created 04.10.2013
 */
public class OntologyTermDetector implements InterestingTermDetector {

	@Override
	public Map<String, Double> getWeightedTermsOfInterest(Article a, String master) {
		Map<String, Double> interestingTerms = new HashMap<String, Double>();

		List<Section<AbbreviatedResourceDefinition>> definitions = Sections.findSuccessorsOfType(
				a.getRootSection(), AbbreviatedResourceDefinition.class);
		for (Section<AbbreviatedResourceDefinition> def : definitions) {
			String termname = def.get().getTermIdentifier(def).toExternalForm().replaceAll("\"", "");
			interestingTerms.put(termname, WEIGHT_DEFINITION);
		}

		List<Section<AbbreviatedResourceReference>> references = Sections.findSuccessorsOfType(
				a.getRootSection(), AbbreviatedResourceReference.class);
		for (Section<AbbreviatedResourceReference> ref : references) {
			String termname = ref.get().getTermIdentifier(ref).toExternalForm().replaceAll(
					"\"", "");
			Collection<Section<? extends TermDefinition>> termDefinitions = DefaultTermDetector.getDefinitions(
					ref, master);
			if (termDefinitions.size() > 0) {
				interestingTerms.put(termname, WEIGHT_REFERENCE);
			}
		}

		return interestingTerms;
	}

}
