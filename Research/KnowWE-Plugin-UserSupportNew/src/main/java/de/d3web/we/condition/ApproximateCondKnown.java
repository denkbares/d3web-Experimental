/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.d3web.we.condition;

import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.TerminalCondition;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.we.finding.ApproximateQuestionReference;
import de.d3web.we.kdom.condition.D3webCondition;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.report.KDOMError;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.report.message.NoSuchObjectError;

/**
 * Implements the CondKnown Condition to be used as child type in
 * {@link TerminalCondition}
 *
 * syntax: KNOWN[<questionID>] / BEKANNT[<questionID>]
 *
 * @author Jochen
 *
 */
public class ApproximateCondKnown extends D3webCondition<ApproximateCondKnown> {

	protected String[] KEYWORDS = {
			"KNOWN", "BEKANNT" };

	@Override
	protected void init() {

		this.sectionFinder = new CondKnownFinder();
		this.setCustomRenderer(new StyleRenderer(StyleRenderer.KEYWORDS.getCssStyle()) {

			@Override
			protected void renderContent(KnowWEArticle article, Section section, UserContext user, StringBuilder string) {
				StringBuilder buffer = new StringBuilder();
				super.renderContent(article, section, user, buffer);
				string.append(buffer.toString());
			}
		});

		ApproximateQuestionReference question = new ApproximateQuestionReference();
		question.setSectionFinder(new SectionFinder() {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section father, Type type) {
				return SectionFinderResult.createSingleItemList(new SectionFinderResult(
						text.indexOf('[') + 1, text.indexOf(']')));
			}
		});
		this.addChildType(question);
	}

	@Override
	protected Condition createCondition(KnowWEArticle article, de.knowwe.core.kdom.parsing.Section<ApproximateCondKnown> section) {
		Section<ApproximateQuestionReference> qRef = Sections.findSuccessor(section, ApproximateQuestionReference.class);
		if (qRef != null) {
			Question q = qRef.get().getTermObject(article, qRef);

			if (q != null) {
				return createCond(q);
			}
			else {
				KDOMError msg = new NoSuchObjectError(qRef.getOriginalText());
				KDOMReportMessage.storeSingleError(article, section, getClass(), msg);
				return null;
			}
		}
		KDOMError msg = new NoSuchObjectError("");
		KDOMReportMessage.storeSingleError(article, section, getClass(), msg);
		return null;
	}

	protected Condition createCond(Question q) {
		return new de.d3web.core.inference.condition.CondKnown(q);
	}

	private class CondKnownFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section father, Type type) {

			for (String key : KEYWORDS) {
				if (text.trim().startsWith(key + "[") && text.trim().endsWith("]")) return new AllTextFinderTrimmed().lookForSections(
						text,
						father, type);
			}
			return null;
		}

	}

}
