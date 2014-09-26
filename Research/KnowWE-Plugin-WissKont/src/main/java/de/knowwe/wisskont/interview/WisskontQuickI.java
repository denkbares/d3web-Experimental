/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont.interview;

import de.d3web.we.quicki.QuickInterviewMarkup;
import de.knowwe.core.kdom.rendering.NothingRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 25.07.2013
 */
public class WisskontQuickI extends QuickInterviewMarkup {

	private static DefaultMarkup m2 = null;

	public static final String MARKUP_NAME = "WissQI";

	public static final String UNKNOWN_KEY = "unknown";

	public static final String ABSTRACTIONS_KEY = "abstractions";

	public static final String ANSWERS_KEY = "answers";

	public static final String SAVE_KEY = "save";

	public static final String MASTER_KEY = "master";

	static {
		m2 = new DefaultMarkup(MARKUP_NAME);
		m2.addAnnotation(UNKNOWN_KEY, false, "true", "false");
		m2.addAnnotationRenderer(UNKNOWN_KEY, NothingRenderer.getInstance());

		m2.addAnnotation(ABSTRACTIONS_KEY, false, "true", "false");
		m2.addAnnotationRenderer(ABSTRACTIONS_KEY, NothingRenderer.getInstance());

		m2.addAnnotation(ANSWERS_KEY, false);
		m2.addAnnotationRenderer(ANSWERS_KEY, NothingRenderer.getInstance());

		m2.addAnnotation(SAVE_KEY, false);
		m2.addAnnotationRenderer(SAVE_KEY, NothingRenderer.getInstance());

		m2.addAnnotation(MASTER_KEY, false);
		m2.addAnnotationRenderer(MASTER_KEY, StyleRenderer.PACKAGE);

	}

	/**
	 * 
	 */
	public WisskontQuickI() {
		super(m2);
		this.clearCompileScripts();
	}
}
