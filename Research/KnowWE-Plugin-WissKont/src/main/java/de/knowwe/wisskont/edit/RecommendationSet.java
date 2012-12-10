/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.wisskont.edit;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 
 * @author jochenreutelshofer
 * @created 05.12.2012
 */
public class RecommendationSet {

	private final Map<String, Double> terms = new HashMap<String, Double>();

	public Collection<String> getRankedTermList() {
		ValueComparator bvc = new ValueComparator(terms);
		TreeMap<String, Double> sortedMap = new TreeMap<String, Double>(bvc);
		sortedMap.putAll(terms);
		return sortedMap.keySet();
	}

	public void clearValue(String term) {
		terms.remove(term);
	}

	public void addValue(String term, Double increment) {
		if (terms.containsKey(term)) {
			terms.put(term, terms.get(term) + increment);
		}
		else {
			terms.put(term, increment);
		}
	}

	public void discount(double factor) {
		Set<String> keySet = terms.keySet();
		for (String term : keySet) {
			double newValue = terms.get(term) * factor;
			if (newValue < 0.1) {
				terms.remove(term);
			}
			else {
				terms.put(term, newValue);
			}
		}
	}

	class ValueComparator implements Comparator<String> {

		Map<String, Double> base;

		public ValueComparator(Map<String, Double> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		@Override
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			}
			else {
				return 1;
			} // returning 0 would merge keys
		}
	}

}
