package de.knowwe.defi;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class Blank extends AbstractType {

	public Blank() {
		this.setSectionFinder(new RegexSectionFinder("---"));
		this.setCustomRenderer(new KnowWEDomRenderer<Blank>() {

			@Override
			public void render(KnowWEArticle article, Section<Blank> sec, UserContext user, StringBuilder string) {
				string.append(KnowWEUtils.maskHTML(("&nbsp;")));

			}
		});
	}
}
