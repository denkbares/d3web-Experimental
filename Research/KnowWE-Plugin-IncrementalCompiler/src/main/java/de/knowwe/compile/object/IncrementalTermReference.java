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
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.report.DefaultErrorRenderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

public abstract class IncrementalTermReference extends SimpleReference {

	final Renderer REF_RENDERER =
			new ToolMenuDecoratingRenderer(new StyleRenderer(
					"color:rgb(25, 180, 120)"));

	final Renderer PREDEFINDED_TERM_RENDERER =
			new ToolMenuDecoratingRenderer(new StyleRenderer(
					"font-weight:bold;font-color:black"));

	public IncrementalTermReference(Class<?> termObjectClass) {
		super(TermRegistrationScope.GLOBAL, termObjectClass);
		super.setRenderer(new ReferenceRenderer(REF_RENDERER));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.knowwe.core.kdom.AbstractType#setRenderer(de.knowwe.core.kdom.rendering
	 * .Renderer)
	 * 
	 * 
	 * makes sure that the ReferenceRenderer for the error-messages is actually
	 * installed and called and does not get overridden
	 */
	@Override
	public void setRenderer(Renderer renderer) {
		if (this.getRenderer() != null) {
			if (this.getRenderer() instanceof ReferenceRenderer) {
				((ReferenceRenderer) this.getRenderer()).setRenderer(renderer);
			}
		}
		else {
			super.setRenderer(renderer);
		}
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
	class ReferenceRenderer implements Renderer {

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

			for (Message kdomReportMessage : messages) {
				if (kdomReportMessage.getType() == Message.Type.ERROR) {
					string.append(DefaultErrorRenderer.INSTANCE_ERROR.preRenderMessage(
							kdomReportMessage, user, null));
				}
				if (kdomReportMessage.getType() == Message.Type.WARNING) {
					string.append(
							DefaultErrorRenderer.INSTANCE_WARNING.preRenderMessage(
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
				string.append(KnowWEUtils.maskHTML("<a name='" + reference.getID() + "'>"));
				r.render(reference, user, string);
				string.append(KnowWEUtils.maskHTML("</a>"));
			}
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
