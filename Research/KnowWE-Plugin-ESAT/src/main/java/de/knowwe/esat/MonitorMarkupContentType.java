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
import de.d3web.strings.Identifier;
import de.d3web.we.knowledgebase.D3webCompiler;
import de.d3web.we.object.SolutionDefinition;
import de.d3web.we.reviseHandler.D3webHandler;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.report.Message;

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
		this.addCompileScript(Priority.HIGHEST, new
				CreateSquareQuestionTermHandler());
		this.addCompileScript(new CreateMonitorKnowledgeHandler());
	}

	class CreateSquareQuestionTermHandler implements D3webHandler<MonitorMarkupContentType> {

		@Override
		public Collection<Message> create(D3webCompiler compiler, Section<MonitorMarkupContentType> section) {
			Section<SolutionDefinition> solutionDef = Sections.successor(section,
					SolutionDefinition.class);
			Solution solution = solutionDef.get().getTermObject(compiler, solutionDef);
			KnowledgeBase knowledgeBase = solution.getKnowledgeBase();
			TerminologyManager terminologyHandler = compiler.getTerminologyManager();
			Class<?> termObjectClass = QuestionNum.class;

			// create width variable
			QuestionNum qWidth = new QuestionNum(knowledgeBase, solution.getName() + "_width");
			qWidth.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION,
					Boolean.TRUE);
			knowledgeBase.getRootQASet().addChild(qWidth);
			knowledgeBase.getManager().putTerminologyObject(qWidth);

			Identifier termIdentifierWidth = new Identifier(solutionDef.get().getTermName(
					solutionDef)
					+ "_width");
			terminologyHandler.registerTermDefinition(compiler, section, termObjectClass, termIdentifierWidth);

			// create height variable
			QuestionNum qHeight = new QuestionNum(knowledgeBase, solution.getName() + "_height");
			qHeight.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION,
					Boolean.TRUE);
			knowledgeBase.getRootQASet().addChild(qHeight);
			knowledgeBase.getManager().putTerminologyObject(qHeight);

			Identifier termIdentifierHeigth = new Identifier(solutionDef.get().getTermName(
					solutionDef)
					+ "_height");
			terminologyHandler.registerTermDefinition(compiler, section,
					termObjectClass, termIdentifierHeigth);

			// create area variable
			QuestionNum qSquare = new QuestionNum(knowledgeBase, solution.getName() + "_a");
			qSquare.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION,
					Boolean.TRUE);
			knowledgeBase.getRootQASet().addChild(qSquare);
			knowledgeBase.getManager().putTerminologyObject(qSquare);

			Identifier termIdentifier = new Identifier(solutionDef.get().getTermName(
					solutionDef)
					+ "_a");
			terminologyHandler.registerTermDefinition(compiler, section, termObjectClass, termIdentifier);

			// create area variable
			QuestionNum qQuotient = new QuestionNum(knowledgeBase, solution.getName() + "_q");
			qQuotient.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION,
					Boolean.TRUE);
			knowledgeBase.getRootQASet().addChild(qQuotient);
			knowledgeBase.getManager().putTerminologyObject(qQuotient);

			Identifier termIdentifierQ = new Identifier(solutionDef.get().getTermName(
					solutionDef)
					+ "_q");
			terminologyHandler.registerTermDefinition(compiler, section, termObjectClass, termIdentifierQ);

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
