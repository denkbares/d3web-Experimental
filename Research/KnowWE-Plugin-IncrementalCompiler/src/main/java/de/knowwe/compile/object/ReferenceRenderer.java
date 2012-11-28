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
package de.knowwe.compile.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.report.DefaultMessageRenderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

/**
 * 
 * @author jochenreutelshofer
 * @created 27.11.2012
 */
/**
 * 
 * This renderer does rendering of error messages. The incremental compilation
 * algorithm currently does not explicitly store instantiated messages but the
 * state of a term can be asked on demand at any time
 * 
 * @author Jochen
 * @created 09.06.2011
 */
public class ReferenceRenderer implements Renderer {

	final Renderer REF_RENDERER =
			new ToolMenuDecoratingRenderer(new StyleRenderer(
					"color:rgb(25, 180, 120)"));

	final Renderer PREDEFINDED_TERM_RENDERER =
			new ToolMenuDecoratingRenderer(new StyleRenderer(
					"font-weight:bold;font-color:black"));

	public Renderer getRenderer() {
		return r;
	}

	public void setRenderer(Renderer r) {
		this.r = r;
	}

	private Renderer r = null;

	public ReferenceRenderer(Renderer renderer) {
		if (renderer != null) {
			r = renderer;
		}
		else {
			r = new DelegateRenderer();
		}
	}

	@Override
	public void render(Section<?> section, UserContext user, StringBuilder string) {

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
				string.append(DefaultMessageRenderer.ERROR_RENDERER.preRenderMessage(
						kdomReportMessage, user, null));
			}
			if (kdomReportMessage.getType() == Message.Type.WARNING) {
				string.append(
						DefaultMessageRenderer.WARNING_RENDERER.preRenderMessage(
								kdomReportMessage, user, null));
			}

		}
		if (IncrementalCompiler.getInstance().getTerminology().isPredefinedObject(
				reference.get().getTermIdentifier(reference))) {
			PREDEFINDED_TERM_RENDERER.render(reference, user, string);
		}
		else if (IncrementalCompiler.getInstance().getTerminology().isImportedObject(
				reference.get().getTermIdentifier(reference))) {
			REF_RENDERER.render(reference, user, string);
		}
		else {
			string.append(Strings.maskHTML("<a name='" + reference.getID() + "'>"));
			string.append(Strings.maskHTML("</a>"));
			r.render(reference, user, string);
		}
		for (Message kdomReportMessage : messages) {
			if (kdomReportMessage.getType() == Message.Type.ERROR) {
				string.append(
						DefaultMessageRenderer.ERROR_RENDERER.postRenderMessage(
								kdomReportMessage, user, null));
			}
			if (kdomReportMessage.getType() == Message.Type.WARNING) {
				string.append(
						DefaultMessageRenderer.WARNING_RENDERER.postRenderMessage(
								kdomReportMessage, user, null));
			}
		}
	}
}
