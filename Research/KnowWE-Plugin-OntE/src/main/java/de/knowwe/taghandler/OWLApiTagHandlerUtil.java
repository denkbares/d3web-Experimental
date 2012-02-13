/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.knowwe.taghandler;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.debugging.DebuggerClassExpressionGenerator;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.manchester.compile.utils.ImportedOntologyManager;
import de.knowwe.kdom.manchester.frame.ImportFrame;
import de.knowwe.kdom.renderer.OnteRenderingUtils;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.util.OntologyFormats;

/**
 * Simple helper methods to retrieve inferred knowledge from the used
 * {@link OWLReasoner}.
 *
 * @author Stefan Mark
 * @created 20.09.2011
 */
public class OWLApiTagHandlerUtil {

	/**
	 * The indent used in the output (hierarchies, etc.).
	 */
	public static final int INDENT = 1;

	private static final OWLAPIConnector connector;
	private static final OWLOntology ontology;
	private static final OWLDataFactory factory;
	private static final OWLReasoner reasoner;

	static {
		connector = OWLAPIConnector.getGlobalInstance();
		ontology = connector.getOntology();
		factory = connector.getManager().getOWLDataFactory();
		reasoner = connector.getReasoner();
	}

	/**
	 * Checks weather the local ontology has inconsistent classes.
	 *
	 * @created 16.10.2011
	 * @return boolean
	 */
	public static boolean hasInconsistentClasses() {
		reasoner.precomputeInferences();
		if (reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom().isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Returns for a given {@link OWLEntity} all OWLAxiom that can be found in
	 * an {@link OWLOntology} the {@link OWLEntity} is linked to.
	 *
	 * @created 16.10.2011
	 * @param OWLEntity entity
	 * @return
	 */
	public static Set<OWLAxiom> getAxiomsForEntitiyInOntology(OWLEntity entity, OWLClassExpression exp) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		// entity can be a OWLClass ...
		if (entity.isOWLClass()) {
			Set<OWLClassAxiom> tmp = ontology.getAxioms(entity.asOWLClass());
			for (OWLClassAxiom axiom : tmp) {
				axioms.add(axiom.getAxiomWithoutAnnotations());
			}
		}
		// .. or a OWLNamedIndividual ...
		else if (entity.isOWLNamedIndividual()) {
			Set<OWLIndividualAxiom> tmp = ontology.getAxioms(entity.asOWLNamedIndividual());
			for (OWLIndividualAxiom axiom : tmp) {
				axioms.add(axiom.getAxiomWithoutAnnotations());
			}
		}
		// ... or a OWLObjectProperty ...
		else if (entity.isOWLDataProperty()) {
			Set<OWLDataPropertyAxiom> tmp = ontology.getAxioms(entity.asOWLDataProperty());
			for (OWLDataPropertyAxiom axiom : tmp) {
				axioms.add(axiom.getAxiomWithoutAnnotations());
			}
		}
		// ... or a OWLDataProperty
		else if (entity.isOWLObjectProperty()) {
			Set<OWLObjectPropertyAxiom> tmp = ontology.getAxioms(entity.asOWLObjectProperty());
			for (OWLObjectPropertyAxiom axiom : tmp) {
				axioms.add(axiom.getAxiomWithoutAnnotations());
			}
		}
		return axioms;
	}

	/**
	 * Ask an {@link OWLReasoner} for the hierarchy of a given {@link OWLClass}.
	 * Also checks if classes within the hierarchy have equivalent classes in
	 * the ontology.
	 *
	 * @created 07.09.2011
	 * @param OWLReasoner reasoner An {@link OWLReasoner} calculating the
	 *        relations
	 * @param OWLClass clazz Any {@link OWLClass} of an ontology.
	 * @param int level The level in the class hierarchy.
	 * @param StringBuilder html The resulting output (may contain HTML)
	 */
	public static void printInferredClassHierarchy(OWLReasoner reasoner, OWLClassExpression clazz, int level, StringBuilder html) {

		reasoner.precomputeInferences();
		if (reasoner.isSatisfiable(clazz)) {
			for (int i = 0; i < level * INDENT; i++) {
				html.append("-");
			}

			if (clazz.isAnonymous()) {
				html.append(OnteRenderingUtils.renderHyperlink(verbalizeToManchesterSyntax(clazz), true));
			}
			else {
				html.append(OnteRenderingUtils.renderHyperlink(labelClass(clazz.asOWLClass()), true));
			}

			// html.append(OnteRenderingUtils.renderHyperlink(labelClass(clazz),
			// true));
			// check for equality of certain classes
			for (OWLClass equivalent : reasoner.getEquivalentClasses(clazz)) {
				if (!equivalent.equals(clazz)) {
					html.append(" &equiv; ").append(
							OnteRenderingUtils.renderHyperlink(labelClass(equivalent), true));
				}
			}
			// &ne; disjoint classes ???

			// get subclasses of current class if found
			html.append("<br />");
			for (OWLClass child : reasoner.getSubClasses(clazz, true).getFlattened()) {
				if (!child.equals(clazz)) {
					printInferredClassHierarchy(reasoner, child, level + 1, html);
				}
			}
		}
	}

	/**
	 * Retrieve all instances of a given {@link OWLClass} class in the
	 * {@link OWLOntology} the {@link OWLReasoner} knows off. Note: Not all the
	 * instances need to be asserted.
	 *
	 * @created 07.09.2011
	 * @param OWLReasoner reasoner An {@link OWLReasoner} calculating the
	 *        relations
	 * @param OWLClass clazz Any {@link OWLClass} of an ontology.
	 * @param StringBuilder html The resulting output (may contain HTML)
	 */
	public static void printInferredIndividuals(OWLReasoner reasoner, OWLClassExpression clazz, StringBuilder html) {

		if (clazz != null || reasoner != null) {

			html.append("<dl>");

			if (clazz.isAnonymous()) {
				html.append("<dt>Instances for: " + verbalizeToManchesterSyntax(clazz) + "</dt>");
			}
			else {
				html.append("<dt>Instances for: " + labelClass(clazz.asOWLClass()) + "</dt>");
			}

			NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(clazz, true);
			if (!individualsNodeSet.isEmpty()) {
				Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();

				html.append("<dd>");
				for (OWLNamedIndividual i : individuals) {
					html.append(" ").append(
							OnteRenderingUtils.renderHyperlink(labelIndividual(i), true));
				}
				html.append("</dd>");
			}
			else {
				html.append("<dd>No instances found!</dd>");
			}
			html.append("</dl>");
		}
	}

	/**
	 * Checks for a given {@link OWLClass} in a given {@link OWLOntology} label
	 * annotations exists. If TRUE the value from the label is used, otherwise
	 * the name of the class but replacing the base IRI.
	 *
	 * @created 07.09.2011
	 * @param OWLClass clazz Any {@link OWLClass} of an ontology.
	 * @return String The label of the {@link OWLClass} given
	 */
	public static String labelClass(OWLClass clazz) {

		IRI annotation = OWLRDFVocabulary.RDFS_LABEL.getIRI();

		Set<OWLAnnotation> annotations = clazz.getAnnotations(ontology,
				factory.getOWLAnnotationProperty(annotation));
		String label = localizedString(annotations);
		if (!label.isEmpty()) {
			return label;
		}
		SimpleShortFormProvider provider = new SimpleShortFormProvider();
		return provider.getShortForm(clazz);
	}

	/**
	 * Checks for a given {@link OWLNamedIndividual} in a given
	 * {@link OWLOntology} label annotations exists. If TRUE the value from the
	 * label is used, otherwise the name of the class but replacing the base
	 * IRI.
	 *
	 * @created 07.09.2011
	 * @param OWLNamedIndividual i Any {@link OWLNamedIndividual} of an
	 *        ontology.
	 * @return String The label of the {@link OWLNamedIndividual}
	 */
	public static String labelIndividual(OWLNamedIndividual i) {

		IRI annotation = OWLRDFVocabulary.RDFS_LABEL.getIRI();

		Set<OWLAnnotation> annotations = i.getAnnotations(ontology,
				factory.getOWLAnnotationProperty(annotation));
		String label = localizedString(annotations);
		if (!label.isEmpty()) {
			return label;
		}

		SimpleShortFormProvider provider = new SimpleShortFormProvider();
		return provider.getShortForm(i);
	}

	/**
	 * Finds in a set of {@link OWLAnnotation} a localized {@link OWLLiteral}.
	 * The language can be specified in a properties file. If an
	 * {@link OWLLiteral} with the set language could be found the according
	 * value is returned.
	 *
	 * @created 27.09.2011
	 * @param Set<OWLAnnotation> annotations
	 * @return
	 */
	public static String localizedString(Set<OWLAnnotation> annotations) {

		ResourceBundle properties = ResourceBundle.getBundle("onte");
		String lang = properties.getString("onte.lang");

		List<OWLLiteral> labels = new ArrayList<OWLLiteral>();
		for (OWLAnnotation label : annotations) {
			if (label.getValue() instanceof OWLLiteral) {
				labels.add((OWLLiteral) label.getValue());
			}
		}

		for (OWLLiteral literal : labels) {
			if (literal.hasLang()) {
				if (literal.getLang().equals(lang)) {
					return literal.getLiteral();
				}
			}
		}
		return "";
	}

	/**
	 *
	 *
	 * @created 27.09.2011
	 * @param OWLClassExpression expression
	 * @return String
	 */
	public static String verbalizeToManchesterSyntax(OWLClassExpression expression) {
		StringWriter sw = new StringWriter();
		ShortFormProvider sfp = new SimpleShortFormProvider();
		ManchesterOWLSyntaxObjectRenderer renderer = new ManchesterOWLSyntaxObjectRenderer(sw, sfp);
		expression.accept(renderer);
		return sw.toString();
	}

	/**
	 *
	 *
	 * @created 27.09.2011
	 * @param OWLClassExpression expression
	 * @return String
	 */
	public static String verbalizeToManchesterSyntax(OWLObject axiom) {
		StringWriter sw = new StringWriter();
		ShortFormProvider sfp = new SimpleShortFormProvider();
		ManchesterOWLSyntaxObjectRenderer renderer = new ManchesterOWLSyntaxObjectRenderer(sw, sfp);
		axiom.accept(renderer);
		return sw.toString();
	}

	/**
	 * Returns an explanation for a given {@link OWLAxiom}.
	 *
	 * @created 27.09.2011
	 * @param OWLAxiom axiom
	 */
	public static Set<OWLAxiom> getExplanations(OWLClassExpression exp, OWLEntity entity) {

		OWLAxiom axiom = null;
		Set<OWLAxiom> explanations = null;

		if (entity.isOWLNamedIndividual()) {
			OWLNamedIndividual ind = entity.asOWLNamedIndividual();
			axiom = factory.getOWLClassAssertionAxiom(exp, ind);
			explanations = getExplanations(axiom);
		}
		else if (entity.isOWLClass()) {
			OWLClass cls = entity.asOWLClass(); // TODO subclass or superclass
												// relationship ???
			axiom = factory.getOWLSubClassOfAxiom(cls, exp);
			explanations = getExplanations(axiom);

		}
		else if (entity.isOWLObjectProperty()) {

		}

		// getOWLEquivalentClassesAxiom(description, cls) equivalent
		// factory.getOWLSubClassOfAxiom(description, superClass) super
		// factory.getOWLSubClassOfAxiom(subClass, description) sub
		// factory.getOWLClassAssertionAxiom(description, ind) individuals

		explanations = getExplanations(axiom);

		if (explanations.isEmpty()) {
			// explanations = getAxiomsForEntitiyInOntology(entity,
			// exp);
		}
		return explanations;
	}

	/**
	 * Returns an explanation for a given {@link OWLAxiom}.
	 *
	 * @created 27.09.2011
	 * @param OWLAxiom axiom
	 */
	public static Set<OWLAxiom> getExplanations(OWLAxiom axiom) {

		DebuggerClassExpressionGenerator visitor = new DebuggerClassExpressionGenerator(factory);
		axiom.accept(visitor);
		OWLClassExpression expression = visitor.getDebuggerClassExpression();

		reasoner.precomputeInferences();

		BlackBoxExplanation explain = new BlackBoxExplanation(ontology, connector.getFactory(),
				reasoner);
		Set<OWLAxiom> axioms = explain.getExplanation(expression);
		Set<Set<OWLAxiom>> setOfSets = new HashSet<Set<OWLAxiom>>();
		setOfSets.add(axioms);
		return axioms;
	}

	/**
	 * <p>
	 * Returns a {@link Set} containing explanations for the current class.
	 * </p>
	 * <p>
	 * Uses an implementation of MultipleExplanationGenerator interface using
	 * Reiter's Hitting Set Tree (HST) algorithm as described in Aditya
	 * Kalyanpur's thesis. This class relies on a SingleExplanationGenerator
	 * that can compute a minimal set of axioms that cause the unsatisfiability.
	 * The core of the functionality is based on Matthew Horridge's
	 * implementation.
	 * </p>
	 *
	 * @see BlackBoxExplanation
	 * @see HSTExplanationGenerator
	 * @created 27.09.2011
	 * @param clazz
	 * @return
	 */
	public static Set<Set<OWLAxiom>> getExplanations(OWLClass clazz) {

		BlackBoxExplanation exp = new BlackBoxExplanation(ontology, connector.getFactory(),
				reasoner);
		HSTExplanationGenerator multExplanator = new HSTExplanationGenerator(exp);
		Set<Set<OWLAxiom>> explanations = multExplanator.getExplanations(clazz);
		return explanations;
	}

	/**
	 * Gets a serialized form of the local OWLOntology. Possible formats are
	 * RDF/XML, OWL/XML, Turtle and Manchester OWL syntax.
	 *
	 * @created 16.10.2011
	 * @param String format The format of the output ontology file
	 * @return
	 * @throws OWLOntologyStorageException
	 */
	public static String getSerializedOntology(String format) throws OWLOntologyStorageException {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		OWLOntologyManager manager = connector.getManager();

		if (format != null) {
			format = format.toLowerCase();
		}

		OWLOntologyFormat ontologyFormat = null;
		if (format.equals(OntologyFormats.RDFXML.getFormat())) {
			manager.saveOntology(ontology, stream);
			return stream.toString();
		}
		else if (format.equals(OntologyFormats.OWLXML.getFormat())) {
			ontologyFormat = new OWLXMLOntologyFormat();
		}
		else if (format.equals(OntologyFormats.TURTLE.getFormat())) {
			ontologyFormat = new TurtleOntologyFormat();
		}
		else if (format.equals(OntologyFormats.MANCHESTER.getFormat())) {
			ontologyFormat = new ManchesterOWLSyntaxOntologyFormat();
		}

		if (ontologyFormat != null) {

			// ... set default prefix
			((PrefixOWLOntologyFormat) ontologyFormat).setDefaultPrefix(OWLAPIConnector.getGlobalInstance().getGlobalBaseIRI().toString());

			// ... and for each import an optional one ...
			Map<IRI, Section<ImportFrame>> imports = ImportedOntologyManager.getInstance().getImportedOntologies();
			for (IRI iri : imports.keySet()) {
				Section<ImportFrame> frame = imports.get(iri);
				if (frame.get().hasPrefix(frame)) {
					String prefixName = frame.get().getPrefix(frame).getText();
					prefixName = prefixName.replace(":", "");
					((PrefixOWLOntologyFormat) ontologyFormat).setPrefix(prefixName, iri.toString()
							+ "#");
				}
				// ... also remove axioms from the local ontology ...
				Set<OWLAxiom> importedAxioms = ImportedOntologyManager.getInstance().getImportedAxiomsForIRI(
						iri);
				manager.removeAxioms(OWLAPIConnector.getGlobalInstance().getOntology(),
						importedAxioms);
			}

			manager.saveOntology(ontology, ontologyFormat, stream);
			return stream.toString();
		}
		return "";
	}

	public static void write() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		OWLOntologyManager manager = connector.getManager();
		OWLOntology ontology = connector.getOntology();

		OWLOntologyFormat format = null;
		try {
			format = new ManchesterOWLSyntaxOntologyFormat();
			manager.saveOntology(ontology, format, stream);
			System.out.println(stream.toString());
		}
		catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}

	public static void renderHyperlink(String conceptName, Section<? extends
			Type> section, StringBuilder doc) {
		if (section != null) {
			doc.append("<a href=\"Wiki.jsp?page=" + section.getArticle().getTitle()
					+ "\" title=\"Goto defining article\">");
			doc.append(conceptName);
			doc.append("</a>");
		}
		else {
			doc.append(conceptName);
		}
	}

}
