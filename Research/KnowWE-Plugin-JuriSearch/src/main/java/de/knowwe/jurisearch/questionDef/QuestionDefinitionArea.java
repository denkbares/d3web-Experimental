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
package de.knowwe.jurisearch.questionDef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import de.d3web.we.kdom.questionTree.QuestionTypeDeclaration;
import de.d3web.we.object.QASetDefinition;
import de.d3web.we.object.QuestionDefinition;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.jurisearch.BoxRenderer;
import de.knowwe.jurisearch.PreDecoratingRenderer;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;

public class QuestionDefinitionArea extends AbstractType {

	private static final String LINEBREAK_REGEX = "\\r?\\n";
	private static final String Q_AREA_REGEX = "^\\s*?FRAGE\\s*?" + LINEBREAK_REGEX
			+ "(.*?)"
			+ LINEBREAK_REGEX
			+ LINEBREAK_REGEX + LINEBREAK_REGEX;

	public QuestionDefinitionArea() {
		super(null);
		this.setSectionFinder(new RegexSectionFinder(Q_AREA_REGEX,
				Pattern.MULTILINE | Pattern.DOTALL, 0));
		this.setRenderer(new PreDecoratingRenderer(new
				BoxRenderer("defaultMarkupFrame")));
		this.addChildType(new QuestionDefinitionContent());
		this.addSubtreeHandler(Priority.PRECOMPILE_HIGH,
				new RegisterPackageSubtreeHandler());
	}

	class RegisterPackageSubtreeHandler extends SubtreeHandler<QuestionDefinitionArea> {

		public RegisterPackageSubtreeHandler() {
			super(true);
		}

		@Override
		public Collection<Message> create(KnowWEArticle article, Section<QuestionDefinitionArea> markupSection) {

			KnowWEEnvironment.getInstance().getPackageManager(
					article.getWeb()).addSectionToPackage(
					markupSection, "default");
			return new ArrayList<Message>();
		}

		@Override
		public void destroy(KnowWEArticle article, Section markupSection) {
			// unregister section in the package manager
			// TODO: refactor this to somewhere else
			if (!markupSection.get().isIgnoringPackageCompile()) {
				KnowWEEnvironment.getInstance().getPackageManager(article.getWeb()).removeSectionFromAllPackages(
						markupSection);
			}
		}
	}

	class QuestionDefinitionContent extends AbstractType {

		public QuestionDefinitionContent() {
			this.setSectionFinder(new RegexSectionFinder(Q_AREA_REGEX,
					Pattern.MULTILINE | Pattern.DOTALL, 1));
			this.setRenderer(new BoxRenderer("markupText"));
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

			this.setRenderer(new StyleRenderer("color:green;"));

			this.addChildType(new QuestionTypeDeclaration());

			QuestionDefinition qRef = new QAreaQuestionDefinition();
			ConstraintSectionFinder qcsf = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			csf.addConstraint(SingleChildConstraint.getInstance());
			csf.addConstraint(AtMostOneFindingConstraint.getInstance());
			qRef.setSectionFinder(qcsf);
			this.addChildType(qRef);
		}

		private final class QAreaQuestionDefinition extends QuestionDefinition {

			@Override
			public QuestionType getQuestionType(Section<QuestionDefinition> s) {
				return QuestionTypeDeclaration
						.getQuestionType(Sections.findSuccessor(
								s.getFather(), QuestionTypeDeclaration.class));
			}

			@Override
			public int getPosition(Section<QuestionDefinition> s) {
				return 0;
			}

			@Override
			public Section<? extends QASetDefinition> getParentQASetSection(Section<? extends QuestionDefinition> qdef) {
				return null;
			}
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
			this.setRenderer(new StyleRenderer("color:pink;"));
		}
	}

}
