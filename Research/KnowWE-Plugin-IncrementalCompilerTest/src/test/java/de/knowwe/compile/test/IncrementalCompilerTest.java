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
package de.knowwe.compile.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
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
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.compile.test.util.Query;
import de.knowwe.compile.test.util.Vocabulary;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * Test for the parser of the @link{IncrementalCompiler}. A special test markup
 * is used in an example page. This page will be parsed by this test. The
 * created RDF-Triples and TermDefinitions will be tested using SPARQL in the
 * first part of this test.
 * 
 * In the second part a couple of updates are performed in order to validate or
 * invalidate some statements. The correct behavior of the IncrementalCompiler
 * is tested with another bunch of SPARQL-Queries.
 * 
 * @see Query, Vocabulary
 * @author Sebastian Furth
 * @created Jun 17, 2011
 */
public class IncrementalCompilerTest {

	/* The underlying test article */
	private static final String TESTFILE = "src/main/resources/" + Vocabulary.ARTICLENAME + ".txt";

	/* Just for convenience */
	private final KnowWEEnvironment environment = KnowWEEnvironment.getInstance();
	private final KnowWEArticleManager articleManager = environment.getArticleManager(KnowWEEnvironment.DEFAULT_WEB);
	private final KnowWEArticle article = articleManager.getArticle(Vocabulary.ARTICLENAME);

	private ReferenceManager manager = IncrementalCompiler.getInstance().getTerminology();
	private final Rdf2GoCore core = Rdf2GoCore.getInstance();

	@BeforeClass
	public static void setUp() throws IOException {
		InitPluginManager.init();
		KnowWEPackageManager.overrideAutocompileArticle(true);
		MyTestArticleManager.getArticle(TESTFILE);
	}

	@Test
	public void testSimpleIRIDefinitionMarkup() {
		// TermDefinitions to be tested
		String[] defs = {
				"subclasso", "Pete", "Peter", "Wuerzburg", "istEin", "wohntIn", "Assi", "Jochen",
				"Perso", "Reinhard" };

		// Test existence of TermDefinitions
		for (String def : defs) {
			assertTrue("Missing TermDefinition for: " + def,
					manager.getTermDefinitions(def).size() >= 1);
		}
	}

	@Test
	public void testComplexIRIDefinitionMarkup() {
		assertTrue(core.sparqlAsk(Query.JOCHENWUERZBURG));
	}

	@Test
	public void testTripleMarkupSimple() {
		assertTrue(core.sparqlAsk(Query.JOCHENASSI));
		assertTrue(core.sparqlAsk(Query.PETERASSI));
		// This one has compile errors and shouldn't be in the store!
		assertFalse(core.sparqlAsk(Query.ASSIPERSON));
		assertTrue(core.sparqlAsk(Query.PETERWUERZBURG));
		assertTrue(core.sparqlAsk(Query.REINHARDWUERZBURG));
	}

	@Test
	public void testCorrectedSimpleIRIDefinition() {
		/* Change text */
		String oldText = "def Perso";
		String newText = "def Person";
		changeText(oldText, newText, SimpleIRIDefintionMarkup.class);
		// Check that the statement is now valid
		assertTrue(core.sparqlAsk(Query.ASSIPERSON));
	}

	@Test
	public void testInvalidatedSimpleIRIDefinition() {
		/* Change text */
		String oldText = "def Assi";
		String newText = "def Assistent";
		changeText(oldText, newText, SimpleIRIDefintionMarkup.class);
		/* Check that the statements are invalid now */
		assertFalse(core.sparqlAsk(Query.JOCHENASSI));
		assertFalse(core.sparqlAsk(Query.PETERASSI));
		assertFalse(core.sparqlAsk(Query.ASSIPERSON));
	}

	@Test
	public void testCorrectedTripleMarkupSimple() {
		/* Change text and test */
		String oldText = " Jochen istEin:: Assi";
		String newText = " Jochen istEin:: Assistent";
		changeText(oldText, newText, TripleMarkupSimple.class);
		assertTrue(core.sparqlAsk(Query.Update.JOCHENASSISTENT));
		/* Change text and test */
		oldText = " Peter istEin:: Assi";
		newText = " Peter istEin:: Assistent";
		changeText(oldText, newText, TripleMarkupSimple.class);
		assertTrue(core.sparqlAsk(Query.Update.PETERASSISTENT));
		/* Change text and test */
		oldText = "Assi subclassof:: Person";
		newText = "Assistent subclassof:: Person";
		changeText(oldText, newText, TripleMarkupSimple.class);
		assertTrue(core.sparqlAsk(Query.Update.ASSISTENTPERSON));
	}

	private <T extends Type> void changeText(String oldText, String newText,
			Class<T> sectionType) {
		Section<T> oldSection = findSectionWithText(oldText, sectionType);
		if (oldSection != null) {
			Map<String, String> nodesMap = new HashMap<String, String>();
			nodesMap.put(oldSection.getID(), newText);
			articleManager.replaceKDOMNodesSaveAndBuild(TestUtils.createTestActionContext("",
					""),
					Vocabulary.ARTICLENAME, nodesMap);
		}
		else {
			Logger.getLogger(getClass()).fatal("Unable to get section with text: " +
					oldText);
		}
	}

	private <T extends Type> Section<T> findSectionWithText(String text, Class<T>
			sectionType) {
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

	@AfterClass
	public static void tearDown() {
		// Remove the statements created in the test to avoid problems
		KnowWEArticle article = MyTestArticleManager.getArticle(TESTFILE);
		Rdf2GoCore.getInstance().removeArticleStatementsRecursive(article);
		Rdf2GoCore.getInstance().removeAllCachedStatements();
		// Finally remove the formerly created article
		MyTestArticleManager.clear();
	}

}
