/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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
package de.d3web.we.drools.terminology;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Question :o
 */
public abstract class Input extends AbstractFact {
	/**
	 * The selected answers of the question
	 */
	protected Collection<Value> values = new LinkedList<Value>();
	
	/**
	 * Creates a new Input object
	 * @param name Unique identifier, required.
	 */
	public Input(String name) {
		super(name);
	}
	
	public Collection<Value> getValues() {
		return values;
	}
	
	public abstract double getNumValue();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Input other = (Input) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Input: ");
		result.append(getName());
		result.append("\nValues: ");
		for (Value v : values) {
			result.append(v.toString());
			result.append(", ");
		}
		result.delete(result.length() - 2, result.length());
		return result.toString();
	}
}
