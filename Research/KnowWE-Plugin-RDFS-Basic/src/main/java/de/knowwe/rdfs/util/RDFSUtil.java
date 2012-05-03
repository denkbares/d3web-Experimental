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

import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.Strings;
import de.knowwe.plugin.Plugins;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.RDFSTermCategory;
import de.knowwe.rdfs.RDFSTerminology;

public class RDFSUtil {

	public static URI getURI(Section<? extends SimpleTerm> s) {
		if (s == null) return null;

		String termIdentifier = s.get().getTermIdentifier(s);

		URI uri = getRDFSURI(termIdentifier);

		if (uri == null) {
			String baseUrl = Rdf2GoCore.localns;
			String name = Strings.encodeURL(termIdentifier);
			uri = new URIImpl(baseUrl + name);
		}
		return uri;

	}

	public static boolean isTermCategory(Section<? extends SimpleTerm> ref, RDFSTermCategory c) {
		String termIdentifier = ref.getText();
		if (ref.get() instanceof SimpleTerm) {
			termIdentifier = ((SimpleTerm) ref.get()).getTermIdentifier(ref);
		}
		Object info = IncrementalCompiler.getInstance().getTerminology().getDefinitionInformationForValidTerm(
				termIdentifier);
		if (info != null) {

			if (info instanceof Map) {
				Set<?> keyset = ((Map<?, ?>) info).keySet();
				for (Object key : keyset) {
					if (((Map<?, ?>) info).get(key) instanceof RDFSTermCategory) {
						RDFSTermCategory rdfsTermCategory = (RDFSTermCategory) ((Map<?, ?>) info).get(key);
						if (rdfsTermCategory.equals(c)) {
							return true;
						}
					}
				}
			}
		}
		return false;
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
