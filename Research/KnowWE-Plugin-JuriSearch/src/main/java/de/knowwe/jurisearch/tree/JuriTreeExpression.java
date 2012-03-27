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
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.user.UserContext;
import de.knowwe.jurisearch.Error;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.dashtree.DashTreeElementContent;
import de.knowwe.kdom.renderer.StyleRenderer;
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
	public static final Object DUMMY = "dummy";

	public static final String BRACKET_OPEN = "\\[";
	public static final String BRACKET_CLOSE = "\\]";

	public JuriTreeExpression() {
		this.setSectionFinder(new AllTextFinderTrimmed());

		this.addChildType(new DummyFlag());
		this.addChildType(new Operator());
		this.addChildType(new NegationFlag());

		this.addChildType(new QuestionIdentifier());

		this.addChildType(new Error());
		this.addSubtreeHandler(new JuriTreeHandler());
	}

	class Operator extends AbstractType {

		Operator() {
			SectionFinder sf = new OneOfStringEnumFinder(new String[] {
					BRACKET_OPEN + OR + BRACKET_CLOSE, BRACKET_OPEN + AND + BRACKET_CLOSE,
					BRACKET_OPEN + SCORE + BRACKET_CLOSE });
			ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
			csf.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(csf);
			this.setRenderer(new BracketRenderer());

			this.addChildType(new BracketContent());
		}
	}

	class NegationFlag extends AbstractType {

		NegationFlag() {
			SectionFinder sf = new RegexSectionFinder(BRACKET_OPEN + NOT + BRACKET_CLOSE);
			ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
			csf.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(csf);
			this.setRenderer(new BracketRenderer());

			this.addChildType(new BracketContent());
		}
	}

	class DummyFlag extends AbstractType {

		DummyFlag() {
			SectionFinder sf = new RegexSectionFinder(BRACKET_OPEN + DUMMY + BRACKET_CLOSE);
			ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
			csf.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(csf);
			this.setRenderer(new BracketRenderer());
			this.addChildType(new BracketContent());
		}
	}

	class BracketContent extends AbstractType {

		BracketContent() {
			this.setSectionFinder(new EmbracedContentFinder(BRACKET_OPEN.charAt(1),
					BRACKET_CLOSE.charAt(1), true));
			this.setRenderer(new StyleRenderer("font-weight:bold"));
		}
	}

	class BracketRenderer implements Renderer {

		@Override
		public void render(Section<?> section, UserContext user, StringBuilder string) {
			string.append("~");
			DelegateRenderer.getInstance().render(section, user, string);
		}

	}
}
