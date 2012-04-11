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
package de.d3web.diaflux.coverage;

import java.util.Collection;

import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;


/**
 * This PSM keeps track of the paths taken in the DiaFlux models during a
 * session.
 * 
 * @author Reinhard Hatko
 * @created 05.08.2011
 */
public class PSMDiaFluxCoverage extends PSMethodAdapter {



	@Override
	public void init(Session session) {
		session.getSessionObject(DiaFluxCoverageTrace.SOURCE);
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeError(facts);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.consumer;
	}

	@Override
	public double getPriority() {
		return 6;
	}


}
