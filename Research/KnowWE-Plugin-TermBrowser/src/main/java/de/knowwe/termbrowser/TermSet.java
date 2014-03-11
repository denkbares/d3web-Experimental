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
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import de.d3web.collections.PartialHierarchyTree;
import de.d3web.collections.PartialHierarchyTree.Node;
import de.d3web.strings.Identifier;
import de.knowwe.core.user.UserContext;

/**
 * 
 * @author jochenreutelshofer
 * @created 05.12.2012
 */
public class TermSet {

	private PartialHierarchyTree<RatedTerm> terms = null;
	private boolean browserIsCollapsed = false;
	private boolean graphIsCollapsed = true;
	private Identifier addedLatest = null;

	public Identifier getTermAddedLatest() {
		return addedLatest;
	}

	public void setTermAddedLatest(Identifier addedLatest) {
		this.addedLatest = addedLatest;
	}

	private final TermBrowserHierarchy hierarchy;

	/**
	 * 
	 */
	public TermSet(UserContext user) {
		hierarchy = new TermBrowserHierarchy(user);
		terms = new PartialHierarchyTree<RatedTerm>(hierarchy);
	}

	public static TermSet createRecommendationSet(UserContext user) {
		return new TermSet(user);
	}

	public void setBrowserIsCollapsed(boolean browserIsCollapsed) {
		this.browserIsCollapsed = browserIsCollapsed;
	}

	public boolean isBrowserIsCollapsed() {
		return browserIsCollapsed;
	}

	public void setGraphIsCollapsed(boolean graphIsCollapsed) {
		this.graphIsCollapsed = graphIsCollapsed;
	}

	public boolean isGraphIsCollapsed() {
		return graphIsCollapsed;
	}

	public List<RatedTerm> getRankedTermList() {
		return toList(terms);
	}

	public static List<RatedTerm> toList(PartialHierarchyTree<RatedTerm> tree) {
		ValueComparator bvc = new ValueComparator();
		TreeSet<RatedTerm> list = new TreeSet<RatedTerm>(bvc);

		list.addAll(tree.getNodeContents());

		return new ArrayList<RatedTerm>(list);
	}

	public void clearValue(Identifier term) {
		terms.removeNodeFromTree(new RatedTerm(term));
	}

	public void addValue(Identifier term, Double increment) {
		RatedTerm newTerm = new RatedTerm(term);
		Node<RatedTerm> node = terms.find(newTerm);
		if (node != null) {
			RatedTerm existingValuedTerm = node.getData();
			existingValuedTerm.incrValue(increment);
		}
		else {
			newTerm.incrValue(increment);
			terms.insertNode(newTerm);
		}
	}

	public PartialHierarchyTree<RatedTerm> getTerms() {
		return terms;
	}

	public void discount(double factor) {
		// build up tree newly
		PartialHierarchyTree<RatedTerm> newTree = new PartialHierarchyTree<RatedTerm>(hierarchy);
		List<RatedTerm> allTerms = getRankedTermList();
		for (RatedTerm ratedTerm : allTerms) {
			double newValue = ratedTerm.getValue();

			// we discount only leafs
			Node<RatedTerm> formerNode = terms.find(ratedTerm);
			if (formerNode != null) {

				if (formerNode.getChildren().size() == 0) {
					newValue *= factor;
				}
			}
			if (newValue > 0.1) {
				newTree.insertNode(new RatedTerm(ratedTerm.getTerm(), newValue));
			}
		}

		terms = newTree;
	}

	public TermBrowserHierarchy getHierarchy() {
		return hierarchy;
	}

	static class ValueComparator implements Comparator<RatedTerm> {

		@Override
		public int compare(RatedTerm a, RatedTerm b) {
			if (a.getValue() >= b.getValue()) {
				return -1;
			}
			else {
				return 1;
			}
		}
	}

	/**
	 * Clears the entire concept list/tree, i.e. removes all entries
	 * 
	 * @created 14.05.2013
	 */
	public void clear() {
		List<RatedTerm> list = toList(terms);
		for (RatedTerm ratedTerm : list) {
			clearValue(ratedTerm.getTerm());
		}

	}

	public void updateUserData(UserContext context) {
		getHierarchy().updateSettings(context);
	}

}
