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
package de.d3web.jurisearch;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;

/**
 * 
 * @author grotheer @created 06.03.2012
 */
public class PSMethodJuri extends PSMethodAdapter {

	private static PSMethodJuri instance = null;
	final KnowledgeKind<JuriModel> JURIMODEL = new KnowledgeKind<JuriModel>(
			"JuriModel", JuriModel.class);

	public PSMethodJuri() {
		super();
	}

	/**
	 * @return the one and only instance of this PSMethod (Singleton)
	 */
	public static PSMethodJuri getInstance() {
		if (instance == null) {
			instance = new PSMethodJuri();
		}
		return instance;
	}

	@Override
	public void init(Session session) {
		// TODO Auto-generated method stub
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {

		Set<JuriRule> rulesToUpdate = new HashSet<JuriRule>();
		for (PropagationEntry change : changes) {
			if (change.hasChanged()) {
				// get all models
				Collection<JuriModel> models = session.getKnowledgeBase().getAllKnowledgeSlicesFor(
						JuriModel.KNOWLEDGE_KIND);
				for (JuriModel model : models) {
					// run over every rule and collect those, which could be
					// directly affected by the change
					for (JuriRule rule : model.getRules()) {
						if (rule.getChildren().keySet().contains(change.getObject())) {
							rulesToUpdate.add(rule);
						}
					}
				}
			}
		}

		// run over every collected rule and evaluate it
		for (JuriRule rule : rulesToUpdate) {
			Fact fact = rule.fire(session);
			if (fact != null) {

				JuriModel juriModel =
						session.getKnowledgeBase().getKnowledgeStore().getKnowledge(JURIMODEL);
				Set juriRules = juriModel.getRules();

				// if a new fact is returned, save it to the sessions blackboard
				session.getBlackboard().addValueFact(fact);

			}
			else {
				// if no fact is returned, remove the fact of the father of the
				// rule.
				session.getBlackboard().removeValueFact(rule.getParent(), rule);
			}
		}
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// no facts to merge.
		return null;
	}

	@Override
	public boolean hasType(Type type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getPriority() {
		return 4;
	}

	@Override
	public Set<TerminologyObject> getPotentialDerivationSources(TerminologyObject derivedObject) {
		// TODO Auto-generated method stub
		return Collections.emptySet();
	}

	@Override
	public Set<TerminologyObject> getActiveDerivationSources(TerminologyObject derivedObject, Session session) {
		// TODO Auto-generated method stub
		return Collections.emptySet();
	}
}
