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

import de.d3web.strings.Identifier;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.ontology.kdom.resource.ResourceDefinition;
import de.knowwe.ontology.kdom.resource.ResourceReference;
import de.knowwe.termbrowser.BrowserTerm;
import de.knowwe.termbrowser.DefaultTermDetector;
import de.knowwe.termbrowser.InterestingTermDetector;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 04.10.2013
 */
public class OntologyTermDetector implements InterestingTermDetector {

	@Override
	public Map<BrowserTerm, Double> getWeightedTermsOfInterest(Article a, UserContext user) {
		Map<BrowserTerm, Double> interestingTerms = new HashMap<BrowserTerm, Double>();

		List<Section<ResourceDefinition>> definitions = Sections.successors(
				a.getRootSection(), ResourceDefinition.class);
		for (Section<ResourceDefinition> def : definitions) {
			Identifier termname = def.get().getTermIdentifier(def);
			interestingTerms.put(new BrowserTerm(termname, user), WEIGHT_DEFINITION);
		}

		List<Section<ResourceReference>> references = Sections.successors(
				a.getRootSection(), ResourceReference.class);
		for (Section<ResourceReference> ref : references) {
			Identifier termname = ref.get().getTermIdentifier(ref);
			Collection<Section<? extends TermDefinition>> termDefinitions = DefaultTermDetector.getDefinitions(
					ref, user);
			if (termDefinitions.size() > 0) {
				interestingTerms.put(new BrowserTerm(termname, user), WEIGHT_REFERENCE);
			}
		}

		return interestingTerms;
	}

}
