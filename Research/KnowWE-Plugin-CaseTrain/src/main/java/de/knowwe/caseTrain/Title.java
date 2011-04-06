package de.knowwe.caseTrain;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.ExactlyOneFindingConstraint;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class Title extends AbstractType {

	public Title() {
		ConstraintSectionFinder cf = new ConstraintSectionFinder(new LineSectionFinder());
		cf.addConstraint(ExactlyOneFindingConstraint.getInstance());
		this.setSectionFinder(cf);

		this.setCustomRenderer(new KnowWEDomRenderer<Title>() {

			@Override
			public void render(KnowWEArticle article, Section<Title> sec, UserContext user, StringBuilder string) {
				if (sec.getOriginalText().trim().equals("")) return; // if empty
																		// do
																		// nothing
				Section<? extends Type> father = sec.getFather().getFather();
				String classPrefix = father.get().getClass().getSimpleName();

				string.append(KnowWEUtils.maskHTML("<div class='" +
							"Titel"
						+ "'>"));
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</div>"));

			}
		});

	}

}
