package de.knowwe.renaming.test;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import connector.DummyConnector;
import junit.framework.TestCase;
import utils.TestUtils;

import com.denkbares.plugin.test.InitPluginManager;
import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.renaming.taghandler.WordBasedRenameFinding;
import de.knowwe.renaming.taghandler.WordBasedRenamingAction;
import de.knowwe.renaming.test.util.SplitObjectType;
import de.knowwe.renaming.test.util.WordObjectType;

public class RenamingToolTest extends TestCase {

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
	}

	private static void waitForCompilation() {
		try {
			Compilers.getCompilerManager(Environment.DEFAULT_WEB).awaitTermination();
		}
		catch (InterruptedException e) {
		}
	}

	public void testFindings() {

		/**
		 * Initialise Environment
		 */
		RootType rootType = RootType.getInstance();
		rootType.addChildType(new SplitObjectType());
		rootType.addChildType(new WordObjectType());
		DummyConnector connector = new DummyConnector();
		connector.setKnowWEExtensionPath(TestUtils.createKnowWEExtensionPath());
		Environment.initInstance(connector);
		Environment _env = Environment.getInstance();
		_env.getArticle("default_web", "Test_Article");

		/**
		 * Create 2 Articles to search in.
		 */
		String content1 = "aaa bbb aaa ccc bbbaaa bbbaaa";
		String content2 = "dd bbdd ccd bb b ccc bbb c dd b";

		TestUtils.processAndUpdateArticleJunit("TestUser", content1, "Test_Article1", "default_web");
		waitForCompilation();
		TestUtils.processAndUpdateArticleJunit("TestUser", content2, "Test_Article2", "default_web");
		waitForCompilation();

		/*
		 * make the requests
		 */
		Map<String, String> map = new HashMap<>();
		map.put(Attributes.TARGET, "ccc");
		map.put(Attributes.CONTEXT_PREVIOUS, "");
		map.put(Attributes.CONTEXT_AFTER, "");
		map.put(Attributes.WEB, "default_web");
		Map<Article, Collection<WordBasedRenameFinding>> findings = renamingToolTest(map);

		/**
		 * Test_Article1
		 */
		Article article = _env.getArticle("default_web", "Test_Article1");
		ArrayList<WordBasedRenameFinding> r = new ArrayList<>(
				findings.get(article));
		WordBasedRenameFinding finding = r.get(0);
		int start = 12;
		assertEquals("Wrong start of finding", start, finding.getStart());

		/**
		 * Context Test Test_Article1
		 */
		String[] expected = {
				" ", "  bbbaaa", "  bbbaaa bbbaaa" };
		String actual;

		// after
		for (int i = 0; i < 3; i++) {
			actual = WordBasedRenameFinding.getAdditionalContext
					(start, "a", i, 3,
							article.getRootSection().getText());
			assertEquals("After context wrong", expected[i], actual);
		}

		// previous
		expected = new String[] {
				"", "aaa ", "bbb aaa ", "aaa bbb aaa " };
		for (int i = 0; i < 4; i++) {
			actual = WordBasedRenameFinding.getAdditionalContext
					(start, "p", i, 3,
							article.getRootSection().getText());
			assertEquals("Previous context wrong", expected[i], actual);
		}

		/**
		 * Test_Article2
		 */
		r.clear();
		article = _env.getArticle("default_web", "Test_Article2");
		r.addAll(findings.get(article));
		finding = r.get(0);
		start = 17;
		assertEquals("Wrong start of finding", start, finding.getStart());

		/**
		 * Context Test Test_Article2
		 */
		// after dd bbdd ccd bb b ccc bbb c dd b
		expected = new String[] {
				" ", "  bbb", "  bbb c", "  bbb c dd", "  bbb c dd b" };
		start = 17;
		for (int i = 0; i < 5; i++) {
			actual = WordBasedRenameFinding.getAdditionalContext
					(start, "a", i, 3,
							article.getRootSection().getText());
			assertEquals("After context wrong", expected[i], actual);
		}

		// previous
		expected = new String[] {
				"", "b ", "bb b ", "ccd bb b ", "bbdd ccd bb b " };
		start = 17;
		for (int i = 0; i < 5; i++) {
			actual = WordBasedRenameFinding.getAdditionalContext
					(start, "p", i, 3,
							article.getRootSection().getText());
			assertEquals("Previous context wrong", expected[i], actual);
		}
	}

	private Map<Article, Collection<WordBasedRenameFinding>> renamingToolTest(
			Map<String, String> map) {
		WordBasedRenamingAction action = new WordBasedRenamingAction();
		return action.scanForFindings(map.get(Attributes.WEB),
				map.get(Attributes.TARGET), map.get(
						Attributes.CONTEXT_PREVIOUS).length(), null);
	}
}
