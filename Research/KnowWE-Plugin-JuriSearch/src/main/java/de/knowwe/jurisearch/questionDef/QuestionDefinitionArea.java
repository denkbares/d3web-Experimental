/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.jurisearch.questionDef;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.jurisearch.BoxRenderer;
import de.knowwe.jurisearch.PreDecoratingRenderer;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;

public class QuestionDefinitionArea extends AbstractType {

	private static final String LINEBREAK_REGEX = "\\r?\\n";
	private static final String Q_AREA_REGEX = "^\\s*?FRAGE\\s*?" + LINEBREAK_REGEX
			+ "(.*?)"
			+ LINEBREAK_REGEX
			+ LINEBREAK_REGEX + LINEBREAK_REGEX;

	public QuestionDefinitionArea() {
		this.setSectionFinder(new RegexSectionFinder(Q_AREA_REGEX,
				Pattern.MULTILINE | Pattern.DOTALL, 0));
		this.setCustomRenderer(new PreDecoratingRenderer(new
				BoxRenderer("defaultMarkupFrame")));
		this.addChildType(new QuestionDefinitionContent());
	}

	class QuestionDefinitionContent extends AbstractType {
		public QuestionDefinitionContent() {
			this.setSectionFinder(new RegexSectionFinder(Q_AREA_REGEX,
					Pattern.MULTILINE | Pattern.DOTALL, 1));
			this.setCustomRenderer(new BoxRenderer("defaultMarkup"));
			this.addChildType(new QuestionTermDefinitionLine());
			this.addChildType(new ExplanationTextArea());
		}
	}

	class QuestionTermDefinitionLine extends AbstractType {
		public QuestionTermDefinitionLine() {
			LineSectionFinder lineSectionFinder = new LineSectionFinder();
			ConstraintSectionFinder csf = new ConstraintSectionFinder(lineSectionFinder);
			// csf.addConstraint(SingleChildConstraint.getInstance());
			csf.addConstraint(AtMostOneFindingConstraint.getInstance());
			this.setSectionFinder(csf);
			this.setCustomRenderer(new StyleRenderer("color:green;"));
			// this.addChildType(new QuestionTypeDeclaration());
		}

	}

	class ExplanationTextArea extends AbstractType {
		public ExplanationTextArea() {
			this.setSectionFinder(new AllTextFinderTrimmed());
			this.addChildType(new ExplanationText());
		}
	}

	class ExplanationText extends AbstractType {
		public ExplanationText() {
			ConstraintSectionFinder csf = new ConstraintSectionFinder(
					new RegexSectionFinder("(Erläuterung:)?(.*)",
							Pattern.MULTILINE | Pattern.DOTALL, 2));
			csf.addConstraint(AtMostOneFindingConstraint.getInstance());
			this.setSectionFinder(csf);
			this.setCustomRenderer(new StyleRenderer("color:pink;"));
		}
	}

}
