/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.kdom.generator;

/**
 * A ParametrizedClass is a QualifiedClass with a value for an additional
 * constructor attribute.
 *
 * @see QualifiedClass
 * @author Sebastian Furth
 * @created Feb 1, 2011
 */
public class ParametrizedClass extends QualifiedClass {

	private final String value;

	/**
	 * Creates a new parametrized Class which is a qualified Class with a value
	 * for an additional constructor attribute.
	 * 
	 * @param packageName package name of the class
	 * @param className class-name of the class
	 * @param value value for the constructor attribute.
	 */
	public ParametrizedClass(String packageName, String className, String value) {
		super(packageName, className);
		if (value == null) {
			throw new IllegalArgumentException();
		}
		this.value = value;
	}

	/**
	 * Returns the value of the one and only constructor parameter.
	 *
	 * @created Feb 4, 2011
	 * @return value of the constructor parameter.
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + value + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (!(obj instanceof ParametrizedClass)) return false;
		ParametrizedClass other = (ParametrizedClass) obj;
		if (value == null) {
			if (other.value != null) return false;
		}
		else if (!value.equals(other.value)) return false;
		return true;
	}

}
