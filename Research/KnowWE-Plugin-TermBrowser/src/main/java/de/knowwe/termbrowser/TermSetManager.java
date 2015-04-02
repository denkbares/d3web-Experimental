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
 * @author Jochen Reutelshöfer
 * @created 05.12.2012
 */
public class TermSetManager implements EventListener {

	private static final double WEIGHT_EXPAND = 1.5;
	private static final double WEIGHT_SEARCHED = 2.0;

	private static TermSetManager instance;
	private boolean automatedTermDetection = false;

	public static TermSetManager getInstance() {
		if (instance == null) {
			instance = new TermSetManager();
		}
		return instance;
	}

	private Collection<InterestingTermDetector> termDetectors = null;
	private final Map<String, TermSet> data = new HashMap<String, TermSet>();

	/**
	 *
	 */
	private TermSetManager() {
		termDetectors = getPluggedTermDetectors();
		EventManager.getInstance().registerListener(this);
	}

	/**
	 * @return
	 * @created 05.06.2013
	 */
	private Collection<InterestingTermDetector> getPluggedTermDetectors() {
		List<InterestingTermDetector> h = new ArrayList<InterestingTermDetector>();
		Extension[] extensions = PluginManager.getInstance().getExtensions(
				"KnowWE-Plugin-TermBrowser", InterestingTermDetector.EXTENSION_POINT_TERM_DETECTOR);
		for (Extension extension : extensions) {
			Object newInstance = extension.getNewInstance();
			if (newInstance instanceof InterestingTermDetector) {
				h.add((InterestingTermDetector) newInstance);
			}
		}
		return h;
	}

	/**
	 * Determines if the term-browser list was open or collapsed for this user
	 *
	 * @param user
	 * @return
	 * @created 14.05.2013
	 */
	public boolean listIsCollapsed(UserContext user) {
		TermSet recommendationSet = data.get(user.getUserName());
		if (recommendationSet == null) return false;
		return recommendationSet.isBrowserIsCollapsed();
	}

	public BrowserTerm getLatestAddedTerm(UserContext user) {
		TermSet recommendationSet = data.get(user.getUserName());
		if (recommendationSet == null) return null;
		return recommendationSet.getTermAddedLatest();
	}

	/**
	 * Determines if the concept overview graph was open or collapsed for this user
	 *
	 * @param user
	 * @return
	 * @created 14.05.2013
	 */
	public boolean graphIsCollapsed(UserContext user) {
		TermSet recommendationSet = data.get(user.getUserName());
		if (recommendationSet == null) return true;
		return recommendationSet.isGraphIsCollapsed();
	}

	public TermSet getRecommendationSet(UserContext user) {
		return findRecommendationSet(user);
	}

	public de.d3web.collections.PartialHierarchyTree<RatedTerm> getRatedTermTreeTop(UserContext user, int count) {
		String username = user.getUserName();
		TermSet termSet = findRecommendationSet(user);

		List<RatedTerm> rankedTermList = termSet.getRankedTermList();
		PartialHierarchyTree<RatedTerm> treeCopy = new PartialHierarchyTree<RatedTerm>(
				termSet.getHierarchy());
		int size = rankedTermList.size();
		for (RatedTerm ratedTerm : rankedTermList) {
			treeCopy.insertNode(ratedTerm);
		}
		int toRemove = size - count;
		if (count == -1) {
			// -1 stands for infinite size of term browser list
			toRemove = 0;
		}
		for (int i = 0; i < toRemove; i++) {
			removeLowestRatedLeaf(treeCopy);
		}
		return treeCopy;
	}

	public List<RatedTerm> getRatedTermListTop(UserContext user, int count) {
		String username = user.getUserName();
		if (!data.containsKey(username)) {
			return new ArrayList<RatedTerm>(0);
		}
		else {
			TermSet recommendationSet = data.get(username);
			List<RatedTerm> rankedTermList = recommendationSet.getRankedTermList();
			if (count < 0) {
				return rankedTermList;

			}
			else {
				int size = rankedTermList.size();
				if (count >= size) {
					return rankedTermList;
				}
				return TermSet.toList(getRatedTermTreeTop(user, count));
			}
		}
	}

	/**
	 * @param tree
	 * @created 12.04.2013
	 */
	private void removeLowestRatedLeaf(PartialHierarchyTree<RatedTerm> tree) {
		PartialHierarchyTree.Node<RatedTerm> root = tree.getRoot();
		PartialHierarchyTree.Node<RatedTerm> lowestNode = findLowestRatedLeaf(root);
		tree.removeNodeFromTree(lowestNode.getData());

	}

