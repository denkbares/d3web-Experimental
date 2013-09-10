/**
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.output.render;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.blackboard.Blackboard;

/**
 * Interface common to all d3web renderers.
 * 
 * TODO CHECK: do we need further methods in here that might be common to all
 * renderers and sub-renderers?
 * 
 * @author Martina Freiberg
 * @created 14.01.2011
 */
public interface D3webRenderer {

	/**
	 * Checks, whether the given TerminologyObject is indicated in the current
	 * session.
	 * 
	 * @created 03.11.2010
	 * @param to The TerminologyObject to be checked
	 * @param bb Blackboard of the current session
	 * @return true, if the TerminologyObject is indicated.
	 */
	//public boolean isIndicated(TerminologyObject to, Blackboard bb);

	// public boolean isParentIndicated(TerminologyObject to, Blackboard bb);

}
