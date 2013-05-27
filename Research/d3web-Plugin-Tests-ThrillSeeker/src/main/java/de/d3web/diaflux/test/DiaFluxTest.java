/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.diaflux.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.utilities.NamedObjectComparator;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.test.D3webTestUtils;
import de.d3web.test.KBTest;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.TestParameter.Mode;


/**
 * Base class for DiaFlux tests
 * 
 * @author Reinhard Hatko
 * @created 21.05.2013
 */
public abstract class DiaFluxTest extends KBTest {

	private final String message;

	public DiaFluxTest(String message) {
		this.message = message;
		addIgnoreParameter(
				"flows",
				de.d3web.testing.TestParameter.Type.Regex,
				Mode.Mandatory,
				"A regular expression naming those flowcharts to be excluded from the tests.");
	}

	@Override
	public Message execute(KnowledgeBase testObject, String[] args, String[]... ignores) throws InterruptedException {
		if (testObject == null) throw new IllegalArgumentException("No knowledge base provided.");
		if (!DiaFluxUtils.hasFlows(testObject)) return new Message(Type.SUCCESS);

		List<Flow> flows = new ArrayList<Flow>(
				D3webTestUtils.filterNamed(DiaFluxUtils.getFlowSet(testObject).getFlows(), ignores));

		Collection<Flow> erroneousFlows = doTest(testObject, flows);

		if (erroneousFlows.size() > 0) {
			List<Flow> sortedFlows = new ArrayList<Flow>(erroneousFlows);
			Collections.sort(sortedFlows, new NamedObjectComparator());
			String error = formatErrorMessage(sortedFlows, args);
			return D3webTestUtils.createErrorMessage(sortedFlows, error);
		}
		else {
			return new Message(Type.SUCCESS);
		}
	}

	/**
	 * This method implements the test logic.
	 */
	protected abstract Collection<Flow> doTest(KnowledgeBase testObject, List<Flow> flows);

	protected String formatErrorMessage(List<Flow> errorObjects, String[] args) {
		return MessageFormat.format(message, getFormatParameters(errorObjects, args));
	}

	/**
	 * Returns the parameters for insertion into the error message. This method
	 * allows to define additional information. Default is only the number of
	 * errors.
	 * 
	 * @created 26.03.2013
	 * @param errorObjects
	 * @param args
	 * @return
	 */
	protected Object[] getFormatParameters(List<Flow> errorObjects, String[] args) {
		return new Object[] { errorObjects.size() };
	}


}
