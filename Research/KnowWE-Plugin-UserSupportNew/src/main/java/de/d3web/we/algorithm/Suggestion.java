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
 * Encapsulates a Suggestion found by a ApproximativeCorrectionProvider.
 * This is basically the same as the Suggestion used for CorrectionProviders
 * but the distance is stored as a double.
 * 
 * @author Johannes Dienst
 * @created 04.10.2011
 */
public class Suggestion implements Comparable<Suggestion>
{
	private final String suggestion;
	private final double distance;

	public Suggestion(String suggestion, double distance)
	{
		this.suggestion = suggestion;
		this.distance = distance;
	}

	/**
	 * Returns the suggested replacement string
	 * 
	 * @created 20.05.2011
	 * @return The suggested string
	 */
	public String getSuggestion()
	{
		return suggestion;
	}

	/**
	 * Returns the distance from the misspelled string, used for sorting
	 * 
	 * @created 20.05.2011
	 * @return distance
	 */
	public double getDistance()
	{
		return distance;
	}

	@Override
	public int compareTo(Suggestion other)
	{
		if ( (other.distance - distance) < 0) return -1;
		if ( (other.distance - distance) == 0) return 0;
		return 1;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((suggestion == null) ? 0 : suggestion.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Suggestion other = (Suggestion) obj;
		if (suggestion == null)
		{
			if (other.suggestion != null)
				return false;
		} else if (!suggestion.equals(other.suggestion))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return suggestion + " distance: " + distance;
	}
}
