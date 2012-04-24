package de.d3web.we.diaflux.datamanagement;

import java.util.HashMap;
import java.util.Set;

public class EvalResult {

	private HashMap<String, Domain> results;

	/*
	 * public boolean add(HashMap<String, List<Domain>> map) { return true; }
	 */

	public EvalResult() {
		results = new HashMap<String, Domain>();
	}

	public boolean add(String var, Domain domain) {
		Domain oldDomain = results.get(var);
		if (oldDomain != null) {
			domain.addAll(oldDomain);
		}
		results.put(var, domain);
//		mergeAll();
		return true;
	}

/*	public boolean add(String var, Domain domain) {
		List<Domain> list = new LinkedList<Domain>();
		list.add(domain);
		return add(var, list);
	}*/

/*	
	 * tries to merge all Domains for every Key in EvalResult
	 
	private boolean mergeAll() {
		return true;
	}*/

	/*
	 * Negates every Domain for every Variable in the EvalResult
	 * and returns an new EvalResult
	 */
	public EvalResult negate() {
		EvalResult result = new EvalResult();
		for (String key : results.keySet()) {
			Domain domain = results.get(key).negate();
			result.add(key, domain);
		}
//		result.mergeAll();
		return result;
	}

	/*
	 * intersects all Domains for every Variable in EvalResult
	 * with all Domains of the equivalent Variable in eRes
	 * and returns an new EvalResult
	 * Variables only appearing in one EvalResult are completely accepted
	 */
	public EvalResult intersect(EvalResult eRes) {
		EvalResult result = new EvalResult();
		Set<String> thisList = results.keySet();
		thisList.removeAll(eRes.results.keySet());
		for(String key : thisList) {
			result.add(key, results.get(key));
		}
		Set<String> otherList = eRes.results.keySet();
		otherList.removeAll(results.keySet());
		for(String key : otherList) {
			result.add(key, eRes.results.get(key));

		}
		for(String key1 : results.keySet()) {
			for(String key2 : eRes.results.keySet()) {
				if(key1.equals(key2)) {
					Domain domain = results.get(key1);
//					if(domain.intersectWith(eRes.results.get(key2))) {
//						result.add(key1, domain);
						if(domain.intersects(eRes.results.get(key2))) {
							result.add(key1, domain.intersectWith(eRes.results.get(key2)));
					}
				}
			}

		}
//		result.mergeAll();
		return result;
	}
	
	public EvalResult restrictWith(EvalResult eRes) {

		EvalResult result = new EvalResult();
		if(eRes.getVariables().isEmpty()) {
			result = this;
		}
		for(String key1 : results.keySet()) {
			boolean contained = false;

			for(String key2 : eRes.results.keySet()) {
				if(key1.equals(key2)) {

					contained = true;
					Domain domain = results.get(key1);
//					if(domain.intersectWith(eRes.results.get(key2))) {
//						result.add(key1, domain);
						if(domain.intersects(eRes.results.get(key2))) {
							result.add(key1, domain.intersectWith(eRes.results.get(key2)));
					}
				}
			}
			if(!contained) {
				result.add(key1, results.get(key1));
			}
		}
//		result.mergeAll();
		return result;
	}
	
	public boolean intersects(EvalResult eRes) {
		for(String key1 : results.keySet()) {
			for(String key2 : eRes.results.keySet()) {
				if(key1.equals(key2)) {
					Domain domain = results.get(key1);
					if(domain.intersects(eRes.results.get(key2))) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean contains(EvalResult eRes) {
		boolean contained = false;
		for(String key1 : eRes.results.keySet()) {
			for(String key2 : results.keySet()) {
				if(key1.equals(key2)) {
					Domain domain = results.get(key1);
					if(domain.contains(eRes.results.get(key2))) {
						return contained = true;
					}
				}
			}
			if(!contained)
				return false;
		}
		return true;
	}
	
	public boolean contains(String var, Domain domain) {
		if(!results.keySet().contains(var))
			return false;
		Domain oldDomain = results.get(var);
		if(!oldDomain.contains(domain))
			return false;
		return true;
	}
	
	public boolean contains(String var, AllValue value) {
		Domain newDomain = new Domain();
		newDomain.add(value);
		return contains(var, newDomain);
	}
	
	/*
	 * merges all Domains for every Variable in EvalResult
	 * with all Domains of the equivalent Variable in eRes
	 * and returns an new EvalResult
	 */
	public EvalResult merge(EvalResult eRes) {
		EvalResult result = new EvalResult();
		for(String key : eRes.results.keySet()) {
			result.add(key, eRes.results.get(key));
		}
		for(String key : results.keySet()) {
			result.add(key, results.get(key));
		}
//		result.mergeAll();
		return result;
	}
	
	public EvalResult substract(EvalResult eRes) {
		EvalResult result = new EvalResult();
		for(String key : results.keySet()) {
			for(String key_eRes : eRes.results.keySet()) {
				if(key.equals(key_eRes)) {
					Domain domain = results.get(key_eRes).substract(eRes.results.get(key_eRes));
					if(!domain.isEmpty()) {
						result.add(key, domain);
					}
				}
			}
		}
		return result;
	}
	
	public String toString() {
		return "EvalResult: " + results.toString();
	}
	
	public Set<String> getVariables() {
		return results.keySet();
	}
}
