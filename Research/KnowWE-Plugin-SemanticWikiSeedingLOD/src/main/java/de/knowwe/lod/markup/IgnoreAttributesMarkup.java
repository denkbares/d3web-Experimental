package de.knowwe.lod.markup;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class IgnoreAttributesMarkup extends DefaultMarkupType {

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("IgnoreAttributes");
		m.addContentType(new IgnoreContentType());
	}

	public IgnoreAttributesMarkup() {
		super(m);
		this.setRenderer(new DefaultMarkupRenderer<DefaultMarkupType>(false));
	}

}
