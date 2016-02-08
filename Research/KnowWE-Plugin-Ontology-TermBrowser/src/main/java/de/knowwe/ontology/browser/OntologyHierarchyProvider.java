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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.collections.PartialHierarchyTree;
import de.knowwe.termbrowser.BrowserTerm;
import de.knowwe.termbrowser.util.TermBrowserUtils;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.core.compile.CompilerFinishedEvent;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.user.UserContext;
import de.knowwe.ontology.browser.cache.SparqlCacheManager;
import de.knowwe.ontology.browser.util.HierarchyUtils;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.termbrowser.HierarchyProvider;
import de.knowwe.termbrowser.TermBrowserMarkup;

/**
 * @author Jochen Reutelshöfer
 * @created 01.10.2013
 */
public class OntologyHierarchyProvider implements HierarchyProvider<BrowserTerm>, EventListener {

	protected List<String> categories = new ArrayList<String>();
	protected List<String> startConceptList = new ArrayList<String>();
	protected List<String> ignoredTerms = new ArrayList<String>();

	protected List<String> relations = new ArrayList<String>();
	protected String master = null;
	protected UserContext user = null;

	protected Map<BrowserTerm, Set<BrowserTerm>> successorshipCache = new HashMap<BrowserTerm, Set<BrowserTerm>>();

	protected boolean mixedRelationHierarchyMode = true;

	public OntologyHierarchyProvider() {
		EventManager.getInstance().registerListener(this);
	}

	private String getShortURI(Identifier termID) {
		String[] termElements = termID.getPathElements();

		if (termElements.length == 1) {
			termElements = new String[] {
					"lns:", termElements[0] };
		}

		return termElements[0] + ":" + Strings.encodeURL(termElements[1]);
	}

	private String getURIString(BrowserTerm termID) {
		Rdf2GoCore core = getCore(termID.getUser());
		return Rdf2GoUtils.expandNamespace(core, getShortURI(termID.getIdentifier()));
	}


	@Override
	public List<BrowserTerm> getChildren(BrowserTerm termID) {
		final Rdf2GoCore core = getCore(termID.getUser());
		URI termURI = new URIImpl(Rdf2GoUtils.expandNamespace(core, getShortURI(termID.getIdentifier())));
		List<BrowserTerm> result = new ArrayList<BrowserTerm>();

		for (String relation : relations) {

			List<URI> childrenConcepts = HierarchyUtils.getChildrenConcepts(termURI, new URIImpl(
					Rdf2GoUtils.expandNamespace(core, relation)),
					core);
			for (URI uri : childrenConcepts) {
                URI clazz = Rdf2GoUtils.findMostSpecificClass(core, uri);
                String type = null;
                if(clazz != null) {
                    type = TermBrowserUtils.abbreviateTypeNameForURI(clazz.toString());
                }
				String reducedNamespace = Rdf2GoUtils.reduceNamespace(core,
						Strings.decodeURL(uri.toString()));

				/*
				 * we check the ignore list and we check whether the term is
				 * child of itself
				 */
				if (!(isIgnored(reducedNamespace)
						|| termID.equals(new Identifier(reducedNamespace.split(":"))))) {
					result.add(new BrowserTerm(type,  Rdf2GoUtils.getLabel(uri, core, null), termID.getUser(), reducedNamespace.split(":")));
				}
			}
		}

		/*
		fill data into cache (to make isSuccessorOf-calls more efficient)
		 */
		Set<BrowserTerm> successors = successorshipCache.get(termID);
		if (successors == null) {
			successors = new HashSet<BrowserTerm>();
			successorshipCache.put(termID, successors);
		}
		successors.addAll(result);

		return result;
	}

