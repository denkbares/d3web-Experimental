package de.d3web.we.diaflux.datamanagement;

import java.util.HashMap;


public class EvalResultBeta {
	private HashMap<String, Domain> results;

	/*
	 * public boolean add(HashMap<String, List<Domain>> map) { return true; }
	 */

	public EvalResultBeta() {
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
	 * Variables only appearing in one EvalResult are omitted
	 */
	public EvalResult intersect(EvalResultBeta eRes) {
		EvalResult result = new EvalResult();
		for(String key1 : results.keySet()) {
			for(String key2 : eRes.results.keySet()) {
				if(key1.equals(key2)) {
					Domain domain = results.get(key1);
					if(domain.intersectWith(eRes.results.get(key2))) {
						result.add(key1, domain);
					}
				}
			}
		}
//		result.mergeAll();
		return result;
	}
	
	public boolean intersects(EvalResultBeta eRes) {
		for(String key1 : results.keySet()) {
			for(String key2 : eRes.results.keySet()) {
				if(key1.equals(key2)) {
					Domain domain = results.get(key1);
//					System.out.println(key1 + " mapt " + domain);
//					System.out.println(key2 + " mapt " + eRes.results.get(key2).getList());
					if(domain.intersects(eRes.results.get(key2))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * merges all Domains for every Variable in EvalResult
	 * with all Domains of the equivalent Variable in eRes
	 * and returns an new EvalResult
	 */
	public EvalResult merge(EvalResultBeta eRes) {
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
	
	public String toString() {
		return "EvalResult: " + results.toString();
	}
}
