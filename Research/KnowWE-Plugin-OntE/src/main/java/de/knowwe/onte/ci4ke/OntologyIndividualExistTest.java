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

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.taghandler.OWLApiTagHandlerUtil;

/**
 * A simple test for the continuous integration plugin for KnowWE. This test
 * checks if a certain amount of individuals could be found for a given concept.
 *
 * @author Stefan Mark
 * @created 17.10.2011
 */
public class OntologyIndividualExistTest extends AbstractCITest {

	@Override
	public CITestResult call() throws Exception {

		// check if the parameters match the necessary amount
		if (!checkIfParametersAreSufficient(2)) {

			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append("The number of arguments for the test '");
			errorMessage.append(this.getClass().getSimpleName());
			errorMessage.append("' are not sufficient. Please specify 2 arguments: ");
			errorMessage.append("arg0: Type of the individuals; arg1: Amount that should exist");

			return new CITestResult(Type.ERROR, errorMessage.toString());
		}

		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLReasoner reasoner = connector.getReasoner();

		reasoner.precomputeInferences();

		String concept = getParameter(0); // concept the individuals belong to
		int amount = Integer.parseInt(getParameter(1)); // amount of individuals
														// to be found

		StringBuilder configuration = new StringBuilder();
		configuration.append("Concept of individuals: ");
		configuration.append(concept);
		configuration.append("; Minimum amount expected: ");
		configuration.append(amount);
		configuration.append("; Reasoner: ");
		configuration.append(reasoner.getReasonerName());

		OWLClass cls = (OWLClass) AxiomFactory.getOWLAPIEntity(concept, OWLClass.class);
		NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(cls, true);
		StringBuilder message = new StringBuilder();

		if (individualsNodeSet.isEmpty()) {

			if (amount == 0) {
				message.append("No individuals found!");
				return new CITestResult(Type.SUCCESSFUL, message.toString(),
						configuration.toString());
			}
			else {
				message.append("No individuals found! But there should be some!");
				return new CITestResult(Type.ERROR, message.toString(), configuration.toString());
			}
		}
		else {
			Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();

			message.append(OWLApiTagHandlerUtil.labelClass(cls));
			message.append("'</strong>:<br />");

			for (OWLNamedIndividual ind : individuals) {
				String individual = OWLApiTagHandlerUtil.labelIndividual(ind);
				message.append("<strong>");
				message.append(individual);
				message.append("</strong> ");
				message.append(OnteCi4keUtil.renderHyperlink(individual));
				message.append(" ");

			}

			if (individuals.size() >= amount) {
				message.insert(0,
						"Ontology contains at least the expected individuals for the concepts <strong>'");
				return new CITestResult(Type.SUCCESSFUL, message.toString(),
						configuration.toString());
			}
			else {
				message.insert(0,
						"Ontology contains not the expected individuals for the concepts <strong>'");
				return new CITestResult(Type.FAILED, message.toString(),
						configuration.toString());
			}
		}
	}
}
