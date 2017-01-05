/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.rdfs.wikiObjectModel.test;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import utils.TestArticleManager;

import com.denkbares.semanticcore.CachedTupleQueryResult;
import com.denkbares.plugin.test.InitPluginManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.sparql.utils.SparqlQuery;

import static org.junit.Assert.assertTrue;

public class WikiObjectModelTesOFF {

	private static final String TESTFILE = "src/test/resources/"
			+ "TestPage.txt";

	@BeforeClass
	public static void setUp() throws IOException {
		InitPluginManager.init();

		TestArticleManager.getArticle(TESTFILE);
	}

	/**
	 * test image exists as such in repo and illustrates the content
	 * 
	 * @created 09.08.2012
	 */
	@Test
	public void testImageCompilation() {

		//
		SparqlQuery queryImage = new SparqlQuery()
				.SELECT("?x")
				.WHERE("?x rdf:type ns:Image");

		CachedTupleQueryResult resultImage = Rdf2GoCore.getInstance().sparqlSelect(queryImage.toString());
		boolean foundImage = false;
		for (BindingSet queryRow : resultImage) {
			Value value = queryRow.getValue("x");
			if (value.stringValue().endsWith("Wolpertinger.jpg")) {
				foundImage = true;
			}
		}
		assertTrue(foundImage);

		// test illustrates relation
		SparqlQuery queryIllustrates = new SparqlQuery()
				.SELECT("?x")
				.WHERE("?x ns:illustrates <http://valid_dummy_base_url/Wiki.jsp?page=TestPage%23Paragraph+121>");

		CachedTupleQueryResult resultIllustrates = Rdf2GoCore.getInstance().sparqlSelect(
				queryIllustrates.toString());
		boolean foundIllustrates = false;
		for (BindingSet queryRow : resultIllustrates) {
			Value value = queryRow.getValue("x");
			if (value.stringValue().endsWith("Wolpertinger.jpg")) {
				foundIllustrates = true;
			}
		}
		assertTrue(foundIllustrates);
	}

	/**
	 * this test includes subproperty reasoning and transitivity of
	 * describesAspect of over subsections
	 * 
	 * @created 09.08.2012
	 */
	@Test
	public void testDescribesAspectOf() {

		SparqlQuery querydescribesAspect = new SparqlQuery()
				.SELECT("?x")
				.WHERE("?x ns:describesAspectOf lns:Wolpertinger");

		CachedTupleQueryResult resultDescribesAspectOf = Rdf2GoCore.getInstance().sparqlSelect(
				querydescribesAspect.toString());
		boolean foundDescribesAspectOf = false;
		boolean foundParagraph = false;
		boolean foundChapter = false;
		for (BindingSet queryRow : resultDescribesAspectOf) {
			Value value = queryRow.getValue("x");
			if (value.stringValue().endsWith("Wolpertinger.jpg")) {
				foundDescribesAspectOf = true;
			}
			if (value.stringValue().equals(
					"http://valid_dummy_base_url/Wiki.jsp?page=TestPage%23Paragraph+112")) {
				foundParagraph = true;
			}
			if (value.stringValue().equals(
					"http://valid_dummy_base_url/Wiki.jsp?page=TestPage%23Chapter+1")) {
				foundChapter = true;
			}

		}
		assertTrue(foundDescribesAspectOf);
		assertTrue(foundParagraph);
		assertTrue(foundChapter);

	}

	/**
	 * tests whether the parsed sections are properly linked to the kdom section
	 * ids in the repo to be able to retrieve actual contents
	 * 
	 * @created 09.08.2012
	 */
	@Test
	public void testSectionContentKDOMIDCompilation() {

		SparqlQuery queryKDOMID = new SparqlQuery()
				.SELECT("?id")
				.WHERE("<http://valid_dummy_base_url/Wiki.jsp?page=TestPage%23Paragraph+112> ns:hasContentKDOMID ?id");

		CachedTupleQueryResult resultKDOMID = Rdf2GoCore.getInstance().sparqlSelect(
				queryKDOMID.toString());
		String kdomID = null;
		for (BindingSet queryRow : resultKDOMID) {
			Value value = queryRow.getValue("id");
			kdomID = value.toString().substring(value.toString().lastIndexOf("#KDOM_") + 6);
		}

		Section<?> contentSectionOfParagraph = Sections.get(kdomID);
		assertTrue(contentSectionOfParagraph.getText().contains(
				"Die Herkunft der Bezeichnung Wolpertinger ist "));

	}

	@AfterClass
	public static void tearDown() {
		// Remove the statements created in the test to avoid problems
		Article article = TestArticleManager.getArticle(TESTFILE);
		Rdf2GoCore.getInstance().removeAllCachedStatements();
		// Finally remove the formerly created article
		TestArticleManager.clear();
	}

}
