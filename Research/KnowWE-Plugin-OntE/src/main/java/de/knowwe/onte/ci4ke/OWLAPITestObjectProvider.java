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
package de.knowwe.onte.ci4ke;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.IRI;

import de.d3web.testing.TestObjectContainer;
import de.d3web.testing.TestObjectProvider;
import de.knowwe.owlapi.OWLAPIConnector;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.05.2012
 */
public class OWLAPITestObjectProvider implements TestObjectProvider {

	@Override
	public <T> List<TestObjectContainer<T>> getTestObjects(Class<T> c, String id) {
		if (c == null) {
			Logger.getLogger(this.getClass().getName()).warning(
					"Class given to TestObjectProvider was 'null'");
			return Collections.emptyList();
		}
		if (!c.equals(OWLAPIConnector.class)) {
			return Collections.emptyList();
		}
		List<TestObjectContainer<T>> result = new ArrayList<TestObjectContainer<T>>();

		if (id == null || id.length() == 0) {
			OWLAPIConnector globalInstance = OWLAPIConnector.getGlobalInstance();
			result.add(new TestObjectContainer<T>(globalInstance.toString(), c.cast(globalInstance)));
		}
		else {
			OWLAPIConnector instance = OWLAPIConnector.getInstance(IRI.create(id));
			result.add(new TestObjectContainer<T>(instance.toString(), c.cast(instance)));
		}
		return result;
	}
}
