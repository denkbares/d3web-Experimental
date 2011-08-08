/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaFlux.dialog;

import de.d3web.we.flow.DiaFluxDisplayEnhancement;
import de.d3web.we.user.UserContext;

/**
 * 
 * @author Reinhard Hatko
 * @created 26.05.2011
 */
public class DiaFluxDialog implements DiaFluxDisplayEnhancement {

	@Override
	public String[] getScripts() {
		return new String[] {
				"KnowWEExtension/scripts/dialog.js",
				"KnowWEExtension/scripts/dialogUtils.js",
				"KnowWEExtension/scripts/dialogSession.js" };
	}

	@Override
	public String[] getStylesheets() {
		return new String[] { "KnowWEExtension/css/dialog.css" };
	}

	public boolean activate(UserContext user) {
		return true;
	}

}
