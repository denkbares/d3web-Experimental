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

package tests;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;
import utils.KBCreationTestUtil;
import utils.MyTestArticleManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.MMInfoObject;
import de.d3web.core.knowledge.terminology.info.MMInfoStorage;
import de.d3web.core.knowledge.terminology.info.MMInfoSubject;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.core.packaging.KnowWEPackageManager;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.logging.Logging;

/**
 * This class tests whether the Questions are created as expected.
 * 
 * @author Sebastian Furth
 * @see KBCreationTestUtil to modify the KB against which everything is tested
 * @see KBCreationTest.txt to modify the Article which is tested
 * 
 */
public class QuestionTreeTest extends TestCase {

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
		//Enfore Autocompile
		KnowWEPackageManager.overrideAutocompileArticle(true);
	}

	public void testNumberOfQuestions() {
		KnowWEArticle art = MyTestArticleManager.getArticle(KBCreationTestUtil.KBCREATION_ARTICLE_FILE);
		KnowledgeBase loadedKB = MyTestArticleManager.getKnowledgeBase(art);
		KnowledgeBase createdKB = KBCreationTestUtil.getInstance().getCreatedKB();
		assertEquals("Number of Questions differ.", createdKB.getQuestions().size(),
				loadedKB.getQuestions().size());
	}

	public void testQuestions() {

		KnowWEArticle art = MyTestArticleManager.getArticle(KBCreationTestUtil.KBCREATION_ARTICLE_FILE);
		KnowledgeBase loadedKB = MyTestArticleManager.getKnowledgeBase(art);
		KnowledgeBase createdKB = KBCreationTestUtil.getInstance().getCreatedKB();

		if (loadedKB.getQuestions().size() == createdKB.getQuestions().size()) {
			for (int i = 0; i < loadedKB.getQuestions().size(); i++) {

				Question expected = createdKB.getQuestions().get(i);
				Question actual = loadedKB.getQuestions().get(i);

				// Test Name & ID
				assertEquals("Question " + expected.getName() + " has wrong ID.",
						expected.getId(), actual.getId());
				assertEquals("Question " + expected.getName() + " has wrong name.",
						expected.getName(), actual.getName());

				// Test Hierarchy
				assertTrue("Question " + expected.getName() + " has wrong parents.", Arrays.equals(
						expected.getParents(), actual.getParents()));
				assertTrue("Question " + expected.getName() + " has wrong children.",
						Arrays.equals(expected.getChildren(), actual.getChildren()));
				// Test Properties (Abstraction, MMINFO)
				assertEquals("Question " + expected.getName() + " should be abstract.",
						expected.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION),
						actual.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION));

				// Test Question Type
				assertEquals("Question " + expected.getName() + " has wrong type.",
						expected.getClass(), actual.getClass());

				// Question Type specific tests
				if (expected instanceof QuestionChoice) {
					assertEquals("Question " + expected.getName()
							+ " has different answer alternatives.",
							((QuestionChoice) expected).getAllAlternatives(),
							((QuestionChoice) actual).getAllAlternatives());
				}

				if (expected instanceof QuestionNum) {
					assertEquals("Question " + expected.getName() + " has wrong unit.",
							expected.getInfoStore().getValue(BasicProperties.UNIT),
							actual.getInfoStore().getValue(BasicProperties.UNIT));
					assertEquals("Question " + expected.getName() + " has wrong range.",
							expected.getInfoStore().getValue(BasicProperties.QUESTION_NUM_RANGE),
							actual.getInfoStore().getValue(BasicProperties.QUESTION_NUM_RANGE));
				}

			}
		}
		else {
			Logging.getInstance().getLogger().warning(
					"QuestionTest: Questions have not been tested!");
		}
	}

	public void testMMInfo() {

		KnowWEArticle art = MyTestArticleManager.getArticle(KBCreationTestUtil.KBCREATION_ARTICLE_FILE);
		KnowledgeBase loadedKB = MyTestArticleManager.getKnowledgeBase(art);
		KnowledgeBase createdKB = KBCreationTestUtil.getInstance().getCreatedKB();

		// Get Question with ID "Q1": "Exhaust fumes"
		Question loadedQuestion = loadedKB.searchQuestion("Q1");
		Question createdQuestion = createdKB.searchQuestion("Q1");

		// Get MMInfoStorage of question
		MMInfoStorage loadedStorage = (MMInfoStorage) loadedQuestion.getInfoStore().getValue(
				BasicProperties.MMINFO);
		MMInfoStorage createdStorage = (MMInfoStorage) createdQuestion.getInfoStore().getValue(
				BasicProperties.MMINFO);
		assertNotNull("Question " + loadedQuestion.getName() + " has no MMInfoStorage.",
				loadedStorage);
		assertNotNull("Question " + createdQuestion.getName() + " has no MMInfoStorage.",
				createdStorage);

		// Create DCMarkup
		DCMarkup markup = new DCMarkup();
		markup.setContent(DCElement.SOURCE, loadedQuestion.getId());
		markup.setContent(DCElement.TITLE, "LT");
		markup.setContent(DCElement.SUBJECT, MMInfoSubject.PROMPT.getName());

		// Get MMInfoObject for created DCMarkup
		MMInfoObject loadedMMInfo = (MMInfoObject) loadedStorage.getMMInfo(markup).toArray()[0];
		MMInfoObject createdMMInfo = (MMInfoObject) createdStorage.getMMInfo(markup).toArray()[0];
		assertNotNull("Question " + loadedQuestion.getName() + " has no MMInfo.", loadedMMInfo);
		assertNotNull("Question " + createdQuestion.getName() + " has no MMInfo.", createdMMInfo);

		// Compare content of MMInfoObject
		assertEquals("Content of MMInfoObject of Diagnosis " + createdQuestion.getName()
				+ " differs.",
						createdMMInfo.getContent(), loadedMMInfo.getContent());

	}

}
