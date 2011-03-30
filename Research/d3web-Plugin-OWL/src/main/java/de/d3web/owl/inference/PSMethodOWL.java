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
package de.d3web.owl.inference;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.owl.IRIConstants;
import de.d3web.owl.IRIUtils;
import de.d3web.owl.OWLOntologyUtil;
import de.d3web.owl.OntologyProvider;
import de.d3web.owl.assignment.Assignment;
import de.d3web.owl.assignment.AssignmentSet;

/**
 * PSMethod which delegates reasoning to an external OWL-Reasoner.
 *
 * @author Sebastian Furth
 * @created Mar 3, 2011
 */
public class PSMethodOWL implements PSMethod, SessionObjectSource, IRIConstants {

	// Just for convenience and code beautification
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void init(Session session) {
		OWLSessionObject so = (OWLSessionObject) session.getSessionObject(this);
		if (so != null) {
			OWLOntology ontology = so.getOntology();
			OWLOntologyManager manager = ontology.getOWLOntologyManager();
			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLOntologyUtil util = so.getOntologyUtil();
			// Create PSSession individual for this session
			IRI sessionIRI = IRIUtils.toIRI(session.getId(), ontology);
			OWLNamedIndividual sessionIndividual = factory.getOWLNamedIndividual(sessionIRI);
			OWLClass sessionClass = util.getOWLClassFor(PSSESSION);
			if (sessionIndividual != null && sessionClass != null) {
				Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
				axioms.add(factory.getOWLClassAssertionAxiom(sessionClass, sessionIndividual));
				manager.addAxioms(ontology, axioms);
			}
			else {
				logger.warning("Unable to create session individual: " + sessionIRI);
			}
		}
		else {
			logger.severe("There is no ontology! Unable to initialize ontology with individuals!");
		}
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		OWLSessionObject so = (OWLSessionObject) session.getSessionObject(this);
		if (so != null) {
			OWLOntology ontology = so.getOntology();
			OWLOntologyUtil util = so.getOntologyUtil();
			// Apply changes to ontology
			updateFactsInOntology(ontology, util, session, changes);
			// Synchronize the reasoner
			OWLReasoner reasoner = so.getReasoner();
			reasoner.flush();
			// Get all assignments
			AssignmentSet assignments = session.getKnowledgeBase().getKnowledgeStore().getKnowledge(
					AssignmentSet.KNOWLEDGE_KIND);
			// Do the assignments
			if (assignments != null) {
				for (Assignment assignment : assignments.getAssignments()) {
					assignment.assign(session, so);
				}
			}
		}
		else {
			logger.severe("OWLSessionObject is null! Unable to propagate changes to ontology!");
		}
	}

	private void updateFactsInOntology(OWLOntology ontology, OWLOntologyUtil util, Session session, Collection<PropagationEntry> changes) {
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (PropagationEntry change : changes) {
			if (change.hasChanged()) {
				// Remove old finding entity
				removeOldFinding(change, ontology, util, manager);
			}
			// Create new finding entity
			createNewFinding(change, ontology, util, factory, axioms, session);
		}
		// add created finding entities to ontology
		manager.addAxioms(ontology, axioms);
	}

	private void createNewFinding(PropagationEntry change, OWLOntology ont, OWLOntologyUtil util, OWLDataFactory factory, Set<OWLAxiom> axioms, Session s) {
		if (change.getNewValue() != null) {
			// Create finding individual in ontology
			IRI findingIRI = IRIUtils.toIRI(change.getObject(), change.getNewValue(), ont);
			OWLNamedIndividual finding = factory.getOWLNamedIndividual(findingIRI);
			OWLClass findingClass = util.getOWLClassFor(FINDING);
			if (finding != null && findingClass != null) {
				// finding axiom
				axioms.add(factory.getOWLClassAssertionAxiom(findingClass, finding));
				// hasInput assertion
				IRI input = IRIUtils.toIRI(change.getObject(), ont);
				doFindingPropertyAssertion(finding, HASINPUT, input, ont, util, factory, axioms);
				// hasValue assertion
				IRI value = IRIUtils.toIRI(change.getNewValue(), ont);
				doFindingPropertyAssertion(finding, HASVALUE, value, ont, util, factory, axioms);
				// isStoredBy assertion
				IRI session = IRIUtils.toIRI(s.getId(), ont);
				doFindingPropertyAssertion(finding, ISSTOREDBY, session, ont, util, factory, axioms);
				// TODO? hasAssignedValue assertion
			}
		}
		else {
			logger.warning("The new value of the finding is null, this won't be represented in the ontology!");
		}
	}

	private void doFindingPropertyAssertion(OWLNamedIndividual finding, IRI propertyIRI, IRI objectIRI, OWLOntology ont, OWLOntologyUtil util, OWLDataFactory factory, Set<OWLAxiom> axioms) {
		OWLIndividual object = util.getOWLIndividualFor(objectIRI);
		OWLObjectProperty property = util.getOWLPropertyFor(propertyIRI);
		if (object != null && property != null) {
			axioms.add(factory.getOWLObjectPropertyAssertionAxiom(property, finding, object));
		}
		else {
			logger.warning("Unable to do property assertion: "
							+ finding.getIRI() + " "
							+ propertyIRI + " "
							+ objectIRI);
		}
	}

	private void removeOldFinding(PropagationEntry change, OWLOntology ont, OWLOntologyUtil util, OWLOntologyManager manager) {
		Value oldValue = change.getOldValue();
		if (oldValue != null && !oldValue.equals(UndefinedValue.getInstance())) {
			// Get the IRI of the old finding
			IRI iri = IRIUtils.toIRI(change.getObject(), oldValue, ont);
			// Get the OWL entity representing the finding
			Set<OWLEntity> findings = ont.getEntitiesInSignature(iri);
			// Remove the old entity
			if (findings != null && findings.size() > 0) {
				OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ont));
				// Get all Finding individuals
				OWLClass findingClass = util.getOWLClassFor(FINDING);
				Set<OWLIndividual> allFindings = findingClass.getIndividuals(ont);
				// remove the entity only if it really is a finding individual!
				for (OWLEntity finding : findings) {
					if (allFindings.contains(finding)) {
						finding.accept(remover);
					}
				}
				// Do the removal!
				manager.applyChanges(remover.getChanges());
			}
			else {
				logger.warning("There is no finding individual for IRI: " + iri);
			}
		}
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// TODO: For now we assume that we don't have to merge facts!
		return Facts.mergeError(facts);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.problem;
	}

	@Override
	public double getPriority() {
		// TODO: ask chiefs for desired priority
		return 6;
	}

	@Override
	public SessionObject createSessionObject(Session session) {
		OntologyProvider provider = session.getKnowledgeBase().getKnowledgeStore().getKnowledge(
				OntologyProvider.KNOWLEDGE_KIND);
		return new OWLSessionObject(this, provider.createOntologyInstance());
	}



}
