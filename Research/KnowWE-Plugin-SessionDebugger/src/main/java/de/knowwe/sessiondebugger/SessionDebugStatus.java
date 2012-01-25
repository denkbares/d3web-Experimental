/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.sessiondebugger;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.d3web.core.session.Session;
import de.d3web.core.utilities.Pair;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.TestCase;

/**
 * Represents the status of an SessionDebuggerSection
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 19.01.2012
 */
public class SessionDebugStatus {

	private Session session;
	private Date stcFileDate;
	private TestCase tc;
	private Date lastExecuted = null;
	private Map<Date, Collection<Pair<Check, Boolean>>> checkResults = new HashMap<Date, Collection<Pair<Check, Boolean>>>();

	public SessionDebugStatus(Date stcFileDate, Session session) {
		super();
		this.stcFileDate = stcFileDate;
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	/**
	 * Sets the session to a new session, clears the results of the checks and
	 * resets the date of the lastExecuted
	 * 
	 * @created 25.01.2012
	 * @param session new Sesion
	 */
	public void setSession(Session session) {
		this.session = session;
		checkResults.clear();
		lastExecuted = null;
	}

	public Date getStcFileDate() {
		return stcFileDate;
	}

	public void setStcFileDate(Date stcFileDate) {
		this.stcFileDate = stcFileDate;
	}

	public TestCase getTestCase() {
		return tc;
	}

	public void setTestCase(TestCase tc) {
		this.tc = tc;
	}

	public Date getLastExecuted() {
		return lastExecuted;
	}

	public void setLastExecuted(Date lastExecuted) {
		this.lastExecuted = lastExecuted;
	}

	/**
	 * Adds the result of a check to this status
	 * 
	 * @created 25.01.2012
	 * @param date Date of the check
	 * @param check executed {@link Check}
	 * @param result result of the check
	 */
	public void addCheckResult(Date date, Check check, boolean result) {
		Collection<Pair<Check, Boolean>> pairCollection = checkResults.get(date);
		if (pairCollection == null) {
			pairCollection = new LinkedList<Pair<Check, Boolean>>();
			checkResults.put(date, pairCollection);
		}
		pairCollection.add(new Pair<Check, Boolean>(check, result));
	}

	/**
	 * Gets the result of all checks on a specified date
	 * 
	 * @created 25.01.2012
	 * @param date specified date
	 * @return Collection of pairs representing checks and their results
	 */
	public Collection<Pair<Check, Boolean>> getCheckResults(Date date) {
		return checkResults.get(date);
	}
}
