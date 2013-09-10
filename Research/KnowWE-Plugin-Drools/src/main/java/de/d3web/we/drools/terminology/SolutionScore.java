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

public enum SolutionScore {

	P1(2), P2(5), P3(10), P4(20), P5(40), P6(80), P7(999),
	N1(-2), N2(-5), N3(-10), N4(-20), N5(-40), N6(-80), N7(-999);
	
	private double value;
	
	/**
	 * Default Constructor. 
	 * @param value specifies the value behind a SolutionScore
	 */
	SolutionScore(double value) {
		this.value = value;
	}
	
	/**
	 * Returns a SolutionScore's value.
	 * @return the value behind the SolutionScore
	 */
	public double getValue() {
		return value;
	}
	
}
