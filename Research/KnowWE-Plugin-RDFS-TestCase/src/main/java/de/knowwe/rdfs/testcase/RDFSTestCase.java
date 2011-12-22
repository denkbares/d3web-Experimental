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
package de.knowwe.rdfs.testcase;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Sebastian Furth
 * @created 20.12.2011
 */
public class RDFSTestCase {

	private final String name;
	private final String sparqlQuery;
	private final Set<Binding> expectedBindings = new HashSet<Binding>();

	public RDFSTestCase(String name, String sparqlQuery) {
		if (name == null) {
			throw new NullPointerException("The name of a test case can't be null!");
		}
		if (sparqlQuery == null) {
			throw new NullPointerException("The SPARQL-query of a test case can't be null!");
		}
		if (name.isEmpty()) {
			throw new IllegalArgumentException("The name of a test case can't be empty!");
		}
		if (sparqlQuery.isEmpty()) {
			throw new IllegalArgumentException("The SPARQL-query of a test case can't be empty!");
		}
		this.name = name;
		this.sparqlQuery = sparqlQuery;
	}

	public void addExpectedBinding(Binding binding) {
		if (binding == null) {
			throw new NullPointerException();
		}
		expectedBindings.add(binding);
	}

	public String getName() {
		return name;
	}

	public String getSparqlQuery() {
		return sparqlQuery;
	}

	public Collection<Binding> getExpectedBindings() {
		return Collections.unmodifiableSet(expectedBindings);
	}

}
