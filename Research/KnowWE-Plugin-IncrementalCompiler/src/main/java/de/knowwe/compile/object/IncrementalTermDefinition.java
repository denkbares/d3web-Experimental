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
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.report.DefaultErrorRenderer;
import de.knowwe.core.report.KDOMError;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.report.KDOMWarning;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.renderer.StyleRenderer;

public abstract class IncrementalTermDefinition<TermObject> extends TermDefinition<TermObject> {

	final StyleRenderer CLASS_RENDERER = new StyleRenderer(
			"color:rgb(125, 80, 102)");

	public IncrementalTermDefinition(Class termObjectClass) {
		super(termObjectClass);
		this.setCustomRenderer(CLASS_RENDERER);
	}

	@Override
	public KnowWEDomRenderer getRenderer() {
		return new ReferenceRenderer(this.customRenderer);
	}

	class ReferenceRenderer extends KnowWEDomRenderer<IncrementalTermDefinition> {

		private KnowWEDomRenderer r = null;

		public ReferenceRenderer(KnowWEDomRenderer renderer) {
			r = renderer;
		}

		@Override
		public void render(KnowWEArticle article, Section<IncrementalTermDefinition> sec, UserContext user, StringBuilder string) {

			Collection<KDOMReportMessage> messages = IncrementalCompiler.getInstance().checkDefinition(
					sec.get().getTermIdentifier(sec));
			for (KDOMReportMessage kdomReportMessage : messages) {
				if (kdomReportMessage instanceof KDOMError) {
					string.append(
							DefaultErrorRenderer.INSTANCE_ERROR.preRenderMessage(
									kdomReportMessage, user));
				}
				if (kdomReportMessage instanceof KDOMWarning) {
					string.append(
							DefaultErrorRenderer.INSTANCE_WARNING.preRenderMessage(
									kdomReportMessage, user));
				}
			}
			r.render(article, sec, user, string);
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
