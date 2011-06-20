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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.MyTestArticleManager;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.core.packaging.KnowWEPackageManager;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.compile.test.util.Query;
import de.knowwe.compile.test.util.Vocabulary;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * Test for the parser of the IncrementalCompilerTest-Markup. All Markups are
 * used in an example page. This page will be parsed by this test. The created
 * RDF-Triples and TermDefinitions will be tested using SPARQL.
 * 
 * @author Sebastian Furth
 * @created Jun 17, 2011
 */
public class IncrementalCompilerParserTest {

	/* The underlying test article */
	private static final String TESTFILE = "src/main/resources/" + Vocabulary.ARTICLENAME + ".txt";

	/* Just for convenience */
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

	@AfterClass
	public static void tearDown() {
		MyTestArticleManager.clear();
	}

}
