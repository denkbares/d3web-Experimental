/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.medicine;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.rdf2go.utils.SparqlType;

public class ButtonRenderer extends KnowWEDomRenderer<SparqlType> {

	private static ButtonRenderer instance;

	public static ButtonRenderer getInstance() {
		if (instance == null) {
			instance = new ButtonRenderer();
		}
		return instance;
	}

	@Override
	public void render(KnowWEArticle article, Section sec, UserContext user,
			StringBuilder result) {
		result.append(KnowWEUtils.maskHTML("<script type='text/javascript' src='KnowWEExtension/scripts/Medicine.js'></script>\n<input type='button' value='Export' title='' onclick='checkIfTableExists();'/><input type='button' value='Import' title='' onclick='readFromDB();'/><div id='medresult'></div>"));
	}

}