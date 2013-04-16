/*
 * Copyright (C) 2012 denkbares GmbH
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.strings.Identifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.Environment;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.event.PageRenderedEvent;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.wisskont.ConceptMarkup;
import de.knowwe.wisskont.util.Tree;

/**
 * 
 * @author jochenreutelshofer
 * @created 05.12.2012
 */
public class TermRecommender implements EventListener {

	private static final double WEIGHT_REFERENCE = 0.5;
	private static final double WEIGHT_DEFINITION = 1.0;
	private static final double WEIGHT_EXPAND = 1.5;
	private static final double WEIGHT_SEARCHED = 2.0;

	private static TermRecommender instance;

	public static TermRecommender getInstance() {
		if (instance == null) {
			instance = new TermRecommender();
		}
		return instance;
	}

	private final Map<String, RecommendationSet> data = new HashMap<String, RecommendationSet>();

	/**
	 * 
	 */
	private TermRecommender() {
		EventManager.getInstance().registerListener(this);
	}

	public Tree<RatedTerm> getRatedTermTreeTop(UserContext user, int count) {
		Tree<RatedTerm> treeCopy = new Tree<RatedTerm>(RatedTerm.ROOT);
		String username = user.getUserName();
		if (!data.containsKey(username)) {
			return treeCopy;
		}
		else {
			RecommendationSet recommendationSet = data.get(username);
			List<RatedTerm> rankedTermList = recommendationSet.getRankedTermList();
			int size = rankedTermList.size();
			for (RatedTerm ratedTerm : rankedTermList) {
				treeCopy.insertNode(ratedTerm);
			}
			int toRemove = size - count;
			for (int i = 0; i < toRemove; i++) {
				removeLowestRatedLeaf(treeCopy);
			}
			return treeCopy;
		}
	}

	public List<RatedTerm> getRatedTermListTop(UserContext user, int count) {
		String username = user.getUserName();
		if (!data.containsKey(username)) {
			return new ArrayList<RatedTerm>(0);
		}
		else {
			RecommendationSet recommendationSet = data.get(username);
			List<RatedTerm> rankedTermList = recommendationSet.getRankedTermList();
			if (count < 0) {
				return rankedTermList;

			}
			else {
				int size = rankedTermList.size();
				if (count >= size) {
					return rankedTermList;
				}
				return RecommendationSet.toList(getRatedTermTreeTop(user, count));
			}
		}
	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param treeCopy
	 */
	private void removeLowestRatedLeaf(Tree<RatedTerm> tree) {
		de.knowwe.wisskont.util.Tree.Node<RatedTerm> root = tree.getRoot();
		de.knowwe.wisskont.util.Tree.Node<RatedTerm> lowestNode = findLowestRatedLeaf(root);
		tree.removeNodeFromTree(lowestNode.getData());

	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param root
	 * @return
	 */
	private de.knowwe.wisskont.util.Tree.Node<RatedTerm> findLowestRatedLeaf(de.knowwe.wisskont.util.Tree.Node<RatedTerm> root) {
		List<de.knowwe.wisskont.util.Tree.Node<RatedTerm>> children = root.getChildren();
		if (root.getChildren() == null || root.getChildren().size() == 0) {
			return root;
		}

		de.knowwe.wisskont.util.Tree.Node<RatedTerm> lowest = null;

		for (de.knowwe.wisskont.util.Tree.Node<RatedTerm> child : children) {
			de.knowwe.wisskont.util.Tree.Node<RatedTerm> lowestSuccessor = findLowestRatedLeaf(child);
			if (lowest == null) {
				lowest = lowestSuccessor;
			}
			else if (lowestSuccessor.getData().getValue() < lowest.getData().getValue()) {
				lowest = lowestSuccessor;
			}
		}
		return lowest;
	}

	public List<RatedTerm> getRatedTermList(UserContext user) {
		return getRatedTermListTop(user, -1);
	}

	public List<String> getRankedTermList(UserContext user) {
		List<RatedTerm> ratedTermList = getRatedTermList(user);
		List<String> result = new ArrayList<String>();
		for (RatedTerm ratedTerm : ratedTermList) {
			result.add(ratedTerm.getTerm());
		}
		return result;
	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		Collection<Class<? extends Event>> result = new HashSet<Class<? extends Event>>();
		result.add(PageRenderedEvent.class);
		return result;
	}

	public void termSearched(UserContext user, String term) {
		RecommendationSet set = null;
		if (data.containsKey(user.getUserName())) {
			set = data.get(user.getUserName());
			set.discount(0.8);
		}
		else {
			set = new RecommendationSet();
			data.put(user.getUserName(), set);
		}
		set.addValue(term, WEIGHT_SEARCHED);
	}

	@Override
	public void notify(Event event) {
		if (event instanceof PageRenderedEvent) {
			String title = ((PageRenderedEvent) event).getTitle();
			UserContext user = ((PageRenderedEvent) event).getUser();
			Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB, title);

			RecommendationSet set = null;
			if (data.containsKey(user.getUserName())) {
				set = data.get(user.getUserName());
				set.discount(0.8);
			}
			else {
				set = new RecommendationSet();
				data.put(user.getUserName(), set);
			}

			List<Section<SimpleDefinition>> definitions = Sections.findSuccessorsOfType(
					article.getRootSection(), SimpleDefinition.class);
			for (Section<SimpleDefinition> def : definitions) {
				String termname = def.get().getTermName(def);
				// only those defined by by concept markups are added to the
				// term recommender
				if (Sections.findAncestorOfType(def, ConceptMarkup.class) != null) {
					set.addValue(termname, WEIGHT_DEFINITION);
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
						set.addValue(termname, WEIGHT_REFERENCE);
					}
				}
			}

		}

	}

	/**
	 * 
	 * @created 11.12.2012
	 * @param context
	 * @param term
	 */
	public void clearTerm(UserActionContext context, String term) {
		RecommendationSet recommendationSet = data.get(context.getUserName());
		if (recommendationSet != null) {
			recommendationSet.clearValue(term);
		}

	}

	/**
	 * 
	 * @created 11.12.2012
	 * @param context
	 * @param term
	 */
	public void expandTerm(UserActionContext context, String term) {
		RecommendationSet recommendationSet = data.get(context.getUserName());
		if (recommendationSet == null) {
			recommendationSet = new RecommendationSet();
			data.put(context.getUserName(), recommendationSet);
		}
		Collection<Section<? extends SimpleDefinition>> defs = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				new Identifier(term));

		if (defs.size() > 0) {
			Section<? extends SimpleDefinition> def = defs.iterator().next();
			URI uri = RDFSUtil.getURI(def);

			String sparql = "SELECT ?x WHERE { ?x lns:unterkonzept <" + uri + ">.}";
			QueryResultTable resultTable = Rdf2GoCore.getInstance().sparqlSelect(sparql);

			ClosableIterator<QueryRow> resultIterator = resultTable.iterator();
			if (!resultIterator.hasNext()) {
				return;
			}
			while (resultIterator.hasNext()) {
				QueryRow parentConceptResult = resultIterator.next();
				Node value = parentConceptResult.getValue("x");
				String urlString = value.asURI().toString();

				String termName = "";
				try {
					termName = URLDecoder.decode(
							urlString.substring(Rdf2GoCore.getInstance().getLocalNamespace().length()),
							"UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				recommendationSet.addValue(termName, WEIGHT_EXPAND);
			}
		}
	}
}
