package de.d3web.we.diaflux.datamanagement;

import java.util.LinkedList;
import java.util.List;

public class Domain<T extends AllValue> {

	private List<T> list;

	public Domain() {
		list = new LinkedList<T>();
	}

	public boolean add(T v) {
		list.add(v);
		mergeAll();
		return true;
	}

	public boolean addAll(Domain<T> d) {
		list.addAll(d.getList());
		mergeAll();
		return false;
	}

	private void mergeAll() {
		List<T> result = new LinkedList<T>();
		result.addAll(this.list);
		List<T> oldList = new LinkedList<T>();
		while (!oldList.equals(result)) {
			oldList = new LinkedList<T>();
			oldList.addAll(result);
			for(int i = 0; i < oldList.size(); i++) {
				for(int j = i+1; j < oldList.size(); j++) {
					T t1 = oldList.get(i);
					T t2 = oldList.get(j);
					if (t1.intersects(t2)) {
						result.remove(t1);
						result.remove(t2);
						result.add((T) t1.mergeWith(t2));

					}
				}
			}
		}
		this.list = result;
	}

	private void intersectAll() {
		List<T> result = new LinkedList<T>();
		result.addAll(this.list);
		List<T> oldList = new LinkedList<T>();
		while (!oldList.equals(result)) {
			oldList = new LinkedList<T>();
			oldList.addAll(result);
			for(int i = 0; i < oldList.size(); i++) {
				for(int j = i+1; j < oldList.size(); j++) {
					T t1 = oldList.get(i);
					T t2 = oldList.get(j);

					if (t1.intersects(t2)) {
						result.remove(t1);
						result.remove(t2);
						result.add((T) t1.intersectWith(t2));
					}
				}
			}
		}
		this.list = result;
	}

	public boolean intersects(Domain<T> d) {
		for (T t1 : d.getList()) {
			for (T t2 : list) {
				if (t1.intersects(t2)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean contains(Domain<T> d) {
		boolean contained = false;
		for (T nV : d.list) {
			for (T nV2 : list) {
				if (nV2.containsValue(nV)) contained = true;
			}
			if (!contained) return false;
		}
		return true;
	}

	// public boolean contains(AllDomain<T> d) {
	// // TODO Auto-generated method stub
	// return false;
	// }

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Domain<T> negate() {
		List<AllValue> newlist = new LinkedList<AllValue>();
		Domain<T> result = new Domain<T>();

		for (T t1 : list) {
			newlist.addAll(t1.negate());
		}
		for (AllValue v : newlist) {
			result.add((T) v);
		}
		result.mergeAll();
		return result;
	}

	public List<T> getList() {
		return list;
	}

	public Domain<T> substract(Domain<T> d) {
		Domain<T> domain = new Domain<T>();
		List<T> resultList = new LinkedList<T>();
		for (T t : list) {
			for (T d_t : d.list) {
				for (Object o : t.substract(d_t)) {
					T new_t = (T) o;
					if (!new_t.isEmpty()) {
						resultList.add(new_t);
						domain.list.add(new_t);
					}
				}
			}
		}

		domain.intersectAll();
		return domain;
	}

	public Domain<T> intersectWith(Domain<T> d) {
		if (!this.intersects(d)) return new Domain<T>();
		Domain<T> result = new Domain<T>();
		for (T t1 : list) {
			for (T t2 : d.list) {
				if (t1.intersects(t2)) result.add((T) t1.intersectWith(t2));
			}
		}
		result.mergeAll();
		return result;
	}

	public String toString() {
		String result = "";
		for (T t : list) {
			result += t.toString() + " ";
		}
		return "Domain: " + result;
	}
}
