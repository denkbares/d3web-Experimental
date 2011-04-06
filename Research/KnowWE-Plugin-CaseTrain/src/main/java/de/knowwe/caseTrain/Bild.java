package de.knowwe.caseTrain;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class Bild extends AbstractType {

	public static String KEY_BILD = "Bild:";
	
	private static String REGEX = "\\{" + KEY_BILD + "(.*?)\\}";

	public Bild() {

		this.setSectionFinder(new RegexSectionFinder(REGEX));
		this.addChildType(new BildContent());
		
		this.setCustomRenderer(new KnowWEDomRenderer<Bild>() {

			@Override
			public void render(KnowWEArticle article, Section<Bild> sec, UserContext user, StringBuilder string) {
				Section<BildContent> bildURL = Sections.findChildOfType(sec,
						BildContent.class);
				string.append(KnowWEUtils.maskHTML("<img height='70' src='"));
				string.append("attach/" + sec.getArticle().getTitle() + "/");
				string.append(bildURL.getOriginalText().trim());
				string.append(KnowWEUtils.maskHTML("'></img>"));
				
			}
		});
		
	}
	
	class BildContent extends AbstractType{
		
		public BildContent() {
			this.setSectionFinder(new RegexSectionFinder(REGEX, 0, 1));
		}
	}


	
}
