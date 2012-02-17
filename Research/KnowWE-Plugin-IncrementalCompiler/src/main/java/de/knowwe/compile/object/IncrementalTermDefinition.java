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
import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWERenderer;
import de.knowwe.core.report.DefaultErrorRenderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

public abstract class IncrementalTermDefinition<TermObject> extends SimpleDefinition {

	final KnowWERenderer CLASS_RENDERER = new ReferenceRenderer(
			new ToolMenuDecoratingRenderer<IncrementalTermReference>(
					new StyleRenderer("color:rgb(125, 80, 102)")));

	public IncrementalTermDefinition(Class termObjectClass) {
		super(TermRegistrationScope.GLOBAL, termObjectClass);
		this.setRenderer(CLASS_RENDERER);
	}

	class ReferenceRenderer implements KnowWERenderer<IncrementalTermDefinition> {

		private KnowWERenderer r = null;

		public ReferenceRenderer(KnowWERenderer renderer) {
			r = renderer;
		}

		@Override
		public void render(Section<IncrementalTermDefinition> sec, UserContext user, StringBuilder string) {

			Collection<Message> messages = IncrementalCompiler.getInstance().checkDefinition(
					sec.get().getTermIdentifier(sec));
			for (Message kdomReportMessage : messages) {
				if (kdomReportMessage.getType() == Message.Type.ERROR) {
					string.append(
							DefaultErrorRenderer.INSTANCE_ERROR.preRenderMessage(
									kdomReportMessage, user, null));
				}
				if (kdomReportMessage.getType() == Message.Type.WARNING) {
					string.append(
							DefaultErrorRenderer.INSTANCE_WARNING.preRenderMessage(
									kdomReportMessage, user, null));
				}
			}

			string.append(KnowWEUtils.maskHTML("<a name='" + sec.getID() + "'>"));

			r.render(sec, user, string);

			string.append(KnowWEUtils.maskHTML("</a>"));

			for (Message kdomReportMessage : messages) {
				if (kdomReportMessage.getType() == Message.Type.ERROR) {
					string.append(
							DefaultErrorRenderer.INSTANCE_ERROR.postRenderMessage(
									kdomReportMessage, user, null));
				}
				if (kdomReportMessage.getType() == Message.Type.WARNING) {
					string.append(
							DefaultErrorRenderer.INSTANCE_WARNING.postRenderMessage(
									kdomReportMessage, user, null));
				}
			}
		}
	}

}
