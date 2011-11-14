/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.tables;

import java.util.Collection;
import java.util.Set;

import de.d3web.KnOfficeParser.SingleKBMIDObjectManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.KnowledgeStore;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.we.kdom.xcl.list.ListSolutionType;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.kdom.AnonymousTypeInvisible;
import de.knowwe.kdom.sectionFinder.StringSectionFinderUnquoted;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;

/**
 *
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class CausalDiagnosisScore extends AbstractType {

	public CausalDiagnosisScore() {
		this.sectionFinder = new AllTextSectionFinder();

		this.addChildType(new ListSolutionType());

		// cut the optional closing }
		AnonymousTypeInvisible closing = new AnonymousTypeInvisible("closing-bracket");
		closing.setSectionFinder(new StringSectionFinderUnquoted("}"));
		this.addChildType(closing);

		this.addChildType(new InnerTable());

		this.addSubtreeHandler(new CausalDiagnosisScoreHandler());
	}

	/**
	 * Handles the creation of XCLRelations from CausalDiagnosisScoreMarkup
	 * 
	 * @author Johannes Dienst
	 * @created 10.11.2011
	 */
	public class CausalDiagnosisScoreHandler extends GeneralSubtreeHandler<CausalDiagnosisScore> {

		@Override
		public Collection<KDOMReportMessage> create(
				KnowWEArticle article, Section<CausalDiagnosisScore> scoreSec) {

			Section<InnerTable> innerTable =
					Sections.findChildOfType(scoreSec, InnerTable.class);
			if (Sections.findSuccessorsOfType(innerTable, TableCell.class).isEmpty())
				return null;

			// TODO Right KnowledgeBase?
			Set<String> packages =
					Sections.findAncestorOfExactType(scoreSec, CausalDiagnosisScoreMarkup.class).getPackageNames();
			String packageName = packages.iterator().next();
			KnowledgeBase kb = D3webUtils.getKB(article.getWeb(), packageName + " - master");


			// Create XCLRelations
			SingleKBMIDObjectManager kbm = new SingleKBMIDObjectManager(kb);

			// First create solution if necessary
			Section<ListSolutionType> sol =
					Sections.findChildOfType(scoreSec, ListSolutionType.class);
			String solText = sol.getText();
			Solution solution = kbm.findSolution(solText);
			if (solution == null) {
				Solution newSolution = kbm.createSolution(solText, null);
				kb.getManager().putTerminologyObject(newSolution);
			}

			// Create XCLRelations: 1. get first column
			//			TableUtils.getColumnCells(columnNumber, table);


			KnowledgeStore store = kbm.getKnowledgeBase().getKnowledgeStore();


			return null;
		}

	}
}
