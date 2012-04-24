package de.d3web.we.diaflux.datamanagement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MOValue implements AllValue {
	
	private boolean isOneChoice = false;
	
	private Set<String> possibleValues;
	private Set<String> actualValues;
	
	public MOValue(Set<String> posValues, Set<String> actValues, boolean oneChoice) {
		possibleValues = posValues;
		actualValues = actValues;
		isOneChoice = oneChoice;
	}
	
//	public void setPossibleValues(Set<String> list) {
//		possibleValues = list;
//	}
	
//	public void setActualValues(Set<String> list) {
//		actualValues = list;
//	}
	
	public void addActualValue(String value) {
		if(isOneChoice) {
			actualValues = new HashSet<String>();
		}
		actualValues.add(value);
	}
	
	public void removeActualValue(String value) {
		actualValues.remove(value);
	}

	@Override
	public boolean intersects(AllValue v) {
		if(!(v instanceof MOValue))
			return false;
		MOValue mov = (MOValue) v;
		for(String val : actualValues) {
			for(String comVal : mov.actualValues) {
				if(val.equals(comVal))
					return true;
			}
		}
		return false;
		}

	@Override
	public boolean containsValue(AllValue v) {
		if(!(v instanceof MOValue))
			return false;
		MOValue mov = (MOValue) v;
		boolean contained = false;
		for(String val : mov.actualValues) {
			for(String comVal : actualValues) {
				if(val.equals(comVal))
					contained = true;
			}
			if(!contained) return false;
		}
		return true;
	}

	@Override
	public AllValue intersectWith(AllValue v) {
		MOValue result = new MOValue(this.possibleValues, new HashSet<String>(), this.isOneChoice);
		if(!(v instanceof MOValue))
			return result;
		MOValue mov = (MOValue) v;
		for(String val : actualValues) {
			for(String comVal : mov.actualValues) {
				if(val.equals(comVal)) {
					result.addActualValue(val);
				}
			}
		}
		return result;
	}

	@Override
	public List<? extends AllValue> negate() {
		List<MOValue> result = new LinkedList<MOValue>();
		Set<String> negValues = new HashSet<String>(possibleValues);
		negValues.removeAll(actualValues);
		MOValue resultVal = new MOValue(this.possibleValues, negValues, this.isOneChoice);
		result.add(resultVal);
		return result;
	}

	@Override
	public AllValue mergeWith(AllValue v) {
		if(!(v instanceof MOValue))
			return this;
		MOValue mov = (MOValue) v;
		HashSet<String> mergedVars = new HashSet<String>();
		for(String value : actualValues) {
			mergedVars.add(value);
		}
		for(String value : mov.actualValues) {
			mergedVars.add(value);
		}
		MOValue result = new MOValue(possibleValues, mergedVars, this.isOneChoice);
		return result;
	}

	public String toString(){
		String result = "MOValue: ";
		result += "(";
		for(String value : actualValues) {
			result += value + " ";
		}
		result += ")";
		return result;
	}

	@Override
	public List<MOValue> substract(AllValue v) {
		MOValue oMO = (MOValue) v;
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
		if(!(o instanceof MOValue))
			return false;
		MOValue other = (MOValue) o;
		if(!other.possibleValues.equals(this.possibleValues))
			return false;
		if(!other.actualValues.equals(this.actualValues))
			return false;
		return true;
	}
}
