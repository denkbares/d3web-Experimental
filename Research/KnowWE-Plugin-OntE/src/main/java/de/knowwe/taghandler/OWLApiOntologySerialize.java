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
package de.knowwe.taghandler;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.taghandler.TagHandler;
import de.knowwe.core.user.UserContext;

/**
 * The {@link OWLApiOntologySerialize} serializes a local ontology to a given
 * format.
 *
 * @author Stefan Mark
 * @created 27.09.2011
 */
public class OWLApiOntologySerialize extends AbstractHTMLTagHandler {

	private static final String NAME = "owlapi.serialize";

	public OWLApiOntologySerialize() {
		super(NAME);
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {

		StringBuilder html = new StringBuilder();
		html.append("<pre style=\"background: none repeat scroll 0 0 #F5F5F5;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");

		if (parameters.get("help") != null) {
			html.append(getDescription(user));
			html.append("</pre>");
			return html.toString();
		}

		String syntax = parameters.get("syntax");

		if (syntax != null) {
			syntax = syntax.toLowerCase();
			try {
				String serialized = OWLApiTagHandlerUtil.getSerializedOntology(syntax);
				html.append(serialized);
			}
			catch (OWLOntologyStorageException e) {
				html.append(getDescription(user));
			}
		}
		else {
			html.append(getDescription(user));
		}
		html.append("</pre>");
		return html.toString();
	}

	/**
	 * Returns an example usage string
	 *
	 * @return A example usage string
	 */
	@Override
	public String getExampleString() {
		StringBuilder example = new StringBuilder();
		example.append("[{KnowWEPlugin " + NAME + " [");
		example.append(", help ");
		example.append(", syntax=[owl|rdf|ms|turtle] ]");
		example.append("}])\n ");
		example.append("The parameters in [ ] are optional.");
		return example.toString();
	}

	/**
	 * Appends a simple how to use message to the output if the
	 * {@link TagHandler} was used incorrectly.
	 *
	 * @created 20.09.2011
	 * @return String The how to use message
	 */
	@Override
	public String getDescription(UserContext user) {

		StringBuilder help = new StringBuilder();
		help.append("<dl>");

		help.append("<dt><strong>NAME</strong></dt>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ "}] - prints the local ontology in the desired serialization form.</dd>");

		help.append("<dt><strong>SYNOPSIS</strong></dt>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ "}] - prints the local ontology in the desired serialization form.</dd>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ " , help}] - Show a how to use message for this taghandler.</dd>");

		help.append("<dt><strong>DESCRIPTION</strong></dt>");
		help.append("<dd>The OWLApiOntologySerialize prints the local ontology in the desired serialization form. Possible forms are: RDF/XML, OWL/XML, Manchester OWL syntax and Turtle.</dd>");

		help.append("</dl>");

		return help.toString();
	}
}
