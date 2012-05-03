package de.d3web.we.diaflux.datamanagement;

import java.util.LinkedList;
import java.util.List;

public class NumValue extends NumericInterval implements AllValue<NumValue> {

	public NumValue(double min, double max, boolean minClosed, boolean maxClosed) {
		super(min, max, minClosed, maxClosed);
	}

	@Override
	public boolean intersects(NumValue v) {
		if (!(v instanceof NumValue)) return false;
		NumValue nI = (NumValue) v;
		if (max > nI.getMin() && min < nI.getMax()) {
			return true;
		}
		else if (max == nI.getMin() && (maxClosed && nI.getMinClosed())) {
			return true;
		}
		else if (min == nI.getMax() && (minClosed && nI.getMaxClosed())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(NumValue v) {
		if (!(v instanceof NumValue)) return false;
		NumericInterval nI = (NumericInterval) v;
		return super.contains(nI);
	}

	@Override
	public NumValue intersectWith(NumValue v) {
		NumValue nI = (NumValue) v;
		NumValue result = new NumValue(this.min, this.max, this.minClosed, this.maxClosed);
		if (min < nI.getMin()) {
			result.setMin(nI.getMin());
			result.setMinClosed(nI.getMinClosed());
		}
		else if (nI.getMin() == min) {
			result.setMinClosed(minClosed & nI.getMinClosed());
		}
		if (max > nI.getMax()) {
			result.setMax(nI.getMax());
			result.setMaxClosed(nI.getMaxClosed());
		}
		else if (nI.getMax() == max) {
			result.setMaxClosed(maxClosed & nI.getMaxClosed());
		}

		return result;
	}

	@Override
	public List<NumValue> negate() {
		List<NumValue> list = new LinkedList<NumValue>();
		if (min > Double.NEGATIVE_INFINITY) {
			list.add(new NumValue(Double.NEGATIVE_INFINITY, min, false, !getMinClosed()));
		}
		if (max < Double.POSITIVE_INFINITY) {
			list.add(new NumValue(max, Double.POSITIVE_INFINITY, !getMaxClosed(), false));
		}
		return list;
	}

	@Override
	public NumValue mergeWith(NumValue v) {
		if (!(v instanceof NumValue)) return this;
		NumValue result = new NumValue(this.min, this.max, this.minClosed, this.maxClosed);
		NumValue nI = (NumValue) v;
		if (min > nI.getMin()) {
			result.setMin(nI.getMin());
			result.setMinClosed(nI.getMinClosed());
		}
		else if (nI.getMin() == min) {
			result.setMinClosed(minClosed | nI.getMinClosed());
		}
		if (max < nI.getMax()) {
			result.setMax(nI.getMax());
			result.setMaxClosed(nI.getMaxClosed());
		}
		else if (nI.getMax() == max) {
			result.setMaxClosed(maxClosed | nI.getMaxClosed());
		}
		return result;
	}

	@Override
	public String toString() {
		String result = "NumValue: ";
		result += super.toString();
		return result;
	}

	@Override
	public List<NumValue> substract(NumValue v) {
		List<NumValue> result = new LinkedList<NumValue>();
		List<NumValue> neg_v = ((NumValue) v).negate();
		for (NumValue numV : neg_v) {
			if (this.intersects(numV)) {
				result.add((NumValue) this.intersectWith(numV));
			}
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
}
