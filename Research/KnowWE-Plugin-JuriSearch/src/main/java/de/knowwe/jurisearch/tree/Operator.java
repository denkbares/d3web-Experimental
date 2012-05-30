package de.knowwe.jurisearch.tree;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.jurisearch.BracketRenderer;
import de.knowwe.jurisearch.EmbracedContent;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.sectionFinder.OneOfStringEnumFinder;

public class Operator extends AbstractType {

	public static final String AND = "und";
	public static final String OR = "oder";
	public static final Object NOT = "nein";
	public static final Object SCORE = "score";

	Operator() {
		SectionFinder sf = new OneOfStringEnumFinder(new String[] {
				EmbracedContent.BRACKET_OPEN_REGEX + OR + EmbracedContent.BRACKET_CLOSE_REGEX,
				EmbracedContent.BRACKET_OPEN_REGEX + AND + EmbracedContent.BRACKET_CLOSE_REGEX,
				EmbracedContent.BRACKET_OPEN_REGEX + SCORE + EmbracedContent.BRACKET_CLOSE_REGEX });
		ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
		csf.addConstraint(SingleChildConstraint.getInstance());
		csf.addConstraint(AtMostOneFindingConstraint.getInstance());

		this.setSectionFinder(csf);
		this.setRenderer(new BracketRenderer());

		this.addChildType(new EmbracedContent());
	}
}