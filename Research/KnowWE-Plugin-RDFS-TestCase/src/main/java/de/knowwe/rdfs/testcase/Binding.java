/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.rdfs.testcase;

/**
 * 
 * @author Sebastian Furth
 * @created 20.12.2011
 */
public class Binding {

	private final String variable;
	private final String value;

	public Binding(String variable, String value) {
		if (variable == null) {
			throw new NullPointerException("The variable is null!");
		}
		if (variable.isEmpty()) {
			throw new IllegalArgumentException("The variable is empty!");
		}
		if (value == null) {
			throw new NullPointerException("The value is null!");
		}
		if (value.isEmpty()) {
			throw new IllegalArgumentException("The value is empty!");
		}
		this.variable = variable;
		this.value = value;
	}

	public String getVariable() {
		return variable;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return variable + " = " + value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((variable == null) ? 0 : variable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Binding other = (Binding) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		}
		else if (!value.equals(other.value)) {
			return false;
		}
		if (variable == null) {
			if (other.variable != null) {
				return false;
			}
		}
		else if (!variable.equals(other.variable)) {
			return false;
		}
		return true;
	}

}
