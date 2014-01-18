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
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.TestArticleManager;
import utils.TestUtils;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.strings.Identifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.compile.test.util.Query;
import de.knowwe.compile.test.util.Vocabulary;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
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
	private final Environment environment = Environment.getInstance();
	private final ArticleManager articleManager = environment.getArticleManager(Environment.DEFAULT_WEB);

	private final ReferenceManager manager = IncrementalCompiler.getInstance().getTerminology();
	private final Rdf2GoCore core = Rdf2GoCore.getInstance();

	private Article getArticle() {
		return articleManager.getArticle(Vocabulary.ARTICLENAME);
	}

	@BeforeClass
	public static void init() throws IOException {
		InitPluginManager.init();
		TestArticleManager.getArticle(TESTFILE);
		TestArticleManager.clear();
	}

	@Before
	public void setUp() throws IOException {
		waitForCompilation();
		TestArticleManager.getArticle(TESTFILE);
		core.commit();
	}

	@Test
	public void testSimpleIRIDefinitionMarkup() {
		// TermDefinitions to be tested
		String[] defs = {
				"subclasso", "Pete", "Peter", "Wuerzburg", "istEin", "wohntIn", "Assi", "Jochen",
				"Perso", "Reinhard" };

		waitForCompilation();

		// Test existence of TermDefinitions
		for (String def : defs) {
			assertTrue("Missing TermDefinition for: " + def,
					manager.getTermDefinitions(new Identifier(def)).size() >= 1);
		}
	}

	@Test
	public void testComplexIRIDefinitionMarkup() {

		waitForCompilation();
		assertTrue(core.sparqlAsk(Query.JOCHENWUERZBURG));
	}

	@Test
	public void testTripleMarkupSimple() {

		waitForCompilation();

		assertTrue(core.sparqlAsk(Query.JOCHENASSI));
		assertTrue(core.sparqlAsk(Query.PETERASSI));
		// This one has compile errors and shouldn't be in the store!
		assertFalse(core.sparqlAsk(Query.ASSIPERSON));
		assertTrue(core.sparqlAsk(Query.PETERWUERZBURG));
		assertTrue(core.sparqlAsk(Query.REINHARDWUERZBURG));
	}

	@Test
	public void testCorrectedSimpleIRIDefinition() throws IOException {
		/* Change text */
		String oldText = "def Perso";
		String newText = "def Person";
		changeText(oldText, newText, SimpleIRIDefintionMarkup.class);

		waitForCompilation();
		core.commit();

		// Check that the statement is now valid
		assertTrue(core.sparqlAsk(Query.ASSIPERSON));
	}

	@Test
	public void testInvalidatedSimpleIRIDefinition() throws IOException {
		/* Change text */
		String oldText = "def Assi";
		String newText = "def Assistent";
		changeText(oldText, newText, SimpleIRIDefintionMarkup.class);

		waitForCompilation();
		core.commit();

		/* Check that the statements are invalid now */
		assertFalse(core.sparqlAsk(Query.JOCHENASSI));
		assertFalse(core.sparqlAsk(Query.PETERASSI));
		assertFalse(core.sparqlAsk(Query.ASSIPERSON));
	}

	@Test
	public void testCorrectedTripleMarkupSimple() throws IOException {
		// Setting the test up again (refactoring for Java7)
		testCorrectedSimpleIRIDefinition();
		testInvalidatedSimpleIRIDefinition();
		// Done setting the test up again
		/* Change text and test */
		String oldText = " Jochen istEin:: Assi";
		String newText = " Jochen istEin:: Assistent";
		changeText(oldText, newText, TripleMarkupSimple.class);

		waitForCompilation();
		core.commit();
		assertTrue(core.sparqlAsk(Query.Update.JOCHENASSISTENT));

		/* Change text and test */
		oldText = " Peter istEin:: Assi";
		newText = " Peter istEin:: Assistent";
		changeText(oldText, newText, TripleMarkupSimple.class);

		waitForCompilation();
		core.commit();
		assertTrue(core.sparqlAsk(Query.Update.PETERASSISTENT));

		/* Change text and test */
		oldText = "Assi subclassof:: Person";
		newText = "Assistent subclassof:: Person";
		changeText(oldText, newText, TripleMarkupSimple.class);

		waitForCompilation();
		core.commit();

		assertTrue(core.sparqlAsk(Query.Update.ASSISTENTPERSON));
	}

	private static void waitForCompilation() {
		try {
			Compilers.getCompilerManager(Environment.DEFAULT_WEB).awaitTermination();
		}
		catch (InterruptedException e) {
		}
	}

	@Test
	public void testChangeAll() throws IOException {
		// Setting the test up again (refactoring for Java7)
		testCorrectedSimpleIRIDefinition();
		testInvalidatedSimpleIRIDefinition();
		String oldText = " Jochen istEin:: Assi";
		String newText = " Jochen istEin:: Assistent";

		waitForCompilation();
		core.commit();
		changeText(oldText, newText, TripleMarkupSimple.class);

		/* Change text and test */
		oldText = " Peter istEin:: Assi";
		newText = " Peter istEin:: Assistent";

		waitForCompilation();
		core.commit();
		changeText(oldText, newText, TripleMarkupSimple.class);

		/* Change text and test */
		oldText = "Assi subclassof:: Person";
		newText = "Assistent subclassof:: Person";

		waitForCompilation();
		core.commit();
		changeText(oldText, newText, TripleMarkupSimple.class);

		/* replace the whole text */
		oldText = getArticle().getRootSection().getText();
		newText = "def Schnurtzelpieper livesIn:: Dingenskirchen" + "\n\n" + "def is"
				+ "\n\n" + "def livesIn" + "\n\n" + "def Dingenskirchen" + "\n\n"
				+ "def inDaHouse" + "\n\n"
				+ "{Schnurtzelpieper is:: inDaHouse}" + "\n\n";

		waitForCompilation();
		core.commit();
		changeText(oldText, newText, RootType.class);

		// Done setting the test up again

		waitForCompilation();
		core.commit();

		/* Check that the old statements are invalid now */
		assertFalse(core.sparqlAsk(Query.ASSIPERSON));
		assertFalse(core.sparqlAsk(Query.JOCHENASSI));
		assertFalse(core.sparqlAsk(Query.JOCHENWUERZBURG));
		assertFalse(core.sparqlAsk(Query.PETERASSI));
		assertFalse(core.sparqlAsk(Query.PETERWUERZBURG));
		assertFalse(core.sparqlAsk(Query.REINHARDWUERZBURG));
		assertFalse(core.sparqlAsk(Query.Update.ASSISTENTPERSON));
		assertFalse(core.sparqlAsk(Query.Update.JOCHENASSISTENT));
		assertFalse(core.sparqlAsk(Query.Update.PETERASSISTENT));
		/* check that new statements are valid */
		assertTrue(core.sparqlAsk(Query.Update.SCHNURTZELPIEPERDINGENSKIRCHEN));
		assertTrue(core.sparqlAsk(Query.Update.SCHNURTZELPIEPERINDAHOUSE));
	}

	private <T extends Type> void changeText(String oldText, String newText,
			Class<T> sectionType) throws IOException {
		Section<T> oldSection = findSectionWithText(oldText, sectionType);
		if (oldSection != null) {
			Map<String, String> nodesMap = new HashMap<String, String>();
			nodesMap.put(oldSection.getID(), newText);
			UserActionContext context = TestUtils.createTestActionContext("", "");
			Sections.replaceSections(context, nodesMap).sendErrors(context);
		}
		else {
			Logger.getLogger(getClass().getName()).severe("Unable to get section with text: " +
					oldText);
		}
	}

	private <T extends Type> Section<T> findSectionWithText(String text, Class<T>
			sectionType) {
		Section<?> root = getArticle().getRootSection();
		List<Section<T>> typedSections = new LinkedList<Section<T>>();
		Sections.findSuccessorsOfType(root, sectionType, typedSections);
		for (Section<T> section : typedSections) {
			if (section.getText().equals(text)) {
				return section;
			}
		}
		return null;
	}

	@After
	public void tearDown() {
		waitForCompilation();
		TestArticleManager.clear();
	}

}
