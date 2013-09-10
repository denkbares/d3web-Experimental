/*
 * Copyright (C) 2012 denkbares GmbH
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
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.report.DefaultMessageRenderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * A renderer for terms which also takes care of rendering error messages (using
 * the incremental compiler).
 * 
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 09.07.2012
 * @param <TermObject>
 */
public class TermDefinitionRenderer<TermObject> implements Renderer {

	private Renderer r = null;

	public TermDefinitionRenderer(Renderer renderer) {
		r = renderer;
	}

	@Override
	public void render(Section<?> sec, UserContext user, RenderResult string) {

		Collection<Message> messages = IncrementalCompiler.getInstance().checkDefinition(
				KnowWEUtils.getTermIdentifier(sec));
		for (Message kdomReportMessage : messages) {
			if (kdomReportMessage.getType() == Message.Type.ERROR) {
				DefaultMessageRenderer.ERROR_RENDERER.preRenderMessage(
						kdomReportMessage, user, null, string);
			}
			if (kdomReportMessage.getType() == Message.Type.WARNING) {
				DefaultMessageRenderer.WARNING_RENDERER.preRenderMessage(
						kdomReportMessage, user, null, string);
			}
		}

		string.appendHtml("<a name='" + sec.getID() + "'>");
		string.appendHtml("</a>");

		r.render(sec, user, string);

		for (Message kdomReportMessage : messages) {
			if (kdomReportMessage.getType() == Message.Type.ERROR) {
				DefaultMessageRenderer.ERROR_RENDERER.postRenderMessage(
						kdomReportMessage, user, null, string);
			}
			if (kdomReportMessage.getType() == Message.Type.WARNING) {
				DefaultMessageRenderer.WARNING_RENDERER.postRenderMessage(
						kdomReportMessage, user, null, string);
			}
		}
	}
}