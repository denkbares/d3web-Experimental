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
package de.d3web.we.finding;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.object.D3webTermReference;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * 
 * Type for question references
 * 
 * @author Jochen/Albrecht
 * @created 26.07.2010
 */
public class ApproximateQuestionReference extends D3webTermReference<Question> {

	public ApproximateQuestionReference() {
		super(Question.class);
		this.setCustomRenderer(StyleRenderer.Question);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Question getTermObjectFallback(KnowWEArticle article, Section<?
			extends TermReference<Question>> s) {

		if (s.get() instanceof ApproximateQuestionReference) {
			Section<ApproximateQuestionReference> sec = (Section<ApproximateQuestionReference>) s;
			String questionName = sec.get().getTermIdentifier(sec);

			KnowledgeBase kb =
					D3webModule.getKnowledgeRepresentationHandler(
							article.getWeb()).getKB(article.getTitle());

			Question question = kb.getManager().searchQuestion(questionName);
			return question;
		}
		return null;
	}

}
