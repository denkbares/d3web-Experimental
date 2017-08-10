/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.utilities.NamedObjectComparator;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.TestParameter.Mode;
import de.d3web.testing.TestingUtils;

/**
 * Base class for tests of TerminologyObjects.
 *
 * @author Reinhard Hatko
 * @created 23.01.2013
 */
public abstract class KBObjectsTest extends KBTest {

	private final String message;

	public KBObjectsTest(String message) {
		this.message = message;
		addIgnoreParameter(
				"objects",
				de.d3web.testing.TestParameter.Type.Regex,
				Mode.Mandatory,
				"A regular expression naming those d3web objects or their parents to be excluded from the tests.");
		addIgnoreParameter(
				"children",
				Mode.Optional,
				"A boolean value, determining if the children should also be ignored", "true",
				"false");
	}

	@Override
	public Message execute(KnowledgeBase kb, String[] args, String[]... ignores) throws InterruptedException {
		if (kb == null) throw new IllegalArgumentException("No knowledge base provided.");

		List<TerminologyObject> objects = new ArrayList<>(
				D3webTestUtils.filter(getBaseObjects(kb, args), ignores, getAdditionalIgnores(args)));

		Collection<TerminologyObject> errorObjects = doTest(kb, objects, args);

		errorObjects.removeIf(errorQuestion -> TestingUtils.isIgnored(errorQuestion.getName(), TestingUtils.compileIgnores(ignores)));

		if (errorObjects.isEmpty()) {
			return new Message(Type.SUCCESS);
		}
		else {
			List<TerminologyObject> result = new ArrayList<>(errorObjects);
			result.sort(new NamedObjectComparator());
			String error = formatErrorMessage(result, args);
			return D3webTestUtils.createFailure(result,
					error);
		}
	}

	private String formatErrorMessage(List<TerminologyObject> errorObjects, String[] args) {
		return MessageFormat.format(message, getFormatParameters(errorObjects, args));
	}

	/**
	 * Returns the parameters for insertion into the error message. This method
	 * allows to define additional information. Default is only the number of
	 * errors.
	 *
	 * @param errorObjects objects that have an error
	 * @param args parameters of the test execution
	 * @return parameters for insertion into the error message
	 * @created 26.03.2013
	 */
	protected Object[] getFormatParameters(List<TerminologyObject> errorObjects, String[] args) {
		return new Object[] { errorObjects.size() };
	}

	/**
	 * Names of objects to ignore by default
	 *
	 * @param args the arguments of the test
	 * @return the names of the objects to be ignored by default
	 * @created 26.03.2013
	 */
	protected String[] getAdditionalIgnores(String[] args) {
		return new String[] {
				"now", "start", "P000" };
	}

	protected abstract Collection<TerminologyObject> doTest(KnowledgeBase kb, List<TerminologyObject> objects, String[] args);

	/**
	 * Returns the base list of objects to test. Filtering of ignores is done
	 * based on this list.
	 *
	 * @param kb the knowledgebase under test
	 * @param args the arguments of the test
	 * @return the list of objects to be tested
	 * @created 26.03.2013
	 */
	protected abstract List<TerminologyObject> getBaseObjects(KnowledgeBase kb, String[] args);
}