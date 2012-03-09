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
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;

/**
 * 
 * @author grotheer
 * @created 06.03.2012
 */
public class PSMethodJuri implements PSMethod {

	@Override
	public void init(Session session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {

		Set<JuriRule> rulesToUpdate = new HashSet<JuriRule>();
		for (PropagationEntry change : changes) {
			System.out.println(change.toString());
			if (change.hasChanged()) {
				Collection<JuriModel> models = session.getKnowledgeBase().getAllKnowledgeSlicesFor(
						JuriModel.KNOWLEDGE_KIND);
				for (JuriModel model : models) {
					for (JuriRule rule : model.getRules()) {
						if (rule.getChildren().contains(change.getObject())) {
							rulesToUpdate.add(rule);
						}
					}
				}
			}
		}
		for (JuriRule rule : rulesToUpdate) {
			Fact fact = rule.fire(session);
			if (fact != null) {
				session.getBlackboard().addValueFact(fact);
			}
			else {
				session.getBlackboard().removeValueFact(rule.getFather(), rule);
			}
		}
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// TODO Auto-generated method stub
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
}
