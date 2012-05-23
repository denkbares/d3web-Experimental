/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.rdfs.testcase.ci4ke;

import java.util.ArrayList;
import java.util.List;

import cc.denkbares.testing.TestObjectProvider;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.05.2012
 */
public class RDF2GoTestObjectProvider implements TestObjectProvider<Rdf2GoCore> {

	@Override
	public List<Rdf2GoCore> getTestObject(Class<Rdf2GoCore> c, String id) {
		List<Rdf2GoCore> result = new ArrayList<Rdf2GoCore>();
		result.add(Rdf2GoCore.getInstance());
		return result;
	}

}
