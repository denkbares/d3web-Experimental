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

import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Choice;

public class MCValue implements IValue {

	private final Set<Choice> possibleValues;
	private final Set<Choice> actualValues;

	public MCValue(Set<Choice> posValues, Set<Choice> actValues) {
		possibleValues = posValues;
		actualValues = actValues;
	}

	public void addActualValue(Choice value) {
		actualValues.clear();
		actualValues.add(value);
	}

	@Override
	public boolean intersects(IValue v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(IValue v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IValue intersectWith(IValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends IValue> negate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue mergeWith(IValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends IValue> substract(IValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

}
