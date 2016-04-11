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
package de.knowwe.compile.test.util;

import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * Vocabulary used in the tests.
 * 
 * @author Sebastian Furth
 * @created June 17, 2011
 */
public class Vocabulary {

	private static final Rdf2GoCore core = Rdf2GoCore.getInstance();
	public static final String ARTICLENAME = "Example";

	/* Subjects */
	public static URI PETE = core.createlocalURI("Pete");
	public static URI PETER = core.createlocalURI("Peter");
	public static URI JOCHEN = core.createlocalURI("Jochen");
	public static URI REINHARD = core.createlocalURI("Reinhard");
	public static URI SCHNURTZELPIEPER = core.createlocalURI("Schnurtzelpieper");

	/* Properties */
	public static URI ISTEIN = core.createlocalURI("istEin");
	public static URI WOHNTIN = core.createlocalURI("wohntIn");
	public static URI SUBCLASSOF = core.createlocalURI("subclassof");
	public static URI IS = core.createlocalURI("is");
	public static URI LIVESIN = core.createlocalURI("livesIn");

	/* Objects */
	public static URI ASSI = core.createlocalURI("Assi");
	public static URI ASSISTENT = core.createlocalURI("Assistent");
	public static URI WUERZBURG = core.createlocalURI("Wuerzburg");
	public static URI PERSON = core.createlocalURI("Person");
	public static URI DINGENSKIRCHEN = core.createlocalURI("Dingenskirchen");
	public static URI INDAHOUSE = core.createlocalURI("inDaHouse");

}
