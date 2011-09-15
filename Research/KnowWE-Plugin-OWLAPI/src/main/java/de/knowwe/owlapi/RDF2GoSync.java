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
package de.knowwe.owlapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.util.RDFTool;
import org.openrdf.rdf2go.RepositoryModelFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.DefaultOntologyFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.d3web.we.kdom.Section;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * RDF2GoSync handles the syncing of @link{OWLOntology} instances and the
 * RDF2Go-Store accessible via the @link{Rdf2GoCore}.
 * 
 * The syncing from the OWLAPI to RDF2Go is done by converting @link{OWLAxiom}s
 * to RDF/XML, creating an RDF2GoModel and adding/removing all statements
 * belonging to this Model instance from the store.
 * 
 * In the other direction (RDF2Go to OWLAPI) syncing is only supported for
 * KnowWE's global @link{OWLOntology}. That means, every statement that is added
 * or removed from the RDF2Go store, will be translated to @link{OWLAxiom}s and
 * then added or removed from KnowWE's global ontology.
 * 
 * The latter shouldn't be a problem, because the RDF2Go-Store is also a global
 * store.
 * 
 * @author Sebastian Furth
 * @created May 24, 2011
 */
public class RDF2GoSync {

	public enum Mode {
		ADD, REMOVE
	};

	private static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private static final Rdf2GoCore rdf2goCore = Rdf2GoCore.getInstance();

	private RDF2GoSync() {
	}

	/**
	 * Applies the changes specified as @link{OWLAxioms} to the Rdf2GoCore. This
	 * will be done by converting the axioms to RDF/XML, creating an RDF2GoModel
	 * and adding/removing all statements belonging to this Model instance from
	 * the store.
	 * 
	 * @created May 24, 2011
	 * @param axioms Set of @link{OWLAxiom}s to be synced with the Rdf2GoCore
	 * @param sec The section which created the @link{OWLAxiom}s.
	 * @param mode specifies whether the @link{OWLAxiom}s are added or removed
	 *        from the @link{Rdf2GoCore}.
	 */
	public static void synchronize(Set<OWLAxiom> axioms, Section<?> sec, RDF2GoSync.Mode mode) {
		if (mode.equals(RDF2GoSync.Mode.ADD)) {
			String rdfXML = axiomsToRDF(axioms);

			RepositoryModelFactory factory = new org.openrdf.rdf2go.RepositoryModelFactory();
			Model model = factory.createModel();
			model.open();
			model.addModel(RDFTool.stringToModel(rdfXML));

			Iterator<Statement> iter = model.iterator();
			Statement s;
			List<Statement> statements = new LinkedList<Statement>();
			while (iter.hasNext()) {
				s = iter.next();
				statements.add(s);
			}
			rdf2goCore.addStatements(statements, sec);
		}
		else if (mode.equals(RDF2GoSync.Mode.REMOVE)) {
			// TODO: Not sure if this is ok...
			rdf2goCore.removeSectionStatementsRecursive(sec);
		}
	}

	private static String axiomsToRDF(Set<OWLAxiom> axioms) {
		// Temporarily create ontology containing just the axioms
		OWLOntology tempOntology = createTempOntology(axioms);
		try {
			// Convert it to RDF
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			manager.setOntologyFormat(tempOntology, new DefaultOntologyFormat());
			manager.saveOntology(tempOntology, bos);
			// Remove the temp ontology
			manager.removeOntology(tempOntology);
			// Return the RDF/XML
			return bos.toString("UTF-8");
		}
		catch (Exception e) {
			Logger.getLogger(RDF2GoSync.class.getSimpleName()).severe(
					"Unable to convert ontology to RDF.");
		}
		// We definitely have to remove the temp ontology!
		manager.removeOntology(tempOntology);
		return null;
	}

	private static OWLOntology createTempOntology(Set<OWLAxiom> axioms) {
		try {
			// Create empty ontology
			OWLOntology tempOntology = manager.createOntology();
			// Add the provided axioms
			manager.addAxioms(tempOntology, axioms);
			return tempOntology;
		}
		catch (OWLOntologyCreationException e) {
			Logger.getLogger(RDF2GoSync.class.getSimpleName()).severe(
					"Unable to create new OWLOntology instance.");
		}
		return null;
	}

	/**
	 * Applies the changes specified as @link{Statement}s to KnowWE's global
	 * instance of @link{OWLAPIConnector}. This will be done by converting the
	 * statements to @link{OWLAxiom}s and adding them to/removing them from
	 * KnowWE's global @link{OWLOntology} instance accessible via the connector.
	 * 
	 * @created May 25, 2011
	 * @param statements Statements to be synchronized with the OWLAPI
	 * @param mode specifies whether the @link{Statement}s are added or removed
	 *        from the @link{OWLOntology}.
	 */
	public static void synchronize(List<Statement> statements, RDF2GoSync.Mode mode) {
		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		String rdfXML = statementsToRDF(statements);
		OWLOntology tempOntology = createTempOntology(rdfXML);
		Set<OWLAxiom> axioms = tempOntology.getAxioms();
		// synchronize!
		if (mode.equals(RDF2GoSync.Mode.ADD)) {
			connector.addAxioms(axioms);
		}
		else if (mode.equals(RDF2GoSync.Mode.REMOVE)) {
			connector.removeAxioms(axioms);
		}
		// the temporaryly created ontology is obsolete...
		manager.removeOntology(tempOntology);
	}

	private static String statementsToRDF(List<Statement> statements) {
		Model rdfModel = RDF2Go.getModelFactory().createModel();
		rdfModel.open();
		rdfModel.addAll(statements.iterator());
		return RDFTool.modelToString(rdfModel);
	}

	private static OWLOntology createTempOntology(String rdfXML) {
		String exceptionMessage = null;
		try {
			InputStream input = new ByteArrayInputStream(rdfXML.getBytes("UTF-8"));
			try {
				return manager.loadOntologyFromOntologyDocument(input);
			}
			catch (OWLOntologyCreationException e) {
				exceptionMessage = e.getMessage();
			}
		}
		catch (UnsupportedEncodingException e) {
			exceptionMessage = e.getMessage();
		}
		Logger.getLogger(RDF2GoSync.class.getSimpleName()).severe(
				"Unable to create new OWLOntology instance: " + exceptionMessage);
		return null;
	}

}
