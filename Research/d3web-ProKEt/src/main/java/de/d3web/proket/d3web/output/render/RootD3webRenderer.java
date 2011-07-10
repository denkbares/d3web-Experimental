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

import org.antlr.stringtemplate.StringTemplate;

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

	public String renderHeaderInfoLine(Session d3webSession);

	public String getAvailableFiles(HttpSession http);

	public void addButtons(StringTemplate st);

	/**
	 * Handles CSS specifications from the specification XML, i.e. checks the
	 * format, retrieves the corresponding CSS files from file system, and adds
	 * them to the final ContainerCollection of the dialog.
	 * 
	 * @created 15.01.2011
	 * @param cc ContainerCollection containing all infos about the resulting
	 *        dialog.
	 * @param d3wcon the d3web Connector for retrieving the css
	 */
	public void handleCss(ContainerCollection cc);

	/**
	 * Defines the necessary JavaScript required by this renderer/dialog, and
	 * adds it to the JS into the ContainerCollection.
	 * 
	 * @created 15.01.2011
	 * @param cc The ContainerCollection
	 */
	public void defineAndAddJS(ContainerCollection cc);

}
