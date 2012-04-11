/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.PropagationListener;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 * 
 * @author Reinhard Hatko
 * @created 02.04.2012
 */
public class DiaFluxCoverageTrace implements PropagationListener, SessionObject {

	private final Session session;
	private final CoverageSessionObject sessionObject;
	public static final SessionObjectSource<DiaFluxCoverageTrace> SOURCE = new SessionObjectSource<DiaFluxCoverageTrace>() {

		@Override
		public DiaFluxCoverageTrace createSessionObject(Session session) {
			return new DiaFluxCoverageTrace(session);
		}
	};

	public DiaFluxCoverageTrace(Session session) {
		this.session = session;
		this.sessionObject = new CoverageSessionObject();
		session.getPropagationManager().addListener(this);
	}

	@Override
	public void propagationStarted(Collection<PropagationEntry> entries) {
		
	}

	@Override
	public void propagationFinished(Collection<PropagationEntry> entries) {
		
	}

	@Override
	public void postPropagationStarted(Collection<PropagationEntry> entries) {
		if (!DiaFluxUtils.isFlowCase(session)) return;

		sessionObject.update(session);
		// session.getSessionObject(session.getPSMethodInstance(PSMDiaFluxCoverage.class)).update(
		// session);
		
	}

	public CoverageSessionObject getSessionObject() {
		return sessionObject;
	}


}