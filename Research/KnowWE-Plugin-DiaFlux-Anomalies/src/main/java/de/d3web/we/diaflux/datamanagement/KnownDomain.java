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
 * @created 13.11.2012
 */
public class KnownDomain implements Domain {


	@Override
	public Domain add(Domain domain) {
		return Domains.add(this, domain);
	}

	@Override
	public Domain negate() {
		return new UnknownDomain();
	}

	@Override
	public boolean contains(Domain domain) {
		return !(domain instanceof UnknownDomain);
	}

	@Override
	public boolean intersects(Domain domain) {
		return !(domain instanceof UnknownDomain);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Domain intersect(Domain domain) {
		return null;
	}

}
