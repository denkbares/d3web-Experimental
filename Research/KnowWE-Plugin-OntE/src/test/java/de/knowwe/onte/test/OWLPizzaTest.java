 /*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but
 WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
 FSF
 * site: http://www.fsf.org.
 */
 package de.knowwe.onte.test;

 import static org.junit.Assert.assertTrue;

 import java.io.IOException;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;

 import junit.framework.TestCase;

 import org.junit.AfterClass;
 import org.junit.BeforeClass;
 import org.junit.Test;
 import org.semanticweb.owlapi.model.OWLEntity;
 import org.semanticweb.owlapi.model.OWLNamedIndividual;
 import org.semanticweb.owlapi.model.OWLObjectProperty;

 import utils.MyTestArticleManager;
 import de.d3web.plugin.test.InitPluginManager;
 import de.knowwe.core.compile.packaging.KnowWEPackageManager;
 import de.knowwe.onte.test.util.OWLReasoningUtils;
 import de.knowwe.onte.test.util.OWLPizzaVocabulary;

 /**
 * Test for the Manchester OWL syntax in the KnowWE-OntE-Plugin for KnowWE.
 This
 * {@link TestCase} checks the functionality of a subset of the Pizza
 ontology.
 *
 * @author Stefan Mark
 * @created 23.09.2011
 */
 public class OWLPizzaTest {

 /* The underlying test article */
 private static final String TESTFILE = "src/test/resources/"
 + OWLPizzaVocabulary.ARTICLENAME
 + ".txt";

 @BeforeClass
 public static void setUp() throws IOException {
 InitPluginManager.init();
 KnowWEPackageManager.overrideAutocompileArticle(true);
 MyTestArticleManager.getArticle(TESTFILE);

 // OWLEntailment.write();
 }

 @Test
 public void testConsistency() {
 assertTrue("Ontology is consistent, but should not be",
 !OWLReasoningUtils.isConsistent());
 }

 @Test
 public void testObjectPropertiesDeclarations() {

 Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();
 properties.add(OWLPizzaVocabulary.HASBASE);
 properties.add(OWLPizzaVocabulary.HASINGREDIENT);
 properties.add(OWLPizzaVocabulary.HASTOPPING);
 properties.add(OWLPizzaVocabulary.HASSPICINESS);
 properties.add(OWLPizzaVocabulary.ISBASEOF);
 properties.add(OWLPizzaVocabulary.ISINGREDIENTOF);

 for (OWLObjectProperty p : properties) {

 boolean found = OWLReasoningUtils.checkOWLDeclaration(p);
 assertTrue(OWLReasoningUtils.getDisplayName(p)
 + " ObjectProperty could not be found, but should be!", found);
 }
 }

 @Test
 public void testClassDeclarations() {

 Set<OWLEntity> entities = new HashSet<OWLEntity>();
 entities.add(OWLPizzaVocabulary.PIZZA);
 entities.add(OWLPizzaVocabulary.PIZZABASE);
 entities.add(OWLPizzaVocabulary.ICECREAM);
 entities.add(OWLPizzaVocabulary.INTERESTING_PIZZA);
 entities.add(OWLPizzaVocabulary.NAMEDPIZZA);
 entities.add(OWLPizzaVocabulary.COUNTRY);
 checkEntities(entities);
 }

 @Test
 public void testIndividualDeclarations() {

 Set<OWLEntity> entities = new HashSet<OWLEntity>();
 entities.add(OWLPizzaVocabulary.ITALY);
 entities.add(OWLPizzaVocabulary.ENGLAND);
 entities.add(OWLPizzaVocabulary.FRANCE);
 entities.add(OWLPizzaVocabulary.GERMANY);
 entities.add(OWLPizzaVocabulary.AMERICA);

 // check for above individuals
 for (OWLEntity e : entities) {
 boolean isEntailed = OWLReasoningUtils.checkIndividualDeclaration(e);
 String msg = e.getEntityType() + ": '" + OWLReasoningUtils.getDisplayName(e)
 + "' "
 + " could not be found, but should be !";
 assertTrue(msg, isEntailed);
 }

 // check that all above individuals are just individuals
 assertTrue("Individual 'Italy' is also a class, but should not be!",
 !OWLReasoningUtils.checkOWLDeclaration(OWLPizzaVocabulary.ITALY_CLAZZ));
 assertTrue("Class 'Country' should not be an Individual, but is!",
 !OWLReasoningUtils.checkOWLDeclaration(OWLPizzaVocabulary.COUNTRY_IND));

 // test different individuals misc frame
 assertTrue(
 "Individuals 'Italy', 'America', 'France', 'Germany' and 'England' are not different, but should be!",
 OWLReasoningUtils.check(OWLPizzaVocabulary.DIFFERENT_IND));
 }

 @Test
 public void testObjectPropertiesComplex() {

 // check some domain and range values
 assertTrue("Objectproperty 'hasBase' has wrong Domain!",
 OWLReasoningUtils.check(OWLPizzaVocabulary.HASBASE_DOMAIN));
 assertTrue("Objectproperty 'hasBase' has wrong Range!",
 OWLReasoningUtils.check(OWLPizzaVocabulary.HASBASE_RANGE));
 assertTrue("Objectproperty 'hasBase' is not subPropertyOf hasIngredient, but should be!",
 OWLReasoningUtils.check(OWLPizzaVocabulary.HASBASE_SUB));
 assertTrue("Objectproperty 'hasBase' is not INVERSE, but sholud be!",
 OWLReasoningUtils.check(OWLPizzaVocabulary.HASBASE_INV));
 assertTrue("Objectproperty 'hasBase' is not FUNCTIONAL, but sholud be!",
 OWLReasoningUtils.check(OWLPizzaVocabulary.HASBASE_FUN));

 // check inferred subclass hierarchy

 Map<String, String> m = new HashMap<String, String>();
 m.put("hasBase", "hasIngredient");
 m.put("hasTopping", "hasIngredient");
 m.put("isBaseOf", "isIngredientOf");
 m.put("isToppingOf", "isIngredientOf");

 for (String decendant : m.keySet()) {
 String parent = m.get(decendant);
 boolean isSub = OWLReasoningUtils.isSubObjectPropertyOf(parent, decendant);
 assertTrue(decendant + " not subPropertyOf " + parent + ", but should be!",
 isSub);
 }
 }

 @Test
 public void testTypeIndividuals() {
 Set<OWLNamedIndividual> individuals = new HashSet<OWLNamedIndividual>();
 individuals.add(OWLPizzaVocabulary.ITALY);
 individuals.add(OWLPizzaVocabulary.FRANCE);
 individuals.add(OWLPizzaVocabulary.ENGLAND);
 individuals.add(OWLPizzaVocabulary.AMERICA);
 individuals.add(OWLPizzaVocabulary.GERMANY);

 for (OWLNamedIndividual owlNamedIndividual : individuals) {
 assertTrue(owlNamedIndividual.getIRI() +
 " is not of the type COUNTRY, but should be",
 OWLReasoningUtils.checkTypeIndividual(owlNamedIndividual,
 OWLPizzaVocabulary.COUNTRY));
 assertTrue(
 owlNamedIndividual.getIRI() + " is not of the type owl:Thing, but should be",
 OWLReasoningUtils.checkTypeIndividual(owlNamedIndividual,
 OWLPizzaVocabulary.THING));
 assertTrue(owlNamedIndividual.getIRI()
 + " is of the type owl:Nothing, but should not be",
 !OWLReasoningUtils.checkTypeIndividual(owlNamedIndividual,
 OWLPizzaVocabulary.NOTHING));
 }
 }


 @Test
 public void testInferredSubClassOf() {
 Map<String, String> m = new HashMap<String, String>();

 m.put("MeatyPizza", "NonVegetarianPizza");
 m.put("American", "MeatyPizza");
 m.put("LaReine", "MeatyPizza");

 m.put("SpicyTopping", "PizzaTopping");
 m.put("CajunSpiceTopping", "SpicyTopping");
 m.put("HotGreenPepperTopping", "SpicyTopping");
 m.put("TobascoPepperSauce", "SpicyTopping");
 m.put("JalapenoPepperTopping", "SpicyTopping");

 m.put("ThinAndCrispyPizza", "Pizza");
 m.put("RealItalianPizza", "ThinAndCrispyPizza");
 m.put("Napoletana", "RealItalianPizza");
 m.put("Veneziana", "RealItalianPizza");

 for (String decendant : m.keySet()) {
 String parent = m.get(decendant);
 boolean isSub = OWLReasoningUtils.isSubClassOf(parent, decendant);
 assertTrue(decendant + " not subClassOf " + parent + ", but should be!",
 isSub);
 }
 }

 @Test
 public void testInferredEquivalentOf() {
 Map<String, String> m = new HashMap<String, String>();

 m.put("SpicyPizza", "SpicyPizzaEquivalent");
 m.put("SpicyPizzaEquivalent", "SpicyPizza");

 m.put("VegetarianPizzaEquivalent1", "VegetarianPizzaEquivalent2");
 m.put("VegetarianPizzaEquivalent2", "VegetarianPizzaEquivalent1");

 for (String equivalent : m.keySet()) {
 String parent = m.get(equivalent);
 boolean isEquiv = OWLReasoningUtils.isEquivalentOf(parent, equivalent);
 assertTrue(equivalent + " not EquivalentOf " + parent + ", but should be!",
 isEquiv);
 }
 }

 @AfterClass
 public static void tearDown() {
 MyTestArticleManager.clear();
 }

 /**
 * Checks if the OWLDeclarations for class, properties and individuals can
 * be found.
 *
 * @created 27.09.2011
 * @param entities
 */
 private static void checkEntities(Set<OWLEntity> entities) {
 for (OWLEntity e : entities) {
 boolean isEntailed = OWLReasoningUtils.checkOWLDeclaration(e);

 String msg = e.getEntityType() + ": '" + OWLReasoningUtils.getDisplayName(e)
 + "' "
 + " could not be found/inferred, but should be !";
 assertTrue(msg, isEntailed);
 }
 }
 }