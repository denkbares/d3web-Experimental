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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.semanticweb.owlapi.util.OWLEntityRemover;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.owl.IRIConstants;
import de.d3web.owl.IRIUtils;
import de.d3web.owl.OntologyProvider;

/**
 * PSMethod which delegates reasoning to an external OWL-Reasoner.
 *
 * @author Sebastian Furth
 * @created Mar 3, 2011
 */
public class PSMethodOWL implements PSMethod, SessionObjectSource, IRIConstants {

	// Just for convenience and code beautification
	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	// Caches for OWL objects, avoids multiple searches
	private final Map<IRI, OWLClass> cachedClasses = new HashMap<IRI, OWLClass>();
	private final Map<IRI, OWLIndividual> cachedIndividuals = new HashMap<IRI, OWLIndividual>();
	private final Map<IRI, OWLObjectProperty> cachedProperties = new HashMap<IRI, OWLObjectProperty>();

	@Override
	public void init(Session session) {
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		OntologySessionObject so = (OntologySessionObject) session.getSessionObject(this);
		if (so != null) {
			updateFactsInOntology(so.getOntology(), session, changes);
			// TODO: neue Fakten abfragen
			// TODO: neue Fakten in Blackboard setzen
		}
		else {
			logger.severe("There is no ontology! Unable to propagate changes to ontology!");
		}
	}

	private void updateFactsInOntology(OWLOntology ontology, Session session, Collection<PropagationEntry> changes) {
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (PropagationEntry change : changes) {
			if (change.hasChanged()) {
				// Remove old finding entity
				removeOldFinding(change, ontology, manager);
			}
			// Create new finding entity
			createNewFinding(change, ontology, factory, axioms);
		}
		// add created finding entities to ontology
		manager.addAxioms(ontology, axioms);
	}

	private void createNewFinding(PropagationEntry change, OWLOntology ont, OWLDataFactory factory, Set<OWLAxiom> axioms) {
		if (change.getNewValue() != null) {
			// Create finding individual in ontology
			IRI findingIRI = IRIUtils.toIRI(change.getObject(), change.getNewValue(), ont);
			OWLNamedIndividual finding = factory.getOWLNamedIndividual(findingIRI);
			OWLClass findingClass = getOWLClassFor(FINDING, ont);
			if (finding != null && findingClass != null) {
				axioms.add(factory.getOWLClassAssertionAxiom(findingClass, finding));
				IRI input = IRIUtils.toIRI(change.getObject(), ont);
				doFindingPropertyAssertion(finding, HASINPUT, input, ont, factory, axioms);
				IRI value = IRIUtils.toIRI(change.getNewValue(), ont);
				doFindingPropertyAssertion(finding, HASVALUE, value, ont, factory, axioms);
				// TODO: isStoredBy assertion
			}
		}
		else {
			logger.warning("The new value of the finding is null, this won't be represented in the ontology!");
		}
	}

	private void doFindingPropertyAssertion(OWLNamedIndividual finding, IRI propertyIRI, IRI objectIRI, OWLOntology ont, OWLDataFactory factory, Set<OWLAxiom> axioms) {
		OWLIndividual object = getOWLIndividualFor(objectIRI, ont);
		OWLObjectProperty property = getOWLPropertyFor(propertyIRI, ont);
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

	private void removeOldFinding(PropagationEntry change, OWLOntology ont, OWLOntologyManager manager) {
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
				OWLClass findingClass = getOWLClassFor(FINDING, ont);
				Set<OWLIndividual> allFindings = findingClass.getIndividuals(ont);
				// remove the entity only if it really is a finding individual!
				for (OWLEntity finding : findings) {
					if (allFindings.contains(finding)) {
						finding.accept(remover);
						// TODO: Check removal of hasInput/hasValue
					}
				}
				// Do the removal!
				manager.applyChanges(remover.getChanges());
				// Warn if not exactly ONE finding was removed
				int removals = remover.getChanges().size();
				if (removals != 1) {
					logger.severe("Removed " + removals + " entities, this shouldn't happen.");
				}
			}
			else {
				logger.warning("There is no finding individual for IRI: " + iri);
			}
		}
	}

	private OWLClass getOWLClassFor(IRI iri, OWLOntology ontology) {
		if (cachedClasses.containsKey(iri)) {
			return cachedClasses.get(iri);
		}
		Set<OWLEntity> entities = ontology.getEntitiesInSignature(iri);
		for (OWLEntity entity : entities) {
			if (entity.getIRI().equals(iri) && entity.isOWLClass()) {
				cachedClasses.put(iri, entity.asOWLClass());
				return entity.asOWLClass();
			}
		}
		logger.warning("No OWLClass found for IRI: " + iri);
		return null;
	}

	private OWLIndividual getOWLIndividualFor(IRI iri, OWLOntology ont) {
		if (cachedIndividuals.containsKey(iri)) {
			return cachedIndividuals.get(iri);
		}
		Set<OWLEntity> entities = ont.getEntitiesInSignature(iri);
		for (OWLEntity entity : entities) {
			if (entity.isOWLNamedIndividual() && entity.getIRI().equals(iri)) {
				cachedIndividuals.put(iri, entity.asOWLNamedIndividual());
				return entity.asOWLNamedIndividual();
			}
		}
		logger.warning("No OWLIndividual found for IRI: " + iri);
		return null;
	}

	private OWLObjectProperty getOWLPropertyFor(IRI iri, OWLOntology ont) {
		if (cachedProperties.containsKey(iri)) {
			return cachedProperties.get(iri);
		}
		Set<OWLEntity> entities = ont.getEntitiesInSignature(iri);
		for (OWLEntity entity : entities) {
			if (entity.isOWLObjectProperty() && entity.getIRI().equals(iri)) {
				cachedProperties.put(iri, entity.asOWLObjectProperty());
				return entity.asOWLObjectProperty();
			}
		}
		logger.warning("No OWLProperty found for IRI: " + iri);
		return null;
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.problem;
	}

	@Override
	public double getPriority() {
		// TODO: Priorität abklären
		return 6;
	}

	@Override
	public SessionObject createSessionObject(Session session) {
		OntologyProvider provider = session.getKnowledgeBase().getKnowledgeStore().getKnowledge(
				OntologyProvider.KNOWLEDGE_KIND);
		return new OntologySessionObject(this, provider.createOntologyInstance());

	}

	/**
	 * Stores the OWLOntology instance created for this session.
	 *
	 * @author Sebastian Furth
	 * @created Mar 24, 2011
	 */
	private class OntologySessionObject extends SessionObject {

		private final OWLOntology ontology;

		/**
		 * Creates a new OntologySessionObject which encapsulates the
		 * OWLOntology of this session.
		 *
		 * @param theSourceObject
		 * @param ontology the ontology of this session.
		 */
		public OntologySessionObject(SessionObjectSource theSourceObject, OWLOntology ontology) {
			super(theSourceObject);
			if (ontology == null) {
				throw new NullPointerException("The ontology can't be null!");
			}
			this.ontology = ontology;
		}

		/**
		 * Returns the OWLOntology of this session.
		 *
		 * @created Mar 24, 2011
		 * @return the ontology of this session.
		 */
		public OWLOntology getOntology() {
			return ontology;
		}
	}

}
