/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.jurisearch.tree;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.jurisearch.questionDef.ExplanationText;
import de.knowwe.jurisearch.questionDef.QuestionDefinitionArea;
import de.knowwe.kdom.dashtree.DashSubtree;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.defaultMarkup.ContentType;
import de.knowwe.kdom.filter.SectionFilter;
import de.uniwue.abstracttools.xml.XmlWriter;

/**
 *
 * @author boehler
 * @created 20.01.2012
 */
public class JuriTreeXmlGenerator {

	private static int idCounter = 0;

	public JuriTreeXmlGenerator(Section<KnowWEArticle> section) {
		if (isRoot(section)) {
			if (isJuriTreeSection(section))
				buildXML(section, "Name of Wiki");
		}
	}

	private boolean isRoot(Section<KnowWEArticle> section) {
		Object o = section.get();
		return o instanceof de.knowwe.core.kdom.KnowWEArticle;
	}

	private boolean isJuriTreeSection(Section<KnowWEArticle> section) {
		Object o = section.get();
		if (o instanceof de.knowwe.jurisearch.tree.QuestionTreeMarkup)
			return true;
		else {
			for (Section s : section.getChildren()) {
				if (isJuriTreeSection(s))
					return true;
			}
		}
		return false;
	}

	private Writer getWriter() {
		try {
			PrintWriter w = new
					PrintWriter("C:\\Program Files\\apache-tomcat-6.0.33\\webapps\\proket\\WEB-INF\\classes\\specs\\prototypes\\wikiTest.xml");
			//PrintWriter w = new PrintWriter(KnowWEEnvironment.getInstance().getWikiConnector().getSavePath()+ "\\wikiTest.xml");
			//PrintWriter w = new PrintWriter("C:\\wikis\\juriSearchWiki\\wikiTest.xml");
			// PrintWriter w = new
			// PrintWriter("C:\\Projekte\\KnowWE\\Research\\d3web-ProKEt\\src\\main\\resources\\specs\\prototypes\\wikiTest.xml");
			return w;			
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	public void buildXML(Section<KnowWEArticle> section, String name) {
		try {
			XmlWriter w = new XmlWriter(getWriter());
			w.writeDirectly("<?xml version='1.0' encoding='UTF-8'?>");
			w.writeStartTag("dialog sub-type='front' type='legal' css='legal, nofoot' header='"
					+ name + "' answer-type='oc'");

			List<Section<? extends Type>> roots = getSectionsWithPath(section,
					RootType.class, QuestionTreeMarkup.class,
					ContentType.class, QuestionTree.class, DashSubtree.class);
			for (Section sec : roots)
				buildXMLForDashSubtree(w, sec, null);
			w.writeEndTag("dialog");
			w.flush();
			w.close();
		} catch (IOException exc) {
			exc.printStackTrace(System.err);
		} catch (XmlWriter.MalformedXmlException mxe) {
			mxe.printStackTrace(System.err);
		}
	}


	public void buildXMLForDashSubtree(XmlWriter w, Section<DashSubtree> section, String parent)
			throws XmlWriter.MalformedXmlException, IOException {
		Section jexp = getSectionsWithPath(section, DashTreeElement.class, JuriTreeExpression.class).getFirst();
		String text = getSectionsWithPath(jexp, QuestionIdentifier.class).getFirst().getText();
		String explanation = getExplanationText(section, text);
		Section nodeMode =
		        getFirstSectionWithPath(jexp, JuriTreeExpression.RoundBracketExp.class, JuriTreeExpression.RoundExpBracketExpContent.class);
		String booleanTypeAdd = getAnswerTypeAttribute(nodeMode);
		String id = getNextUniqueID();
		String parentString = "";
		String bonusString = "";
		if (explanation != null) bonusString = " bonus-text=\"" + escapeQuotes(explanation) + "\""; 
		if (parent != null) parentString = " parent-id='" + parent + "'";
		w.writeFullTag("legalQuestion title='" + text + "' id='" + id + "'" + parentString + " " + booleanTypeAdd + bonusString);
		for (Section<? extends Type> s : getSectionsWithPath(section, DashSubtree.class)) {
			buildXMLForDashSubtree(w, (Section<DashSubtree>) s, id);
		}
	}


	private String getExplanationText(Section<DashSubtree> section, String questionText) {
	    String explanationText = null;
        de.knowwe.core.compile.terminology.TerminologyManager terminologyHandler = KnowWEEnvironment
                .getInstance().getTerminologyHandler(
                        KnowWEEnvironment.DEFAULT_WEB,
                        section.getArticle().getTitle());
        Section<?> definingSection = terminologyHandler
                .getTermDefiningSection(questionText);
        if (definingSection != null) {
            Section<QuestionDefinitionArea> area = Sections.findAncestorOfType(
                    definingSection, QuestionDefinitionArea.class);
            Section<ExplanationText> explSection = Sections.findSuccessor(area,
                    ExplanationText.class);
            explanationText = explSection.getText();
        }
        return explanationText;
	}

	
	private String escapeQuotes(String s) {
		if (s == null) return null;
		int i = 0;
		while (i!=-1) {
			i = s.indexOf("\"",i);
			if (i!=-1) {
				if (i>0) {
					if (s.charAt(i-1) != '\\') {
						s = s.substring(0, i) + "\\" + s.substring(i);
					}
				} else {
					s = "\\" + s;
				}
			}
		}
		return s;
	}
	
	private String getAnswerTypeAttribute(Section bracketExp) {
		if (bracketExp != null) {
			String roundBracketText = bracketExp.getText();
			if ("oder".equals(roundBracketText))
				return "and-or-type='OR'";
			if ("und".equals(roundBracketText))
				return "";
		}
		return "";
	}

	private Section<? extends Type> getFirstSectionWithPath(
			Section<? extends Type> section, Class... c) {
		LinkedList<Section<? extends Type>> l = getSectionsWithPath(section, c);
		if (l.size() > 0)
			return l.getFirst();
		else
			return null;
	}

	private LinkedList<Section<? extends Type>> getSectionsWithPath(
			Section<? extends Type> section, Class... c) {
		LinkedList<Section<? extends Type>> l = new LinkedList<Section<? extends Type>>();
		if (c.length == 0) {
			l.add(section);
		} else {
			final Class firstClass = c[0];
			List<Section<? extends Type>> children = section
					.getChildren(new SectionFilter() {
						@Override
						public boolean accept(Section<?> section) {
							return section.get().getClass().equals(firstClass);
						}
					});
			if (children.size() > 0) {
				Class[] c1 = new Class[c.length - 1];
				for (int i = 0; i < c.length - 1; i++)
					c1[i] = c[i + 1];
				for (Section<? extends Type> s : children)
					l.addAll(getSectionsWithPath(s, c1));
			}
		}
		return l;
	}

	private Section<? extends Type> getChildOfClass(
			Section<? extends Type> section, Class c) {
		List<Section<? extends Type>> children = section.getChildren();
		for (Section<? extends Type> s : children)
			if (s.get().getClass().equals(c))
				return s;
		return null;
	}

	private static String getNextUniqueID() {
		return "" + idCounter++;
	}

}
