/*
 * Copyright (C) 2010 denkbares GmbH, Wuerzburg
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
package de.d3web.we.ci4ke.groovy;

import groovy.lang.Script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.d3web.we.ci4ke.handling.CIConfig;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITest;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;

/**
 * Abstract {@link Script}-based implementation of a {@link CITest}.
 * Functionality copied from {@link AbstractCITest}.
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 22.11.2010
 */
public abstract class GroovyCITestScript extends Script implements CITest {

	protected CIConfig config;

	protected List<String> parameters;

	public GroovyCITestScript() {
		this.config = CIConfig.DUMMY_CONFIG;
		this.parameters = new ArrayList<String>();
	}

	@Override
	public void init(CIConfig config) {
		try {
			this.config = (CIConfig) config.clone();
		}
		catch (CloneNotSupportedException e) {
		}
	}

	@Override
	public void setParameters(List<String> parameters) {
		this.parameters = Collections.unmodifiableList(parameters);
	}

	public String getParameter(int index) {
		return parameters.get(index);
	}

	public Collection<KnowWEArticle> getAllArticles() {
		return KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticles();
	}

	// public KnowWEArticle getArticle() {
	// return
	// KnowWEEnvironment.getInstance().getArticle(KnowWEEnvironment.DEFAULT_WEB,
	// this.config.getMonitoredArticleTitle());
	// }

	// public TestSuite getTestSuite() {
	// Section<TestSuiteType> section = getArticle().getSection().
	// findSuccessor(TestSuiteType.class);
	// if (section != null) {
	// TestSuite suite = (TestSuite) KnowWEUtils.getStoredObject(section,
	// TestSuiteType.TESTSUITEKEY);
	// return suite;
	// }
	// return null;
	// }

	// public List<String> findXCListsWithLessThenXRelations(int limitRelations)
	// {
	//
	// List<String> sectionIDs = new ArrayList<String>();
	//
	// List<Section<XCList>> found = new ArrayList<Section<XCList>>();
	// getArticle().getSection() Sections.findSuccessorsOfType(XCList.class, found);
	//
	// for (Section<XCList> xclSection : found) {
	// List<Section<XCLRelation>> relations = new
	// ArrayList<Section<XCLRelation>>();
	// xclSection Sections.findSuccessorsOfType(XCLRelation.class, relations);
	// if (relations.size() < limitRelations) {
	// sectionIDs.add(xclSection.getID());
	// }
	// }
	// return sectionIDs;
	// }

}
