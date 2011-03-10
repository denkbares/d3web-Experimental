package de.knowwe.defi.table;

import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;

public class TableEntryType extends DefaultMarkupType {
		
	public TableEntryType(DefaultMarkup markup) {
		super(markup);
		this.setCustomRenderer(new DefaultMarkupRenderer<DefaultMarkupType>(false));
	}

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("Tabellendaten");
		// m.addContentType();
		m.addAnnotation("tableid", true);
	}

	public TableEntryType() {
		super(m);
		this.setCustomRenderer(new DefaultMarkupRenderer<DefaultMarkupType>(false));
	}
}
