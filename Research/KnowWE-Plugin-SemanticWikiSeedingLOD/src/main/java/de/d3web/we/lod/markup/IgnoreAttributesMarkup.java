package de.d3web.we.lod.markup;

import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;

public class IgnoreAttributesMarkup extends DefaultMarkupType {

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("IgnoreAttributes");
		m.addContentType(new IgnoreContentType());
	}

	public IgnoreAttributesMarkup() {
		super(m);
	}

}
