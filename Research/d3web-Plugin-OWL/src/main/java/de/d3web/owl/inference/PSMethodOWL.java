/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.owl.inference;

import java.util.Collection;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;

/**
 * PSMethod which delegates the reasoning to an external OWL-Reasoner.
 *
 * @author Sebastian Furth
 * @created Mar 3, 2011
 */
public class PSMethodOWL implements PSMethod {

	@Override
	public void init(Session session) {
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {

		updateFactsInOntology(session, changes);

		// TODO: neue Fakten abfragen

		// TODO: neue Fakten in Blackboard setzen

	}

	private void updateFactsInOntology(Session session, Collection<PropagationEntry> changes) {
		for (PropagationEntry change : changes) {
		}
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.problem;
	}

	@Override
	public double getPriority() {
		// TODO: Joba fragen, wie wichtig ihm die OWL-Fakten sind
		return 6;
	}

}
