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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import de.knowwe.wisskont.util.Tree;

/**
 * 
 * @author jochenreutelshofer
 * @created 05.12.2012
 */
public class RecommendationSet {

	private Tree<RatedTerm> terms = new Tree<RatedTerm>(RatedTerm.ROOT);
	private boolean browserIsCollapsed = true;
	private boolean graphIsCollapsed = true;

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

	public static List<RatedTerm> toList(Tree<RatedTerm> tree) {
		ValueComparator bvc = new ValueComparator();
		TreeSet<RatedTerm> list = new TreeSet<RatedTerm>(bvc);

		list.addAll(tree.getNodes());
		list.remove(RatedTerm.ROOT);

		return new ArrayList<RatedTerm>(list);
	}

	public void clearValue(String term) {
		terms.removeNodeFromTree(new RatedTerm(term));
	}

	public void addValue(String term, Double increment) {
		RatedTerm newTerm = new RatedTerm(term);
		RatedTerm existingValuedTerm = terms.find(newTerm);
		if (existingValuedTerm != null) {
			existingValuedTerm.incrValue(increment);
		}
		else {
			newTerm.incrValue(increment);
			terms.insertNode(newTerm);
		}
	}

	public Tree<RatedTerm> getTerms() {
		return terms;
	}

	public void discount(double factor) {
		// build up tree newly
		Tree<RatedTerm> newTree = new Tree<RatedTerm>(RatedTerm.ROOT);
		List<RatedTerm> allTerms = getRankedTermList();
		for (RatedTerm ratedTerm : allTerms) {
			double newValue = ratedTerm.getValue() * factor;
			if (newValue > 0.1) {
				newTree.insertNode(new RatedTerm(ratedTerm.getTerm(), newValue));
			}
		}

		terms = newTree;
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

}
