/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.diaflux.datamanagement;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.NamedObject;

/**
 * This class represents the results of an evaluation of a condition or action.
 * 
 * @author Reinhard Hatko
 * @created 28.06.2012
 */
public class EvalResult {

	private final Map<String, Domain> domains;

	public EvalResult() {
		domains = new HashMap<String, Domain>();
	}

	public EvalResult(NamedObject question, Domain domain) {
		this();
		domains.put(question.getName(), domain);
	}

	/**
	 * For use with CondOr
	 * 
	 * @created 28.06.2012
	 * @param other
	 */
	public EvalResult merge(EvalResult other) {
		EvalResult result = new EvalResult();

		for (String object : other.getObjects()) {
			Domain otherDomain = other.getDomain(object);
			if (!this.containsObject(object)) {
				result.put(object, otherDomain);
			} else {
				Domain thisDomain = getDomain(object);
				result.put(object, Domains.add(thisDomain, otherDomain));
			}
		}
		
		List<String> list = new LinkedList<String>(this.getObjects());
		list.removeAll(other.getObjects());

		for (String object : list) {
			result.put(object, getDomain(object));
		}

		return result;

	}

	/**
	 * For use with CondAnd
	 * 
	 * @created 28.06.2012
	 * @param other
	 * @return
	 */
	public EvalResult intersectWith(EvalResult other) {
		EvalResult result = new EvalResult();
		for (String object : other.getObjects()) {
			Domain otherDomain = other.getDomain(object);
			if (!this.domains.containsKey(object)) {
				result.put(object, otherDomain);
			}
			else {
				Domain thisDomain = this.getDomain(object);
				result.put(object, Domains.intersect(thisDomain, otherDomain));

			}
		}

		List<String> list = new LinkedList<String>(this.getObjects());
		list.removeAll(other.getObjects());

		for (String object : list) {
			result.put(object, getDomain(object));
		}

		return result;

	}

	void put(TerminologyObject key, Domain value) {
		this.domains.put(key.getName(), value);
	}

	void put(String key, Domain value) {
		this.domains.put(key, value);
	}

	public void negate() {

		for (String q : new LinkedList<String>(getObjects())) {
			put(q, getDomain(q).negate());
		}

	}

	public boolean isEmpty() {
		return domains.isEmpty();
	}

	public boolean containsObject(String key) {
		return domains.containsKey(key);
	}

	public Domain getDomain(TerminologyObject key) {
		return domains.get(key.getName());
	}

	public Domain getDomain(String key) {
		return domains.get(key);
	}

	public Collection<String> getObjects() {
		return Collections.unmodifiableCollection(domains.keySet());
	}

	public Collection<Domain> getDomains() {
		return Collections.unmodifiableCollection(domains.values());
	}

	// public Set<Entry<TerminologyObject, Domain>> getEntries() {
	// return domains.entrySet();
	// }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domains == null) ? 0 : domains.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		EvalResult other = (EvalResult) obj;
		if (domains == null) {
			if (other.domains != null) return false;
		}
		else if (!domains.equals(other.domains)) return false;
		return true;
	}

}
