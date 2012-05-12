/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaflux.anomalies;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.utils.Strings;

/**
 * 
 * @author Roland Jerg
 * @created 09.05.2012
 */
public abstract class AbstractAnomalyTest extends AbstractCITest {

	@Override
	public CITestResult call() throws Exception {
		String articleName = getParameter(0);
		String config = "knowledge base article: " + articleName;
		// StringBuffer msg = new StringBuffer();
		String errormsg = "";

		KnowledgeBase kb =
				D3webUtils.getKnowledgeBase(
						Environment.DEFAULT_WEB, articleName);

		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);

		errormsg = test(kb);

		// String errormsg = msg.toString();
		if (!errormsg.isEmpty()) {
			Strings.maskHTML(errormsg);
			res = new CITestResult(Type.FAILED, errormsg, config);
		}

		return res;
	}

	/**
	 * Each Anomalytest implements its own testmethod, which is then called by
	 * this class' call()
	 * 
	 * @created 10.05.2012
	 * @param kb
	 * @return
	 */
	protected abstract String test(KnowledgeBase kb);
}
