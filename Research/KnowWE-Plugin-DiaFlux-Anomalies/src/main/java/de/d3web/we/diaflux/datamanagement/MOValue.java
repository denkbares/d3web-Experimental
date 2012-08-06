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

public class MOValue implements IValue<MOValue> {

	private boolean isOneChoice = false;

	private final Set<String> possibleValues;
	private Set<String> actualValues;

	public MOValue(Set<String> posValues, Set<String> actValues, boolean oneChoice) {
		possibleValues = posValues;
		actualValues = actValues;
		isOneChoice = oneChoice;
	}

	// public void setPossibleValues(Set<String> list) {
	// possibleValues = list;
	// }

	// public void setActualValues(Set<String> list) {
	// actualValues = list;
	// }

	public void addActualValue(String value) {
		if (isOneChoice) {
			actualValues = new HashSet<String>();
		}
		actualValues.add(value);
	}

	public void removeActualValue(String value) {
		actualValues.remove(value);
	}

	@Override
	public boolean intersects(MOValue mov) {
		for (String val : actualValues) {
			for (String comVal : mov.actualValues) {
				if (val.equals(comVal)) return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsValue(MOValue mov) {
		boolean contained = false;
		for (String val : mov.actualValues) {
			for (String comVal : actualValues) {
				if (val.equals(comVal)) contained = true;
			}
			if (!contained) return false;
		}
		return true;
	}

	@Override
	public MOValue intersectWith(MOValue mov) {
		MOValue result = new MOValue(this.possibleValues, new HashSet<String>(), this.isOneChoice);
		for (String val : actualValues) {
			for (String comVal : mov.actualValues) {
				if (val.equals(comVal)) {
					result.addActualValue(val);
				}
			}
		}
		return result;
	}

	@Override
	public List<? extends MOValue> negate() {
		List<MOValue> result = new LinkedList<MOValue>();
		Set<String> negValues = new HashSet<String>(possibleValues);
		negValues.removeAll(actualValues);
		MOValue resultVal = new MOValue(this.possibleValues, negValues, this.isOneChoice);
		result.add(resultVal);
		return result;
	}

	@Override
	public MOValue mergeWith(MOValue mov) {
		HashSet<String> mergedVars = new HashSet<String>();
		for (String value : actualValues) {
			mergedVars.add(value);
		}
		for (String value : mov.actualValues) {
			mergedVars.add(value);
		}
		MOValue result = new MOValue(possibleValues, mergedVars, this.isOneChoice);
		return result;
	}

	@Override
	public String toString() {
		String result = "MOValue: ";
		result += "(";
		for (String value : actualValues) {
			result += value + " ";
		}
		result += ")";
		return result;
	}

	@Override
	public List<MOValue> substract(MOValue oMO) {
		Set<String> act = new HashSet<String>();
		act.addAll(this.actualValues);
		act.removeAll(oMO.actualValues);
		MOValue result = new MOValue(this.possibleValues, act, this.isOneChoice);
		List<MOValue> resultList = new LinkedList<MOValue>();
		resultList.add(result);
		return resultList;
	}

	@Override
	public boolean isEmpty() {
		return actualValues.isEmpty();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MOValue)) return false;
		MOValue other = (MOValue) o;
		if (!other.possibleValues.equals(this.possibleValues)) return false;
		if (!other.actualValues.equals(this.actualValues)) return false;
		return true;
	}
}
