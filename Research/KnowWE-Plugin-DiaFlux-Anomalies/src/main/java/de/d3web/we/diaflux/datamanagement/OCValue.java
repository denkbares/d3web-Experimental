package de.d3web.we.diaflux.datamanagement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Choice;


public class OCValue implements AllValue{
	private Set<Choice> possibleValues;
	private Set<Choice> actualValues;
	
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
	public boolean intersects(AllValue v) {
		if(!(v instanceof OCValue))
			return false;
		OCValue oc = (OCValue)v;
		for(Choice c : oc.actualValues) {
			if(actualValues.contains(c))
				return true;
		}
		return false;
	}

	/*
	 * returns true if all Values of v are in actualValues of this
	 * or the actualValues of this are empty
	 */
	@Override
	public boolean containsValue(AllValue v) {
		if(!(v instanceof OCValue))
			return false;
		OCValue oc = (OCValue)v;
		if(actualValues.isEmpty())
			return true;
		for(Choice c : oc.actualValues) {
			if(!actualValues.contains(c))
				return false;
		}
		return true;
	}
	

	@Override
	public AllValue intersectWith(AllValue v) {
		OCValue oc = (OCValue)v;
		OCValue result = new OCValue(possibleValues, actualValues);
		for(Choice c : result.actualValues) {
			if(!oc.actualValues.contains(c))
				result.removeActualValue(c);
		}
		return result;
	}

	@Override
	public List<? extends AllValue> negate() {
		List<OCValue> result = new LinkedList<OCValue>();
		Set<Choice> actValues = new HashSet<Choice>();
		for(Choice c : possibleValues) {
			if(!actualValues.contains(c)) {
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
	public AllValue mergeWith(AllValue v) {
		return intersectWith(v);
	}

	@Override
	public List<? extends AllValue> substract(AllValue v) {
		OCValue oc = (OCValue)v;
		List<OCValue> result = new LinkedList<OCValue>();
		Set<Choice> actValues = new HashSet<Choice>();
		actValues.addAll(actualValues);
		for(Choice c : oc.actualValues) {
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
