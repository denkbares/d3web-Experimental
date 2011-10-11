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

package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.onte.owl.terminology.URIUtil;

/**
 * Reference type for basic RDFS/OWL vocabulary
 *
 * @author Jochen
 * @created 26.05.2011
 */
public class BasicVocabularyReference extends AbstractType implements RDFNodeType{

	public static final StyleRenderer REF_RENDERER = new StyleRenderer("font-weight:bold");


	public BasicVocabularyReference() {
		this.setCustomRenderer(REF_RENDERER);
	}


	@Override
	public URI getNode(Section<? extends RDFNodeType> s) {
		if(s.get() instanceof BasicVocabularyReference) {
			return URIUtil.getURI((Section<? extends BasicVocabularyReference>)s);
		}
		return null;
	}

}
