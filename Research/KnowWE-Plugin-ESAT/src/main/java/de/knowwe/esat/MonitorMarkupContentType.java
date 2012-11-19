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
package de.knowwe.esat;

import java.util.ArrayList;
import java.util.Collection;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.we.object.SolutionDefinition;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 18.11.2012
 */
public class MonitorMarkupContentType extends AbstractType {

	/**
	 * 
	 */
	public MonitorMarkupContentType() {
		this.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(new MonitorSolutionDefinition());
		this.addSubtreeHandler(Priority.HIGHEST, new
				CreateSquareQuestionTermHandler());
		this.addSubtreeHandler(new CreateMonitorKnowledgeHandler());
	}

	class CreateSquareQuestionTermHandler extends SubtreeHandler<MonitorMarkupContentType> {

		@Override
		public Collection<Message> create(Article article, Section<MonitorMarkupContentType> section) {
			Section<SolutionDefinition> solutionDef = Sections.findSuccessor(section,
					SolutionDefinition.class);
			Solution solution = solutionDef.get().getTermObject(article, solutionDef);
			KnowledgeBase knowledgeBase = solution.getKnowledgeBase();
			TerminologyManager terminologyHandler = KnowWEUtils.getTerminologyManager(article);
			Class<?> termObjectClass = QuestionNum.class;

			// create width variable
			QuestionNum qWidth = new QuestionNum(knowledgeBase, solution.getName() + "_width");
			qWidth.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION,
					Boolean.TRUE);
			knowledgeBase.getRootQASet().addChild(qWidth);
			knowledgeBase.getManager().putTerminologyObject(qWidth);

			TermIdentifier termIdentifierWidth = new TermIdentifier(solutionDef.get().getTermName(
					solutionDef)
					+ "_width");
			terminologyHandler.registerTermDefinition(section, termObjectClass, termIdentifierWidth);

			// create height variable
			QuestionNum qHeight = new QuestionNum(knowledgeBase, solution.getName() + "_height");
			qHeight.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION,
					Boolean.TRUE);
			knowledgeBase.getRootQASet().addChild(qHeight);
			knowledgeBase.getManager().putTerminologyObject(qHeight);

			TermIdentifier termIdentifierHeigth = new TermIdentifier(solutionDef.get().getTermName(
					solutionDef)
					+ "_height");
			terminologyHandler.registerTermDefinition(section, termObjectClass,
					termIdentifierHeigth);

			// create area variable
			QuestionNum qSquare = new QuestionNum(knowledgeBase, solution.getName() + "_a");
			qSquare.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION,
					Boolean.TRUE);
			knowledgeBase.getRootQASet().addChild(qSquare);
			knowledgeBase.getManager().putTerminologyObject(qSquare);

			TermIdentifier termIdentifier = new TermIdentifier(solutionDef.get().getTermName(
					solutionDef)
					+ "_a");
			terminologyHandler.registerTermDefinition(section, termObjectClass, termIdentifier);

			return new ArrayList<Message>(0);
		}

	}

	class MonitorSolutionDefinition extends SolutionDefinition {

		/**
		 * 
		 */
		public MonitorSolutionDefinition() {
			this.setSectionFinder(new AllTextFinderTrimmed());
		}
	}
}
