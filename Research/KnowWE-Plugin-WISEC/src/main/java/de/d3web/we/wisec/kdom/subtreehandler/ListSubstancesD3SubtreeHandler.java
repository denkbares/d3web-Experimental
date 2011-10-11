/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.we.wisec.kdom.subtreehandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.we.basic.WikiEnvironmentManager;
import de.d3web.we.kdom.decisionTree.QuestionsSection;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.wisec.kdom.ListSubstancesRootType;
import de.d3web.we.wisec.kdom.ListSubstancesType;
import de.d3web.we.wisec.kdom.WISECTable;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.table.TableCellContent;
import de.knowwe.kdom.table.TableLine;
import de.knowwe.logging.Logging;
import de.knowwe.report.message.NewObjectCreated;
import de.knowwe.report.message.ObjectCreationError;

public class ListSubstancesD3SubtreeHandler extends D3webSubtreeHandler<ListSubstancesType> {

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<ListSubstancesType> s) {

		KnowledgeBase kb = getKB(article);

		if (kb != null) {
			// Get the ListID
			Section<ListSubstancesRootType> root = Sections.findAncestorOfType(s,
					ListSubstancesRootType.class);
			String listID = DefaultMarkupType.getAnnotation(root, "ListID");

			// Create Substance Questionnaire
			new QContainer(kb.getRootQASet(), "Substances");

			// Check if we want to use the KDOM
			boolean useKDom = s.get().getAllowedChildrenTypes().size() > 0 ? true : false;

			// Process the Table Content
			if (useKDom) createD3ObjectsUsingKDom(s, kb, listID, article.getWeb());
			else {
				createD3Objects(s.getOriginalText().trim(), kb, listID, article.getWeb());
			}

			return Arrays.asList((KDOMReportMessage) new NewObjectCreated(
					"Successfully created D3Web Objects"));

		}
		else return Arrays.asList((KDOMReportMessage) new ObjectCreationError(
				"Unable to create d3web Objects. KBM was null!",
				this.getClass()));
	}

	private void createD3ObjectsUsingKDom(Section<ListSubstancesType> section,
			KnowledgeBase kb, String listID, String web) {

		boolean failed = false;

		// Check if the table was recognized
		if (Sections.findSuccessor(section, WISECTable.class) == null) {
			failed = true;
		}
		else {
			// Get all lines
			List<Section<TableLine>> tableLines = new ArrayList<Section<TableLine>>();
			 Sections.findSuccessorsOfType(section, TableLine.class, tableLines);

			// Find the SGN row
			int sgnIndex = -1;
			if (tableLines.size() > 1) sgnIndex = findSGNIndexKDOM(tableLines.get(0));

			// Process all tableLines if SGN was found
			if (sgnIndex == -1) {
				failed = true;
			}
			else {
				for (int i = 1; i < tableLines.size(); i++) {
					ArrayList<Section<TableCellContent>> contents = new ArrayList<Section<TableCellContent>>();
					Sections.findSuccessorsOfType(tableLines.get(i), TableCellContent.class,
							contents);

					// Create OWL statements from cell content
					if (contents.size() >= sgnIndex) {
						String sgn = contents.get(sgnIndex).getOriginalText().trim();
						// QuestionOC sgnQ =
						// kbm.createQuestionOC(sgn,
						// kbm.findQContainer("Substances"),
						// new String[] {
						// "included", "excluded" });
						addGlobalQuestion(sgn, web, kb);
						// createListRule(kbm, listID, sgnQ);
					}
					else {
						failed = true;
					}
				}
			}
		}

		if (failed) { // Try to process the content without KDOM
			Logging.getInstance().warning("Processing via KDOM failed, trying it without KDOM");
			createD3Objects(section.getOriginalText().trim(), kb, listID, web);
		}
	}

	private void addGlobalQuestion(String sgn, String web, KnowledgeBase kb) {
		KnowWEArticle globalsArticle = KnowWEEnvironment.getInstance().getArticleManager(web).getArticle(
				"WISEC_D3Globals");
		Section<QuestionsSection> questionsSection = Sections.findSuccessor(
					globalsArticle.getSection(), QuestionsSection.class);

		if (globalsArticle != null && questionsSection != null) {
			if (kb.getManager().searchQContainer("Substances") == null) new QContainer(
					kb.getRootQASet(), "Substances");
			if (kb.getManager().searchQuestion(sgn) == null) {
				new QuestionOC(kb.getManager().searchQContainer("Substances"), sgn, "included",
						"excluded");
				WikiEnvironmentManager.registerKnowledgeBase(kb,
						globalsArticle.getTitle(), globalsArticle.getWeb());
			}
		}
	}

	/*
	 * Replaced by OWL + SPARQL => {@link WISECFindingSetEventListener}!
	 */
	// private void createListRule(KnowledgeBaseUtils kbm, String listID,
	// QuestionOC sgnQuestion) {
	//
	// // Create condition
	// Choice includedAnswer = kbm.findChoice(sgnQuestion,
	// "included");
	// CondEqual condition = new CondEqual(sgnQuestion, new
	// ChoiceValue(includedAnswer));
	//
	// // Get abstract List-Question
	// QuestionChoice listQuestion = (QuestionChoice) kbm.findQuestion(listID);
	// Choice activeAnswer = kbm.findChoice(listQuestion, "active");
	//
	// // Create Rule
	// RuleFactory.createSetValueRule(kbm.createRuleID(), listQuestion,
	// new Object[] { activeAnswer }, condition);
	//
	// }

	private void createD3Objects(String tableContent,
			KnowledgeBase kb, String listID, String web) {

		// Remove the trailing dashes
		StringBuilder bob = new StringBuilder(tableContent);
		while (bob.charAt(bob.length() - 1) == '-')
			bob.delete(bob.length() - 1, bob.length());
		tableContent = bob.toString();

		// Get the lines
		String[] lines = tableContent.split("\n");
		int sgnIndex = -1;

		// We need at least a head and one content line
		if (lines.length > 1) sgnIndex = findSGNIndex(lines[0]);

		// if "SGN"-row was not found further processing is not possible
		if (sgnIndex > -1) {
			Pattern cellPattern = Pattern.compile("\\s*\\|\\s*");
			String[] cells;
			// lines[0] was the headline and is already processed
			for (int i = 1; i < lines.length; i++) {
				cells = cellPattern.split(lines[i]);
				String sgn = cells[sgnIndex].trim();
				// QuestionOC sgnQ =
				// kbm.createQuestionOC(sgn, kbm.findQContainer("Substances"),
				// new String[] {
				// "included", "excluded" });
				addGlobalQuestion(sgn, web, kb);
				// createListRule(kbm, listID, sgnQ);
			}
		}
	}

	private int findSGNIndex(String tablehead) {
		Pattern cellPattern = Pattern.compile("\\s*\\|{2}\\s*");
		String[] cells = cellPattern.split(tablehead);
		for (int i = 0; i < cells.length; i++) {
			if (cells[i].trim().equalsIgnoreCase("CAS_No")) return i;
		}
		return -1;
	}

	private int findSGNIndexKDOM(Section<TableLine> section) {
		ArrayList<Section<TableCellContent>> contents = new ArrayList<Section<TableCellContent>>();
		Sections.findSuccessorsOfType(section, TableCellContent.class, contents);
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i).getOriginalText().trim().equalsIgnoreCase("CAS_No")) return i;
		}
		Logging.getInstance().warning("CAS_No row was not found!");
		return -1;
	}

}