	private Rdf2GoCore getCore(UserContext user) {
		return Compilers.getCompiler(TermBrowserMarkup.getTermBrowserMarkup(user), Rdf2GoCompiler.class).getRdf2GoCore();
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
	public List<BrowserTerm> getParents(BrowserTerm termID) {
		Rdf2GoCore core = getCore(termID.getUser());

		URI termURI = new URIImpl(getURIString(termID));
		List<BrowserTerm> result = new ArrayList<BrowserTerm>();

		for (String relation : relations) {

			List<URI> parentConcepts = HierarchyUtils.getParentConcepts(termURI, new URIImpl(
					Rdf2GoUtils.expandNamespace(core, relation)),
					core);
			for (URI uri : parentConcepts) {
                String reducedNamespace = Rdf2GoUtils.reduceNamespace(core,
                        Strings.decodeURL(uri.toString()));
                final PartialHierarchyTree<URI> classHierarchy = Rdf2GoUtils.getClassHierarchy(core, uri);
                URI clazz = Rdf2GoUtils.findMostSpecificClass(classHierarchy);

                String type = null;
                if(clazz != null) {
                    type = TermBrowserUtils.abbreviateTypeNameForURI(clazz.toString());
                }
                result.add(new BrowserTerm(type,Rdf2GoUtils.getLabel(uri, core, null), termID.getUser(),reducedNamespace.split(":")));
			}
		}

		/*
		fill data into cache (to make isSuccessorOf-calls more efficient)
		 */
		for (BrowserTerm parent : result) {
			addSuccessorToCache(parent, termID);
		}

		return result;
	}



    private void addSuccessorToCache(BrowserTerm parent, BrowserTerm successor) {
		Set<BrowserTerm> successors = successorshipCache.get(parent);
		if (successors == null) {
			successors = new HashSet<>();
			successorshipCache.put(parent, successors);
		}
		successors.add(successor);
	}

	@Override
	public boolean isSuccessorOf(BrowserTerm termID1, BrowserTerm termID2) {
		if (successorshipCache.containsKey(termID2) && successorshipCache.get(termID2).contains(termID1)) {
			return true;
		}

		Rdf2GoCore core = getCore(termID1.getUser());

		URI term1URI = new URIImpl(getURIString(termID1));
		URI term2URI = new URIImpl(getURIString(termID2));

		for (String relation : relations) {
			boolean is = HierarchyUtils.isSubConceptOf(term1URI, term2URI, new URIImpl(
					Rdf2GoUtils.expandNamespace(core, relation)), core);
			if (is) {
				addSuccessorToCache(termID2, termID1);
				return true;
			}
			else {
				if (mixedRelationHierarchyMode) {
					List<BrowserTerm> children = getChildren(termID2);
					for (BrowserTerm child : children) {
						boolean found = isSuccessorOf(termID1, child);
						if (found) {
							addSuccessorToCache(termID2, termID1);
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public Collection<BrowserTerm> getAllTerms(UserContext user) {
		// TODO: find solution for accessing rdf2goCore from here
		TerminologyManager terminologyManager = HierarchyUtils.getCompiler(master).getTerminologyManager();
        final Collection<BrowserTerm> result = new HashSet<BrowserTerm>();
        final Collection<Identifier> allDefinedTerms = terminologyManager.getAllDefinedTerms();
        for (Identifier definedTerm : allDefinedTerms) {
            // TODO: equip BrowserTerms with types and labels here ??
           result.add(new BrowserTerm(definedTerm, user));
        }
        return result;
    }

	@Override
	public Collection<BrowserTerm> getStartupTerms(UserContext user) {

		// TODO: find solution for accessing rdf2goCore from here
		List<BrowserTerm> startConcepts = new ArrayList<BrowserTerm>();
		for (String startConceptName : startConceptList) {
			startConcepts.add(new BrowserTerm(new Identifier(startConceptName.split(":")),user));
		}
		return startConcepts;
	}

	@Override
	public Collection<BrowserTerm> filterInterestingTerms(Collection<BrowserTerm> terms) {

		// we do not filter if no filter classes are defined
		if (categories == null || categories.size() == 0) return terms;

		Rdf2GoCore core = HierarchyUtils.getCompiler(master).getRdf2GoCore();
		List<BrowserTerm> resultConcepts = new ArrayList<BrowserTerm>();
		List<String> classes = this.categories;
		List<URI> classURIs = new ArrayList<>();

		for (String clazz : classes) {
			String expandedNamespace = Rdf2GoUtils.expandNamespace(
					core,
					clazz);
			URI classURI = new URIImpl(expandedNamespace);
			classURIs.add(classURI);
		}

		// we check for each term whether it is instance of at least one of the
		// filter classes
		for (BrowserTerm term : terms) {
			String[] pathElements = term.getIdentifier().getPathElements();
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
		startConceptList = TermBrowserMarkup.getCurrentTermbrowserMarkupStartConcept(user);
		this.master = TermBrowserMarkup.getCurrentTermbrowserMarkupMaster(user);
		this.user = user;
	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		List<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>();
		events.add(CompilerFinishedEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {
		this.successorshipCache.clear();
	}
}
