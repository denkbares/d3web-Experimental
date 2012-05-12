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
package de.d3web.we.diaflux.datamanagement;

import java.util.HashMap;
import java.util.Set;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class EvalResult {

	private final HashMap<String, Domain> results;

	public EvalResult() {
		results = new HashMap<String, Domain>();
	}

	public boolean add(String var, Domain domain) {
		Domain oldDomain = results.get(var);
		if (oldDomain != null) {
			domain.addAll(oldDomain);
		}
		results.put(var, domain);
		return true;
	}

	/**
	 * Negates every Domain for every Variable in the EvalResult and returns an
	 * new EvalResult
	 * 
	 * @created 08.05.2012
	 * @return
	 */
	public EvalResult negate() {
		EvalResult result = new EvalResult();
		for (String key : results.keySet()) {
			Domain domain = results.get(key).negate();
			result.add(key, domain);
		}
		// result.mergeAll();
		return result;
	}

	/**
	 * intersects all Domains for every Variable in EvalResult with all Domains
	 * of the equivalent Variable in eRes and returns an new EvalResult
	 * Variables only appearing in one EvalResult are completely accepted
	 * 
	 * @created 08.05.2012
	 * @param eRes
	 * @return
	 */
	public EvalResult intersect(EvalResult eRes) {
		EvalResult result = new EvalResult();
		Set<String> thisList = results.keySet();
		thisList.removeAll(eRes.results.keySet());
		for (String key : thisList) {
			result.add(key, results.get(key));
		}
		Set<String> otherList = eRes.results.keySet();
		otherList.removeAll(results.keySet());
		for (String key : otherList) {
			result.add(key, eRes.results.get(key));

		}
		for (String key1 : results.keySet()) {
			for (String key2 : eRes.results.keySet()) {
				if (key1.equals(key2)) {
					Domain domain = results.get(key1);
					if (domain.intersects(eRes.results.get(key2))) {
						result.add(key1, domain.intersectWith(eRes.results.get(key2)));
					}
				}
			}

		}
		return result;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param eRes
	 * @return
	 */
	public EvalResult restrictWith(EvalResult eRes) {

		EvalResult result = new EvalResult();
		if (eRes.getVariables().isEmpty()) {
			result = this;
		}
		for (String key1 : results.keySet()) {
			boolean contained = false;

			for (String key2 : eRes.results.keySet()) {
				if (key1.equals(key2)) {

					contained = true;
					Domain domain = results.get(key1);
					if (domain.intersects(eRes.results.get(key2))) {
						result.add(key1, domain.intersectWith(eRes.results.get(key2)));
					}
				}
			}
			if (!contained) {
				result.add(key1, results.get(key1));
			}
		}
		return result;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param eRes
	 * @return
	 */
	public boolean intersects(EvalResult eRes) {
		for (String key1 : results.keySet()) {
			for (String key2 : eRes.results.keySet()) {
				if (key1.equals(key2)) {
					Domain domain = results.get(key1);
					if (domain.intersects(eRes.results.get(key2))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param eRes
	 * @return
	 */
	public boolean contains(EvalResult eRes) {
		boolean contained = false;
		for (String key1 : eRes.results.keySet()) {
			for (String key2 : results.keySet()) {
				if (key1.equals(key2)) {
					Domain domain = results.get(key1);
					if (domain.contains(eRes.results.get(key2))) {
						return contained = true;
					}
				}
			}
			if (!contained) return false;
		}
		return true;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param var
	 * @param domain
	 * @return
	 */
	public boolean contains(String var, Domain domain) {
		if (!results.keySet().contains(var)) return false;
		Domain oldDomain = results.get(var);
		if (!oldDomain.contains(domain)) return false;
		return true;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param var
	 * @param value
	 * @return
	 */
	public boolean contains(String var, IValue value) {
		Domain newDomain = new Domain();
		newDomain.add(value);
		return contains(var, newDomain);
	}

	/**
	 * merges all Domains for every Variable in EvalResult with all Domains of
	 * the equivalent Variable in eRes and returns an new EvalResult
	 * 
	 * @created 08.05.2012
	 * @param eRes
	 * @return
	 */
	public EvalResult merge(EvalResult eRes) {
		EvalResult result = new EvalResult();
		for (String key : eRes.results.keySet()) {
			result.add(key, eRes.results.get(key));
		}
		for (String key : results.keySet()) {
			result.add(key, results.get(key));
		}
		return result;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param eRes
	 * @return
	 */
	public EvalResult substract(EvalResult eRes) {
		EvalResult result = new EvalResult();
		for (String key : results.keySet()) {
			for (String key_eRes : eRes.results.keySet()) {
				if (key.equals(key_eRes)) {
					Domain domain = results.get(key_eRes).substract(eRes.results.get(key_eRes));
					if (!domain.isEmpty()) {
						result.add(key, domain);
					}
				}
			}
		}
		return result;
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return "EvalResult: " + results.toString();
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @return
	 */
	public Set<String> getVariables() {
		return results.keySet();
	}
}
