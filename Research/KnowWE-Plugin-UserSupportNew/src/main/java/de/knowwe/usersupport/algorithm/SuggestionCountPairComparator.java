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

import java.util.Comparator;


/**
 * 
 * To sort {@link SuggestionValuePairs} according to their value.
 * 
 * @author Johannes Dienst
 * @created 22.11.2011
 */
public class SuggestionCountPairComparator implements Comparator<SuggestionCountPair>
{

	@Override
	public int compare(SuggestionCountPair o1, SuggestionCountPair o2) {
		
		// compare the count first
		if (o1.getCount() > o2.getCount()) return -1;
		else if (o1.getCount() < o2.getCount()) return 1;
		
		// compare the distance if count is equal
		double d1 = o1.getSuggestion().getDistance();
		double d2 = o2.getSuggestion().getDistance();
		if (d1 > d2) return -1;
		else if (d1 < d2) return 1;
		else return 0;
	}

}
