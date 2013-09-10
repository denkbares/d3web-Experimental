/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.we.drools.terminology;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

public abstract class ChoiceInput extends Input {

	/**
	 * This collection contains all valid and selectable values for the given
	 * ChoiceInput
	 */
	private final Collection<TextValue> possibleValues = new LinkedList<TextValue>();

	public ChoiceInput(String name) {
		super(name);
	}

	public Collection<TextValue> getPossibleValues() {
		return possibleValues;
	}

	public void addPossibleValue(TextValue value) {
		if (value != null) {
			this.getPossibleValues().add(value);
		}
	}

	public void addPossibleValues(Collection<TextValue> collection) {
		if (collection != null) {
			this.getPossibleValues().addAll(collection);
		}
	}

	@Override
	public double getNumValue() {
		throw new AssertionError("doubleValue is not applicable to this question type.");
	}

	public abstract void setValue(TextValue answer);

	public void setValue(String value) {
		for (TextValue t : possibleValues) {
			if (((String) t.getValue()).equals(value)) {
				setValue(t);
				return;
			}
		}
		Logger.getLogger(this.getClass().getName()).warning(
				"\"" + value + "\" is not a possible Value for Input \"" + this.getName()
						+ "\", no value was set!");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((possibleValues == null) ? 0 : possibleValues.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		ChoiceInput other = (ChoiceInput) obj;
		if (possibleValues == null) {
			if (other.possibleValues != null) return false;
		}
		else if (!possibleValues.equals(other.possibleValues)) return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("ChoiceInput: ");
		result.append(getName());
		result.append("\nValues: ");
		if (values.size() > 0) {
			for (Value v : values) {
				result.append(v.toString());
				result.append(", ");
			}
			result.delete(result.length() - 2, result.length());
		}
		result.append("\nPossible Values: ");
		if (possibleValues.size() > 0) {
			for (Value v : possibleValues) {
				result.append(v.toString());
				result.append(", ");
			}
			result.delete(result.length() - 2, result.length());
		}
		return result.toString();
	}

	@Override
	public String getStatusText() {
		StringBuilder result = new StringBuilder();
		result.append(getName());
		result.append(" = {");
		if (values.size() > 0) {
			for (Value v : values) {
				result.append(v.toString());
				result.append(", ");
			}
			result.delete(result.length() - 2, result.length());
		}
		result.append("}");
		return result.toString();
	}

}
