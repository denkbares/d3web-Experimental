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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.utilities.Pair;
import de.d3web.testcase.model.Check;

/**
 * Represents the status of an SessionDebuggerSection
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 19.01.2012
 */
public class SessionDebugStatus {

	private Session session;
	private Date lastExecuted = null;
	private Map<Date, Collection<Pair<Check, Boolean>>> checkResults = new HashMap<Date, Collection<Pair<Check, Boolean>>>();
	private Map<Date, Map<Question, Value>> timeValues = new HashMap<Date, Map<Question, Value>>();
	private HashSet<Question> questionsToObserve = new HashSet<Question>();

	public SessionDebugStatus(Session session) {
		super();
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
	 * @param session new Session
	 */
	public void setSession(Session session) {
		this.session = session;
		checkResults.clear();
		timeValues.clear();
		lastExecuted = null;
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

	public boolean addQuestionToObserve(Question q) {
		return questionsToObserve.add(q);
	}

	/**
	 * Needs to be called when all findings of a date are set to the session.
	 * This method saves all values of questions, the user wants to observe.
	 * 
	 * @created 31.01.2012
	 * @param date specified Date
	 */
	public void finished(Date date) {
		Map<Question, Value> values = timeValues.get(date);
		if (values == null) {
			values = new HashMap<Question, Value>();
			timeValues.put(date, values);
		}
		for (Question q : questionsToObserve) {
			values.put(q, session.getBlackboard().getValue(q));
		}
	}

	/**
	 * Returns the value of a question at a specified time. The question must
	 * have been added by the method addQuestionsToObserve before finishing the
	 * date. If no value is found, null is returned.
	 * 
	 * @created 01.02.2012
	 * @param q specified {@link Question}
	 * @param d specified {@link Date}
	 * @return Value of the Question at the Date or null, if no value has been
	 *         saved
	 */
	public Value getValue(Question q, Date d) {
		Map<Question, Value> values = timeValues.get(d);
		if (values != null) {
			return values.get(q);
		}
		return null;
	}
}
