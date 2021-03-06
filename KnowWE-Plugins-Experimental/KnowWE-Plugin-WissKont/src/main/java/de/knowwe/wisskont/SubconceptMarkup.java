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
package de.knowwe.wisskont;

import org.ontoware.rdf2go.model.node.URI;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 27.11.2012
 */
public class SubconceptMarkup extends RelationMarkup {

	public static final String KEY = "Unterbegriffe";
	public static final String SUBCONCEPT_PROPERTY = "unterkonzept";

	/**
	 * @param key
	 */
	public SubconceptMarkup() {
		super(KEY);
	}

	@Override
	public String getName() {
		return "Unterbegriffe";
	}

	@Override
	public URI getRelationURI() {
		return createURI(SUBCONCEPT_PROPERTY);
	}

	@Override
	public boolean isInverseDir() {
		return true;
	}

	@Override
	public String getDerivationMessagePrefix() {
		// not required
		return null;
	}

}
