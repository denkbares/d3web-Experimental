/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.testcase;

import de.d3web.we.core.packaging.KnowWEPackageManager;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;


/**
 * 
 * @author Reinhard Hatko
 * @created 18.01.2011
 */
public class TestcaseTableType extends DefaultMarkupType {

	private static final String ANNOTATION_MASTER = "master";
	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("TestcaseTable");
		m.addContentType(new TestcaseTable());
		m.addAnnotation(KnowWEPackageManager.ATTRIBUTE_NAME, false);
		m.addAnnotation(ANNOTATION_MASTER, true);

	}
	/**
	 * 
	 */
	public TestcaseTableType() {
		super(m);
	}

	public static String getMaster(Section<TestcaseTableType> section) {
		return DefaultMarkupType.getAnnotation(section, ANNOTATION_MASTER);
	}

}
