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

/**
 *
 */
package tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.core.semantic.ISemanticCore;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.ActionContext;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.RootType;
import de.knowwe.search.GenericSearchResult;
import de.knowwe.tagging.TaggingMangler;
import dummies.KnowWETestWikiConnector;

/**
 * @author kazamatzuri
 * 
 */
public class TaggingManglerTest extends TestCase {

	private KnowWEArticleManager am;
	private TaggingMangler tm;
	private UserActionContext context;
	private RootType type;
	private KnowWEEnvironment ke;
	private ISemanticCore sc;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		/*
		 * Initialise KnowWEEnvironment
		 */
		KnowWEEnvironment.initKnowWE(new KnowWETestWikiConnector());
		ke = KnowWEEnvironment.getInstance();
		am = KnowWEEnvironment.getInstance().getArticleManager("default_web");
		type = ke.getRootType();

		/*
		 * Init first Article
		 */
		KnowWEArticle article1 = KnowWEArticle.createArticle("", "TagTest", type,
				"default_web");

		am.registerArticle(article1);

		Map<String, String> map = new HashMap<String, String>();
		map.put(KnowWEAttributes.WEB, KnowWEEnvironment.DEFAULT_WEB);
		map.put(KnowWEAttributes.USER, "testuser");
		context = new ActionContext("", "", map, null, null, null, null);
		tm = TaggingMangler.getInstance();
		sc = SemanticCoreDelegator.getInstance();
	}

	/**
	 * Test method for
	 * {@link de.d3web.we.core.semantic.tagging.TaggingMangler#clone()}.
	 */
	@Test
	public void testClone() {
		boolean thrown = false;
		try {
			tm.clone();
		}
		catch (CloneNotSupportedException e) {
			thrown = true;

		}
		assertTrue("CloneNotSupportedException now thrown", thrown);
	}

	/**
	 * Test method for
	 * {@link de.d3web.we.core.semantic.tagging.TaggingMangler#addTag(java.lang.String, java.lang.String, de.d3web.we.core.KnowWEParameterMap)}
	 * . Test method for
	 * {@link de.d3web.we.core.semantic.tagging.TaggingMangler#removeTag(java.lang.String, java.lang.String, de.d3web.we.core.KnowWEParameterMap)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAddRemoveTag() throws IOException {
		KnowWEArticle article1 = KnowWEArticle.createArticle("", "AddTag", type,
				"default_web");
		am.registerArticle(article1);
		tm.addTag("AddTag", "tagtest", context);
		assertEquals("%%tags\ntagtest\n%", am.getArticle("AddTag")
				.getSection().getText());
		tm.removeTag("AddTag", "tagtest", context);
		assertEquals("%%tags\n%", am.getArticle("AddTag").getSection()
				.getText());
		am.deleteArticle(am.getArticle("AddTag"));
	}

	/**
	 * Test method for
	 * {@link de.d3web.we.core.semantic.tagging.TaggingMangler#addTag(java.lang.String, java.lang.String, de.d3web.we.core.KnowWEParameterMap)}
	 * . Test method for
	 * 
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAddTag() throws IOException {
		KnowWEArticle article1 = KnowWEArticle.createArticle("", "AddTag", type,
				KnowWEEnvironment.DEFAULT_WEB);
		am.registerArticle(article1);
		tm.addTag("AddTag", "tagtest", context);
		// remember: article* are not the current articles anymore, changes to
		// the articles by the TaggingMangler do not backpropagate to those
		// variables
		String keyorig = article1.getSection().getID().hashCode() + "";
		assertEquals("%%tags\ntagtest\n%", am.getArticle("AddTag")
				.getSection().getText());
		ArrayList<String> tags = tm.getPageTags("AddTag");
		assertEquals(1, tags.size());
		assertEquals("tagtest", tags.get(0));
		// remove statements from triplestore
		assertEquals(keyorig, article1.getSection().getID().hashCode() + "");
		sc.clearContext(am.getArticle("AddTag"));
		tags = tm.getPageTags("AddTag");
		// make sure it is gone
		assertEquals(0, tags.size());

		// now add another tag
		tm.addTag("AddTag", "stein", context);
		tags = tm.getPageTags("AddTag");
		assertEquals(2, tags.size());

		// now test with another article in the triplestore
		KnowWEArticle article2 = KnowWEArticle.createArticle("", "Tag1", type,
				KnowWEEnvironment.DEFAULT_WEB);
		am.registerArticle(article2);
		// add the same tag to the second article to check for interferences
		tm.addTag("Tag1", "stein", context);
		assertEquals(2, tags.size());
		am.deleteArticle(am.getArticle("Tag1"));
		am.deleteArticle(am.getArticle("AddTag"));
	}

	/**
	 * Test method for
	 * {@link de.d3web.we.core.semantic.tagging.TaggingMangler#getPages(java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetPages() throws IOException {
		KnowWEArticle article1 = KnowWEArticle.createArticle("", "Tag1", type,
				KnowWEEnvironment.DEFAULT_WEB);
		KnowWEArticle article2 = KnowWEArticle.createArticle("", "Tag2", type,
				KnowWEEnvironment.DEFAULT_WEB);
		KnowWEArticle article3 = KnowWEArticle.createArticle("", "Tag3", type,
				KnowWEEnvironment.DEFAULT_WEB);
		KnowWEArticle article4 = KnowWEArticle.createArticle("", "Tag4", type,
				KnowWEEnvironment.DEFAULT_WEB);
		am.registerArticle(article1);
		am.registerArticle(article2);
		am.registerArticle(article3);
		am.registerArticle(article4);
		tm.addTag("Tag1", "live", context);
		tm.addTag("Tag2", "live", context);
		tm.addTag("Tag3", "tod", context);
		tm.addTag("Tag4", "live", context);
		// remember: article* are not the current articles anymore, changes to
		// the articles by the TaggingMangler do not backpropagate to those
		// variables
		List<String> pages = tm.getPages("live");
		assertNotNull(pages);
		assertEquals(3, pages.size());
		assertTrue("not found page Tag1", pages.contains("Tag1"));
		assertTrue("not found page Tag2", pages.contains("Tag2"));
		assertTrue("not found page Tag4", pages.contains("Tag4"));
		assertTrue("found page Tag3", !pages.contains("Tag3"));
		am.deleteArticle(am.getArticle("Tag1"));
		am.deleteArticle(am.getArticle("Tag2"));
		am.deleteArticle(am.getArticle("Tag3"));
		am.deleteArticle(am.getArticle("Tag4"));
	}

	/**
	 * Test method for
	 * {@link de.d3web.we.core.semantic.tagging.TaggingMangler#getPageTags(java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetPageTags() throws IOException {
		KnowWEArticle article = KnowWEArticle.createArticle("", "Tag", type,
				"default_web");
		am.registerArticle(article);
		tm.addTag("Tag", "tick", context);
		tm.addTag("Tag", "trick", context);
		tm.addTag("Tag", "track", context);
		// remember: article* are not the current articles anymore, changes to
		// the articles by the TaggingMangler do not backpropagate to those
		// variables
		ArrayList<String> tags = tm.getPageTags("Tag");
		assertTrue(tags.contains("tick"));
		assertTrue(tags.contains("trick"));
		assertTrue(tags.contains("track"));
		am.deleteArticle(am.getArticle("Tag"));
	}

	/**
	 * Test method for
	 * {@link de.d3web.we.core.semantic.tagging.TaggingMangler#getAllTags()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetAllTags() throws IOException {
		KnowWEArticle article1 = KnowWEArticle.createArticle("", "Tag1", type,
				"default_web");
		KnowWEArticle article2 = KnowWEArticle.createArticle("", "Tag2", type,
				"default_web");
		KnowWEArticle article3 = KnowWEArticle.createArticle("", "Tag3", type,
				"default_web");
		am.registerArticle(article1);
		am.registerArticle(article2);
		am.registerArticle(article3);
		tm.addTag("Tag1", "tag", context);
		tm.addTag("Tag2", "leben", context);
		tm.addTag("Tag3", "tod", context);
		// remember: article* are not the current articles anymore, changes to
		// the articles by the TaggingMangler do not backpropagate to those
		// variables
		List<String> tags = tm.getAllTags();
		assertNotNull(tags);
		assertTrue(tags.contains("tag"));
		assertTrue(tags.contains("leben"));
		assertTrue(tags.contains("tod"));
		am.deleteArticle(am.getArticle("Tag1"));
		am.deleteArticle(am.getArticle("Tag2"));
		am.deleteArticle(am.getArticle("Tag3"));
		tags = tm.getAllTags();
		assertEquals(0, tags.size());
	}

	/**
	 * Test method for
	 * {@link de.d3web.we.core.semantic.tagging.TaggingMangler#getCloudList(int, int)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetCloudList() throws IOException {
		KnowWEArticle article1 = KnowWEArticle.createArticle("", "Tag1", type,
				"default_web");
		KnowWEArticle article2 = KnowWEArticle.createArticle("", "Tag2", type,
				"default_web");
		KnowWEArticle article3 = KnowWEArticle.createArticle("", "Tag3", type,
				"default_web");
		am.registerArticle(article1);
		am.registerArticle(article2);
		am.registerArticle(article3);
		tm.addTag("Tag1", "tag", context);
		tm.addTag("Tag2", "leben", context);
		tm.addTag("Tag3", "tod", context);
		tm.addTag("Tag3", "leben", context);
		// remember: article* are not the current articles anymore, changes to
		// the articles by the TaggingMangler do not backpropagate to those
		// variables
		Map<String, Integer> tags = tm.getCloudList(10, 20);
		assertEquals(Integer.valueOf(20), tags.get("leben"));
		assertEquals(Integer.valueOf(10), tags.get("tod"));
		am.deleteArticle(am.getArticle("Tag1"));
		am.deleteArticle(am.getArticle("Tag2"));
		am.deleteArticle(am.getArticle("Tag3"));

	}

	/**
	 * Test method for
	 * {@link de.d3web.we.core.semantic.tagging.TaggingMangler#getCloudList(int, int)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetCloudList2() throws IOException {
		// clear out the articles from the previous tests
		Iterator<KnowWEArticle> ait = am.getArticleIterator();
		while (ait.hasNext()) {
			KnowWEArticle art = ait.next();
			am.deleteArticle(art);
		}
		KnowWEArticle article1 = KnowWEArticle.createArticle("", "Tag1", type,
				"default_web");
		KnowWEArticle article2 = KnowWEArticle.createArticle("", "Tag2", type,
				"default_web");
		KnowWEArticle article3 = KnowWEArticle.createArticle("", "Tag3", type,
				"default_web");
		am.registerArticle(article1);
		am.registerArticle(article2);
		am.registerArticle(article3);
		tm.addTag("Tag1", "tag", context);
		tm.addTag("Tag2", "leben", context);
		tm.addTag("Tag3", "tod", context);
		// remember: article* are not the current articles anymore, changes to
		// the articles by the TaggingMangler do not backpropagate to those
		// variables
		Map<String, Integer> tags = tm.getCloudList(10, 20);
		assertEquals(Integer.valueOf(15), tags.get("leben"));
		assertEquals(Integer.valueOf(15), tags.get("tod"));
		assertEquals(Integer.valueOf(15), tags.get("tag"));
		am.deleteArticle(am.getArticle("Tag1"));
		am.deleteArticle(am.getArticle("Tag2"));
		am.deleteArticle(am.getArticle("Tag3"));
	}

	/**
	 * Test method for
	 * {@link de.d3web.we.core.semantic.tagging.TaggingMangler#setTags(java.lang.String, java.lang.String, de.d3web.we.core.KnowWEParameterMap)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSetTags() throws IOException {
		KnowWEArticle article1 = KnowWEArticle.createArticle("", "AddTag", type,
				"default_web");
		am.registerArticle(article1);
		tm.setTags("AddTag", "tag1 tag2 tag3", context);
		assertEquals("%%tags\ntag1 tag2 tag3\n%", am.getArticle("AddTag")
				.getSection().getText());
		am.deleteArticle(am.getArticle("AddTag"));
	}

	/**
	 * Test method for
	 * {@link de.d3web.we.core.semantic.tagging.TaggingMangler#searchPages(java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSearchPages() throws IOException {
		KnowWEArticle article1 = KnowWEArticle.createArticle("", "Tag1", type,
				"default_web");
		KnowWEArticle article2 = KnowWEArticle.createArticle("", "Tag2", type,
				"default_web");
		KnowWEArticle article3 = KnowWEArticle.createArticle("", "Tag3", type,
				"default_web");
		am.registerArticle(article1);
		am.registerArticle(article2);
		am.registerArticle(article3);
		tm.addTag("Tag1", "tag", context);
		tm.addTag("Tag2", "leben", context);
		tm.addTag("Tag3", "tod", context);
		tm.addTag("Tag3", "leben", context);
		// remember: article* are not the current articles anymore, changes to
		// the articles by the TaggingMangler do not backpropagate to those
		// variables
		List<GenericSearchResult> pages = tm.searchPages("leben");
		assertEquals(2, pages.size());
		GenericSearchResult a = pages.get(0);
		GenericSearchResult b = pages.get(1);
		assertEquals("Tag3", a.getPagename());
		assertEquals("Tag2", b.getPagename());
		am.deleteArticle(am.getArticle("Tag1"));
		am.deleteArticle(am.getArticle("Tag2"));
		am.deleteArticle(am.getArticle("Tag3"));
	}

}
