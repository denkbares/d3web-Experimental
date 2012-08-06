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

public class NumValue extends NumericInterval implements IValue<NumValue> {

	public NumValue(double min, double max, boolean minClosed, boolean maxClosed) {
		super(min, max, minClosed, maxClosed);
	}

	@Override
	public boolean intersects(NumValue nI) {
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
	public boolean containsValue(NumValue nI) {
		return super.contains(nI);
	}

	@Override
	public NumValue intersectWith(NumValue nI) {
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
	public NumValue mergeWith(NumValue nI) {
		NumValue result = new NumValue(this.min, this.max, this.minClosed, this.maxClosed);
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
		List<NumValue> neg_v = v.negate();
		for (NumValue numV : neg_v) {
			if (this.intersects(numV)) {
				result.add(this.intersectWith(numV));
			}
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		// empty if not contains its middle element
		return contains((getMin() + getMax()) / 2);
	}
}
