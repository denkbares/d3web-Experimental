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

package de.d3web.we.core.semantic.rdf2go.example;

import java.util.ArrayList;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Statement;
import org.openrdf.query.MalformedQueryException;

import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class SparqlRenderer extends KnowWEDomRenderer<AddStatementType> {

	private static SparqlRenderer instance;

	public static SparqlRenderer getInstance() {
		if (instance == null) {
			instance = new SparqlRenderer();
		}
		return instance;
	}

	@Override
	public void render(KnowWEArticle article, Section sec,
			KnowWEUserContext user, StringBuilder result) {

		ArrayList<Statement> l = new ArrayList<Statement>();

		String sparqlString = "";
		try {
			String section = sec.getOriginalText();
			section = section.replaceAll("%sparql%","");
			section = section.replaceAll("%/sparql%", "");
			sparqlString = section.replaceAll("\n", "");
			sparqlString = section.replaceAll("\r", "");

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			result.append(KnowWEUtils.maskHTML(Rdf2GoCore.getInstance().renderedSparqlSelect(
					sparqlString)));
		}
		catch (ModelRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
