/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.compile.object.renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.TypeRestrictedReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.report.DefaultMessageRenderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * This renderer does rendering of error messages. The incremental compilation
 * algorithm currently does not explicitly store instantiated messages but the
 * state of a term can be asked on demand at any time
 * 
 * @author jochenreutelshofer
 * @created 28.11.2012
 */
public class ReferenceSurroundingRenderer implements SurroundingRenderer {

	@Override
	public void renderPre(Section<?> section, UserContext user, RenderResult string) {
		@SuppressWarnings("unchecked")
		Section<? extends SimpleTerm> reference = (Section<? extends SimpleTerm>) section;

		Collection<Message> messages = IncrementalCompiler.getInstance().checkDefinition(
				KnowWEUtils.getTermIdentifier(reference));

		// insert TypeConstraint-warnings
		if (reference.get() instanceof TypeRestrictedReference) {
			if (((TypeRestrictedReference) reference.get()).checkTypeConstraints(reference) == false) {
				messages.add(new Message(
						Message.Type.WARNING,
						((TypeRestrictedReference) reference.get()).getMessageForConstraintViolation(reference)));
			}
		}

		List<Message> messageList = new ArrayList<Message>();
		messageList.addAll(messages);
		Collections.sort(messageList, new Comparator<Message>() {

			@Override
			public int compare(Message arg0, Message arg1) {
				return arg0.getType().compareTo(arg1.getType());
			}
		});
		for (Message kdomReportMessage : messageList) {
			if (kdomReportMessage.getType() == Message.Type.ERROR) {
				DefaultMessageRenderer.ERROR_RENDERER.preRenderMessage(
						kdomReportMessage, user, null, string);
			}
			if (kdomReportMessage.getType() == Message.Type.WARNING) {
				DefaultMessageRenderer.WARNING_RENDERER.preRenderMessage(
						kdomReportMessage, user, null, string);
			}

		}
	}

	@Override
	public void renderPost(Section<?> section, UserContext user, RenderResult string) {
		@SuppressWarnings("unchecked")
		Section<? extends SimpleTerm> reference = (Section<? extends SimpleTerm>) section;

		Collection<Message> messages = IncrementalCompiler.getInstance().checkDefinition(
				KnowWEUtils.getTermIdentifier(reference));
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
