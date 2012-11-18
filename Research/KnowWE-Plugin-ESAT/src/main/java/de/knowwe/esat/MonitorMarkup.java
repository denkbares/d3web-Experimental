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
package de.knowwe.esat;

import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * 
 * @author jochenreutelshofer
 * @created 18.11.2012
 */
public class MonitorMarkup extends DefaultMarkupType {

	public static final String LINK = "link";
	public static final String NAME = "name";
	public static final String BREITE = "breite";
	public static final String HOEHE = "höhe";

	public MonitorMarkup(DefaultMarkup markup) {
		super(markup);
	}

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("monitor");
		m.addContentType(new MonitorMarkupContentType());
		m.addAnnotation(PackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		m.addAnnotationRenderer(PackageManager.PACKAGE_ATTRIBUTE_NAME,
				StyleRenderer.ANNOTATION);
		m.addAnnotation(LINK, false);
		m.addAnnotation(NAME, false);
		m.addAnnotation(BREITE, false);
		m.addAnnotation(HOEHE, false);
	}

	public MonitorMarkup() {
		super(m);
		this.setRenderer(new DefaultMarkupRenderer(
				"KnowWEExtension/d3web/icon/rule24.png"));
	}
}
