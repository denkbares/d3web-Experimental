/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.onte.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.MyTestArticleManager;
import utils.TestUtils;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.packaging.KnowWEPackageManager;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.knowwe.kdom.classHierarchy.SubClassingDashTreeElement;
import de.knowwe.kdom.turtle.TurtleMarkup;
import de.knowwe.onte.test.util.Query;
import de.knowwe.onte.test.util.Vocabulary;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * Changes successively the article tested in @link{OntEParserTest} and checks
 * if the changes are applied to the triple store.
 * 
 * @author Sebastian Furth
 * @created Apr 28, 2011
 */
public class OntEUpdateTest {

	/* The underlying test article */
	private static final String TESTFILE = "src/test/resources/" + Vocabulary.ARTICLENAME + ".txt";

	/* Just for convenience */
	private final Rdf2GoCore core = Rdf2GoCore.getInstance();
	private final KnowWEEnvironment environment = KnowWEEnvironment.getInstance();
	private final KnowWEArticleManager manager = environment.getArticleManager(KnowWEEnvironment.DEFAULT_WEB);
	private final KnowWEArticle article = manager.getArticle(Vocabulary.ARTICLENAME);

	@BeforeClass
	public static void setUp() throws IOException {
		InitPluginManager.init();
		KnowWEPackageManager.overrideAutocompileArticle(true);
		MyTestArticleManager.getArticle(TESTFILE);
	}

	@Test
	public void testSubjectDefinitionChange() {
		/* Change text */
		String oldText = "[def Alexander the Great type:: Person]";
		String newText = "[def Alexander the Little type:: Person]";
		changeText(oldText, newText, TurtleMarkup.class);
		/* Check that old triples are invalid now */
		assertFalse(core.sparqlAsk(Query.ALEXANDERPERSON));
		assertFalse(core.sparqlAsk(Query.ALEXANDERDEATHPLACE));
		assertFalse(core.sparqlAsk(Query.ALEXANDERYEAROFDEATH));
		assertFalse(core.sparqlAsk(Query.THISDESCRIBES));
		// Check new triple
		assertTrue(core.sparqlAsk(Query.Update.ALEXANDERLITTLEPERSON));
	}

	@Test
	public void testSubjectChange() {
		/* Change text */
		String oldText = "[Alexander the Great deathPlace:: Babylon]";
		String newText = "[Alexander the Little deathPlace:: Babylon]";
		changeText(oldText, newText, TurtleMarkup.class);
		// Check that old triple is invalid now */
		assertFalse(core.sparqlAsk(Query.ALEXANDERDEATHPLACE));
		// Check new triple
		assertTrue(core.sparqlAsk(Query.Update.ALEXANDERLITTLEDEATHPLACE));
	}

	@Test
	public void testPredicateChange() {
		/* Change text */
		String oldText2 = "[deathPlace range:: Location]";
		String newText2 = "[deathPlace domain:: Location]";
		changeText(oldText2, newText2, TurtleMarkup.class);
		// Check that old triple is invalid now */
		assertFalse(core.sparqlAsk(Query.DEATHPLACERANGE));
		// Check new triple
		assertTrue(core.sparqlAsk(Query.Update.DEATHPLACEDOMAIN));

		/* Change text */
		String oldText = "[deathPlace domain:: Person]";
		String newText = "[deathPlace range:: Person]";
		changeText(oldText, newText, TurtleMarkup.class);
		// Check that old triple is invalid now */
		assertFalse(core.sparqlAsk(Query.DEATHPLACEDOMAIN));
		// Check new triple
		assertTrue(core.sparqlAsk(Query.Update.DEATHPLACERANGE));


		// TODO? Check validity of other triples...
	}

	@Test
	public void testObjectChange() {
		/* Change text */
		String oldText = "[def deathPlace type:: ObjectProperty]";
		String newText = "[def deathPlace type:: DatatypeProperty]";
		changeText(oldText, newText, TurtleMarkup.class);
		// Check that old triple is invalid now */
		assertFalse(core.sparqlAsk(Query.DEATHPLACE));
		// Check new triple
		assertTrue(core.sparqlAsk(Query.Update.DEATHPLACE));
	}

	@Test
	public void testLiteralObjectChange() {
		/* Change text */
		String oldText = "[Alexander the Great yearOfDeath:: 323bc]";
		String newText = "[Alexander the Little yearOfDeath:: 2011]";
		changeText(oldText, newText, TurtleMarkup.class);
		// Check that old triple is invalid now */
		assertFalse(core.sparqlAsk(Query.ALEXANDERYEAROFDEATH));
		// Check new triple
		assertTrue(core.sparqlAsk(Query.Update.ALEXANDERYEAROFDEATH));
	}

	@Test
	public void testThisChanges() {
		/* Change text */
		String oldText = "[def this type:: Historical Essay]";
		String newText = "[def this type:: Person]";
		changeText(oldText, newText, TurtleMarkup.class);
		oldText = "[this describes:: Alexander the Great]";
		newText = "[this describes:: Alexander the Little]";
		changeText(oldText, newText, TurtleMarkup.class);
		// Check that old triple is invalid now */
		assertFalse(core.sparqlAsk(Query.THISTYPE));
		assertFalse(core.sparqlAsk(Query.THISDESCRIBES));
		// Check new triple
		assertTrue(core.sparqlAsk(Query.Update.THISTYPE));
		assertTrue(core.sparqlAsk(Query.Update.THISDESCRIBES));
	}

	@Test
	public void testSubClassingChange() {
		/* Change text */
		String oldText = "King";
		String newText = "Queen";
		changeText(oldText, newText, SubClassingDashTreeElement.class);
		oldText = "[def King isA:: Class]";
		newText = "[def Queen isA:: Class]";
		changeText(oldText, newText, TurtleMarkup.class);
		// Check that old triple is invalid now */
		// TODO: FIX! Triple is not updated / invalidated!
		// assertFalse(core.sparqlAsk(Query.PERSONKING));
		// Check new triple
		assertTrue(core.sparqlAsk(Query.Update.PERSONQUEEN));
	}

	private <T extends Type> void changeText(String oldText, String newText, Class<T> sectionType) {
		Section<T> oldSection = findSectionWithText(oldText, sectionType);
		if (oldSection != null) {
			Map<String, String> nodesMap = new HashMap<String, String>();
			nodesMap.put(oldSection.getID(), newText);
			manager.replaceKDOMNodesSaveAndBuild(TestUtils.createTestActionContext("", ""),
					Vocabulary.ARTICLENAME, nodesMap);
		}
		else {
			Logger.getLogger(getClass()).warn("Unable to get section with text: " + oldText);
		}
	}

	/* TODO? Move to @link{Sections} */
	private <T extends Type> Section<T> findSectionWithText(String text, Class<T> sectionType) {
		Section<?> root = article.getSection();
		List<Section<T>> typedSections = new LinkedList<Section<T>>();
		Sections.findSuccessorsOfType(root, sectionType, typedSections);
		for (Section<T> section : typedSections) {
			if (section.getText().equals(text)) {
				return section;
			}
		}
		return null;
	}

}
