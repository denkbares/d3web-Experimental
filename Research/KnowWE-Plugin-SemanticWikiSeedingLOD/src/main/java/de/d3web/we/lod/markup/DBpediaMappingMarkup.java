package de.d3web.we.lod.markup;

import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;

public class DBpediaMappingMarkup extends DefaultMarkupType {

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("DBpediaMapping");
		m.addContentType(new NewLine(new DBpediaContentType()));
	}

	public DBpediaMappingMarkup() {
		super(m);
	}

}
