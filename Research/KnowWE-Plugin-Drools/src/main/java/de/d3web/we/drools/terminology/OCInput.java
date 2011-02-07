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

/**
 * OCInput: One Choice Input 
 */
public class OCInput extends ChoiceInput {
	/**
	 * Creates a new MCInput object with a name
	 * @param name A unique identifier for the object.
	 */
	public OCInput(String name) {
		super(name);
	}

	@Override
	public void setValue(TextValue value) {
		if (value == null)
			throw new IllegalArgumentException("value must not be null.");
		
		if (getPossibleValues().contains(value)) {
			this.values.clear();
			this.values.add(value);
		} else {
			throw new IllegalArgumentException("The given value is not listed in the possible values for this input.");
		}
	}
	
	@Override
	public AbstractFact copy() {
		OCInput copy = new OCInput(this.getName());
		copy.addPossibleValues(this.getPossibleValues());
		return copy;
	}


}
