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

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.compile.TerminologyHandler;
import de.knowwe.core.kdom.objects.GlobalTermReference;
import de.knowwe.core.kdom.objects.KnowWETerm;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

public class IRITermReference extends GlobalTermReference<IRIEntityType> implements RDFNodeType {

	@SuppressWarnings("unchecked")
	final KnowWEDomRenderer<IRITermReference> REF_RENDERER =
			new ToolMenuDecoratingRenderer<IRITermReference>(new StyleRenderer(
					"color:rgb(25, 180, 120)"));

	public IRITermReference() {
		super(IRIEntityType.class);
		this.setCustomRenderer(REF_RENDERER);
	}

	@Override
	public String getTermIdentifier(Section<? extends KnowWETerm<IRIEntityType>> s) {
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
	public URI getNode(Section<? extends RDFNodeType> s) {
		if (s.get() instanceof TermReference) {
			TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
			Section<? extends TermDefinition> definingSection = terminologyHandler.getTermDefiningSection(
					s.getArticle(), ((TermReference) s.get()).getTermIdentifier(s),
					Scope.GLOBAL);
			if (definingSection == null) return null;
			// KnowWEArticle main =
			// KnowWEEnvironment.getInstance().getArticleManager(KnowWEEnvironment.DEFAULT_WEB).getArticle("Main");

			Object termObject = definingSection.get().getTermObject(null,
					definingSection);
			if (termObject instanceof IRIEntityType) {
				return ((IRIEntityType) termObject).getIRI();
			}
		}
		return null;
	}

}
