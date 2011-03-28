/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.owl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Class which offers some convenience methods for the handling of OWL
 * ontologies. These convenience methods offer easy access to OWL objects such
 * as classes, individuals or properties.
 *
 * 
 * @author Sebastian Furth
 * @created Mar 28, 2011
 */
public class OWLOntologyUtil {

	private final OWLOntology ontology;

	// Caches for OWL objects, avoids multiple searches
	private final Map<IRI, OWLClass> cachedClasses = new HashMap<IRI, OWLClass>();
	private final Map<IRI, OWLIndividual> cachedIndividuals = new HashMap<IRI, OWLIndividual>();
	private final Map<IRI, OWLObjectProperty> cachedProperties = new HashMap<IRI, OWLObjectProperty>();

	// Just for convenience and code beautification
	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	public OWLOntologyUtil(OWLOntology ontology) {
		if (ontology == null) {
			throw new NullPointerException("The ontology can't be null!");
		}
		this.ontology = ontology;
	}

	public OWLClass getOWLClassFor(IRI iri) {
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

	public OWLIndividual getOWLIndividualFor(IRI iri) {
		if (cachedIndividuals.containsKey(iri)) {
			return cachedIndividuals.get(iri);
		}
		Set<OWLEntity> entities = ontology.getEntitiesInSignature(iri);
		for (OWLEntity entity : entities) {
			if (entity.isOWLNamedIndividual() && entity.getIRI().equals(iri)) {
				cachedIndividuals.put(iri, entity.asOWLNamedIndividual());
				return entity.asOWLNamedIndividual();
			}
		}
		logger.warning("No OWLIndividual found for IRI: " + iri);
		return null;
	}

	public OWLObjectProperty getOWLPropertyFor(IRI iri) {
		if (cachedProperties.containsKey(iri)) {
			return cachedProperties.get(iri);
		}
		Set<OWLEntity> entities = ontology.getEntitiesInSignature(iri);
		for (OWLEntity entity : entities) {
			if (entity.isOWLObjectProperty() && entity.getIRI().equals(iri)) {
				cachedProperties.put(iri, entity.asOWLObjectProperty());
				return entity.asOWLObjectProperty();
			}
		}
		logger.warning("No OWLProperty found for IRI: " + iri);
		return null;
	}

}
