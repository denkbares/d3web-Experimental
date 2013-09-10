package de.knowwe.owlapi.query.pellet;

import de.knowwe.core.taghandler.TagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 *
 *
 * @author Stefan Mark
 * @created 08.12.2011
 */
public class PelletSparqlAsk extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;
	public static final String QUERY = "query";

	static {
		MARKUP = new DefaultMarkup("owlapi.ask");
		MARKUP.addAnnotation(QUERY, true);
	}

	public PelletSparqlAsk() {
		super(MARKUP);
		this.setRenderer(new PelletAskRenderer());
		this.setIgnorePackageCompile(true);
	}

	/**
	 * Appends a simple how to use message to the output if the
	 * {@link TagHandler} was used incorrectly.
	 *
	 * @created 20.09.2011
	 * @return String The how to use message
	 */
	public static String getDescription(UserContext user) {

		StringBuilder help = new StringBuilder();
		help.append("<dl>");

		help.append("<dt><strong>NAME</strong></dt>");
		help.append("<dd>&#37;&#37;owlapi.ask<br />@query=some kind of query<br />&#37;<br />"
				+ " - Prints results of a SPARQL ask query</dd>");

		help.append("<dt><strong>SYNOPSIS</strong></dt>");
		help.append("<dd>&#37;&#37;owlapi.ask"
				+ " - Prints results of a SPARQL ask query"
				+ "</dd>");

		help.append("<dt><strong>DESCRIPTION</strong></dt>");
		help.append("<dd>The PelletSparqlAsk Markup prints results of a SPARQL ask query.</dd>");

		help.append("</dl>");

		return help.toString();
	}

}
