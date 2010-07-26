/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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
package de.d3web.we.kdom.objects;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.we.d3webModule.D3webModule;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.renderer.FontColorRenderer;

/**
 * 
 * Abstract Type for questionnaire references
 * 
 * @author Jochen/Albrecht
 * @created 26.07.2010 
 */
public class QuestionnaireReference extends D3webTermReference<QContainer> {

	public QuestionnaireReference() {
		this.setCustomRenderer(FontColorRenderer.getRenderer(FontColorRenderer.COLOR5));
		this.setOrderSensitive(true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public QContainer getTermObjectFallback(KnowWEArticle article, Section<? extends
			TermReference<QContainer>> s) {

		if (s.get() instanceof QuestionnaireReference) {
			Section<QuestionnaireReference> sec = (Section<QuestionnaireReference>) s;
			String qcName = sec.get().getTermName(sec);

			KnowledgeBaseManagement mgn =
					D3webModule.getKnowledgeRepresentationHandler(
							article.getWeb())
							.getKBM(article.getTitle());

			QContainer q = mgn.findQContainer(qcName);
			return q;
		}
		return null;
	}

}
