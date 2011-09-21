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
package de.knowwe.rdfs.test.util;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * Vocabulary used in the tests.
 * 
 * @author Sebastian Furth
 * @created June 17, 2011
 */
public class Vocabulary {

	private static final Rdf2GoCore core = Rdf2GoCore.getInstance();
	
	public static URI RDFS_SUBCLASSOF = new URIImpl("http://www.w3.org/2000/01/rdf-schema#subClassOf");
	public static URI RDFS_SUBPROPERTYOF = new URIImpl("http://www.w3.org/2000/01/rdf-schema#subPropertyOf");

	public static URI MAMMAL = core.createlocalURI("Mammal");
	public static URI PERSON = core.createlocalURI("Person");
	public static URI ANIMAL = core.createlocalURI("Animal");
	
	public static URI BOB = core.createlocalURI("Bob");
	public static URI JIM = core.createlocalURI("Jim");
	public static URI KNOWS = core.createlocalURI("knows");
	public static URI ISFRIENDOF = core.createlocalURI("isFriendOf");


}
