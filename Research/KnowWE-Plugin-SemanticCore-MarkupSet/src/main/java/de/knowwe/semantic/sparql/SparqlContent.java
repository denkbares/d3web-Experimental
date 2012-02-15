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

/**
 *
 */
package de.knowwe.semantic.sparql;

import java.util.HashMap;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.xml.XMLContent;

/**
 * @author kazamatzuri
 * 
 */
public class SparqlContent extends XMLContent {

	private final HashMap<Section<SparqlContent>, String> queries;

	public void addQuery(Section<SparqlContent> s, String str) {
		queries.put(s, str);
	}

	public HashMap<Section<SparqlContent>, String> getQueries() {
		return queries;
	}

	public SparqlContent() {
		queries = new HashMap<Section<SparqlContent>, String>();
		this.setRenderer(SparqlDelegateRenderer.getInstance());
	}

	// public String getQuery() {
	// return queries.get(this);
	// }
}
