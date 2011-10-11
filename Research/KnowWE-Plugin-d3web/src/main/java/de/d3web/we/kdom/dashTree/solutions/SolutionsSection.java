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

package de.d3web.we.kdom.dashTree.solutions;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import de.d3web.KnOfficeParser.SingleKBMIDObjectManager;
import de.d3web.KnOfficeParser.dashtree.SolutionsBuilder;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.report.Message;
import de.d3web.report.Report;
import de.d3web.we.kdom.kopic.AbstractKopicSection;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.utils.KnowWEUtils;

public class SolutionsSection extends AbstractKopicSection {

	public static final String TAG = "Solutions-section";

	public SolutionsSection() {
		super(TAG);
	}

	@Override
	protected void init() {
		childrenTypes.add(new SolutionsContent());
		this.setOrderSensitive(true);
		this.addSubtreeHandler(Priority.increment(Priority.HIGHER),
				new SolutionsSubTreeHandler());
	}

	private class SolutionsSubTreeHandler extends D3webSubtreeHandler {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section s) {

			KnowledgeBase kb = getKB(article);

			Section content = ((AbstractKopicSection) s.get()).getContentChild(s);

			if (content != null) {

				List<de.d3web.report.Message> messages = SolutionsBuilder
						.parse(new StringReader(content.getOriginalText()), new SingleKBMIDObjectManager(kb));

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
