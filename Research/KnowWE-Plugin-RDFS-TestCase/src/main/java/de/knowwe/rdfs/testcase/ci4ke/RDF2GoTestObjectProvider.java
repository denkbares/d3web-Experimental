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
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import cc.denkbares.testing.TestObjectProvider;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.05.2012
 */
public class RDF2GoTestObjectProvider implements TestObjectProvider {

	@Override
	public <T> List<T> getTestObjects(Class<T> c, String id) {
		if (c == null) {
			Logger.getLogger(this.getClass()).warn("Class given to TestObjectProvider was 'null'");
			return Collections.emptyList();
		}
		if (!c.equals(Rdf2GoCore.class)) {
			return Collections.emptyList();
		}
		List<T> result = new ArrayList<T>();
		result.add(c.cast(Rdf2GoCore.getInstance()));
		return result;
	}

	@Override
	public <T> String getTestObjectName(T testObject) {
		return Rdf2GoCore.class.getSimpleName();
	}

}
