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

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.we.object.QASetDefinition;
import de.d3web.we.object.QuestionDefinition;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.jurisearch.BoxRenderer;
import de.knowwe.jurisearch.BracketRenderer;
import de.knowwe.jurisearch.EmbracedContent;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;
import de.knowwe.usersupport.util.UserSupportUtil;

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
		// this.setRenderer(new PreDecoratingRenderer(new
		// BoxRenderer("defaultMarkupFrame")));
		this.setRenderer(new JuriInstantEditRenderer());
		this.addChildType(new QuestionDefinitionContent());
		this.addSubtreeHandler(Priority.PRECOMPILE_MIDDLE,
				new RegisterPackageSubtreeHandler());
	}

	class JuriInstantEditRenderer implements Renderer {

		public JuriInstantEditRenderer() {

		}

		@Override
		public void render(Section<?> section, UserContext user, StringBuilder string)
		{
			string.append(KnowWEUtils.maskHTML("<div id=\"" + section.getID() + "\">"));
			string.append(KnowWEUtils.maskHTML("<div class=\"defaultMarkupFrame\">"));

			string.append(KnowWEUtils.maskHTML("<div class=\"jurisearch-instantedit\">"
					// + getFrameName(sec)
					// + getEditorIcon(sec)
					+ UserSupportUtil.renderTools(section, user)
					// + getLink(sec)
					+ "</div>"));
			string.append(KnowWEUtils.maskHTML("<pre>"));
			DelegateRenderer.getInstance().render(section, user, string);
			string.append(KnowWEUtils.maskHTML("</pre>"));
			string.append(KnowWEUtils.maskHTML("</div>"));
			string.append(KnowWEUtils.maskHTML("</div>"));
		}
	}

	class RegisterPackageSubtreeHandler extends SubtreeHandler<QuestionDefinitionArea> {

		public RegisterPackageSubtreeHandler() {
			super(true);
		}

		@Override
		public Collection<Message> create(Article article, Section<QuestionDefinitionArea> markupSection) {

			Environment.getInstance().getPackageManager(article.getWeb()).addSectionToPackage(
					markupSection, "default");
			return new ArrayList<Message>();
		}

		@Override
		public void destroy(Article article, Section markupSection) {
			// unregister section in the package manager
			// TODO: refactor this to somewhere else
			if (!markupSection.get().isIgnoringPackageCompile()) {
				Environment.getInstance().getPackageManager(article.getWeb()).removeSectionFromAllPackages(
						markupSection);
			}
		}
	}

	class QuestionDefinitionContent extends AbstractType {

		public QuestionDefinitionContent() {
			this.setSectionFinder(new RegexSectionFinder(Q_AREA_REGEX,
					Pattern.MULTILINE | Pattern.DOTALL, 1));
			this.setRenderer(new BoxRenderer("markupText"));

			// this.setOrderSensitive(true);
			this.addChildType(new QuestionTermDefinitionLine());

			this.addChildType(new AnswerDefinitionLine());
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

			// this.addChildType(new QuestionTypeDeclaration());
			this.addChildType(new ShortAnswerDefinitionBracket());

			QASetDefinition<Question> qRef = new QAreaQuestionDefinition();
			ConstraintSectionFinder qcsf = new ConstraintSectionFinder(new AllTextFinderTrimmed());
			csf.addConstraint(SingleChildConstraint.getInstance());
			csf.addConstraint(AtMostOneFindingConstraint.getInstance());
			qRef.setSectionFinder(qcsf);
			this.addChildType(qRef);
		}

		public final class QAreaQuestionDefinition extends QuestionDefinition {

			public QAreaQuestionDefinition() {
				this.setRenderer(new StyleRenderer("color:green;"));

				// this.addSubtreeHandler(Priority.HIGHER, new
				// CreateYNMQuestionHandler());
				// this.setRenderer(StyleRenderer.Question);
				// this.setOrderSensitive(true);
			}

			@Override
			public QuestionType getQuestionType(Section<QuestionDefinition> s) {
				return QuestionDefinition.QuestionType.OC;
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

	class AnswerDefinitionLine extends AbstractType {

		public AnswerDefinitionLine() {
			this.setSectionFinder(new RegexSectionFinder(EmbracedContent.BRACKET_OPEN_REGEX
					+ "(.)+"
					+ EmbracedContent.BRACKET_CLOSE_REGEX));
			this.addChildType(new JuriAnswerDefinition());
			this.setRenderer(new BracketRenderer());
		}
	}

	class ShortAnswerDefinitionBracket extends AbstractType {

		ShortAnswerDefinitionBracket() {
			this.setSectionFinder(new RegexSectionFinder(EmbracedContent.BRACKET_OPEN_REGEX
					+ JuriAnswerDefinitionShort.YES + JuriAnswerDefinitionShort.NO
					+ JuriAnswerDefinitionShort.MAYBE
					+ EmbracedContent.BRACKET_CLOSE_REGEX));
			this.addChildType(new JuriAnswerDefinitionShort());
			this.setRenderer(new BracketRenderer());
		}
	}
}
