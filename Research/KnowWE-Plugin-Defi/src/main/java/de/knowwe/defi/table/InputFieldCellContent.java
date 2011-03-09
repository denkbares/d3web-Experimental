package de.knowwe.defi.table;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.StringSectionFinder;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class InputFieldCellContent extends AbstractType {

	public InputFieldCellContent() {
		this.setSectionFinder(new StringSectionFinder("INPUT"));
		this.setCustomRenderer(new InputRenderer());
	}

	class InputRenderer extends KnowWEDomRenderer<InputFieldCellContent> {

		@Override
		public void render(KnowWEArticle article, Section<InputFieldCellContent> sec, UserContext user, StringBuilder string) {
			string.append(KnowWEUtils.maskHTML("<input type='text' value='test'\\>"));
			
		}
	}

}
