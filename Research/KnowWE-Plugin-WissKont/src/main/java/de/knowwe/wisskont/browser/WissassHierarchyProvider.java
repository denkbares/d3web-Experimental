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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.ontology.browser.util.HierarchyUtils;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.termbrowser.HierarchyProvider;
import de.knowwe.wisskont.SubconceptMarkup;
import de.knowwe.wisskont.ValuesMarkup;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 03.06.2013
 */
public class WissassHierarchyProvider implements HierarchyProvider<Identifier> {
	
	public static final String MAIN_CONCEPT = "Wissass-Begriff";

	@Override
	public List<Identifier> getChildren(Identifier term) {
		return MarkupUtils.getChildrenConcepts(term);
	}

	@Override
	public List<Identifier> getParents(Identifier term) {
		return MarkupUtils.getParentConcepts(term);
	}

	@Override
	public boolean isSuccessorOf(Identifier term1, Identifier term2) {
		String baseUrl = Rdf2GoCore.getInstance().getLocalNamespace();

		String thisConceptURLString = Strings.encodeURL(Strings.unquote(term1.toExternalForm()));
		String thisURL = baseUrl + thisConceptURLString;
		URI thisURI = new URIImpl(thisURL);

		String otherConceptURLString = Strings.encodeURL(Strings.unquote(term2.toExternalForm()));
		String otherURL = baseUrl + otherConceptURLString;
		URI otherURI = new URIImpl(otherURL);

		// TODO: disjunct does not support transitivity properly
		// FIX: there should be a list of properties passed to the
		// isSubConcept-Method
		// HINT: consider direction of property respectively!
		return HierarchyUtils.isSubConceptOf(thisURI, otherURI, new URIImpl(
				baseUrl + SubconceptMarkup.SUBCONCEPT_PROPERTY), null)
				|| HierarchyUtils.isSubConceptOf(otherURI, thisURI, new URIImpl(
						baseUrl + ValuesMarkup.VALUE_PROPERTY), null);
	}


	@Override
	public Collection<Identifier> getAllTerms() {
		Collection<Section<? extends SimpleDefinition>> allTermDefinitions =
				IncrementalCompiler.getInstance().getTerminology().getAllTermDefinitions();
		Set<Identifier> result = new HashSet<Identifier>();
		for (Section<? extends SimpleDefinition> def : allTermDefinitions) {
			result.add(def.get().getTermIdentifier(def));
		}
		return result;
	}

	@Override
	public Collection<Identifier> getStartupTerms() {
		List<Identifier> startTerms = new ArrayList<Identifier>();
		startTerms.add(new Identifier(MAIN_CONCEPT));
		return startTerms;
	}



	@Override
	public Collection<Identifier> filterInterestingTerms(Collection<Identifier> terms) {
		// we do not filter for now
		return terms;
	}


	@Override
	public void updateSettings(UserContext user) {
		// nothing to update
	}
}
