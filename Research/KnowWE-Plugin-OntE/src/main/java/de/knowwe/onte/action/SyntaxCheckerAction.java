/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.onte.action;

import java.io.IOException;
import java.util.Set;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.owlapi.query.OWLApiQueryEngine;

/**
 * Checks an input string for correct syntax and returns hint how the user can
 * correct the errors.
 *
 * @author Stefan Mark
 * @created 12.10.2011
 */
public class SyntaxCheckerAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		OWLApiQueryEngine engine = new OWLApiQueryEngine(shortFormProvider);
		StringBuilder html = new StringBuilder();

		try {
			String query = context.getParameter("query");
			engine.getParser().parseManchesterOWLsyntax(query);
			html.append("{ 'success' : true}");
		}
		catch (ParserException e) {
			prepareTipps(e);
			html.append("{ 'success' : false}");
		}
		context.getWriter().write(html.toString());
	}

	private void prepareTipps(ParserException e) {
		int column = e.getColumnNumber();
		int line = e.getLineNumber();
		String token = e.getCurrentToken();
		Set<String> expectedKeywords = e.getExpectedKeywords();
		String t = "";

		// check equality to terms in the terminology

		for (String string : expectedKeywords) {
			t += string + " ";
		}
		// some isExpected functions exists

		expectedKeywords.isEmpty();
		token.isEmpty();

	}
}
