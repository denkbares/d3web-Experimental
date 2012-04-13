/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;
import utils.TestUtils;
import connector.DummyConnector;
import de.d3web.plugin.test.InitPluginManager;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.kdom.xml.GenericXMLContent;
import de.knowwe.kdom.xml.GenericXMLObjectType;
import de.knowwe.kdom.xml.XMLSectionFinder;

public class XMLSectionFinderTest extends TestCase {

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
	}

	public void testXMLSectionFinder() {

		/**
		 * Initialise Environment
		 */
		DummyConnector connector = new DummyConnector();
		connector.setKnowWEExtensionPath(TestUtils.createKnowWEExtensionPath());
		Environment.initInstance(connector);
		Environment.getInstance().getArticle("default_web", "Test_Article");

		/**
		 * Setup
		 */
		String content = this.readXMLFile("2");

		Article article = Article.createArticle(content, "Test_Article", "default_web");
		Section artSec = article.getRootSection();

		/**
		 * The Tests.
		 */
		XMLSectionFinder f;
		List<SectionFinderResult> findings;
		int start;
		int end;

		// Test 1
		f = new XMLSectionFinder("Start");
		findings = f.lookForSections(content, artSec, null);
		start = 0;
		end = 254;
		assertEquals("Element <Start> begin index wrong", start, findings.get(0).getStart());
		assertEquals("Element <Start> end index wrong", end, findings.get(0).getEnd());

		// Test2
		f = new XMLSectionFinder("SubSection");
		findings = f.lookForSections(content, artSec, null);
		start = 9;
		end = 189;
		assertEquals("Element <SubSection> begin index wrong", start, findings.get(0).getStart());
		assertEquals("Element <SubSection> end index wrong", end, findings.get(0).getEnd());

		// Test3
		f = new XMLSectionFinder("SubSubSection1");
		findings = f.lookForSections(content, artSec, null);
		start = 61;
		end = 97;
		assertEquals("Element <SubSubSection1> begin index wrong", start,
				findings.get(0).getStart());
		assertEquals("Element <SubSubSection1> end index wrong", end, findings.get(0).getEnd());

		f = new XMLSectionFinder("SubSubSection2");
		findings = f.lookForSections(content, artSec, null);
		start = 100;
		end = 174;
		assertEquals("Element <SubSubSection2> begin index wrong", start,
				findings.get(0).getStart());
		assertEquals("Element <SubSubSection2> end index wrong", end, findings.get(0).getEnd());

		// Test4
		f = new XMLSectionFinder("Text2");
		findings = f.lookForSections(content, artSec, null);
		start = 120;
		end = 154;
		assertEquals("Element <Text2> begin index wrong", start, findings.get(0).getStart());
		assertEquals("Element <Text2> end index wrong", end, findings.get(0).getEnd());
		start = 191;
		end = 225;
		assertEquals("Element <Text2> begin index wrong", start, findings.get(1).getStart());
		assertEquals("Element <Text2> end index wrong", end, findings.get(1).getEnd());

		/**
		 * Tests for Generic XMLSectionFinder
		 */
		f = new XMLSectionFinder();
		findings = f.lookForSections(content, artSec, null);
		start = 0;
		end = 254;
		assertEquals("Generic SectionFinder failed", start, findings.get(0).getStart());
		assertEquals("Generic SectionFinder failed", end, findings.get(0).getEnd());

		/**
		 * Build a complete Article using GenericXMLObjectType
		 */

		RootType rootType = RootType.getInstance();
		rootType.addChildType(GenericXMLObjectType.getInstance());

		content = this.readXMLFile("0");
		article = Article.createArticle(content, "Test_Article2", "default_web");
		artSec = article.getRootSection();

		// Test children counts
		int expected = 3;
		Section artChild = Sections.findChildOfType(artSec, RootType.class).getChildren().get(0);
		assertEquals("ArticleSection: Childcount wrong", expected, artChild.getChildren().size());

		artChild = (Section) artChild.getChildren().get(1);
		expected = 2;
		assertEquals("Wrong subtree count", expected,
				Sections.findChildrenOfType(artChild, GenericXMLObjectType.class).size());

		// Test left subtree
		Section subRoot = Sections.findChildrenOfType(artChild, GenericXMLObjectType.class).get(0);
		expected = 3;
		assertEquals("Error in Left subtree:", expected, subRoot.getChildren().size());

		subRoot = Sections.findChildrenOfType(subRoot, GenericXMLContent.class).get(0);
		expected = 7;
		assertEquals("Error in Left subtree:", expected, subRoot.getChildren().size());

		expected = 3;
		assertEquals("Error in Left subtree:", expected,
				((Section) subRoot.getChildren().get(1)).getChildren().size());

		expected = 3;
		assertEquals("Error in Left subtree:", expected,
				((Section) subRoot.getChildren().get(3)).getChildren().size());

		expected = 3;
		subRoot = (Section) subRoot.getChildren().get(5);
		subRoot = (Section) subRoot.getChildren().get(1);
		subRoot = (Section) subRoot.getChildren().get(1);
		assertEquals("Error in Left subtree:", expected, subRoot.getChildren().size());

		// Test right subtree
		subRoot = (Section) artChild.getChildren().get(1);
		expected = 3;
		assertEquals("Error in right subtree", expected, subRoot.getChildren().size());

	}

	/**
	 * Reads the xml-date from the test-File.
	 * 
	 * @return
	 */
	private String readXMLFile(String number) {
		File f = new File(
				"src/test/resources/testXML" + number + ".txt");
		FileInputStream s;
		try {
			s = new FileInputStream(f);

			BufferedReader r = new BufferedReader(new InputStreamReader(s));
			String st = TestUtils.readBytes(r);
			s.close();
			r.close();
			return st;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
