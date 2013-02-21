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
package de.knowwe.kdom.manchester;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 * 
 * 
 * @author smark
 * @created 03.09.2011
 */
public class OWLStoreDebuggingTagHandler extends AbstractHTMLTagHandler {

	private static int INDENT = 4;
	private static final OWLAPIConnector connector;
	private static final OWLOntology ontology;
	private static final OWLOntologyManager manager;
	private static final OWLReasoner reasoner;

	static {
		connector = OWLAPIConnector.getGlobalInstance();
		ontology = connector.getOntology();
		manager = connector.getManager();
		reasoner = connector.getReasoner();
	}

	public OWLStoreDebuggingTagHandler() {
		super("owldebug");
	}

	@Override
	public void renderHTML(String web, String topic, UserContext user, Map<String, String> parameters, RenderResult result) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		String syntax = parameters.get("syntax");

		StringBuilder html = new StringBuilder();
		html.append("<pre style=\"white-space:pre-wrap;background-color:#C8C8C8;\">");

		if (syntax != null) {
			syntax = syntax.toLowerCase();

			OWLOntologyFormat format = null;
			try {
				if (syntax.equals("rdf")) {
					manager.saveOntology(ontology, stream);
					html.append(Strings.encodeHtml(stream.toString()));
					html.append("</pre>");
					result.appendHTML(html.toString());
					return;
				}
				else if (syntax.equals("owl")) {
					format = new OWLXMLOntologyFormat();
				}
				else if (syntax.equals("turtle")) {
					format = new TurtleOntologyFormat();
				}
				else if (syntax.equals("ms")) {
					format = new ManchesterOWLSyntaxOntologyFormat();

				}
				if (format != null) {
					manager.saveOntology(ontology, format, stream);
					html.append(Strings.encodeHtml(stream.toString()));
				}
			}
			catch (OWLOntologyStorageException e) {
				e.printStackTrace();
			}
		}
		html.append("</pre>");
		result.appendHTML(html.toString());
	}

	/**
	 * 
	 * 
	 * @created 08.09.2011
	 * @param html
	 */
	@SuppressWarnings("unused")
	private void printObjectProperties(StringBuilder html) {
		html.append("<br /><br />");
		Set<OWLObjectProperty> p = ontology.getObjectPropertiesInSignature();
		for (OWLObjectProperty owlObjectProperty : p) {
			html.append("<br />");
			// html.append("ObjectProperty: ");
			html.append(owlObjectProperty.getIRI().toString().replace(
					OWLAPIConnector.getGlobalInstance().getGlobalBaseIRI().toString(), ""));
			html.append("<br />");

			Set<OWLClassExpression> expressions = null;

			expressions = owlObjectProperty.getDomains(ontology);
			if (expressions.size() > 0) {
				html.append("&nbsp;&nbsp;&nbsp;&nbsp;<small>Domain: </small>");
				for (OWLClassExpression e : owlObjectProperty.getDomains(ontology)) {
					html.append(label((OWLClass) e) + " ");
				}
				html.append("<br />");
			}

			expressions = owlObjectProperty.getRanges(ontology);
			if (expressions.size() > 0) {
				html.append("&nbsp;&nbsp;&nbsp;&nbsp;<small>Rang: </small>");
				for (OWLClassExpression e : owlObjectProperty.getRanges(ontology)) {
					html.append(label((OWLClass) e) + " ");
				}
				html.append("<br />");
			}

			checkCharacteristics(owlObjectProperty, html);

			if (owlObjectProperty.getSuperProperties(ontology).size() == 0) {
				for (OWLObjectPropertyExpression propertyExpression : owlObjectProperty.getSubProperties(ontology)) {
					html.append("    " + propertyExpression);
				}
			}
			// Set<OWLSubPropertyChainOfAxiom> subPropertyChainOf =
			// ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF);

			OWLObjectPropertyExpression e =
					owlObjectProperty.getInverseProperty();
			if (e != null) {
				html.append("&nbsp;&nbsp;&nbsp;&nbsp;<small>InverseOf: </small>");
				html.append(owlObjectProperty.getInverseProperty());
			}
			html.append("<br />");
		}
	}

	private void checkCharacteristics(OWLObjectProperty property, StringBuilder html) {

		StringBuilder c = new StringBuilder();

		if (property.isAsymmetric(ontology)) {
			c.append(ManchesterSyntaxKeywords.ASYMMETRIC.getKeyword() + " ");
		}
		if (property.isSymmetric(ontology)) {
			c.append(ManchesterSyntaxKeywords.SYMMETRIC.getKeyword() + " ");
		}
		if (property.isFunctional(ontology)) {
			c.append(ManchesterSyntaxKeywords.FUNCTIONAL.getKeyword() + " ");
		}
		if (property.isInverseFunctional(ontology)) {
			c.append(ManchesterSyntaxKeywords.INVERSE_FUNCTIONAL.getKeyword() + " ");
		}
		if (property.isTransitive(ontology)) {
			c.append(ManchesterSyntaxKeywords.TRANSITIVE.getKeyword() + " ");
		}
		if (property.isReflexive(ontology)) {
			c.append(ManchesterSyntaxKeywords.REFLEXIVE.getKeyword() + " ");
		}
		if (property.isIrreflexive(ontology)) {
			c.append(ManchesterSyntaxKeywords.IRREFLEXIVE.getKeyword() + " ");
		}
		if (c.length() > 0) {
			html.append("&nbsp;&nbsp;&nbsp;&nbsp;<small>Characteristics: </small>");
			html.append(c.toString());
			html.append("<br />");
		}
	}

	/**
	 * 
	 * 
	 * @created 08.09.2011
	 * @param html
	 */
	@SuppressWarnings("unused")
	private void printClasses(StringBuilder html) {
		Set<OWLClass> c = ontology.getClassesInSignature();
		for (OWLClass owlClass : c) {
			html.append("CCC: ");
			html.append(owlClass);
			html.append("<br />");
		}
	}

	/**
	 * Print the hierarchy of the classes in the ontology.
	 * 
	 * @created 07.09.2011
	 * @param clazz
	 * @param level
	 * @param html
	 */
	@SuppressWarnings("unused")
	private void printHierarchy(OWLClass clazz, int level, StringBuilder html) {

		if (reasoner.isSatisfiable(clazz)) {
			for (int i = 0; i < level * INDENT; i++) {
				html.append(" ");
			}
			html.append("<br />");
			html.append(label(clazz));
			for (OWLClass child : reasoner.getSubClasses(clazz, true).getFlattened()) {
				if (!child.equals(clazz)) {
					this.printHierarchy(child, level + 1, html);
				}
			}
		}
	}

	/**
	 * Returns a nice label for the class.
	 * http://owlapi.svn.sourceforge.net/viewvc
	 * /owlapi/v3/trunk/tutorial/src/main
	 * /java/uk/ac/manchester/owl/owlapi/tutorial
	 * /examples/SimpleHierarchyExample.java?revision=1830&view=markup
	 * 
	 * @created 07.09.2011
	 * @param clazz
	 * @return
	 */
	private String label(OWLClass clazz) {
		String label = null;
		Set<OWLAnnotation> annotations = clazz.getAnnotations(ontology);
		for (OWLAnnotation annotation : annotations) {
			if (annotation.getProperty().isLabel()) {
				OWLLiteral c = (OWLLiteral) annotation.getValue();
				label = c.getLiteral();
			}
		}
		if (label != null) {
			return label;
		}
		else {
			return clazz.getIRI().toString().replace(
					OWLAPIConnector.getGlobalInstance().getGlobalBaseIRI().toString(), "");
		}
	}
}
