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

public class NumericInterval {

	protected double min;
	protected double max;

	protected boolean minClosed;
	protected boolean maxClosed;

	public NumericInterval(double min, double max, boolean minClosed, boolean maxClosed) {
		set(min, max, minClosed, maxClosed);
	}

	public NumericInterval(double min, double max) {
		this(min, max, false, false);
	}

	public NumericInterval(NumericInterval nI) {
		this(nI.getMin(), nI.getMax(), nI.getMinClosed(), nI.getMaxClosed());
	}

	private void validate() {
		if (min > max) {
			/*
			 * throw new
			 * Exception("min limit mustn't be greater than max limit");
			 */
			double d = min;
			boolean b = minClosed;
			min = max;
			minClosed = maxClosed;
			max = d;
			maxClosed = b;
		}
	}

	public void set(double min, double max, boolean minOpen, boolean maxOpen) {
		this.min = min;
		this.max = max;
		this.minClosed = minOpen;
		this.maxClosed = maxOpen;
		validate();
	}

	public void set(NumericInterval nI) {
		set(nI.getMin(), nI.getMax(), nI.getMinClosed(), nI.getMaxClosed());
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public boolean getMinClosed() {
		return minClosed;
	}

	public boolean getMaxClosed() {
		return maxClosed;
	}

	public void setMin(double min) {
		this.min = min;
		validate();
	}

	public void setMax(double max) {
		this.max = max;
		validate();
	}

	public void setMinClosed(boolean closed) {
		minClosed = closed;
	}

	public void setMaxClosed(boolean closed) {
		maxClosed = closed;
	}

	public boolean contains(Double d) {
		if (Double.NEGATIVE_INFINITY == d && Double.NEGATIVE_INFINITY == min) {
			return true;
		}
		if (Double.POSITIVE_INFINITY == d && Double.POSITIVE_INFINITY == max) {
			return true;
		}
		return ((minClosed ? min <= d : min < d) && (maxClosed ? d <= max : d < max));
	}

	public boolean contains(NumericInterval nI) {
		// if(contains(nI.min)) {
		//
		// }
		//
		// return false;
		return (contains(nI.min) && contains(nI.max));
	}

	/*
	 * public boolean intersects(NumericInterval nI) { if (max > nI.getMin() &&
	 * min < nI.getMax()) { return true; }else if(max == nI.getMin() &&
	 * (maxClosed || nI.getMinClosed())){ return true; } else if(min ==
	 * nI.getMax() && (minClosed || nI.getMaxClosed())) { return true; } return
	 * false; // return (!(min > nI.getMax() || max < nI.getMin())); }
	 * 
	 * public NumericInterval intersectWith(NumericInterval nI) {
	 * NumericInterval result = new NumericInterval(this); if(min < nI.getMin())
	 * { result.setMin(nI.getMin()); result.setMinClosed(nI.getMinClosed());
	 * }else if(nI.getMin() == min) { result.setMinClosed(minClosed &
	 * nI.getMinClosed()); } if(max > nI.getMax()) { result.setMax(nI.getMax());
	 * result.setMaxClosed(nI.getMaxClosed()); }else if(nI.getMax() == max) {
	 * result.setMaxClosed(maxClosed & nI.getMaxClosed()); }
	 * 
	 * return result; }
	 * 
	 * public NumericInterval mergeWith(NumericInterval nI) { NumericInterval
	 * result = new NumericInterval(this); if(min > nI.getMin()) {
	 * result.setMin(nI.getMin()); result.setMinClosed(nI.getMinClosed()); }else
	 * if(nI.getMin() == min) { result.setMinClosed(minClosed |
	 * nI.getMinClosed()); } if(max < nI.getMax()) { result.setMax(nI.getMax());
	 * result.setMaxClosed(nI.getMaxClosed()); }else if(nI.getMax() == max) {
	 * result.setMaxClosed(maxClosed | nI.getMaxClosed()); } return result; }
	 * 
	 * public List<NumericInterval> getOuterIntervals() { List<NumericInterval>
	 * list = new LinkedList<NumericInterval>();
	 * if(min>Double.NEGATIVE_INFINITY) { list.add(new
	 * NumericInterval(Double.NEGATIVE_INFINITY,min, false, !getMinClosed())); }
	 * if(max < Double.POSITIVE_INFINITY) { list.add(new NumericInterval(max,
	 * Double.POSITIVE_INFINITY, !getMaxClosed(), false)); } return list; }
	 * 
	 * public List<NumericInterval> substract(NumericInterval other) {
	 * List<NumericInterval> result = new LinkedList<NumericInterval>();
	 * if(this.intersects(other)) {
	 * 
	 * } else { result.add(this); } return result; }
	 */

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NumericInterval)) return false;
		NumericInterval other = (NumericInterval) o;
		return ((min == other.min) && (max == other.max) && (minClosed == other.minClosed) && (maxClosed == other.maxClosed));
	}

	@Override
	public String toString() {
		return (minClosed ? "[" : "]") + min + "," + max + (maxClosed ? "]" : "[");
	}
}
