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
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.Strings;
import de.knowwe.plugin.Plugins;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.RDFSTermCategory;
import de.knowwe.rdfs.RDFSTerminologyExtension;

public class RDFSUtil {

	/**
	 * Creates a URI for a term. It looks whether this term is a predefined term
	 * plugged in the system holding a specific namespace. If not a URI using
	 * the local namespace is created.
	 * 
	 * @created 12.07.2012
	 * @param s
	 * @return
	 */
	public static URI getURI(Section<? extends SimpleTerm> s) {
		if (s == null) return null;

		String termName = s.get().getTermName(s);

		URI uri = getURIForPredefinedConcept(termName);

		if (uri == null) {
			String baseUrl = Rdf2GoCore.getInstance().getLocalNamespace();
			String name = Strings.encodeURL(termName);
			uri = new URIImpl(baseUrl + name);
		}
		return uri;

	}

	public static boolean isTermCategory(Section<? extends SimpleTerm> ref, RDFSTermCategory c) {
		TermIdentifier termIdentifier = ref.get().getTermIdentifier(ref);
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

	public static URI getURIForPredefinedConcept(String termname) {
		URI uri = null;

		Extension[] exts = PluginManager.getInstance().getExtensions(
				Plugins.EXTENDED_PLUGIN_ID,
				Plugins.EXTENDED_POINT_TERMINOLOGY);
		for (Extension extension : exts) {
			Object o = extension.getSingleton();

			if (o instanceof RDFSTerminologyExtension) {
				URI tmp = (((RDFSTerminologyExtension) o).getURIForTerm(termname));
				if (tmp != null) {
					uri = tmp;
				}
			}
		}
		return uri;

	}

}
