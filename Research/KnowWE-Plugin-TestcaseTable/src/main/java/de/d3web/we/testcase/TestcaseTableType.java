/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.testcase;

import de.knowwe.core.compile.packaging.KnowWEPackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * 
 * @author Reinhard Hatko
 * @created 18.01.2011
 */
public class TestcaseTableType extends DefaultMarkupType {

	public static final String ANNOTATION_MASTER = "master";
	public static final String ANNOTATION_SHOW_SKIP_BUTTON = "showskip";
	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("TestcaseTable");
		m.addContentType(new TestcaseTable());
		m.addAnnotation(KnowWEPackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		m.addAnnotationRenderer(KnowWEPackageManager.PACKAGE_ATTRIBUTE_NAME,
				StyleRenderer.ANNOTATION);
		m.addAnnotation(ANNOTATION_MASTER, false);
		m.addAnnotationRenderer(ANNOTATION_MASTER, StyleRenderer.ANNOTATION);
		m.addAnnotation(ANNOTATION_SHOW_SKIP_BUTTON, false, "true", "false");
		m.addAnnotationRenderer(ANNOTATION_SHOW_SKIP_BUTTON, StyleRenderer.ANNOTATION);

	}

	/**
	 * 
	 */
	public TestcaseTableType() {
		super(m);
	}

	/**
	 * Gets the name of the master specified in the master annotation of the
	 * supplied section. If no annotation is present, the second argument is
	 * returned.
	 * 
	 * @created 22.01.2011
	 * @param section
	 * @param topic
	 * @return
	 */
	public static String getMaster(Section<TestcaseTableType> section, String topic) {
		String master = DefaultMarkupType.getAnnotation(section, ANNOTATION_MASTER);
		if (master != null) return master;
		else return topic;
	}

}
