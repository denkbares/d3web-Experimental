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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.semanticweb.owlapi.debugging.DebuggerClassExpressionGenerator;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;

import de.knowwe.owlapi.OWLAPIConnector;

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
	public static final int INDENT = 4;

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
	public static void printInferredClassHierarchy(OWLReasoner reasoner, OWLClass clazz, int level, StringBuilder html) {

		if (reasoner.isSatisfiable(clazz)) {
			for (int i = 0; i < level * INDENT; i++) {
				html.append("-");
			}
			html.append(labelClass(clazz));
			// check for equality of certain classes
			for (OWLClass equivalent : reasoner.getEquivalentClasses(clazz)) {
				if (!equivalent.equals(clazz)) {
					html.append(" &equiv; " + labelClass(equivalent));
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
	public static void printInferredIndividuals(OWLReasoner reasoner, OWLClass clazz, StringBuilder html) {

		if (clazz != null || reasoner != null) {

			html.append("<dl>");
			html.append("<dt>Instances for: " + labelClass(clazz) + "</dt>");

			NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(clazz, true);
			if (!individualsNodeSet.isEmpty()) {
				Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();

				html.append("<dd>");
				for (OWLNamedIndividual i : individuals) {
					html.append(" " + labelIndividual(i));
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
	public static String verbalizeToManchesterSyntax(OWLAxiom axiom) {
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
	public static Set<Set<OWLAxiom>> getExplanation(OWLAxiom axiom) {

		DebuggerClassExpressionGenerator visitor = new DebuggerClassExpressionGenerator(factory);
		axiom.accept(visitor);
		OWLClassExpression expression = visitor.getDebuggerClassExpression();

		BlackBoxExplanation explain = new BlackBoxExplanation(ontology, connector.getFactory(),
				reasoner);
		Set<OWLAxiom> axioms = explain.getExplanation(expression);
		Set<Set<OWLAxiom>> setOfSets = new HashSet<Set<OWLAxiom>>();
		setOfSets.add(axioms);
		return setOfSets;
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
}