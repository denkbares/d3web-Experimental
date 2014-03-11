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
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.core.user.UserContext;
import de.knowwe.ontology.browser.cache.SparqlCacheManager;
import de.knowwe.ontology.browser.util.HierarchyUtils;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.termbrowser.HierarchyProvider;
import de.knowwe.termbrowser.TermBrowserMarkup;

/**
 * @author jochenreutelshofer
 * @created 01.10.2013
 */
public class OntologyHierarchyProvider implements HierarchyProvider<Identifier> {

	protected List<String> categories = new ArrayList<String>();
	protected List<String> ignoredTerms = new ArrayList<String>();

	protected List<String> relations = new ArrayList<String>();
	protected String master = null;
	protected UserContext user = null;

	protected boolean mixedRelationHierarchyMode = false;

	private String getShortURI(Identifier termID) {
		String[] termElements = termID.getPathElements();

		if (termElements.length == 1) {
			termElements = new String[] {
					"lns:", termElements[0] };
		}

		return termElements[0] + ":" + Strings.encodeURL(termElements[1]);
	}

	private String getURIString(Identifier termID) {
		Rdf2GoCore core = getCore();
		return Rdf2GoUtils.expandNamespace(core, getShortURI(termID));
	}

	private Rdf2GoCore getCore() {
		Rdf2GoCore core;
		if (master == null) {
			core = Rdf2GoCore.getInstance();
		}
		else {
			Rdf2GoCompiler compiler = Compilers.getCompiler(TermBrowserMarkup.getTermBrowserMarkup(user), Rdf2GoCompiler.class);
			if (compiler == null) {
				core = Rdf2GoCore.getInstance(Environment.DEFAULT_WEB, master);
			}
			else {
				core = compiler.getRdf2GoCore();
			}

		}
		return core;
	}

	@Override
	public List<Identifier> getChildren(Identifier termID) {
		Rdf2GoCore core = getCore();

		URI termURI = new URIImpl(Rdf2GoUtils.expandNamespace(core, getShortURI(termID)));
		List<Identifier> result = new ArrayList<Identifier>();

		for (String relation : relations) {

			List<URI> childrenConcepts2 = HierarchyUtils.getChildrenConcepts(termURI, new URIImpl(
					Rdf2GoUtils.expandNamespace(core, relation)),
					master);
			for (URI uri : childrenConcepts2) {
				String reducedNamespace = Rdf2GoUtils.reduceNamespace(core,
						Strings.decodeURL(uri.toString()));
				/*
				 * we check the ignore list and we check whether the term is
				 * child of itself
				 */
				if (!(isIgnored(reducedNamespace)
						|| termID.equals(new Identifier(reducedNamespace.split(":"))))) {
					result.add(new Identifier(reducedNamespace.split(":")));
				}
			}
		}

		return result;
	}

	private boolean isIgnored(String term) {
		for (String ignoredTerm : ignoredTerms) {
			if (term.equals(ignoredTerm)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Identifier> getParents(Identifier termID) {
		Rdf2GoCore core = getCore();

		URI termURI = new URIImpl(getURIString(termID));
		List<Identifier> result = new ArrayList<Identifier>();

		for (String relation : relations) {

			List<URI> childrenConcepts2 = HierarchyUtils.getParentConcepts(termURI, new URIImpl(
					Rdf2GoUtils.expandNamespace(core, relation)),
					master);
			for (URI uri : childrenConcepts2) {
				String reducedNamespace = Rdf2GoUtils.reduceNamespace(core,
						Strings.decodeURL(uri.toString()));
				result.add(new Identifier(reducedNamespace.split(":")));
			}
		}

		return result;
	}

	@Override
	public boolean isSuccessorOf(Identifier termID1, Identifier termID2) {
		Rdf2GoCore core = getCore();

		URI term1URI = new URIImpl(getURIString(termID1));
		URI term2URI = new URIImpl(getURIString(termID2));

		for (String relation : relations) {
			boolean is = HierarchyUtils.isSubConceptOf(term1URI, term2URI, new URIImpl(
					Rdf2GoUtils.expandNamespace(core, relation)), master);
			if (is) {
				return true;
			}
			else {
				if (mixedRelationHierarchyMode) {
					List<Identifier> children = getChildren(termID2);
					for (Identifier child : children) {
						boolean found = isSuccessorOf(termID1, child);
						if (found) return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public Collection<Identifier> getAllTerms() {
		TerminologyManager terminologyManager = HierarchyUtils.getCompiler(master).getTerminologyManager();
		return terminologyManager.getAllDefinedTerms();
	}

	@Override
	public Collection<Identifier> getStartupTerms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Identifier> filterInterestingTerms(Collection<Identifier> terms) {

		// we do not filter if no filter classes are defined
		if (categories == null || categories.size() == 0) return terms;

		Rdf2GoCore core = HierarchyUtils.getCompiler(master).getRdf2GoCore();
		List<Identifier> resultConcepts = new ArrayList<Identifier>();
		List<String> classes = this.categories;
		List<URI> classURIs = new ArrayList<URI>();

		for (String clazz : classes) {
			String expandedNamespace = Rdf2GoUtils.expandNamespace(
					core,
					clazz);
			URI classURI = new URIImpl(expandedNamespace);
			classURIs.add(classURI);
		}

		// we check for each term whether it is instance of at least one of the
		// filter classes
		for (Identifier term : terms) {
			String[] pathElements = term.getPathElements();
			if (pathElements.length == 2) {
				URI termURI = core.createURI(pathElements[0], pathElements[1]);
				for (URI classURI : classURIs) {
					String query = "ASK { " + termURI.toSPARQL() + " rdf:type "
							+ classURI.toSPARQL()
							+ ".}";
					boolean isInstanceOfClass = SparqlCacheManager.getInstance().getCachedSparqlEndpoint(
							core).executeSparqlAskQuery(query);
					if (isInstanceOfClass) {
						resultConcepts.add(term);
						break;
					}
				}
			}
			//else {
			// we ignore other stuff (namespace and package defs..etc)
			//}
		}

		return resultConcepts;
	}

	@Override
	public void updateSettings(UserContext user) {
		relations = TermBrowserMarkup.getCurrentTermbrowserMarkupHierarchyRelations(user);
		categories = TermBrowserMarkup.getCurrentTermbrowserMarkupHierarchyCategories(user);
		ignoredTerms = TermBrowserMarkup.getCurrentTermbrowserIgnoredTerms(user);
		this.master = TermBrowserMarkup.getCurrentTermbrowserMarkupMaster(user);
		this.user = user;
	}

}
