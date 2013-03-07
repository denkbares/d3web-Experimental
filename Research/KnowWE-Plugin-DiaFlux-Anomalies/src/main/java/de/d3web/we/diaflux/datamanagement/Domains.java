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

/**
 * 
 * @author Reinhard Hatko
 * @created 13.11.2012
 */
public class Domains {

	private Domains() {
	}

	public static Domain add(Domain d1, Domain d2) {
		if (d1 instanceof EmptyDomain) {
			return d2;
		}
		else if (d2 instanceof EmptyDomain) {
			return d1;
		}
		else if (d1 instanceof FullDomain) {
			return d1;
		}
		else if (d2 instanceof FullDomain) {
			return d2;
		}
		// TODO unknown + X
		else if ((d1 instanceof KnownDomain && d2 instanceof UnknownDomain)
				|| (d1 instanceof UnknownDomain && d2 instanceof KnownDomain)) {
			return new FullDomain();
		}
		else {
			return d1.add(d2);
		}

	}

	/**
	 * 
	 * @created 13.11.2012
	 * @param oldDomain
	 * @param domain
	 * @return
	 */
	public static Domain intersect(Domain d1, Domain d2) {
		// empty dominates
		if (d1 instanceof EmptyDomain) {
			return d1;
		}
		else if (d2 instanceof EmptyDomain) {
			return d2;
		}
		// if one is full, return the other
		else if (d1 instanceof FullDomain) {
			return d2; // TODO addunknown
		}
		else if (d2 instanceof FullDomain) {
			return d1;// TODO addunknown
		} // unknown - something else is empty
		else if (d1 instanceof UnknownDomain || d2 instanceof UnknownDomain) {
			return new EmptyDomain();
		} // known is dominated
		else if (d1 instanceof KnownDomain) {
			return d2;
		}
		else if (d2 instanceof KnownDomain) {
			return d1;
		}// do normal intersection
		else {
			return d1.intersect(d2);

		}
	}

}
