/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.rdfs.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.knowwe.core.kdom.objects.KnowWETerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.plugin.Plugins;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.RDFSTerminology;

public class RDFSUtil {

	public static URI getURI(Section<? extends KnowWETerm> s) {
		if (s == null) return null;

		URI uri = getRDFSURI(s.get().getTermIdentifier(s));

		if (uri == null) {
			String baseUrl = Rdf2GoCore.localns;
			try {
				String name = URLEncoder.encode(s.get().getTermIdentifier(s), "UTF-8");
				uri = new URIImpl(baseUrl + name);
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return uri;

	}

	public static URI getRDFSURI(String termname) {
		URI uri = null;

		Extension[] exts = PluginManager.getInstance().getExtensions(
				Plugins.EXTENDED_PLUGIN_ID,
				Plugins.EXTENDED_POINT_TERMINOLOGY);
		for (Extension extension : exts) {
			Object o = extension.getSingleton();
			if (o instanceof RDFSTerminology) {
				uri = (((RDFSTerminology) o).getURIForTerm(termname));
			}
		}
		return uri;

	}

}
