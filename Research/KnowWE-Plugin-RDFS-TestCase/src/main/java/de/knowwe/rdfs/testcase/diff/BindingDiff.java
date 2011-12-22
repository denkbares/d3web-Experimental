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
package de.knowwe.rdfs.testcase.diff;

import de.knowwe.rdfs.testcase.Binding;

/**
 * 
 * @author Sebastian Furth
 * @created 20.12.2011
 */
public abstract class BindingDiff implements Diff {

	private final Binding binding;

	public BindingDiff(Binding binding) {
		if (binding == null) {
			throw new NullPointerException("The binding is null!");
		}
		this.binding = binding;
	}

	public String getVariable() {
		return binding.getVariable();
	}

	public String getValue() {
		return binding.getValue();
	}

	public Binding getBinding() {
		return binding;
	}

	@Override
	public String toString() {
		return getVariable() + " = " + getValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((binding == null) ? 0 : binding.hashCode());
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
		BindingDiff other = (BindingDiff) obj;
		if (binding == null) {
			if (other.binding != null) {
				return false;
			}
		}
		else if (!binding.equals(other.binding)) {
			return false;
		}
		return true;
	}

}
