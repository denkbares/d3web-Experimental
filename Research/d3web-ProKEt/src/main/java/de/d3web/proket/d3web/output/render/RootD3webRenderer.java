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

import javax.servlet.http.HttpSession;

import de.d3web.core.session.Session;
import de.d3web.proket.output.container.ContainerCollection;

/**
 * 
 * @author Albrecht Striffler
 * @created 27.05.2011
 */
public interface RootD3webRenderer {

	/**
	 * Starting point of the rendering. Begins by initializing basic rendering
	 * settings etc.
	 * 
	 * @created 14.01.2011
	 * @param cc the ContainerCollection that is to be used.
	 * @return the resulting ContainerCollection specifying the dialog.
	 */
	public ContainerCollection renderRoot(ContainerCollection cc, Session d3webSession, HttpSession http);

}