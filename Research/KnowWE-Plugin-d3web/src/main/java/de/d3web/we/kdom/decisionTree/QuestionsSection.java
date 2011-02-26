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

package de.d3web.we.kdom.decisionTree;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import de.d3web.KnOfficeParser.SingleKBMIDObjectManager;
import de.d3web.KnOfficeParser.decisiontree.D3DTBuilder;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.report.Message;
import de.d3web.report.Report;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.kopic.AbstractKopicSection;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.utils.KnowWEUtils;

public class QuestionsSection extends AbstractKopicSection {

	public static final String TAG = "Questions-section";

	public QuestionsSection() {
		super(TAG);
	}

	@Override
	protected void init() {
		childrenTypes.add(new QuestionsSectionContent());
		this.setOrderSensitive(true);
		this.addSubtreeHandler(Priority.HIGHER, new QuestionsSectionSubTreeHandler());

	}

	private class QuestionsSectionSubTreeHandler extends D3webSubtreeHandler {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section s) {

			KnowledgeBaseUtils kbm = getKBM(article);

			Section content = ((AbstractKopicSection) s.getObjectType()).getContentChild(s);
			if (content != null) {

				List<de.d3web.report.Message> messages = D3DTBuilder
						.parse(new StringReader(content.getOriginalText()),
								new SingleKBMIDObjectManager(kbm.getKnowledgeBase()));

				KnowWEUtils.storeMessages(article, s, this.getClass(), Message.class, messages);
				Report ruleRep = new Report();
				for (Message messageKnOffice : messages) {
					ruleRep.add(messageKnOffice);
				}
			}
			return null;
		}
	}

}
