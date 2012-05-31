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
package de.knowwe.onte.ci4ke;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.DLExpressivityChecker;

import cc.denkbares.testing.Message;
import cc.denkbares.testing.Message.Type;
import de.d3web.we.ci4ke.testmodules.AbstractTest;
import de.knowwe.owlapi.OWLAPIConnector;

/**
 * A simple test for the continuous integration plugin for KnowWE. This test
 * checks the expressivity of the ontology. eg. if its is in OWL Lite, OWL DL or
 * OWL Full. The result of the check is printed to the CI dashboard.
 * 
 * @author Stefan Mark
 * @created 17.10.2011
 */
public class OntologyDLExpressivityTest extends AbstractTest<OWLAPIConnector> {

	@Override
	public Message execute(OWLAPIConnector connector, String[] args) {

		// OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLOntologyManager manager = connector.getManager();
		OWLOntology ontology = connector.getOntology();
		OWLReasoner reasoner = connector.getReasoner();

		Set<OWLOntology> importsClosure = manager.getImportsClosure(ontology);

		StringBuilder configuration = new StringBuilder();
		configuration.append("Ontology IRI: ");
		configuration.append(ontology.getOntologyID().getOntologyIRI());
		configuration.append("; Reasoner: ");
		configuration.append(reasoner.getReasonerName());

		DLExpressivityChecker checker = new DLExpressivityChecker(importsClosure);

		return new Message(Type.SUCCESS, "Expressivity of the ontology: "
				+ checker.getDescriptionLogicName());
	}

	@Override
	public int numberOfArguments() {
		return 0;
	}

	@Override
	public Class<OWLAPIConnector> getTestObjectClass() {
		return OWLAPIConnector.class;
	}
}
