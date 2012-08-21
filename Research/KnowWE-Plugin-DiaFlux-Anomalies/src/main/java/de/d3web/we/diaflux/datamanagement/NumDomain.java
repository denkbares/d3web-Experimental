/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.diaflux.datamanagement;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;


/**
 * This domain represents the possible ranges of a QuestionNum.
 * 
 * @author Reinhard Hatko
 * @created 21.06.2012
 */
public class NumDomain extends QuestionDomain<QuestionNum> {

	private final List<NumericalInterval> intervals;

	public NumDomain(QuestionNum question, List<NumericalInterval> intervals) {
		super(question);
		this.intervals = new LinkedList<NumericalInterval>(intervals);
		normalize(this.intervals);
	}

	// public NumDomain(NumDomain domain) {
	// this(domain.getQuestion(), domain.intervals);
	// }

	public NumDomain(QuestionNum question) {
		this(question, new NumericalInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));

	}

	public NumDomain(QuestionNum question, NumericalInterval interval) {
		this(question, Arrays.asList(interval));
	}

	public NumDomain(QuestionNum question, double value) {
		this(question, new NumericalInterval(value, value));
	}

	@Override
	public NumDomain add(Domain d) {
		NumDomain domain = (NumDomain) d;
		List<NumericalInterval> intervals = new LinkedList<NumericalInterval>();
		intervals.addAll(this.intervals);
		intervals.addAll(domain.intervals);

		normalize(intervals);

		return new NumDomain(getQuestion(), intervals);
	}

	/**
	 * 
	 * @created 27.06.2012
	 * @param intervals
	 */
	private void normalize(List<NumericalInterval> intervals) {
		removeEmpty(intervals);

		Collections.sort(intervals);
		for (int i = 0; i < intervals.size() - 1;) {
			NumericalInterval first = intervals.get(i);
			NumericalInterval second = intervals.get(i + 1);
			if (first.contains(second)) {
				// remove second, is contained in first one
				intervals.remove(i + 1);
			}
			else if (first.intersects(second)) {
				intervals.remove(i); // remove first
				intervals.remove(i); // remove second (now at i)
				NumericalInterval joined;
				joined = new NumericalInterval(first.getLeft(), second.getRight(),
						first.isLeftOpen(), second.isRightOpen());

				intervals.add(i, joined);
			}
			else {
				i++;
			}

		}
		Collections.sort(intervals); // TODO necessary?
	}


	@Override
	public NumDomain negate() {

		List<NumericalInterval> intervals = new LinkedList<NumericalInterval>();
		List<NumericalInterval> leftBounded = new LinkedList<NumericalInterval>();
		List<NumericalInterval> rightBounded = new LinkedList<NumericalInterval>();

		for (NumericalInterval interval : this.intervals) {
			rightBounded.add(new NumericalInterval(Double.NEGATIVE_INFINITY,
					interval.getLeft(), false, !interval.isLeftOpen()));

			leftBounded.add(new NumericalInterval(interval.getRight(),
					Double.POSITIVE_INFINITY, !interval.isRightOpen(), false));
		}
		removeEmpty(leftBounded);
		removeEmpty(rightBounded);

		Collections.sort(leftBounded);
		Collections.sort(rightBounded);

		next:
		for (int i = 0; i < leftBounded.size(); i++) {
			NumericalInterval left = leftBounded.get(i);
			for (int j = 0; j < rightBounded.size(); j++) {
				NumericalInterval right = rightBounded.get(j);

				if (left.intersects(right)) {
					NumericalInterval joined;
					joined = new NumericalInterval(left.getLeft(), right.getRight(),
							left.isLeftOpen(), right.isRightOpen());
					rightBounded.remove(j);
					j--;
					intervals.add(joined);
					continue next;
				}

			}
			intervals.add(left);

		}
		intervals.addAll(rightBounded);
		removeEmpty(intervals);
		Collections.sort(intervals);
		return new NumDomain(this.getQuestion(), intervals);
	}


	private void removeEmpty(List<NumericalInterval> intervals) {
		for (int i = intervals.size() - 1; i >= 0; i--) {
			if (intervals.get(i).isEmpty()) {
				intervals.remove(i);
				continue;
			}
		}

	}

	@Override
	public boolean contains(Domain d) {
		NumDomain domain = (NumDomain) d;
		next: for (NumericalInterval other : domain.intervals) {
			for (NumericalInterval interval : intervals) {
				if (interval.contains(other)) continue next;
			}
			return false;
		}

		return true;

	}

	@Override
	public boolean intersects(Domain d) {
		NumDomain domain = (NumDomain) d;
		for (NumericalInterval interval : intervals) {
			for (NumericalInterval other : domain.intervals) {
				if (interval.intersects(other)) return true;
			}

		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		if (intervals.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}


	@Override
	public NumDomain intersect(Domain d) {
		NumDomain domain = (NumDomain) d;
		LinkedList<NumericalInterval> intervals = new LinkedList<NumericalInterval>();

		for (NumericalInterval interval : this.intervals) {
			for (NumericalInterval other : domain.intervals) {
				if (interval.intersects(other)) {
					intervals.add(interval.intersect(other));
				}
			}
		}
		return new NumDomain(getQuestion(), intervals);
	}

	public List<NumericalInterval> getIntervals() {
		return Collections.unmodifiableList(intervals);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((intervals == null) ? 0 : intervals.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NumDomain other = (NumDomain) obj;
		if (intervals == null) {
			if (other.intervals != null) return false;
		}
		else if (!intervals.equals(other.intervals)) return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + intervals.toString();
	}
}
