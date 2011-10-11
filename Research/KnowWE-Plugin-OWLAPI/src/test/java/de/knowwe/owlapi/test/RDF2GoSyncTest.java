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
package de.knowwe.owlapi.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;

import de.d3web.plugin.test.InitPluginManager;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.compile.packaging.KnowWEPackageManager;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.event.ArticleUpdatesFinishedEvent;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.owlapi.RDF2GoSync;
import de.knowwe.rdf2go.Rdf2GoCore;
import dummies.KnowWETestWikiConnector;

/**
 * Test for the @link{RDF2GoSync} class.
 * 
 * @author Sebastian Furth
 * @created May 25, 2011
 */
public class RDF2GoSyncTest {

	private final IRI testIRI = IRI.create(Vocabulary.BASEURI);

	@BeforeClass
	public static void setUp() throws IOException {
		InitPluginManager.init();
		KnowWEPackageManager.overrideAutocompileArticle(true);
		KnowWEEnvironment.initKnowWE(new KnowWETestWikiConnector());
	}

	@Test
	public void testRDF2GoCoreSync() {
		Set<OWLAxiom> axioms = createSimpleOWLAxioms();
		Section<?> sec = getDummySection();
		Rdf2GoCore core = Rdf2GoCore.getInstance();
		core.init();
		// Add the axioms
		RDF2GoSync.synchronize(axioms, sec, RDF2GoSync.Mode.ADD);
		EventManager.getInstance().fireEvent(new ArticleUpdatesFinishedEvent());
		assertTrue(core.sparqlAsk(SPARQL.ASUBCLASSB));
		// Remove the axioms
		RDF2GoSync.synchronize(axioms, sec, RDF2GoSync.Mode.REMOVE);
		EventManager.getInstance().fireEvent(new ArticleUpdatesFinishedEvent());
		assertFalse(core.sparqlAsk(SPARQL.ASUBCLASSB));
	}

	private Set<OWLAxiom> createSimpleOWLAxioms() {
		OWLAPIConnector connector = OWLAPIConnector.getInstance(testIRI);
		OWLDataFactory factory = connector.getManager().getOWLDataFactory();
		OWLClass clsA = factory.getOWLClass(IRI.create(Vocabulary.A.asJavaURI()));
		OWLClass clsB = factory.getOWLClass(IRI.create(Vocabulary.B.asJavaURI()));
		OWLAxiom axiom = factory.getOWLSubClassOfAxiom(clsA, clsB);
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(axiom);
		return axioms;
	}

	private Section<?> getDummySection() {
		KnowWEArticle article = KnowWEArticle.createArticle("Wayne", "Juckts",
				KnowWEEnvironment.getInstance().getRootType(), "default_web");
		return Section.createSection("Wayne Juckts", article, article.getSection());
	}

	@Test
	public void testOWLAPISync() {

	}

	/**
	 * Private class encapsulating URI/Vocabulary used in the test.
	 * 
	 * @author Sebastian Furth
	 * @created May 25, 2011
	 */
	private static class Vocabulary {

		private static final Rdf2GoCore core = Rdf2GoCore.getInstance();

		public static final String BASEURI = "http://test.de/ontology.owl#";

		public static final URI A = core.createURI(BASEURI + "A");
		public static final URI B = core.createURI(BASEURI + "B");

	}

	/**
	 * Private class encapsulating SPARQL Queries used in the test.
	 * 
	 * @author Sebastian Furth
	 * @created May 25, 2011
	 */
	private static class SPARQL {

		public static String ASUBCLASSB =
				"ASK { <" + Vocabulary.A + "> " +
						"<http://www.w3.org/2000/01/rdf-schema#subClassOf> "
						+ "<" + Vocabulary.B + "> . }";

	}

}
