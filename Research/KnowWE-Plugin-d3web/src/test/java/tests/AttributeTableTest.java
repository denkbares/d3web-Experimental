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

import junit.framework.TestCase;
import utils.KBTestUtil;
import utils.MyTestArticleManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.kdom.KnowWEArticle;

/**
 * This class tests whether the Objects got the right MMInfo from the
 * AttributeTable
 * 
 * @author Sebastian Furth
 * 
 */
public class AttributeTableTest extends TestCase {

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
	}

	public void testMMInfo() {

		KnowWEArticle art = MyTestArticleManager.getArticle(KBTestUtil.KBCREATION_ARTICLE_FILE);
		KnowledgeBase loadedKB = KBTestUtil.getInstance().getKnowledgeBase(art);
		KnowledgeBase createdKB = KBTestUtil.getInstance().getCreatedKB();

		// Get Diagnosis with ID "P1": "Mechanical Problem"
		Solution loadedDiag = loadedKB.getManager().searchSolution("P1");
		Solution createdDiag = createdKB.getManager().searchSolution("P1");

		// Get Property
		String loadedValue = loadedDiag.getInfoStore().getValue(MMInfo.DESCRIPTION);
		String createdValue = createdDiag.getInfoStore().getValue(MMInfo.DESCRIPTION);
		assertNotNull("Diagnosis " + loadedDiag.getName() + " has no Description.", loadedValue);
		assertNotNull("Diagnosis " + createdDiag.getName() + " has no Description.", createdValue);

		// Compare content of MMInfoObject
		assertEquals("Content of Description of Diagnosis " + createdDiag.getName() + " differs.",
				createdValue, loadedValue);

	}

}
