/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.knowwe.medicine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class MedicineObject {

	private int id;
	private int parent;
	private String subject;

	private HashSet<MedicineObject> childs;
	private HashMap<MedicineParameter, Object> values;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public MedicineObject() {
		values = new HashMap<MedicineParameter, Object>();
		childs = new HashSet<MedicineObject>();
	}

	public void addValue(MedicineParameter p, Object o) {
		values.put(p, o);
	}

	public boolean containsKey(MedicineParameter p) {
		return values.containsKey(p);
	}

	public Object get(MedicineParameter p) {
		return values.get(p);
	}

	public HashMap<MedicineParameter, Object> getMap() {
		return values;
	}

	public void addChild(MedicineObject e) {
		childs.add(e);
	}

	public HashSet<MedicineObject> getChilds() {
		return childs;
	}

	public void addChildren(ArrayList<MedicineObject> l) {
		childs.addAll(l);
	}

	@Override
	public String toString() {
		String result = "EKGObject:\n";
		for (Entry<MedicineParameter, Object> e : values.entrySet()) {
			result += "Parameter: " + e.getKey().getName() + ", Value: " + e.getValue() + "\n";
		}
		return result + "\n";
	}
}
