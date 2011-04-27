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

import javax.servlet.http.HttpSession;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.proket.output.container.ContainerCollection;

/**
 * Interface common to all d3web renderers.
 *
 * TODO CHECK: do we need further methods in here that might be common to all
 * renderers and sub-renderers?
 *
 * @author Martina Freiberg
 * @created 14.01.2011
 */
public interface ID3webRenderer {

	/**
	 * Starting point of the rendering. Begins by initializing basic rendering
	 * settings etc.
	 *
	 * @created 14.01.2011
	 * @param cc the ContainerCollection that is to be used.
	 * @return the resulting ContainerCollection specifying the dialog.
	 */
	public ContainerCollection renderRoot(ContainerCollection cc, Session d3webSession, HttpSession http);

	/**
	 * Renders the current terminology object.
	 *
	 * @created 14.01.2011
	 * @param cc ContainerCollection to be used.
	 * @param to TerminologyObject to be rendered.
	 * @param parent The parent object of to
	 * @return the resulting String that is inserted in the parent
	 *         StringTemplate as a result.
	 */
	public String renderTerminologyObject(ContainerCollection cc, TerminologyObject to,
			TerminologyObject parent);

	/**
	 * Renders the current terminology object, only using a Choice object,
	 * instead of further TerminologyObjects.
	 *
	 * @see renderTerminologyObject(cc, to, parent)
	 * @created 14.01.2011
	 * @param cc ContainerCollection to be used.
	 * @param c Choice to be rendered.
	 * @param parent The parent object of to
	 * @return the resulting String that is inserted in the parent
	 *         StringTemplate as a result.
	 */
	public String renderTerminologyObject(ContainerCollection cc, Choice c,
			TerminologyObject to, TerminologyObject parent);

	/**
	 * Checks, whether the given TerminologyObject is indicated in the current
	 * session.
	 *
	 * @created 03.11.2010
	 * @param to The TerminologyObject to be checked
	 * @param bb Blackboard of the current session
	 * @return true, if the TerminologyObject is indicated.
	 */
	public boolean isIndicated(TerminologyObject to, Blackboard bb);

	public boolean isParentIndicated(TerminologyObject to, Blackboard bb);

}
