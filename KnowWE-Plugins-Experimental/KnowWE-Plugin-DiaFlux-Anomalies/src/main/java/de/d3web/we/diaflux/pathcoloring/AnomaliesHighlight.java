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
package de.d3web.we.diaflux.pathcoloring;

import de.knowwe.core.user.UserContext;
import de.knowwe.diaflux.DiaFluxDisplayEnhancement;
import de.knowwe.diaflux.DiaFluxTraceHighlight;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class AnomaliesHighlight implements DiaFluxDisplayEnhancement {

	public static String[] SCRIPTS = new String[] { "AnomalyEXT/scripts/anomalieshighlight.js" };
	public static String[] CSSS = new String[] { "AnomalyEXT/css/anomalieshighlight.css" };

	public static final String ANOMALIES_HIGHLIGHT = "anomalies";

	@Override
	public boolean activate(UserContext user, String scope) {
		return DiaFluxTraceHighlight.checkForHighlight(user, ANOMALIES_HIGHLIGHT);
	}

	@Override
	public String[] getScripts() {
		return SCRIPTS;
	}

	@Override
	public String[] getStylesheets() {
		return CSSS;
	}

}
