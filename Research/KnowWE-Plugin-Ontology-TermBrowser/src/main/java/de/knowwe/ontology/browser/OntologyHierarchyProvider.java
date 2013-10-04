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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.strings.Identifier;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.HierarchyUtils;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.termbrowser.HierarchyProvider;

/**
 * 
 * @author jochenreutelshofer
 * @created 01.10.2013
 */
public class OntologyHierarchyProvider implements HierarchyProvider {

	private List<String> relations = null;
	private String master = null;

	@Override
	public void setAdditionalHierarchyRelations(List<String> relations) {
		this.relations = relations;
	}

	@Override
	public List<String> getChildren(String term) {
		Rdf2GoCore core = Rdf2GoCore.getInstance(Environment.DEFAULT_WEB, master);
		term = expandTermLocalNSIfNecessary(term, core);
		URI termURI = new URIImpl(Rdf2GoUtils.expandNamespace(core, term));
		List<String> result = new ArrayList<String>();

		for (String relation : relations) {

			List<URI> childrenConcepts2 = HierarchyUtils.getChildrenConcepts(termURI, new URIImpl(
					Rdf2GoUtils.expandNamespace(core, relation)),
					master);
			for (URI uri : childrenConcepts2) {
				String reducedNamespace = Rdf2GoUtils.reduceNamespace(core, uri.toString());
				String name = reducedNamespace.substring(reducedNamespace.indexOf(":") + 1);
				result.add(name);
			}
		}

		return result;
	}

	@Override
	public List<String> getParents(String term) {
		Rdf2GoCore core = Rdf2GoCore.getInstance(Environment.DEFAULT_WEB, master);
		term = expandTermLocalNSIfNecessary(term, core);

		URI termURI = new URIImpl(Rdf2GoUtils.expandNamespace(core, term));
		List<String> result = new ArrayList<String>();

		for (String relation : relations) {

			List<URI> childrenConcepts2 = HierarchyUtils.getParentConcepts(termURI, new URIImpl(
					Rdf2GoUtils.expandNamespace(core, relation)),
					master);
			for (URI uri : childrenConcepts2) {
				String reducedNamespace = Rdf2GoUtils.reduceNamespace(core, uri.toString());
				String name = reducedNamespace.substring(reducedNamespace.indexOf(":") + 1);
				result.add(name);
			}
		}

		return result;
	}

	/**
	 * 
	 * @created 01.10.2013
	 * @param term
	 * @return
	 */
	private String expandTermLocalNSIfNecessary(String term, Rdf2GoCore core) {
		if (!(term.contains(":"))) {
			String namespace = Rdf2GoUtils.expandNamespace(core, "lns:");
			term = namespace + term;
		}
		return term;
	}

	@Override
	public boolean isSubNodeOf(String term1, String term2) {
		Rdf2GoCore core = Rdf2GoCore.getInstance(Environment.DEFAULT_WEB, master);
		term1 = expandTermLocalNSIfNecessary(term1, core);
		term2 = expandTermLocalNSIfNecessary(term2, core);
		URI term1URI = new URIImpl(Rdf2GoUtils.expandNamespace(core, term1));
		URI term2URI = new URIImpl(Rdf2GoUtils.expandNamespace(core, term2));

		for (String relation : relations) {
			boolean is = HierarchyUtils.isSubConceptOf(term1URI, term2URI, new URIImpl(
					Rdf2GoUtils.expandNamespace(core, relation)), master);
			if (is) return true;
		}

		return false;
	}

	@Override
	public void setMaster(String master) {
		this.master = master;
	}

	@Override
	public Collection<String> getAllTerms() {
		TerminologyManager terminologyManager = Environment.getInstance().getTerminologyManager(
				Environment.DEFAULT_WEB, master);
		Collection<Identifier> definedTerms = terminologyManager.getAllDefinedTerms();
		Set<String> result = new HashSet<String>();
		for (Identifier definedTerm : definedTerms) {
			result.add(definedTerm.getLastPathElement());
		}
		return result;
	}

}
