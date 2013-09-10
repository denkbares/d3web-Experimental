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
package de.knowwe.onte.test.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.owlapi.query.OWLApiQueryEngine;

/**
 * Some helper functions for interaction with the ontology.
 *
 * @author Stefan Mark
 * @created 23.09.2011
 */
public class OWLReasoningUtils {

	private static final OWLAPIConnector connector;
	private static final OWLDataFactory factory;
	private static final OWLReasoner reasoner;
	private static final OWLOntology ontology;
	private static final OWLApiQueryEngine engine;

	static {
		connector = OWLAPIConnector.getGlobalInstance();
		factory = connector.getManager().getOWLDataFactory();
		reasoner = connector.getReasoner();
		ontology = connector.getOntology();
		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		engine = new OWLApiQueryEngine(shortFormProvider);
	}

	public static boolean isConsistent() {

		OWLReasoner r = OWLAPIConnector.getGlobalInstance().getReasoner();
		r.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		Node<OWLClass> unsatNodes = r.getUnsatisfiableClasses();
		Set<OWLClass> unsatisfiable = unsatNodes.getEntitiesMinusBottom();
		return unsatisfiable.isEmpty();
	}

	/**
	 * Checks if a given class is subClassOf another.
	 *
	 * @created 27.10.2011
	 * @param parent
	 * @param child
	 * @return
	 */
	public static boolean isSubClassOf(String parent, String child) {

		try {
			Set<OWLClass> classes = engine.getSubClasses(parent, false);

			for (OWLClass owlClass : classes) {
				String identifier = getDisplayName(owlClass);
				if (identifier.equals(child)) {
					return true;
				}
			}
		}
		catch (ParserException e) {
			return false;
		}
		return false;
	}

	/**
	 * Checks if a given class is subClassOf another.
	 *
	 * @created 27.10.2011
	 * @param parent
	 * @param child
	 * @return
	 */
	public static boolean isEquivalentOf(String parent, String child) {

		try {
			Set<OWLClass> classes = engine.getEquivalentClasses(parent);
			for (OWLClass owlClass : classes) {

				String identifier = getDisplayName(owlClass);
				if (identifier.equals(child)) {
					return true;
				}
			}
		}
		catch (ParserException e) {
			return false;
		}
		return false;
	}

	/**
	 *
	 *
	 * @created 27.10.2011
	 * @param parent
	 * @param child
	 * @return
	 */
	public static boolean isSubObjectPropertyOf(String parent, String child) {

		try {
			Set<OWLObjectPropertyExpression> entities = engine.getSubProperties(parent, false);
			for (OWLObjectPropertyExpression e : entities) {
				if (!e.isAnonymous()) {
					OWLObjectProperty p = e.getNamedProperty();

					String identifier = getDisplayName(p);

					if (identifier.equals(child)) {
						return true;
					}
				}
			}
		}
		catch (ParserException e) {
			return false;
		}
		return false;
	}

	public static boolean check(OWLAxiom a) {
		// boolean contains = ontology.containsAxiom(a);
		boolean entails = reasoner.isEntailed(a);
		return entails;
	}

	public static boolean contains(OWLAxiom a) {
		return ontology.containsAxiom(a);
	}

	public static boolean isSatisfiable(OWLClassExpression e) {
		return reasoner.isSatisfiable(e);
	}

	/**
	 * Checks if the declarations of properties and classes can be found in the
	 * ontology. If not an error is thrown to indicate possible errors in the
	 * source code.
	 *
	 * @created 27.10.2011
	 * @param o OWLEntity
	 * @return boolean
	 */
	public static boolean checkOWLDeclaration(OWLEntity o) {
		OWLAxiom a = factory.getOWLDeclarationAxiom(o);
		return ontology.containsAxiom(a);
	}

	/**
	 * Checks if the given individual is contained in the ontology. If not an
	 * error is thrown to indicate possible errors in the source code.
	 *
	 * @created 27.10.2011
	 * @param o OWLEntity
	 * @return boolean
	 */
	public static boolean checkIndividualDeclaration(OWLEntity o) {
		OWLAxiom a = factory.getOWLDeclarationAxiom(o);
		return reasoner.isEntailed(a);
	}

	/* ------------------ OBJECT PROPRTIES QUERIES ----------------- */
	public static boolean isFunctional(OWLObjectProperty p) {
		OWLOntology o = connector.getOntology();
		return p.isFunctional(o);
	}

	public static boolean isInverseOf(OWLObjectProperty p) {
		OWLOntology o = connector.getOntology();
		return p.isInverseFunctional(o);
	}

	public static boolean isSymmetric(OWLObjectProperty p) {
		OWLOntology o = connector.getOntology();
		return p.isSymmetric(o);
	}

	/* ------------------ INDIVIDUAL QUERIES ----------------------- */

	public static boolean checkSameIndividual(OWLNamedIndividual one, OWLNamedIndividual two) {
		OWLAxiom a = factory.getOWLSameIndividualAxiom(one, two);
		return check(a);
	}

	public static boolean checkTypeIndividual(OWLNamedIndividual i, OWLClass c) {
		NodeSet<OWLClass> types = reasoner.getTypes(i, false);
		for (Node<OWLClass> node : types) {
			if (node.contains(c)) {
				return true;
			}
		}
		return false;
	}

	public static String getDisplayName(OWLEntity entity) {
		String identifier = "";
		if (entity.getIRI().getFragment() != null) {
			identifier = entity.getIRI().getFragment();
		}
		else {
			String t = entity.getIRI().toString();
			identifier = t.substring(t.lastIndexOf("/") + 1);
		}

		return identifier;
	}

	public static void write() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		OWLOntologyManager manager = connector.getManager();
		OWLOntology ontology = connector.getOntology();

		OWLOntologyFormat format = null;
		try {
			format = new ManchesterOWLSyntaxOntologyFormat();
			manager.saveOntology(ontology, format, stream);

			String tempdir = System.getProperty("java.io.tmpdir");
			if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
				tempdir = tempdir + System.getProperty("file.separator");
			}

			File file = new File(tempdir + "952859ec92e9f3da8b57750eee777afa.owl");
			try {
				FileWriter writer = new FileWriter(file, false);
				writer.write(System.getProperty("line.separator"));
				writer.write(stream.toString());
				writer.flush();
				writer.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}
}
