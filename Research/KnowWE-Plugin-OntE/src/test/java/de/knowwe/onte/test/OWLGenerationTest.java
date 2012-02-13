// /*
// * Copyright (C) 2011 University Wuerzburg, Computer Science VI
// *
// * This is free software; you can redistribute it and/or modify it under the
// * terms of the GNU Lesser General Public License as published by the Free
// * Software Foundation; either version 3 of the License, or (at your option)
// any
// * later version.
// *
// * This software is distributed in the hope that it will be useful, but
// WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS
// * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
// more
// * details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with this software; if not, write to the Free Software Foundation,
// * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
// FSF
// * site: http://www.fsf.org.
// */
// package de.knowwe.onte.test;
//
// import static org.junit.Assert.assertTrue;
//
// import java.io.IOException;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.Map;
// import java.util.Set;
//
// import junit.framework.TestCase;
//
// import org.junit.AfterClass;
// import org.junit.BeforeClass;
// import org.junit.Test;
// import org.semanticweb.owlapi.model.OWLEntity;
// import org.semanticweb.owlapi.model.OWLNamedIndividual;
//
// import utils.MyTestArticleManager;
// import de.d3web.plugin.test.InitPluginManager;
// import de.knowwe.core.compile.packaging.KnowWEPackageManager;
// import de.knowwe.onte.test.util.OWLGenerationVocabulary;
// import de.knowwe.onte.test.util.OWLReasoningUtils;
//
// /**
// * Test for the Manchester OWL syntax in the KnowWE-OntE-Plugin for KnowWE.
// This
// * {@link TestCase} checks the functionality of a subset of the Generation
// * ontology.
// *
// * @author Stefan Mark
// * @created 23.09.2011
// */
// public class OWLGenerationTest {
//
// /* The underlying test article */
// private static final String TESTFILE = "src/test/resources/"
// + OWLGenerationVocabulary.ARTICLENAME
// + ".txt";
//
// @BeforeClass
// public static void setUp() throws IOException {
// InitPluginManager.init();
// KnowWEPackageManager.overrideAutocompileArticle(true);
// MyTestArticleManager.getArticle(TESTFILE);
//
// // OWLEntailment.write();
// }
//
// @Test
// public void testConsistency() {
// assertTrue("Ontology is inconsistent", OWLReasoningUtils.isConsistent());
// }
//
// @Test
// public void testObjectPropertiesDeclarations() {
// Set<OWLEntity> entities = new HashSet<OWLEntity>();
// entities.add(OWLGenerationVocabulary.HASCHILD);
// entities.add(OWLGenerationVocabulary.HASPARENT);
// entities.add(OWLGenerationVocabulary.HASSEX);
// entities.add(OWLGenerationVocabulary.HASSIBLING);
// checkEntities(entities);
// }
//
// @Test
// public void testClassDeclarations() {
// Set<OWLEntity> entities = new HashSet<OWLEntity>();
// entities.add(OWLGenerationVocabulary.MAN);
// entities.add(OWLGenerationVocabulary.WOMAN);
// entities.add(OWLGenerationVocabulary.PERSON);
// entities.add(OWLGenerationVocabulary.SEX);
// checkEntities(entities);
// }
//
// @Test
// public void testIndividualDeclarations() {
// Set<OWLEntity> entities = new HashSet<OWLEntity>();
// entities.add(OWLGenerationVocabulary.PETER);
// entities.add(OWLGenerationVocabulary.MALESEX);
// entities.add(OWLGenerationVocabulary.FEMALESEX);
// entities.add(OWLGenerationVocabulary.GEMMA);
// entities.add(OWLGenerationVocabulary.WILLIAM);
// entities.add(OWLGenerationVocabulary.MATT);
// entities.add(OWLGenerationVocabulary.MATTHEW);
//
// for (OWLEntity e : entities) {
// boolean isEntailed = OWLReasoningUtils.checkIndividualDeclaration(e);
// assertTrue(e.getIRI() + " is not entailed in the Ontology!", isEntailed);
// }
// }
//
// @Test
// public void testInferredSubClassOf() {
// Map<String, String> m = new HashMap<String, String>();
// m.put("Woman", "Female");
// m.put("Daughter", "Woman");
// m.put("Mother", "Woman");
// m.put("Sister", "Woman");
// m.put("GrandMother", "Mother");
//
// m.put("Man", "Male");
// m.put("Son", "Man");
// m.put("Father", "Man");
// m.put("Brother", "Man");
// m.put("GrandFather", "Father");
//
// for (String decendant : m.keySet()) {
//
// String parent = m.get(decendant);
// boolean isSub = OWLReasoningUtils.isSubClassOf(parent, decendant);
// assertTrue(decendant + " not subClassOf " + parent + ", but should be!",
// isSub);
// }
// }
//
// @Test
// public void testSimpleObjectPropertiesComplex() {
// assertTrue("hasSex is not functional, but should be!",
// OWLReasoningUtils.check(OWLGenerationVocabulary.HASSEX_FUNCTIONAL));
// assertTrue("Person is not rdfs:range of hasSex, but should be!",
// OWLReasoningUtils.check(OWLGenerationVocabulary.HASSEX_RANGE));
// assertTrue("hasParent is not inverseOf hasChild, but should be!",
// OWLReasoningUtils.check(OWLGenerationVocabulary.HASPARENT_INVERSE));
// assertTrue("hasChild is not inverseOf hasParent, but should be!",
// OWLReasoningUtils.check(OWLGenerationVocabulary.HASCHILD_INVERSE));
// assertTrue("hasSibling is not symmetric, but should be!",
// OWLReasoningUtils.check(OWLGenerationVocabulary.HASSIBLING_SYMMETRIC));
// assertTrue("hasSex is symmetric, but should not be!",
// !OWLReasoningUtils.check(OWLGenerationVocabulary.HASSEX_SYMMETRIC));
// }
//
// @Test
// public void testSameIndividuals() {
// assertTrue("Individuals should be same!",
// OWLReasoningUtils.checkSameIndividual(
// OWLGenerationVocabulary.MATT, OWLGenerationVocabulary.MATTHEW));
// assertTrue("Individuals should not be same!",
// !OWLReasoningUtils.checkSameIndividual(
// OWLGenerationVocabulary.MATTHEW, OWLGenerationVocabulary.PETER));
// assertTrue("Individuals should be same!",
// OWLReasoningUtils.checkSameIndividual(
// OWLGenerationVocabulary.MATTHEW, OWLGenerationVocabulary.MATT));
// }
//
// @Test
// public void testTypeIndividuals() {
//
// Set<OWLNamedIndividual> individuals = new HashSet<OWLNamedIndividual>();
// individuals.add(OWLGenerationVocabulary.MATT);
// individuals.add(OWLGenerationVocabulary.GEMMA);
//
// for (OWLNamedIndividual owlNamedIndividual : individuals) {
// boolean isEntailed =
// OWLReasoningUtils.checkTypeIndividual(owlNamedIndividual,
// OWLGenerationVocabulary.PERSON);
// assertTrue(owlNamedIndividual.getIRI() +
// " is not of the type PERSON, but should be!",
// isEntailed);
// }
//
// for (OWLNamedIndividual owlNamedIndividual : individuals) {
// boolean isEntailed =
// OWLReasoningUtils.checkTypeIndividual(owlNamedIndividual,
// OWLGenerationVocabulary.SEX);
// assertTrue(owlNamedIndividual.getIRI() +
// " is of type SEX, but should not be!",
// !isEntailed);
// }
// }
//
// @Test
// public void testFactsIndividuals() {
// assertTrue("MATT should have a parent called PETER",
// OWLReasoningUtils.check(OWLGenerationVocabulary.MATT_FACTS));
// assertTrue("GEMMA should haveSex FEMALESEX",
// OWLReasoningUtils.check(OWLGenerationVocabulary.GEMMA_FACTS));
// }
//
// @AfterClass
// public static void tearDown() {
// MyTestArticleManager.clear();
// }
//
// /**
// * Checks if the OWLDeclarations for class, properties and individuals can
// * be found.
// *
// * @created 27.09.2011
// * @param entities
// */
// private static void checkEntities(Set<OWLEntity> entities) {
// for (OWLEntity e : entities) {
// boolean isEntailed = OWLReasoningUtils.checkOWLDeclaration(e);
// assertTrue(e.getIRI() + " is not entailed in the Ontology!", isEntailed);
// }
// }
// }
