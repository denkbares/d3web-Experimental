/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.we.wisec.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import common.Logger;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.NumValue;
import de.d3web.we.basic.WikiEnvironment;
import de.d3web.we.basic.WikiEnvironmentManager;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.event.Event;
import de.d3web.we.event.EventListener;
import de.d3web.we.wisec.util.Criteria;
import de.knowwe.d3web.event.FindingSetEvent;

public class WISECFindingSetEventListener implements EventListener {

	private static final String LIST = "list";
	private final List<String> activeLists = new LinkedList<String>();

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(1);
		events.add(FindingSetEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {

		if (!(event instanceof FindingSetEvent)) return;

		FindingSetEvent findingEvent = (FindingSetEvent) event;

		if (!checkQuestion(findingEvent.getQuestion())) return;

		// Specifies whether vales are added (true) or subtracted (false)
		boolean add = true;
		if (findingEvent.getValue().toString().equals("excluded")) add = false;

		// Stores the currently computed lists
		List<String> computedLists = new LinkedList<String>();

		for (String criteria : Criteria.getAllCriterias()) {

			try {
				Query query = createQuery(findingEvent.getQuestion(), findingEvent.getValue(),
						criteria);
				TupleQueryResult result = evaluateQuery(query);
				double accumulatedValue = computeNumValue(criteria, result, computedLists, add);
				setCounterValue(criteria, accumulatedValue, findingEvent.getWeb(),
						findingEvent.getUsername(),
						findingEvent.getNamespace());
				System.out.println(criteria + ": " + accumulatedValue);
			}
			catch (RepositoryException e) {
				Logger.getLogger(this.getClass()).warn(
						"Unable to create SPARQL-Query, no values set! " + e.getMessage());
			}
			catch (MalformedQueryException e) {
				Logger.getLogger(this.getClass()).warn(
						"Unable to create SPARQL-Query, no values set! " + e.getMessage());
			}
			catch (QueryEvaluationException e) {
				Logger.getLogger(this.getClass()).warn(
						"Unable to evaluate SPARQL-Query, no values set! " + e.getMessage());
			}

		}

		if (add) // Set all computed Lists as active Lists
		activeLists.addAll(computedLists);
		else // Or remove them in case of substraction
		activeLists.removeAll(computedLists);
	}

	/**
	 * Checks if the question which triggered this event is a question of the
	 * "Substances"-Questionnaire.
	 */
	private boolean checkQuestion(Question question) {
		QContainer qc = question.getKnowledgeBase().getManager().searchQContainer("Substances");
		if (qc == null) return false;
		return Arrays.asList(qc.getChildren()).contains(question);
	}

	/**
	 * Creates a SPARQL-Query with the appropriate substance and criteria.
	 */
	private Query createQuery(Question question, Value value, String criteria) throws RepositoryException, MalformedQueryException {

		String queryString = SemanticCoreDelegator.getInstance().getSparqlNamespaceShorts() +
								"SELECT ?" + LIST + " ?" + criteria + " " +
								"WHERE { " +
								"<http://ki.informatik.uni-wuerzburg.de/d3web/we/knowwe.owl#" +
								question.getName() + "> w:onListRelation ?substancelistrelation . "
				+
								"?" + LIST + " w:hasSubstanceRelation ?substancelistrelation . " +
								"?" + LIST + " w:" + criteria + " ?" + criteria + " ." +
								"}";

		RepositoryConnection con = SemanticCoreDelegator.getInstance().getUpper().getConnection();
		return con.prepareQuery(QueryLanguage.SPARQL, queryString);
	}

	/**
	 * Evaluates a SPARQL-Query and returns a TupleQueryResult containing the
	 * required numerical values.
	 */
	private TupleQueryResult evaluateQuery(Query query) throws QueryEvaluationException {
		if (!(query instanceof TupleQuery)) throw new IllegalStateException(
				"Query needs to be an instance of TupleQuery.");

		return ((TupleQuery) query).evaluate();
	}

	/**
	 * Computes the numerical value which will be added to the counter. The
	 * value is based on the committed TupleQueryResult.
	 */
	private double computeNumValue(String criteria, TupleQueryResult result,
			List<String> computedLists, boolean add) throws QueryEvaluationException {
		double accumulatedValue = 0;
		while (result.hasNext()) {
			BindingSet binding = result.next();
			String currentList = binding.getValue(LIST).stringValue();
			// TODO: Check this!
			if ((activeLists.contains(currentList) && add) // Don't add if list
															// was already
															// computed
					|| (!activeLists.contains(currentList) && !add)) // Don't
																		// subtract
																		// if
																		// list
																		// hasn't
																		// been
																		// computed
																		// yet
			continue;
			accumulatedValue += Double.parseDouble(binding.getValue(criteria).stringValue());
			computedLists.add(currentList);
		}
		return add ? accumulatedValue : accumulatedValue * (-1);
	}

	/**
	 * Adds the computed value to the counter of the current criteria.
	 */
	private void setCounterValue(String criteria, double accumulatedValue,
			String web, String user, String namespace) {

		if (accumulatedValue == 0) return;

		// Get the KnowledgeServiceSession
		WikiEnvironment env = WikiEnvironmentManager.getInstance().getEnvironments(web);
		Session kss =
				env.getBroker(user).getSession(namespace);
		if (kss == null) {
			Logger.getLogger(this.getClass()).error(
					"Unable to get KnowledgeServiceSession for namespace: " + namespace);
			return;
		}

		// Search the Counter-Question (P, B, Aqua_Tox etc.)
		Question counterQuestion = kss.getKnowledgeBase().getManager().searchQuestion(
				criteria);
		if (counterQuestion == null) {
			Logger.getLogger(this.getClass()).error(
					"Counter-Question: " + criteria + " was not found! No value set!");
			return;
		}

		// Get the old value
		Value oldValue = kss.getBlackboard().getValue(counterQuestion);
		double oldNumValue = oldValue instanceof NumValue ? (Double) oldValue.getValue() : 0;
		NumValue newValue = new NumValue(oldNumValue + accumulatedValue);

		// Set the new value
		List<Object> newValueList = new LinkedList<Object>();
		newValueList.add(newValue);
	}

}
