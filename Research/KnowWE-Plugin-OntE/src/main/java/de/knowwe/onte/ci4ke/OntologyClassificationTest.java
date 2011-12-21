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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.knowwe.kdom.renderer.OnteRenderingUtils;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.owlapi.query.OWLApiQueryEngine;

/**
 * A simple test for the continuous integration plugin for KnowWE. This test
 * checks the classification of the given concepts.
 *
 * @author Stefan Mark
 * @created 17.10.2011
 */
public class OntologyClassificationTest extends AbstractCITest {

	public static final char GREATER_THAN = '\u003E';
	public static final char HYPHEN_MINUS = '\u002D';

	private static OWLApiQueryEngine engine;

	static {
		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		engine = new OWLApiQueryEngine(shortFormProvider);
	}

	@Override
	public CITestResult call() throws Exception {

		// check if the parameters match the necessary amount
		if (!checkIfParametersAreSufficient(1)) {

			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append("The number of arguments for the test '");
			errorMessage.append(this.getClass().getSimpleName());
			errorMessage.append("' are not sufficient. Please specify at least 1 argument: ");
			errorMessage.append("arg0: The classification hierarchy of the to check concepts, e.g. Food>Pizza>NamedPizza");

			return new CITestResult(Type.ERROR, errorMessage.toString());
		}

		String classification = getParameter(0);

		// check if arguments contains more than one concepts
		if (classification.indexOf(Character.toString(GREATER_THAN)) == -1) {
			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append("The classification hierarchy is invalid. ");
			errorMessage.append("Maybe you forgot to wrap it into quotes. ");
			errorMessage.append("Please specify in the following format: ");
			errorMessage.append("@\"Food>Pizza>NamedPizza\".");

			return new CITestResult(Type.ERROR, errorMessage.toString());
		}

		String[] concepts = classification.split(Character.toString(GREATER_THAN));
		Map<String, String> m = new HashMap<String, String>();

		for (int i = 0, l = concepts.length; i < l; i++) {
			if (i + 1 < l) {
				String parent = concepts[i];
				String decendant = concepts[i + 1];
				m.put(decendant, parent);
			}
		}

		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLReasoner reasoner = connector.getReasoner();
		reasoner.precomputeInferences();

		boolean isCorrect = true;
		StringBuilder message = new StringBuilder();
		message.append("The classification hierarchy is not completly entailed in the ontology.");
		message.append(" The following classification could not be found: <br /> ");

		for (String decendant : m.keySet()) {
			String parent = m.get(decendant);

			boolean isSub = isSubClassOf(parent, decendant);
			if(!isSub) {
				isCorrect = false;
				message.append(parent);
				message.append(GREATER_THAN);
				message.append(decendant);
			}
		}

		StringBuilder configuration = new StringBuilder();
		configuration.append("Reasoner: ");
		configuration.append(reasoner.getReasonerName());
		configuration.append("; Classification hierarchy: ");
		configuration.append(classification);

		if (isCorrect) {
			return new CITestResult(Type.SUCCESSFUL,
					"The classification hierarchy is entailed in the ontology!",
					configuration.toString());
		}
		else {
			return new CITestResult(Type.FAILED, message.toString(), configuration.toString());
		}
	}

	private boolean isSubClassOf(String parent, String child) {
		try {
			Set<OWLClass> classes = engine.getSubClasses(parent, true);
			for (OWLClass owlClass : classes) {
				String displayName = OnteRenderingUtils.getDisplayName(owlClass);
				if (displayName.equals(child)) {
					return true;
				}
			}
		}
		catch (ParserException e) {
			return false;
		}
		return false;
	}
}
