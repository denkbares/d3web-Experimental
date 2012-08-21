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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.d3web.core.knowledge.TerminologyObject;

/**
 * This class represents the results of an evaluation of a condition or action.
 * 
 * @author Reinhard Hatko
 * @created 28.06.2012
 */
public class EvalResult {

	private final Map<TerminologyObject, Domain> domains;

	public EvalResult() {
		domains = new HashMap<TerminologyObject, Domain>();
	}

	public EvalResult(TerminologyObject question, Domain domain) {
		this();
		domains.put(question, domain);
	}

	/**
	 * For use with CondOr
	 * 
	 * @created 28.06.2012
	 * @param result
	 */
	public void mergeWith(EvalResult result) {
		for (Map.Entry<TerminologyObject, Domain> entry : result.getEntries()) {
			TerminologyObject q = entry.getKey();
			Domain domain = entry.getValue();
			if (!this.containsObject(q)) {
				this.domains.put(q, domain);
			} else {
				Domain oldDomain = this.domains.remove(q);
				this.domains.put(q, oldDomain.add(domain));

			}
			
		}
		
	}

	/**
	 * For use with CondAnd
	 * 
	 * @created 28.06.2012
	 * @param result
	 */
	public void intersectWith(EvalResult result) {
		for (Map.Entry<TerminologyObject, Domain> entry : result.getEntries()) {
			TerminologyObject q = entry.getKey();
			Domain domain = entry.getValue();
			if (!this.containsObject(q)) {
				this.domains.put(q, domain);
			}
			else {
				Domain oldDomain = this.domains.remove(q);
				this.domains.put(q, oldDomain.intersect(domain));

			}

		}

	}

	public void negate() {

		for (TerminologyObject q : new LinkedList<TerminologyObject>(getObjects())) {
			Domain domain = this.domains.get(q);
			this.domains.put(q, domain.negate());
		}

	}

	public boolean isEmpty() {
		return domains.isEmpty();
	}

	public boolean containsObject(TerminologyObject key) {
		return domains.containsKey(key);
	}

	public Domain getDomain(TerminologyObject key) {
		return domains.get(key);
	}

	public Set<TerminologyObject> getObjects() {
		return domains.keySet();
	}

	public Collection<Domain> getDomains() {
		return domains.values();
	}

	public Set<Entry<TerminologyObject, Domain>> getEntries() {
		return domains.entrySet();
	}

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
