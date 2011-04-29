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
package de.knowwe.onte.test.util;

import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * Vocabulary used in the tests.
 * 
 * @author Sebastian Furth
 * @created Apr 1, 2011
 */
public class Vocabulary {

	private static final Rdf2GoCore core = Rdf2GoCore.getInstance();
	public static final String ARTICLENAME = "OntE-Test-Article";

	/* Used for "this" keyword */
	public static URI THIS = core.createlocalURI(ARTICLENAME);

	/* Individuals */
	public static URI ALEXANDER = core.createlocalURI("Alexander the Great");
	public static URI ALEXANDERLITTLE = core.createlocalURI("Alexander the Little");
	public static URI BABYLON = core.createlocalURI("Babylon");

	/* Classes */
	public static URI PERSON = core.createlocalURI("Person");
	public static URI LOCATION = core.createlocalURI("Location");
	public static URI CITY = core.createlocalURI("City");
	public static URI HISTORICALESSAY = core.createlocalURI("Historical Essay");
	public static URI CONCEPTOFHISTORY = core.createlocalURI("Concept of History");
	public static URI ISLAND = core.createlocalURI("Island");
	public static URI KING = core.createlocalURI("King");
	public static URI QUEEN = core.createlocalURI("Queen");

	/* Properties */
	public static URI DEATHPLACE = core.createlocalURI("deathPlace");
	public static URI ISRELATEDTO = core.createlocalURI("isRelatedTo");
	public static URI YEAROFDEATH = core.createlocalURI("yearOfDeath");
	public static URI DESCRIBES = core.createlocalURI("describes");

}
