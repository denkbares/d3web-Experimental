package de.knowwe.lod.markup;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class MappingMarkup extends DefaultMarkupType {

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("Mapping");
		m.addContentType(new NewLine(new MappingContentType()));
	}

	public MappingMarkup() {
		super(m);
		this.setRenderer(new DefaultMarkupRenderer<DefaultMarkupType>(false));
	}

}
