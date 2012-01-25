/*
 * Copyright (C) 2011 denkbares GmbH
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
import de.d3web.core.session.Session;
import de.d3web.proket.output.container.ContainerCollection;

/**
 * 
 * @author Albrecht Striffler
 * @created 27.05.2011
 */
public interface IQuestionD3webRenderer {

	/**
	 * Renders the current terminology object.
	 * 
	 * @created 14.01.2011
	 * @param d3webSession TODO
	 * @param cc ContainerCollection to be used.
	 * @param to TerminologyObject to be rendered.
	 * @param parent The parent object of to
         * @param localeIdent identifier which language is to be used
	 * @return the resulting String that is inserted in the parent
	 *         StringTemplate as a result.
	 */
	public String renderTerminologyObject(Session d3webSession, ContainerCollection cc,
			TerminologyObject to, TerminologyObject parent, int localeIdent);

}
