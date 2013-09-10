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
package de.d3web.dependency;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;

/**
 * 
 * @author Reinhard Hatko
 * @created 12.11.2012
 */
public class Dependency implements Iterable<TerminologyObject> {
	
	public enum Type {
		Derivation, // values are directly used to calculate dependent object
		Precondition // values are not used to calculate dependent object, e.g.
						// condition in a rule
	}
	
	private final TerminologyObject object;
	private final Collection<TerminologyObject> dependencies;
	private final Type type;
	private final Class<? extends PSMethod> psMethod;
	private final String verbalization;
	
	public Dependency(TerminologyObject object, Collection<? extends TerminologyObject> dependencies, Type type, Class<? extends PSMethod> psMethod, String verbalization) {
		this.object = object;
		this.dependencies = new HashSet<TerminologyObject>(dependencies);
		this.type = type;
		this.psMethod = psMethod;
		this.verbalization = verbalization;
	}

	public TerminologyObject getObject() {
		return object;
	}

	public Collection<TerminologyObject> getBackwardObjects() {
		return dependencies;
	}

	public Class<? extends PSMethod> getPSMethod() {
		return psMethod;
	}

	public String getVerbalization() {
		return verbalization;
	}
	
	public Type getType() {
		return type;
	}

	@Override
	public Iterator<TerminologyObject> iterator() {
		return dependencies.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dependencies == null) ? 0 : dependencies.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Dependency other = (Dependency) obj;
		if (dependencies == null) {
			if (other.dependencies != null) return false;
		}
		else if (!dependencies.equals(other.dependencies)) return false;
		if (object == null) {
			if (other.object != null) return false;
		}
		else if (!object.equals(other.object)) return false;
		return true;
	}

}
