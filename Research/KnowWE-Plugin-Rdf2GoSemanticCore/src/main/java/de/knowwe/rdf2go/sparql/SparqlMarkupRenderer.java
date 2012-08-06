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
package de.knowwe.rdf2go.sparql;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;

public class SparqlMarkupRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder result) {

		String sparqlString = sec.getText();
		sparqlString = sparqlString.trim();
		sparqlString = sparqlString.replaceAll("\n", "");
		sparqlString = sparqlString.replaceAll("\r", "");

		try {
			if (sparqlString.toLowerCase().startsWith("construct")) {
				result.append(Strings.maskHTML("<tt>" + sec.getText() + "</tt>"));
			}
			else {
				Section<SparqlMarkupType> markupSection = Sections.findAncestorOfType(sec,
						SparqlMarkupType.class);
				String rawOutput = DefaultMarkupType.getAnnotation(markupSection,
						SparqlMarkupType.RAW_OUTPUT);
				QueryResultTable resultSet = Rdf2GoCore.getInstance().sparqlSelect(
						sparqlString);
				result.append(SparqlResultRenderer.getInstance().renderQueryResult(resultSet,
						rawOutput != null && rawOutput.equals("true")));
			}

		}
		catch (ModelRuntimeException e) {
			result.append(Strings.maskHTML("<span class='warning'>"
					+ e.getMessage() + "</span>"));
		}
	}

}