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

import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class SparqlRenderer extends KnowWEDomRenderer<SparqlType> {

	private static SparqlRenderer instance;

	public static SparqlRenderer getInstance() {
		if (instance == null) {
			instance = new SparqlRenderer();
		}
		return instance;
	}

	@Override
	public void render(KnowWEArticle article, Section sec, UserContext user,
			StringBuilder result) {

		ArrayList<Statement> l = new ArrayList<Statement>();

		String sparqlString = "";
		String section = sec.getOriginalText();
		section = section.replaceAll("<sparql2go>", "");
		section = section.replaceAll("</sparql2go>", "");
		sparqlString = section.replaceAll("\n", "");
		sparqlString = section.replaceAll("\r", "");

		boolean collapse = false;
		if (section.startsWith("collapse;")) {
			collapse = true;
			sparqlString = sparqlString.replaceFirst("collapse;", "");
			sparqlString = sparqlString.trim();			
		}

		try {
			if (collapse) {
				result.append("%%collapsebox \n<h4>Sparql-Query '"
						+ sparqlString + "'</h4>\n");
			}
			result.append(KnowWEUtils.maskHTML(Rdf2GoCore.getInstance()
					.renderedSparqlSelect(sparqlString)));
			if (collapse) {
				result.append("/%");
			}
		} catch (ModelRuntimeException e) {
			result.append(KnowWEUtils.maskHTML("<span class='warning'>"
					+ e.getMessage() + "</span>"));
		}
	}
}
