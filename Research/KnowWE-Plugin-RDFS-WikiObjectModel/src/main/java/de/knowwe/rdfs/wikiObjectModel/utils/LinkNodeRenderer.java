/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.rdfs.wikiObjectModel.utils;

import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.ontology.sparql.RenderMode;
import de.knowwe.ontology.sparql.SparqlResultNodeRenderer;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class LinkNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable, UserContext user, Rdf2GoCore core, RenderMode mode) {
		String label = Rdf2GoUtils.reduceNamespace(core, text);
		RenderResult result = new RenderResult(user);
		if (mode == RenderMode.HTML) {
			result.appendHtml("<a href=\"" + text + "\">");
		}
		result.append(label);
		if (mode == RenderMode.HTML) {
			result.appendHtml("</a>");
		}
		return result.toStringRaw();
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return false;
	}

}
