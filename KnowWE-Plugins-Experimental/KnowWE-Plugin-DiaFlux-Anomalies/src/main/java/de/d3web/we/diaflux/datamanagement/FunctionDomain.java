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


/**
 * 
 * @author Reinhard Hatko
 * @created 04.12.2012
 */
public class FunctionDomain implements Domain {

	private final String function;

	public FunctionDomain(String function) {
		this.function = function;
	}

	@Override
	public Domain add(Domain domain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Domain negate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Domain domain) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(Domain domain) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Domain intersect(Domain domain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((function == null) ? 0 : function.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		FunctionDomain other = (FunctionDomain) obj;
		if (function == null) {
			if (other.function != null) return false;
		}
		else if (!function.equals(other.function)) return false;
		return true;
	}

}
