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

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.GlobalTermReference;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.objects.TermReference;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.tools.ToolMenuDecoratingRenderer;
import de.d3web.we.utils.KnowWEUtils;

public class OWLTermReference extends GlobalTermReference<URIObject> implements RDFResourceType {

	@SuppressWarnings("unchecked")
	final KnowWEDomRenderer<OWLTermReference> REF_RENDERER =
			new ToolMenuDecoratingRenderer<OWLTermReference>(new StyleRenderer(
					"color:rgb(25, 180, 120)"));

	public OWLTermReference() {
		super(URIObject.class);
		this.setCustomRenderer(REF_RENDERER);
	}

	@Override
	public String getTermName(Section<? extends KnowWETerm<URIObject>> s) {
		// dirty hack for colons '::'
		// TODO: fix
		if (s.getOriginalText().endsWith("::")) return s.getOriginalText().substring(0,
				s.getOriginalText().length() - 2);

		return s.getOriginalText();

	}

	@Override
	public String getTermObjectDisplayName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public URI getNode(Section<? extends RDFResourceType> s) {
		if (s.get() instanceof TermReference) {
			TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
			Section<? extends TermDefinition> definingSection = terminologyHandler.getTermDefiningSection(
					s.getArticle(), ((TermReference) s.get()).getTermName(s),
					KnowWETerm.GLOBAL);
			if (definingSection == null) return null;
			// KnowWEArticle main =
			// KnowWEEnvironment.getInstance().getArticleManager(KnowWEEnvironment.DEFAULT_WEB).getArticle("Main");

			Object termObject = definingSection.get().getTermObject(null,
					definingSection);
			if (termObject instanceof URIObject) {
				return ((URIObject) termObject).getURI();
			}
		}
		return null;
	}

}
