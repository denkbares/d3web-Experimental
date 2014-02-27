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
package de.knowwe.termbrowser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.d3web.collections.PartialHierarchyTree;
import de.d3web.collections.PartialHierarchyTree.Node;
import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.d3web.strings.Identifier;
import de.knowwe.core.Environment;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.user.UserContext;
import de.knowwe.event.PageRenderedEvent;

/**
 * 
 * @author jochenreutelshofer
 * @created 05.12.2012
 */
public class TermRecommender implements EventListener {

	private static final double WEIGHT_EXPAND = 1.5;
	private static final double WEIGHT_SEARCHED = 2.0;

	private static TermRecommender instance;

	public static TermRecommender getInstance() {
		if (instance == null) {
			instance = new TermRecommender();
		}
		return instance;
	}

	private InterestingTermDetector termDetector = null;
	// private HierarchyProvider hierarchy = null;
	private final Map<String, RecommendationSet> data = new HashMap<String, RecommendationSet>();

	/**
	 * 
	 */
	private TermRecommender() {
		termDetector = getPluggedTermDetector();
		EventManager.getInstance().registerListener(this);

	}

	/**
	 * 
	 * @created 05.06.2013
	 * @return
	 */
	private InterestingTermDetector getPluggedTermDetector() {
		InterestingTermDetector h = null;
		Extension[] extensions = PluginManager.getInstance().getExtensions(
				"KnowWE-Plugin-TermBrowser", InterestingTermDetector.EXTENSION_POINT_TERM_DETECTOR);
		for (Extension extension : extensions) {
			Object newInstance = extension.getNewInstance();
			if (newInstance instanceof InterestingTermDetector) {
				h = (InterestingTermDetector) newInstance;
			}
		}
		return h;
	}

	/**
	 * Determines if the term-browser list was open or collapsed for this user
	 * 
	 * @created 14.05.2013
	 * @param user
	 * @return
	 */
	public boolean listIsCollapsed(UserContext user) {
		RecommendationSet recommendationSet = data.get(user.getUserName());
		if (recommendationSet == null) return false;
		return recommendationSet.isBrowserIsCollapsed();
	}

	public Identifier getLatestAddedTerm(UserContext user) {
		RecommendationSet recommendationSet = data.get(user.getUserName());
		if (recommendationSet == null) return null;
		return recommendationSet.getTermAddedLatest();
	}

	/**
	 * Determines if the concept overview graph was open or collapsed for this
	 * user
	 * 
	 * @created 14.05.2013
	 * @param user
	 * @return
	 */
	public boolean graphIsCollapsed(UserContext user) {
		RecommendationSet recommendationSet = data.get(user.getUserName());
		if (recommendationSet == null) return true;
		return recommendationSet.isGraphIsCollapsed();
	}

	public RecommendationSet getRecommendationSet(UserContext user) {
		return findRecommendationSet(user);
	}

