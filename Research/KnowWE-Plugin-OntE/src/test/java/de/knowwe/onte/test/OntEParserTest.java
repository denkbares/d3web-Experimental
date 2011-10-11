package de.knowwe.onte.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import utils.MyTestArticleManager;
import de.d3web.plugin.test.InitPluginManager;
import de.knowwe.core.compile.packaging.KnowWEPackageManager;
import de.knowwe.onte.test.util.Query;
import de.knowwe.onte.test.util.Vocabulary;
import de.knowwe.rdf2go.Rdf2GoCore;

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

/**
 * Test for the parser and compiler for OntE knowledge. All OntE Markups are
 * used in an example page. This page will be parsed by this test. The created
 * RDF-Triples will be tested using SPARQL.
 * 
 * @author Sebastian Furth
 * @created Apr 1, 2011
 */
public class OntEParserTest {

	/* The underlying test article */
	private static final String TESTFILE = "src/test/resources/" + Vocabulary.ARTICLENAME + ".txt";

	/* Just for convenience */
	private final Rdf2GoCore core = Rdf2GoCore.getInstance();

	@BeforeClass
	public static void setUp() throws IOException {
		InitPluginManager.init();
		KnowWEPackageManager.overrideAutocompileArticle(true);
		MyTestArticleManager.getArticle(TESTFILE);
	}

	@Test
	public void testIndividualsAndClasses() {
		assertTrue(core.sparqlAsk(Query.ALEXANDERPERSON));
		assertTrue(core.sparqlAsk(Query.PERSONCLASS));
	}

	@Test
	public void testObjectProperties() {
		assertTrue(core.sparqlAsk(Query.DEATHPLACE));
		assertTrue(core.sparqlAsk(Query.DEATHPLACEDOMAIN));
		assertTrue(core.sparqlAsk(Query.DEATHPLACERANGE));
		assertTrue(core.sparqlAsk(Query.DEATHPLACESUBPROPERTY));
		assertTrue(core.sparqlAsk(Query.ALEXANDERDEATHPLACE));
	}

	@Test
	public void testDatatypeProperties() {
		assertTrue(core.sparqlAsk(Query.YEAROFDEATH));
		assertTrue(core.sparqlAsk(Query.ALEXANDERYEAROFDEATH));
	}

	@Test
	public void testThisTriples() {
		assertTrue(core.sparqlAsk(Query.THISTYPE));
		assertTrue(core.sparqlAsk(Query.THISDESCRIBES));
	}

	@Test
	public void testClassHierarchy() {
		assertTrue(core.sparqlAsk(Query.CONCEPTOFHISTORYPERSON));
		assertTrue(core.sparqlAsk(Query.CONCEPTOFHISTORYLOCATION));
		assertTrue(core.sparqlAsk(Query.CONCEPTOFHISTORYLOCATION));
		assertTrue(core.sparqlAsk(Query.PERSONKING));
		assertTrue(core.sparqlAsk(Query.LOCATIONCITY));
		assertTrue(core.sparqlAsk(Query.LOCATIONISLAND));
	}

}
