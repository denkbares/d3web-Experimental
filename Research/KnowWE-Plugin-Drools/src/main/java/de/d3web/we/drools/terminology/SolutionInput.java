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
 * SolutionInput
 */
public class SolutionInput extends NumInput {	
	/**
	 * Returns the State of this Solution.
	 * @return the SolutionState 
	 */
	public SolutionState getState() {
		if (getNumValue() >= SolutionScore.P6.getValue()) 
			return SolutionState.ESTABLISHED;
		else if (getNumValue() >= SolutionScore.P3.getValue()) 
			return SolutionState.SUGGESTED;
		else if (getNumValue() <= SolutionScore.N6.getValue()) 
			return SolutionState.EXCLUDED;
		
		return SolutionState.UNCLEAR;
	}
	
	/**
	 * Adds the specified value to the score.
	 * @param the to be added value
	 */
	@Override
	public void setValue(NumValue score) {
		super.setValue(new NumValue(getNumValue() + ((Double) score.getValue()).doubleValue()));
	}
	
	/**
	 * Adds the value of an SolutionScore to the current score.
	 * TODO ENGLISH PLS
	 * @param score the SolutionScore whose score should be added.	
	 */
	public void setValue(SolutionScore score) {
		setValue(new NumValue(score.getValue()));
	}

	/**
	 * Creates a new SolutionInput object
	 * @param name Unique identifier, required.
	 */
	public SolutionInput(String name) {
		super(name);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("SolutionInput: ");
		result.append(getName());
		result.append("\nScore: ");
		result.append(getNumValue());
		result.append("\nState: ");
		result.append(getState());
		return result.toString();
	}
	
	@Override
	public String getStatusText() {
		StringBuilder result = new StringBuilder();
		result.append(getName());
		result.append(" = {");
		result.append(getState());
		result.append("}");
		return result.toString();
	}
	
	@Override
	public AbstractFact copy() {
		return new SolutionInput(this.getName());
	}
}
