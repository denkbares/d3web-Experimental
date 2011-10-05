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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.MyTestArticleManager;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.packaging.KnowWEPackageManager;
import de.d3web.we.kdom.KnowWEArticle;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.test.util.Query;
import de.knowwe.rdfs.test.util.Vocabulary;

/**
 * Tests rdfs:subPropertyOf with reasoning 
 * 
 * @see Query, Vocabulary
 * @author Jochen Reutelshoefer
 * @created Sept 21, 2011
 */
public class SubPropertyOfTest {


	private final Rdf2GoCore core = Rdf2GoCore.getInstance();

	private static final String TESTFILE = "src/test/resources/" + "SubPropertyOf-Example.txt";

	@BeforeClass
	public static void setUp() throws IOException {
		InitPluginManager.init();
		KnowWEPackageManager.overrideAutocompileArticle(true);
		MyTestArticleManager.getArticle(TESTFILE);
	}

	@Test
	public void testComplexIRIDefinitionMarkup() {
		
		// asserted
		assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.ISFRIENDOF, Vocabulary.RDFS_SUBPROPERTYOF,Vocabulary.KNOWS )));
		assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.BOB, Vocabulary.ISFRIENDOF,Vocabulary.JIM )));
		
		// subproperty inheritance
		assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.BOB, Vocabulary.KNOWS,Vocabulary.JIM )));
		
		// reflexivity
		assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.ISFRIENDOF, Vocabulary.RDFS_SUBPROPERTYOF,Vocabulary.ISFRIENDOF )));
		assertTrue(core.sparqlAsk(Query.createQuery(Vocabulary.KNOWS, Vocabulary.RDFS_SUBPROPERTYOF,Vocabulary.KNOWS )));

		
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