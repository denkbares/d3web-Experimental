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

import java.util.NoSuchElementException;

/**
 * NumInput: Numerical Input
 */
public class NumInput extends Input {
	/**
	 * Creates a new NumInput object with a name
	 * @param name A unique identifier for the object.
	 */
	public NumInput(String name) {
		super(name);
	}

	public void setValue(NumValue value) {
		if (value == null)
			throw new IllegalArgumentException("The answer can't be null!");
		
		this.values.clear();
		this.values.add(value);
	}
	
	public void setValue(double value) {
		setValue(new NumValue(value));
	}
	
	@Override
	public double getNumValue() {
		try {
			return ((Double) values.iterator().next().value).doubleValue();
		} catch (NoSuchElementException e) {
			return 0;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("NumInput: ");
		result.append(getName());
		result.append("\nNumValue: ");
		result.append(getNumValue());
		return result.toString();
	}
	
	@Override
	public String getStatusText() {
		StringBuilder result = new StringBuilder();
		result.append(getName());
		result.append(" = {");
		result.append(getNumValue());
		result.append("}");
		return result.toString();
	}
	
	@Override
	public AbstractFact copy() {
		return new NumInput(this.getName());
	}
}
