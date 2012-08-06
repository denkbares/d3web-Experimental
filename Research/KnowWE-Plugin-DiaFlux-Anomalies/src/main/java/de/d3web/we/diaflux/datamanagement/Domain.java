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

import java.util.LinkedList;
import java.util.List;

public class Domain<T extends IValue<T>> {

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
			for (int i = 0; i < oldList.size(); i++) {
				for (int j = i + 1; j < oldList.size(); j++) {
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
			for (int i = 0; i < oldList.size(); i++) {
				for (int j = i + 1; j < oldList.size(); j++) {
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

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Domain<T> negate() {
		List<T> newlist = new LinkedList<T>();
		Domain<T> result = new Domain<T>();

		for (T t1 : list) {
			newlist.addAll(t1.negate());
		}
		for (T v : newlist) {
			result.add(v);
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
				for (T new_t : t.substract(d_t)) {
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

	@Override
	public String toString() {
		String result = "";
		for (T t : list) {
			result += t.toString() + " ";
		}
		return "Domain: " + result;
	}
}
