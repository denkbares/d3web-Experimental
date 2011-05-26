/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

public class IRIEntityType {

	public enum IRIDeclarationType { CLASS, OBJECT_PROPERTY, DATATYPE_PROPERTY, NAMED_INDIVIDUAL, UNSPECIFIED};

	private final URI uri;
	private IRIDeclarationType type;

	public IRIEntityType(URI rui) {
		this.uri = rui;
		type = IRIDeclarationType.UNSPECIFIED;
	}

	public IRIDeclarationType getIRIDeclarationType() {
		return type;
	}

	public void setIRIDeclarationType(IRIDeclarationType type) {
		this.type = type;
	}

	public IRIEntityType(URI rui, IRIDeclarationType type) {
		this.uri = rui;
		this.type = type;
	}

	public URI getIRI() {
		return uri;
	}

	@Override
	public String toString() {
		return uri.toString()+ "("+type.toString()+")";

	}

}
