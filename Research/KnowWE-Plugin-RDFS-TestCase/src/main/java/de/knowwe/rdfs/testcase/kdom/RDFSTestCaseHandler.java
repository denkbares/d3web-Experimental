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
package de.knowwe.rdfs.testcase.kdom;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.basicType.PlainText;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.sparql.validator.Validator;
import de.knowwe.rdf2go.sparql.validator.ValidatorResult;
import de.knowwe.rdfs.testcase.Binding;
import de.knowwe.rdfs.testcase.RDFSTestCase;

/**
 * 
 * @author Sebastian Furth
 * @created 20.12.2011
 */
public class RDFSTestCaseHandler extends SubtreeHandler<RDFSTestCaseType> {

	@Override
	public Collection<Message> create(Article article, Section<RDFSTestCaseType> section) {

		// Get name
		String name = DefaultMarkupType.getAnnotation(section,
				RDFSTestCaseType.ANNOTATION_NAME);

		if (name == null) {
			return Messages.asList(Messages.error("The test case has no name!"));
		}

		// Get and validate SPARQL-Query
		Section<SPARQLQueryType> sparqlSection = Sections.findSuccessor(section,
				SPARQLQueryType.class);
		if (sparqlSection == null) {
			return Messages.asList(Messages.syntaxError("Unable to find SPARQL-Query! Check the syntax!"));
		}
		String sparqlQuery = SPARQLQueryType.getSPARQLQuery(sparqlSection);
		ValidatorResult validation = Validator.validate(Rdf2GoCore.getInstance(), sparqlQuery);

		if (validation.hasErrors()) {
			Collection<Message> messages = new LinkedList<Message>();
			for (Exception e : validation.getErrors()) {

				messages.add(Messages.syntaxError(e.getLocalizedMessage()));
			}
			return messages;
		}

		// create new test case
		RDFSTestCase testCase = new RDFSTestCase(name, sparqlQuery);

		// get expected bindings
		List<Section<ExpectedBindingType>> expectedBindings =
				Sections.findSuccessorsOfType(section, ExpectedBindingType.class);

		if (expectedBindings == null || expectedBindings.isEmpty()) {
			return Messages.asList(Messages.syntaxError("Unable to find expected bindings! Check the syntax!"));
		}

		for (Section<ExpectedBindingType> expectedBinding : expectedBindings) {

			// get value
			List<Section<ValueType>> values =
					Sections.findSuccessorsOfType(expectedBinding, ValueType.class);
			if (values == null || values.isEmpty()) {
				return Messages.asList(Messages.syntaxError("There is no value in binding: "
						+ expectedBinding.getText()));
			}

			// create binding object, add values and add it to the test case
			Binding b = new Binding();
			for (Section<ValueType> value : values) {
				b.addURI(value.getText().trim());
			}
			testCase.addExpectedBinding(b);
		}

		// check for expected findings which were not recognized...
		Section<ExpectedBindingsType> expectedBindingsType = Sections.findSuccessor(section,
				ExpectedBindingsType.class);
		List<Section<PlainText>> plainTexts = Sections.findChildrenOfType(expectedBindingsType,
				PlainText.class);
		for (Section<PlainText> plainText : plainTexts) {
			if (!plainText.getText().matches("\\s*")) {
				return Messages.asList(Messages.syntaxError("There is an syntax errors in the expected binding: "
						+ plainText.getText().trim()));
			}
		}

		// save RDFS test case in KnowWE's object store
		String key = RDFSTestCaseType.MARKUP_NAME + testCase.getName();
		KnowWEUtils.storeObject(article, section, key, testCase);

		return Collections.emptyList();
	}
}
