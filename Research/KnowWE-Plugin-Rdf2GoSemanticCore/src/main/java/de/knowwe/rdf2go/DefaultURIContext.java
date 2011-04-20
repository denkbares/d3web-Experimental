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

package de.knowwe.rdf2go;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.contexts.StringContext;

public class DefaultURIContext extends StringContext {

	public final static String CID = "URI_CONTEXT";

	private URI soluri;

	public DefaultURIContext(String sol) {
		setSubject(sol);
	}

	public DefaultURIContext() {
	}

	public void setSubject(String sol) {
		attributes.put("solution", sol);
	}

	public void setSubjectURI(URI solutionuri) {
		soluri = solutionuri;
	}

	public String getSubject() {
		return attributes.get("solution");
	}

	public URI getSolutionURI() {
		if (soluri == null) {
			soluri = Rdf2GoCore.getInstance().createlocalURI(getSubject());
		}
		return soluri;
	}

	@Override
	public String getCID() {
		return CID;
	}

	@Override
	public boolean isValidForSection(Section s) {
		return true;
	}
}