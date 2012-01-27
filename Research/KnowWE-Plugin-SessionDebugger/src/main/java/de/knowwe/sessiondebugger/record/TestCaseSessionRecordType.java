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
package de.knowwe.sessiondebugger.record;

import de.knowwe.core.compile.packaging.KnowWEPackageManager;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.sessiondebugger.ProviderRefreshRenderer;

/**
 * Type for TestCaseSessionRecord Markup
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 26.01.2012
 */
public class TestCaseSessionRecordType extends DefaultMarkupType {

	public static final String ANNOTATION_MASTER = "master";
	public static final String ANNOTATION_FILE = "file";

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("TestCaseSessionRecord");
		MARKUP.addAnnotation(ANNOTATION_MASTER, true);
		MARKUP.addAnnotation(ANNOTATION_FILE, true);
		MARKUP.addAnnotation(KnowWEPackageManager.PACKAGE_ATTRIBUTE_NAME, false);
	}

	public TestCaseSessionRecordType() {
		super(MARKUP);
		addSubtreeHandler(new TestCaseSessionRecordSubtreeHandler());
		this.setCustomRenderer(new ProviderRefreshRenderer<TestCaseSessionRecordType>());
	}

}
