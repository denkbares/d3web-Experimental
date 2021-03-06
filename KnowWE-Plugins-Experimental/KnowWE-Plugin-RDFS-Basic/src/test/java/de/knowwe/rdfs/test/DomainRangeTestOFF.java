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
package de.knowwe.rdfs.test;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.TestArticleManager;

import com.denkbares.plugin.test.InitPluginManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.test.util.Query;
import de.knowwe.rdfs.test.util.Vocabulary;

import static org.junit.Assert.assertTrue;

/**
 * Tests rdfs:domain and rdfs:range with reasoning
 * 
 * @see Query, Vocabulary
 * @author Jochen Reutelshoefer
 * @created Sept 21, 2011
 */
public class DomainRangeTestOFF {

	private final Rdf2GoCore core = Rdf2GoCore.getInstance();

	private static final String TESTFILE = "src/test/resources/" + "Domain-Range-Example.txt";

	@BeforeClass
	public static void setUp() throws IOException {
		InitPluginManager.init();

		// System.out.println(IncrementalCompiler.getInstance().getTerminology().toString());

		TestArticleManager.getArticle(TESTFILE);
	}

	@Test
	public void testDomainAndRange() {

		// core.dumpModel();

		// asserted
		assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.EATS, Vocabulary.RDFS_RANGE,
				Vocabulary.FOOD)));
		assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.EATS, Vocabulary.RDFS_DOMAIN,
				Vocabulary.ANIMAL)));
		assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.BOB, Vocabulary.EATS,
				Vocabulary.REDAPPLE)));

		// class membership reasoning
		assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.BOB, Vocabulary.RDF_TYPE,
				Vocabulary.ANIMAL)));
		assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.REDAPPLE, Vocabulary.RDF_TYPE,
				Vocabulary.FOOD)));

		// reflexivity
		// assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.ISFRIENDOF,
		// Vocabulary.RDFS_SUBPROPERTYOF,Vocabulary.ISFRIENDOF )));
		// assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.KNOWS,
		// Vocabulary.RDFS_SUBPROPERTYOF,Vocabulary.KNOWS )));

	}

	@AfterClass
	public static void tearDown() {
		// Remove the statements created in the test to avoid problems
		Article article = TestArticleManager.getArticle(TESTFILE);
		Rdf2GoCore.getInstance().removeAllCachedStatements();
		// Finally remove the formerly created article
		// TestArticleManager.deleteArticle(TESTFILE);
		// IncrementalCompiler.getInstance().reset();
		TestArticleManager.clear();

		System.out.println("teardown!");
	}

}
