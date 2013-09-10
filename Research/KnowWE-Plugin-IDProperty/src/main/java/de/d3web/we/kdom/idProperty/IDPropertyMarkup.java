package de.d3web.we.kdom.idProperty;

import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.core.TextLine;

/**
 * This is a markup, that allows to attach some additional identifier to
 * (d3web-)objects It aims to keep these identifier unique and stable
 * 
 * example:
 * 
 * %%IDProperty
 * 
 * foo : foooID
 * 
 * weight : weightID
 * 
 * %
 * 
 * * foo and weight are objects (e.g., questions) and additional identifiers are
 * attached
 * 
 * * if the identfier are equal, an error message is thrown
 * 
 * * when one identifier is changed later, also a error message is thrown as
 * those identifiers arent meant to be changed (this error message, however,
 * doesnt occur with full parse)
 * 
 * @author Jochen
 * @created 13.12.2010
 */
public class IDPropertyMarkup extends DefaultMarkupType {

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("IDProperty");
		m.addContentType(new TextLine(new IDPropertyType()));
	}

	public IDPropertyMarkup() {
		super(m);
	}

}
