/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.jurisearch.tree;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.jurisearch.BracketContent;
import de.knowwe.jurisearch.BracketRenderer;
import de.knowwe.jurisearch.Error;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.dashtree.DashTreeElementContent;
import de.knowwe.kdom.sectionFinder.EmbracedContentFinder;
import de.knowwe.kdom.sectionFinder.OneOfStringEnumFinder;

/**
 * 
 * @author boehler
 * @created 18.01.2012
 */
public class JuriTreeExpression extends DashTreeElementContent {

	public static final String AND = "und";
	public static final String OR = "oder";
	public static final Object NOT = "nein";
	public static final Object SCORE = "score";

	public JuriTreeExpression() {
		this.setSectionFinder(new AllTextFinderTrimmed());

		this.addChildType(new DummyExpression());
		this.addChildType(new Operator());
		this.addChildType(new AnswerBracket());
		this.addChildType(new QuestionIdentifier());

		this.addChildType(new Error());
		this.addSubtreeHandler(new JuriTreeHandler());
	}

	class Operator extends AbstractType {

		Operator() {
			SectionFinder sf = new OneOfStringEnumFinder(new String[] {
					BracketContent.BRACKET_OPEN + OR + BracketContent.BRACKET_CLOSE,
					BracketContent.BRACKET_OPEN + AND + BracketContent.BRACKET_CLOSE,
					BracketContent.BRACKET_OPEN + SCORE + BracketContent.BRACKET_CLOSE });
			ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
			csf.addConstraint(SingleChildConstraint.getInstance());
			csf.addConstraint(AtMostOneFindingConstraint.getInstance());

			this.setSectionFinder(csf);
			this.setRenderer(new BracketRenderer());

			this.addChildType(new BracketContent());
		}
	}

	// class NegationFlag extends AbstractType {
	//
	// NegationFlag() {
	// SectionFinder sf = new RegexSectionFinder(BracketContent.BRACKET_OPEN +
	// NOT
	// + BracketContent.BRACKET_CLOSE);
	// ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
	// csf.addConstraint(SingleChildConstraint.getInstance());
	// csf.addConstraint(AtMostOneFindingConstraint.getInstance());
	//
	// this.setSectionFinder(csf);
	// this.setRenderer(new BracketRenderer());
	//
	// this.addChildType(new BracketContent());
	// }
	// }

	// class DummyFlag extends AbstractType {
	//
	// DummyFlag() {
	// SectionFinder sf = new RegexSectionFinder(BracketContent.BRACKET_OPEN
	// + DummyExpression.DUMMY + BracketContent.BRACKET_CLOSE);
	// ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
	// csf.addConstraint(SingleChildConstraint.getInstance());
	// csf.addConstraint(AtMostOneFindingConstraint.getInstance());
	//
	// this.setSectionFinder(csf);
	// this.setRenderer(new BracketRenderer());
	// this.addChildType(new BracketContent());
	// }
	// }

	class AnswerBracket extends AbstractType {

		AnswerBracket() {
			SectionFinder sf = new EmbracedContentFinder(BracketContent.BRACKET_OPEN_CHAR,
					BracketContent.BRACKET_CLOSE_CHAR);
			ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
			csf.addConstraint(SingleChildConstraint.getInstance());
			csf.addConstraint(AtMostOneFindingConstraint.getInstance());

			this.setSectionFinder(csf);
			this.setRenderer(new BracketRenderer());
			this.addChildType(new AnswerIdentifier());
		}
	}
}
