/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.rdfs.testcase;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Sebastian Furth
 * @created 20.12.2011
 */
public class Binding {

	private final Set<String> uris = new HashSet<String>();

	public void addURI(String uri) {
		if (uri == null) {
			throw new NullPointerException("An URI can't be null!");
		}
		if (uri.isEmpty()) {
			throw new IllegalArgumentException("An URI can't be empty!");
		}
		uris.add(uri);
	}

	public Collection<String> getURIs() {
		return Collections.unmodifiableCollection(uris);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String uri : uris) {
			builder.append(uri);
			builder.append(", ");
		}
		builder.delete(builder.length() - 2, builder.length());
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uris == null) ? 0 : uris.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Binding other = (Binding) obj;
		if (uris == null) {
			if (other.uris != null) {
				return false;
			}
		}
		else if (!uris.equals(other.uris)) {
			return false;
		}
		return true;
	}

}
