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
package de.knowwe.sessiondebugger;

import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.renderer.ReRenderSectionMarkerRenderer;

/**
 * DefaultMarkupType for SessionDebugger
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 19.01.2012
 */
public class SessionDebuggerType extends DefaultMarkupType {

	public static final String ANNOTATION_MASTER = "master";
	public static final String STC = "stc";

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("SessionDebugger");
		MARKUP.addAnnotation(ANNOTATION_MASTER, true);
		MARKUP.addAnnotation(STC, true);
	}

	public SessionDebuggerType() {
		super(MARKUP);
		this.setCustomRenderer(this.getRenderer());
	}

	@Override
	public KnowWEDomRenderer<SessionDebuggerType> getRenderer() {
		return new ReRenderSectionMarkerRenderer<SessionDebuggerType>(new SessionDebuggerRenderer());
	}

}
