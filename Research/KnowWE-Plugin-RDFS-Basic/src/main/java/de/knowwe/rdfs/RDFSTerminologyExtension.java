/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.rdfs;

import java.util.ResourceBundle;
import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.core.compile.terminology.TerminologyExtension;
import de.knowwe.rdf2go.Rdf2GoCore;

public class RDFSTerminologyExtension implements TerminologyExtension {

	protected ResourceBundle terms = null;

	// private static RDFSTerminology instance;
	//
	// public static RDFSTerminology getInstance() {
	// if (instance == null) {
	// instance = new RDFSTerminology();
	// }
	// return instance;
	// }

	public RDFSTerminologyExtension() {
		// TODO: implement singleton properly
		// instance = this;
		terms = ResourceBundle.getBundle("RDFS-terminology");
	}

	@Override
	public String[] getTermNames() {
		return terms.keySet().toArray(new String[terms.keySet().size()]);
	}

	public URI getURIForTerm(String term) {
		String keyCaseInsensitive = termContainKeyCaseInsensitive(term);
		if (keyCaseInsensitive != null) {
			return Rdf2GoCore.getInstance().createURI(terms.getString(keyCaseInsensitive));
		}
		else {
			return null;
		}
	}

	private String termContainKeyCaseInsensitive(String term) {
		Set<String> keySet = terms.keySet();
		for (String string : keySet) {
			if (string.equalsIgnoreCase(term)) {
				return string;
			}
		}
		return null;
	}

}
