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
package de.knowwe.usersupport.algorithm;


/**
 * Stores a Suggestion with a count-Value to
 * count how often a Suggestion was proposed
 * by a bunch of MatchingAlgorithms
 * 
 * @author Johannes Dienst
 * @created 22.11.2011
 */
public class SuggestionCountPair
{
	private Suggestion s;
	private int count;

	public SuggestionCountPair(Suggestion suggestion)
	{
		this(suggestion, 0);
	}
	public SuggestionCountPair(Suggestion suggestion, int value)
	{
		s = suggestion;
		count = value;
	}
	public Suggestion getSuggestion()
	{
		return s;
	}
	public int getCount()
	{
		return count;
	}
	public void increment()
	{
		count++;
	}

	/**
	 * When the s2 is the same Suggestion as
	 * stored in this container. The distance
	 * value is set to the higher of the two.
	 * 
	 * @created 09.02.2012
	 * @param s2
	 */
	public void updateDistance(Suggestion s2)
	{
		if (s.compareTo(s2) == -1)
			this.s = s2;
	}
	
	@Override
	public String toString()
	{
		return s.toString() + " Count: " + count;
	}
}
