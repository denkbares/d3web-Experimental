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
import utils.KBTestUtil;
import utils.MyTestArticleManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.core.packaging.KnowWEPackageManager;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.logging.Logging;

/**
 * This class tests whether the Diagnoses are created as expected.
 * 
 * @author Sebastian Furth
 * @see KBTestUtil to modify the KB against which everything is tested
 * @see KBCreationTest.txt to modify the Article which is tested
 * 
 */
public class SolutionsTest extends TestCase {

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
		// Enfore Autocompile
		KnowWEPackageManager.overrideAutocompileArticle(true);
	}

	public void testNumberOfSolutions() {

		KnowWEArticle art = MyTestArticleManager.getArticle(KBTestUtil.KBCREATION_ARTICLE_FILE);
		KnowledgeBase loadedKB = KBTestUtil.getInstance().getKnowledgeBase(art);
		KnowledgeBase createdKB = KBTestUtil.getInstance().getCreatedKB();

		assertEquals("Number of Solutions differ.", createdKB.getManager().getSolutions().size(),
				loadedKB.getManager().getSolutions().size());
	}

	public void testSolutions() {

		KnowWEArticle art = MyTestArticleManager.getArticle(KBTestUtil.KBCREATION_ARTICLE_FILE);
		KnowledgeBase loadedKB = KBTestUtil.getInstance().getKnowledgeBase(art);
		KnowledgeBase createdKB = KBTestUtil.getInstance().getCreatedKB();

		if (loadedKB.getManager().getSolutions().size() == createdKB.getManager().getSolutions().size()) {
			for (int i = 0; i < loadedKB.getManager().getSolutions().size(); i++) {

				Solution expected = createdKB.getManager().getSolutions().get(i);
				Solution actual = loadedKB.getManager().getSolutions().get(i);

				// Test ID & Name
				assertEquals("Solution " + expected.getName() + " has wrong ID.",
						expected.getId(), actual.getId());
				assertEquals("Solution " + expected.getName() + " has wrong name.",
						expected.getName(), actual.getName());

				// Test Hierarchy
				assertTrue("Solution " + expected.getName() + " has wrong parents.",
						Arrays.equals(expected.getParents(), actual.getParents()));
				assertTrue("Solution " + expected.getName() + " has wrong children.",
						Arrays.equals(expected.getChildren(), actual.getChildren()));

				// Test Explanation
				assertEquals("Solution " + expected.getName() + " has wrong explanation.",
						expected.getInfoStore().getValue(MMInfo.DESCRIPTION),
						actual.getInfoStore().getValue(MMInfo.DESCRIPTION));
			}
		}
		else {
			Logging.getInstance().getLogger().warning(
					"SolutionsTest: Solutions have not been tested!");
		}
	}
}
