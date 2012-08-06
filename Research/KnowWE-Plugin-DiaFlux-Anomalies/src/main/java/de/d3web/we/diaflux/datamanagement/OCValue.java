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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Choice;

public class OCValue implements IValue<OCValue> {

	private final Set<Choice> possibleValues;
	private final Set<Choice> actualValues;

	public OCValue(Set<Choice> posValues, Set<Choice> actValues) {
		possibleValues = posValues;
		actualValues = actValues;
	}

	public void setActualValue(Choice value) {
		actualValues.clear();
		actualValues.add(value);
	}

	public void removeActualValue(Choice value) {
		actualValues.remove(value);
	}

	/*
	 * returns true if at least one Choice of v is in this.actualValues
	 */
	@Override
	public boolean intersects(OCValue oc) {
		for (Choice c : oc.actualValues) {
			if (actualValues.contains(c)) return true;
		}
		return false;
	}

	/*
	 * returns true if all Values of v are in actualValues of this or the
	 * actualValues of this are empty
	 */
	@Override
	public boolean containsValue(OCValue oc) {
		if (actualValues.isEmpty()) return true;
		for (Choice c : oc.actualValues) {
			if (!actualValues.contains(c)) return false;
		}
		return true;
	}

	@Override
	public OCValue intersectWith(OCValue oc) {
		OCValue result = new OCValue(possibleValues, actualValues);
		for (Choice c : result.actualValues) {
			if (!oc.actualValues.contains(c)) result.removeActualValue(c);
		}
		return result;
	}

	@Override
	public List<? extends OCValue> negate() {
		List<OCValue> result = new LinkedList<OCValue>();
		Set<Choice> actValues = new HashSet<Choice>();
		for (Choice c : possibleValues) {
			if (!actualValues.contains(c)) {
				actValues.add(c);
			}
		}
		OCValue ocvalue = new OCValue(possibleValues, actValues);
		result.add(ocvalue);
		return result;
	}

	/*
	 * OC has only one actual Value
	 */
	@Override
	public OCValue mergeWith(OCValue v) {
		return intersectWith(v);
	}

	@Override
	public List<? extends OCValue> substract(OCValue oc) {
		List<OCValue> result = new LinkedList<OCValue>();
		Set<Choice> actValues = new HashSet<Choice>();
		actValues.addAll(actualValues);
		for (Choice c : oc.actualValues) {
			actValues.remove(c);
		}
		OCValue ocvalue = new OCValue(possibleValues, actValues);
		result.add(ocvalue);
		return result;
	}

	@Override
	public boolean isEmpty() {
		return actualValues.isEmpty();
	}

}
