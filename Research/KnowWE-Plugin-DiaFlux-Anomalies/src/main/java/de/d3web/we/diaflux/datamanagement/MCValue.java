package de.d3web.we.diaflux.datamanagement;

import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Choice;


public class MCValue implements AllValue{
	
	private Set<Choice> possibleValues;
	private Set<Choice> actualValues;
	
	public MCValue(Set<Choice> posValues, Set<Choice> actValues) {
		possibleValues = posValues;
		actualValues = actValues;
	}
	public void addActualValue(Choice value) {
		actualValues.clear();
		actualValues.add(value);
	}

	@Override
	public boolean intersects(AllValue v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(AllValue v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AllValue intersectWith(AllValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends AllValue> negate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AllValue mergeWith(AllValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends AllValue> substract(AllValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

}
