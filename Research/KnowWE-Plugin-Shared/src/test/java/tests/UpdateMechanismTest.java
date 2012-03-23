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
import java.util.List;

import junit.framework.TestCase;
import utils.TestUtils;
import de.d3web.plugin.test.InitPluginManager;
import de.knowwe.core.Environment;
import de.knowwe.core.Environment.CompilationMode;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.xml.GenericXMLObjectType;
import dummies.TestWikiConnector;

public class UpdateMechanismTest extends TestCase {

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
	}

	public void testUpdatingKDOM() {
		/*
		 * Initialise Environment
		 */
		Environment.initKnowWE(new TestWikiConnector());
		Environment.getInstance().setCompilationMode(CompilationMode.INCREMENTAL);

		/*
		 * Setup
		 */
		String content = TestUtils.readTxtFile("src/test/resources/UpdatingTest1.txt");

		// types.add(DefaultTextType.getInstance());

		/*
		 * Init first Article
		 */
		RootType rootType = RootType.getInstance();
		rootType.clearChildrenTypes();
		rootType.addChildType(GenericXMLObjectType.getInstance());
		Article article1 = Article.createArticle(content, "UpdatingTest",
				rootType, "default_web");
		Environment.getInstance().getArticleManager("default_web").registerArticle(
				article1);

		/*
		 * Init a second, identical Article
		 */
		Article article2 = Article.createArticle(content, "UpdatingTest",
				rootType, "default_web");

		List<Section<?>> sections1 = Sections.getSubtreePreOrder(article1.getRootSection());

		List<Section<?>> sections2 = Sections.getSubtreePreOrder(article2.getRootSection());

		assertEquals("Articles dont have the same amount of sections:", sections1.size(),
				sections2.size());

		for (int i = 2; i < sections1.size(); i++) {
			assertSame("The Sections in the different articles should be the same",
					sections1.get(i), sections2.get(i));
		}
	}

}
