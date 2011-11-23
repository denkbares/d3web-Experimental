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

package de.knowwe.compile.object;

import java.util.Collection;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.report.DefaultErrorRenderer;
import de.knowwe.core.report.KDOMError;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.report.KDOMWarning;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

public abstract class IncrementalTermReference<TermObject> extends TermReference<TermObject> {

	@SuppressWarnings("unchecked")
	final KnowWEDomRenderer<IncrementalTermReference> REF_RENDERER =
			new ToolMenuDecoratingRenderer<IncrementalTermReference>(new StyleRenderer(
					"color:rgb(25, 180, 120)"));
	@SuppressWarnings("unchecked")
	final KnowWEDomRenderer<IncrementalTermReference> PREDEFINDED_TERM_RENDERER =
			new ToolMenuDecoratingRenderer<IncrementalTermReference>(new StyleRenderer(
					"font-weight:bold;font-color:black"));

	public IncrementalTermReference(Class termObjectClass) {
		super(termObjectClass);
	}

	@Override
	public KnowWEDomRenderer getRenderer() {
		return new ReferenceRenderer(REF_RENDERER);
	}

	/**
	 * 
	 * This renderer does rendering of error messages. The incremental
	 * compilation algorithm currently does not explicitly store instantiated
	 * messages but the state of a term can be asked on demand at any time
	 * 
	 * @author Jochen
	 * @created 09.06.2011
	 */
	class ReferenceRenderer extends KnowWEDomRenderer<IncrementalTermReference> {

		private KnowWEDomRenderer r = null;

		public ReferenceRenderer(KnowWEDomRenderer renderer) {
			if (renderer != null) {
				r = renderer;
			}
			else {
				r = new DelegateRenderer();
			}
		}

		@Override
		public void render(KnowWEArticle article, Section<IncrementalTermReference> sec, UserContext user, StringBuilder string) {

			Collection<KDOMReportMessage> messages = IncrementalCompiler.getInstance().checkDefinition(
					sec.get().getTermIdentifier(sec));
			for (KDOMReportMessage kdomReportMessage : messages) {
				if (kdomReportMessage instanceof KDOMError) {
					string.append(DefaultErrorRenderer.INSTANCE_ERROR.preRenderMessage(
									kdomReportMessage, user));
				}
				if (kdomReportMessage instanceof KDOMWarning) {
					string.append(
							DefaultErrorRenderer.INSTANCE_WARNING.preRenderMessage(
									kdomReportMessage, user));
				}
			}
			if (IncrementalCompiler.getInstance().getTerminology().isPredefinedObject(
					sec.get().getTermIdentifier(sec))) {
				PREDEFINDED_TERM_RENDERER.render(article, sec, user, string);
			}
			else {
				r.render(article, sec, user, string);
			}
			for (KDOMReportMessage kdomReportMessage : messages) {
				if (kdomReportMessage instanceof KDOMError) {
					string.append(
							DefaultErrorRenderer.INSTANCE_ERROR.postRenderMessage(
									kdomReportMessage, user));
				}
				if (kdomReportMessage instanceof KDOMWarning) {
					string.append(
							DefaultErrorRenderer.INSTANCE_WARNING.postRenderMessage(
									kdomReportMessage, user));
				}
			}
		}

	}

}
