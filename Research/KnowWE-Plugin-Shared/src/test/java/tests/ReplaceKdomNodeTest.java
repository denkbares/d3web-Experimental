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
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import objectTypes.SplitObjectType;
import objectTypes.WordObjectType;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.action.ActionContext;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.jspwiki.JSPActionDispatcher;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;
import dummies.KnowWETestWikiConnector;

/**
 * Name speaks for functionality.
 *
 * @author Johannes Dienst
 *
 */
public class ReplaceKdomNodeTest extends TestCase {

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
	}

	public void testReplaceKdomNode() throws IOException {

		/**
		 * Initialise KnowWEEnvironment
		 */
		KnowWEEnvironment _env;
		KnowWEEnvironment.initKnowWE(new KnowWETestWikiConnector());
		_env = KnowWEEnvironment.getInstance();
		_env.getArticle("default_web", "Test_Article");

		/**
		 * Build an Article.
		 */
		String content = "aaa bbb -ababba- aba - bbbaa-abba aab";
		Type rootType = new AbstractType(new AllTextSectionFinder()) {

			{
				addChildType(new SplitObjectType());
				addChildType(new WordObjectType());
			}
		};

		_env.processAndUpdateArticleJunit("TestUser", content, "Test_Article", "default_web",
				rootType);

		/**
		 * Replace KdomNode.
		 */
		KnowWEArticle article = _env.getArticle("default_web", "Test_Article");
		Section<?> artSec = article.getSection();
		String toReplace = ((Section) artSec.getChildren().get(0)).getID();
		Map<String, String> map = new HashMap<String, String>();
		map.put(KnowWEAttributes.WEB, "default_web");
		map.put(KnowWEAttributes.TARGET, toReplace);
		map.put(KnowWEAttributes.TOPIC, "Test_Article");
		map.put(KnowWEAttributes.TEXT, "Ersetzt");
		map.put("action", "ReplaceKDOMNodeAction");
		map.put(KnowWEAttributes.USER, "testuser");
		ActionContext actionContext = new ActionContext("ReplaceKDOMNodeAction", "", map, null,
				null, null, null);
		JSPActionDispatcher.getInstance().performAction(actionContext);

		article = _env.getArticle("default_web", "Test_Article");
		artSec = article.getSection();
		String original = artSec.getOriginalText();

		/**
		 * The tests. 1. Normal
		 */
		boolean actual = original.contains("Ersetzt");
		assertEquals("Word \"Ersetzt\" not contained: " + original, true, actual);

		actual = original.equals(content);
		assertEquals("Original equals replaced", false, actual);

		// TODO: Replace KDOM-Check with something new
		// actual = KDOMValidator.getFileHandlerInstance().validateArticle(
		// _env.getArticle("default_web", "Test_Article"));
		// assertEquals("Article no longer valid", true, actual);

		/**
		 * 2. Build new subtree.
		 */
		toReplace = ((Section<?>) artSec.getChildren().get(0)).getID();
		map.put(KnowWEAttributes.TARGET, toReplace);
		map.put(KnowWEAttributes.TOPIC, "Test_Article");
		map.put(KnowWEAttributes.TEXT, "-aa-");
		ActionContext actionContext2 = new ActionContext("ReplaceKDOMNodeAction", "", map, null,
				null, null, null);
		JSPActionDispatcher.getInstance().performAction(actionContext2);

		// TODO: Replace KDOM-Check with something new
		// actual = KDOMValidator.getFileHandlerInstance().validateArticle(
		// _env.getArticle("default_web", "Test_Article"));
		// assertEquals("Article no longer valid", true, actual);

		article = _env.getArticle("default_web", "Test_Article");
		artSec = article.getSection();
		original = artSec.getOriginalText();

		actual = original.contains("-aa-");
		assertEquals("Word \"-aa-\" not contained", true, actual);

		actual = original.equals(content);
		assertEquals("Original equals replaced", false, actual);

		artSec = artSec.getChildren().get(0).getChildren().get(0);
		String objectTypeName = artSec.get().getName();
		assertEquals("SplitObjectType not contained", "SplitObjectType", objectTypeName);
	}
}