	public de.d3web.collections.PartialHierarchyTree<RatedTerm> getRatedTermTreeTop(UserContext user, int count) {
		String username = user.getUserName();
		if (!data.containsKey(username)) {
			// init new term set
			String masta = TermBrowserMarkup.getCurrentTermbrowserMarkupMaster(user);
			List<String> relations = TermBrowserMarkup.getCurrentTermbrowserMarkupHierarchyRelations(user);
			List<String> categories = TermBrowserMarkup.getCurrentTermbrowserMarkupHierarchyCategories(user);
			List<String> ignores = TermBrowserMarkup.getCurrentTermbrowserIgnoredTerms(user);
			TermBrowserHierarchy hierarchy = new TermBrowserHierarchy(masta, relations, categories,
					ignores);
			PartialHierarchyTree<RatedTerm> tree = new PartialHierarchyTree<RatedTerm>(hierarchy);
			Collection<Identifier> startupTerms = hierarchy.getStartupTerms();
			if(startupTerms != null) {
				for (Identifier identifier : startupTerms) {
					tree.insertNode(new RatedTerm(identifier));
				}
			}
			return tree;
		}
		else {
			// existing term set
			RecommendationSet recommendationSet = data.get(username);
			List<RatedTerm> rankedTermList = recommendationSet.getRankedTermList();
			PartialHierarchyTree<RatedTerm> treeCopy = new PartialHierarchyTree<RatedTerm>(
					recommendationSet.getHierarchy());
			int size = rankedTermList.size();
			for (RatedTerm ratedTerm : rankedTermList) {
					treeCopy.insertNode(ratedTerm);
			}
			int toRemove = size - count;
			if (count == -1) {
				// -1 stand for infinite size of term browser list
				toRemove = 0;
			}
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
	private void removeLowestRatedLeaf(PartialHierarchyTree<RatedTerm> tree) {
		PartialHierarchyTree.Node<RatedTerm> root = tree.getRoot();
		PartialHierarchyTree.Node<RatedTerm> lowestNode = findLowestRatedLeaf(root);
		tree.removeNodeFromTree(lowestNode.getData());

	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param root
	 * @return
	 */
	private PartialHierarchyTree.Node<RatedTerm> findLowestRatedLeaf(Node<RatedTerm> root) {
		List<Node<RatedTerm>> children = root.getChildren();
		if (root.getChildren() == null || root.getChildren().size() == 0) {
			return root;
		}

		Node<RatedTerm> lowest = null;

		for (Node<RatedTerm> child : children) {
			Node<RatedTerm> lowestSuccessor = findLowestRatedLeaf(child);
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

	public List<Identifier> getRankedTermList(UserContext user) {
		List<RatedTerm> ratedTermList = getRatedTermList(user);
		List<Identifier> result = new ArrayList<Identifier>();
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

	public void termSearched(UserContext user, Identifier term) {
		RecommendationSet set = null;
		if (data.containsKey(user.getUserName())) {
			set = data.get(user.getUserName());
			set.discount(0.8);
		}
		else {
			set = RecommendationSet.createRecommendationSet(user);
			data.put(user.getUserName(), set);
		}
		set.addValue(term, WEIGHT_SEARCHED);
		set.setTermAddedLatest(term);
	}

	@Override
	public void notify(Event event) {
		if (event instanceof PageRenderedEvent) {
			String title = ((PageRenderedEvent) event).getTitle();
			UserContext user = ((PageRenderedEvent) event).getUser();
			Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB, title);
			String master = TermBrowserMarkup.getCurrentTermbrowserMarkupMaster(user);
			RecommendationSet set = null;
			if (data.containsKey(user.getUserName())) {
				set = data.get(user.getUserName());
				set.discount(0.8);
			}
			else {
				List<String> relations = TermBrowserMarkup.getCurrentTermbrowserMarkupHierarchyRelations(user);
				List<String> categories = TermBrowserMarkup.getCurrentTermbrowserMarkupHierarchyCategories(user);
				List<String> ignores = TermBrowserMarkup.getCurrentTermbrowserIgnoredTerms(user);
				set = new RecommendationSet(master, relations, categories, ignores);
				data.put(user.getUserName(), set);
			}


			Map<Identifier, Double> interestingTerms = termDetector.getWeightedTermsOfInterest(
					article,
					master);

			// we add the values for the terms filtered by the hierarchy
			// provider
			Collection<Identifier> filteredTerms = set.getHierarchy().filterInterestingTerms(
					interestingTerms.keySet());

			for (Identifier term : filteredTerms) {
				set.addValue(term, interestingTerms.get(term));
			}

		}

	}

	/**
	 * 
	 * @created 11.12.2012
	 * @param context
	 * @param term
	 */
	public void clearTerm(UserContext context, Identifier term) {
		RecommendationSet recommendationSet = data.get(context.getUserName());
		if (recommendationSet != null) {
			recommendationSet.clearValue(term);
			recommendationSet.setTermAddedLatest(null);
		}

	}

	/**
	 * 
	 * @created 11.12.2012
	 * @param context
	 * @param term
	 */
	public void expandTerm(UserActionContext context, Identifier term) {
		RecommendationSet recommendationSet = findRecommendationSet(context);

		// discount existing terms to make sure expanded ones are top rated
		recommendationSet.discount(0.9);

		// add children
		List<Identifier> children = recommendationSet.getHierarchy().getChildren(term);
		for (Identifier child : children) {
			recommendationSet.addValue(child, WEIGHT_EXPAND);
		}
		// also add some score to the expanded concept itself
		recommendationSet.addValue(term, WEIGHT_EXPAND);
		recommendationSet.setTermAddedLatest(term);

	}

	private RecommendationSet findRecommendationSet(UserContext context) {
		RecommendationSet recommendationSet = data.get(context.getUserName());
		if (recommendationSet == null) {
			recommendationSet = RecommendationSet.createRecommendationSet(context);
			data.put(context.getUserName(), recommendationSet);
		}
		recommendationSet.updateUserData(context);
		return recommendationSet;
	}

	/**
	 * 
	 * @created 03.05.2013
	 * @param context
	 * @param term
	 */
	public void collapseTerm(UserActionContext context, Identifier term) {
		RecommendationSet recommendationSet = findRecommendationSet(context);

		List<Identifier> children = recommendationSet.getHierarchy().getChildren(term);
		for (Identifier child : children) {
			recommendationSet.clearValue(child);
		}
		recommendationSet.setTermAddedLatest(null);
	}

	/**
	 * 
	 * @created 14.05.2013
	 * @param context
	 */
	public void openList(UserContext context) {
		RecommendationSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setBrowserIsCollapsed(false);
	}

	/**
	 * 
	 * @created 14.05.2013
	 * @param context
	 */
	public void collapseList(UserActionContext context) {
		RecommendationSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setBrowserIsCollapsed(true);
	}

	/**
	 * 
	 * @created 14.05.2013
	 * @param context
	 */
	public void openGraph(UserActionContext context) {
		RecommendationSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setGraphIsCollapsed(false);
	}

	/**
	 * 
	 * @created 14.05.2013
	 * @param context
	 */
	public void collapseGraph(UserActionContext context) {
		RecommendationSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setGraphIsCollapsed(true);
	}

	/**
	 * 
	 * @created 14.05.2013
	 * @param context
	 */
	public void clearList(UserContext context) {
		RecommendationSet recommendationSet = data.get(context.getUserName());
		if (recommendationSet != null) {
			recommendationSet.clear();
			recommendationSet.setTermAddedLatest(null);
		}

	}

	/**
	 * 
	 * @created 14.05.2013
	 * @param context
	 */
	public void toggleCollapse(UserContext context) {
		RecommendationSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setBrowserIsCollapsed(!recommendationSet.isBrowserIsCollapsed());

	}

	/**
	 * 
	 * @created 28.05.2013
	 * @param context
	 */
	public void toggleGraph(UserActionContext context) {
		RecommendationSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setGraphIsCollapsed(!recommendationSet.isGraphIsCollapsed());

	}

	/**
	 * 
	 * @created 31.05.2013
	 * @param context
	 * @param term
	 */
	public void addParentTerm(UserActionContext context, Identifier term) {
		RecommendationSet recommendationSet = findRecommendationSet(context);
		List<Identifier> parents = recommendationSet.getHierarchy().getParents(term);
		// there should be only one parent
		if (parents.size() > 0) {
			// in any case we only take the first one
			Identifier parent = parents.get(0);
			recommendationSet.addValue(parent, WEIGHT_EXPAND);
			recommendationSet.setTermAddedLatest(parent);
		}

	}
}
