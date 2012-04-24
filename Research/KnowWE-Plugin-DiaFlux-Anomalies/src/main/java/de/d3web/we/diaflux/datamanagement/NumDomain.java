package de.d3web.we.diaflux.datamanagement;

import java.util.LinkedList;
import java.util.List;

public class NumDomain implements Domain {

	private List<NumericInterval> list;

	public NumDomain() {
		list = new LinkedList<NumericInterval>();
	}

	@Override
	public boolean addAll(Domain d) {
		if (!(d instanceof NumDomain)) return false;
		NumDomain numD = (NumDomain) d;
		list.addAll(numD.getList());
		mergeAll();
		return false;
	}

	@Override
	public boolean intersects(Domain d) {
		if (!(d instanceof NumDomain)) return false;
		NumDomain nDom = (NumDomain) d;
		for (Object o1 : nDom.getList()) {
			NumericInterval nI1 = (NumericInterval) o1;
			for (NumericInterval nI2 : list) {
				if (nI1.intersects(nI2)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	//TODO richtiges return (Domain, nciht booelan)
	public boolean intersectWith(Domain d) {
		if (!this.intersects(d)) return false;
		if (!(d instanceof NumDomain)) return false;
		NumDomain numD = (NumDomain) d;
		NumDomain result = new NumDomain();
		for (NumericInterval nI : list) {
			for (NumericInterval nI2 : numD.list) {
				if (nI.intersects(nI2)) result.add(nI.intersectWith(nI2));
			}
		}
		mergeAll();
		return true;
	}

	@Override
	public List<NumericInterval> getList() {
		return list;
	}

	@Override
	public boolean contains(Domain d) {
		if (!(d instanceof NumDomain)) return false;
		NumDomain numD = (NumDomain) d;
		boolean contained = false;
		for (NumericInterval nV : numD.list) {
			for (NumericInterval nV2 : list) {
				if (nV2.contains(nV)) contained = true;
			}
			if (!contained) return false;
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Domain substract(Domain d) {
		if (!(d instanceof NumDomain)) {
			return this;
		}
		NumDomain numD = (NumDomain) d;
		for (NumericInterval nI : list) {
			for (NumericInterval nId : numD.list) {
// TODO
			}
		}
		mergeAll();

		return null;
	}

	@Override
	public boolean add(Value v) {
		if (!(v instanceof NumericInterval)) return false;
		NumericInterval interval = (NumericInterval) v;
		/*for (NumericInterval in2 : list) {
			if (interval.intersects(in2)) {

			}
		}*/
		list.add(interval);
		mergeAll();
		return true;
	}

/*	@Override
	public boolean add(List<Value> listV) {
		for (Value v : listV) {
			add(v);
		}
		return true;
	}*/

	@Override
	public Domain negate() {
		List<NumericInterval> newlist = new LinkedList<NumericInterval>();
		NumDomain result = new NumDomain();

		for (NumericInterval nI : list) {
			newlist.addAll(nI.getOuterIntervals());
		}
		for(NumericInterval nI : newlist) {
			result.add(nI);
		}
		result.mergeAll();
		return result;
	}

	private void mergeAll() {
		NumDomain result = new NumDomain();
		result.list = this.getList();
		List<NumericInterval> oldList = new LinkedList<NumericInterval>();
		while (oldList != result.list) {
			oldList = result.list;
			for (NumericInterval nI : result.list) {
				for (NumericInterval nI2 : result.list) {
					if (!nI.equals(nI2)) {
						if (nI.intersects(nI2)) {
							result.list.add(nI.mergeWith(nI2));
							result.list.remove(nI);
							result.list.remove(nI2);
						}
					}
				}
			}
		}
	}
	
	public String toString() {
		String result = "";
		for(NumericInterval nI : list) {
			result += nI.toString() + " ";
		}
		return "Domain: " + result;
	}
}
