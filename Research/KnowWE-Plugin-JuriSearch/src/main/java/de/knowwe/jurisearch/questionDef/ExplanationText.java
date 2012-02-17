package de.knowwe.jurisearch.questionDef;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.renderer.StyleRenderer;

public class ExplanationText extends AbstractType {

	public ExplanationText() {
		ConstraintSectionFinder csf = new ConstraintSectionFinder(
				new RegexSectionFinder("(Erläuterung:)?(.*)",
						Pattern.MULTILINE | Pattern.DOTALL, 2));
		csf.addConstraint(AtMostOneFindingConstraint.getInstance());
		this.setSectionFinder(csf);
		this.setRenderer(new StyleRenderer("color:pink;"));
	}
}