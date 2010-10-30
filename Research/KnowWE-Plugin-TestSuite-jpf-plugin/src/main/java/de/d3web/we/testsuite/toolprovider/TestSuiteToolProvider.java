/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.we.testsuite.toolprovider;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.tools.DefaultTool;
import de.d3web.we.tools.Tool;
import de.d3web.we.tools.ToolProvider;
import de.d3web.we.wikiConnector.KnowWEUserContext;

/**
 * ToolProvider which provides some download links for the TestSuiteType.
 * 
 * @author Sebastian Furth (denkbares GmbH)
 * @created 18/10/2010
 */
public class TestSuiteToolProvider implements ToolProvider {

	@Override
	public Tool[] getTools(KnowWEArticle article, Section<?> section, KnowWEUserContext userContext) {

		Tool downloadXML = getDownloadXMLTool(article.getTitle(), article.getWeb(), section.getID());
		Tool downloadTXT = getDownloadTXTTool(article.getTitle(), article.getWeb(), section.getID());
		return new Tool[] {
				downloadXML, downloadTXT };
	}

	protected Tool getDownloadXMLTool(String topic, String web, String id) {
		// tool to provide download capability
		String jsAction = "window.location='action/TestSuiteServlet?type=case&KWiki_Topic="
				+ topic + "&web=" + web + "&filename=" + topic.replaceAll(" ", "_")
				+ "_testsuite.xml'";
		return new DefaultTool(
				"KnowWEExtension/images/xml.png",
				"Download XML",
				"Download the whole test suite into a single xml file.",
				jsAction);
	}

	protected Tool getDownloadTXTTool(String topic, String web, String id) {
		// tool to provide download capability
		String jsAction = "window.location='action/TestSuiteServlet?type=case&KWiki_Topic="
				+ topic + "&web=" + web + "&filename=" + topic.replaceAll(" ", "_")
				+ "_testsuite.txt'";
		return new DefaultTool(
				"KnowWEExtension/images/txt.png",
				"Download TXT",
				"Download the whole test suite into a single txt file.",
				jsAction);
	}

}
