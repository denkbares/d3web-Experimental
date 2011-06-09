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

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.DefaultErrorRenderer;
import de.d3web.we.kdom.report.message.NoSuchObjectError;
import de.d3web.we.user.UserContext;
import de.knowwe.compile.IncrementalCompiler;

public abstract class IncrementalTermDefinition<TermObject>  extends TermDefinition<TermObject>{
	
	public IncrementalTermDefinition(Class termObjectClass) {
		super(termObjectClass);
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

			boolean hasError = ! IncrementalCompiler.getInstance().hasValidDefinition(
					sec.get().getTermIdentifier(sec));
			if (hasError) {
				string.append(DefaultErrorRenderer.INSTANCE_ERROR.preRenderMessage(
						new NoSuchObjectError(sec.getOriginalText()), user));
			}
			r.render(article, sec, user, string);
			if (hasError) {
				string.append(DefaultErrorRenderer.INSTANCE_ERROR.postRenderMessage(
						new NoSuchObjectError(sec.getOriginalText()), user));
			}
		}

	}

}
