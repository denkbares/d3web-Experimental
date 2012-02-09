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
package de.d3web.we.algorithm;


/**
 * 
 * @author Johannes Dienst
 * @created 22.11.2011
 */
public class SuggestionValuePair {
	private Suggestion s;
	private int val;

	public SuggestionValuePair(Suggestion suggestion) {
		this(suggestion, 0);
	}
	public SuggestionValuePair(Suggestion suggestion, int value) {
		s = suggestion;
		val = value;
	}
	public Suggestion getSuggestion() {
		return s;
	}
	public int getValue() {
		return val;
	}
	public void increment(){
		val++;
	}
	/**
	 * 
	 * @created 09.02.2012
	 * @param s2
	 */
	public void updateDistance(Suggestion s2)
	{
		if (s.compareTo(s2) == -1)
			this.s = s2;
	}
}
