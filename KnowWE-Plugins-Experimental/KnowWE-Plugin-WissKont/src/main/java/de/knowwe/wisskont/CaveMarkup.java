/*
 * Copyright (C) 2013 denkbares GmbH
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
 * @created 25.06.2013
 */
public class CaveMarkup extends RelationMarkup {

	public static final String KEY = "cave";

	/**
	 * @param key
	 */
	public CaveMarkup() {
		super(KEY);
	}

	@Override
	public String getName() {
		return "Cave-Relationen";
	}

	@Override
	public URI getRelationURI() {
		return createURI(KEY);
	}

	@Override
	public String getDerivationMessagePrefix() {
		return "VORSICHT ";
	}

}