	/**
	 * @param root
	 * @return
	 * @created 12.04.2013
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

	public List<BrowserTerm> getRankedTermList(UserContext user) {
		List<RatedTerm> ratedTermList = getRatedTermList(user);
		List<BrowserTerm> result = new ArrayList<BrowserTerm>();
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

	public void termSearched(UserContext user, BrowserTerm term) {
		TermSet set = null;
		if (data.containsKey(user.getUserName())) {
			set = data.get(user.getUserName());
			set.discount(0.8);
		}
		else {
			set = TermSet.createRecommendationSet(user);
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
			TermSet set = findRecommendationSet(user);
			set.discount(0.8);

			boolean autoCollect = TermBrowserMarkup.getCurrentTermbrowserMarkupAutoCollectFlag(user);

			if (autoCollect) {
                String master = TermBrowserMarkup.getCurrentTermbrowserMarkupMaster(user);
                for (InterestingTermDetector termDetector : termDetectors) {

                    Map<BrowserTerm, Double> interestingTerms = termDetector.getWeightedTermsOfInterest(
                            article,
                            user);

                    // we add the values for the terms filtered by the hierarchy
                    // provider
                    Collection<BrowserTerm> filteredTerms = set.getHierarchy().filterInterestingTerms(
                            interestingTerms.keySet());

                    for (BrowserTerm term : filteredTerms) {
                        set.addValue(term, interestingTerms.get(term));
                    }
                }
            }

		}

	}

	/**
	 * @param context
	 * @param term
	 * @created 11.12.2012
	 */
	public void clearTerm(UserContext context, BrowserTerm term) {
		TermSet recommendationSet = data.get(context.getUserName());
		if (recommendationSet != null) {
			recommendationSet.clearValue(term);
			recommendationSet.setTermAddedLatest(null);
		}

	}

	/**
	 * @param context
	 * @param term
	 * @created 11.12.2012
	 */
	public void expandTerm(UserActionContext context, BrowserTerm term) {
		TermSet recommendationSet = findRecommendationSet(context);

		// discount existing terms to make sure expanded ones are top rated
		recommendationSet.discount(0.9);

		// add children
		List<BrowserTerm> children = recommendationSet.getHierarchy().getChildren(term);
		for (BrowserTerm child : children) {
			recommendationSet.addValue(child, WEIGHT_EXPAND);
		}
		// also add some score to the expanded concept itself
		recommendationSet.addValue(term, WEIGHT_EXPAND);
		recommendationSet.setTermAddedLatest(term);

	}

	private TermSet findRecommendationSet(UserContext context) {
		TermSet recommendationSet = data.get(context.getUserName());
		if (recommendationSet == null) {
			recommendationSet = TermSet.createRecommendationSet(context);
			Collection<BrowserTerm> startupTerms = recommendationSet.getHierarchy().getStartupTerms(context);
			if (startupTerms != null) {
				for (BrowserTerm identifier : startupTerms) {
					recommendationSet.addValue(identifier, 1.0);
				}
			}
			data.put(context.getUserName(), recommendationSet);
		}
		recommendationSet.updateUserData(context);
		return recommendationSet;
	}

	/**
	 * @param context
	 * @param term
	 * @created 03.05.2013
	 */
	public void collapseTerm(UserActionContext context, BrowserTerm term) {
		TermSet recommendationSet = findRecommendationSet(context);

		List<BrowserTerm> children = recommendationSet.getHierarchy().getChildren(term);
		for (BrowserTerm child : children) {
			recommendationSet.clearValue(child);
		}
		recommendationSet.setTermAddedLatest(null);
	}

	/**
	 * @param context
	 * @created 14.05.2013
	 */
	public void openList(UserContext context) {
		TermSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setBrowserIsCollapsed(false);
	}

	/**
	 * @param context
	 * @created 14.05.2013
	 */
	public void collapseList(UserActionContext context) {
		TermSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setBrowserIsCollapsed(true);
	}

	/**
	 * @param context
	 * @created 14.05.2013
	 */
	public void openGraph(UserActionContext context) {
		TermSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setGraphIsCollapsed(false);
	}

	/**
	 * @param context
	 * @created 14.05.2013
	 */
	public void collapseGraph(UserActionContext context) {
		TermSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setGraphIsCollapsed(true);
	}

	/**
	 * @param context
	 * @created 14.05.2013
	 */
	public void clearList(UserContext context) {
		TermSet recommendationSet = data.get(context.getUserName());
		if (recommendationSet != null) {
			recommendationSet.clear();
			recommendationSet.setTermAddedLatest(null);
			TermBrowserHierarchy hierarchy = recommendationSet.getHierarchy();
			Collection<BrowserTerm> startupTerms = hierarchy.getStartupTerms(context);
			if (startupTerms != null) {
				for (BrowserTerm identifier : startupTerms) {
					recommendationSet.addValue(identifier, 1.0);
				}
			}
		}
	}

	/**
	 * @param context
	 * @created 14.05.2013
	 */
	public void toggleCollapse(UserContext context) {
		TermSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setBrowserIsCollapsed(!recommendationSet.isBrowserIsCollapsed());

	}

	/**
	 * @param context
	 * @created 28.05.2013
	 */
	public void toggleGraph(UserActionContext context) {
		TermSet recommendationSet = findRecommendationSet(context);
		recommendationSet.setGraphIsCollapsed(!recommendationSet.isGraphIsCollapsed());

	}

	/**
	 * @param context
	 * @param term
	 * @created 31.05.2013
	 */
	public void addParentTerm(UserActionContext context, BrowserTerm term) {
		TermSet recommendationSet = findRecommendationSet(context);
		List<BrowserTerm> parents = recommendationSet.getHierarchy().getParents(term);
		// there should be only one parent
		if (parents.size() > 0) {
			// in any case we only take the first one
            BrowserTerm parent = parents.get(0);
			recommendationSet.addValue(parent, WEIGHT_EXPAND);
			recommendationSet.setTermAddedLatest(parent);
		}

	}
}
