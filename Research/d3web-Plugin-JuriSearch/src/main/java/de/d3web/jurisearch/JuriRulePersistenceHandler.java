/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
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

package de.d3web.jurisearch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionOC;

/**
 * PersistenceHandler for XCLModels
 * 
 * @author kazamatzuri, Markus Friedrich (denkbares GmbH)
 * 
 */
public class JuriRulePersistenceHandler implements KnowledgeReader,
		KnowledgeWriter {

	public final static String ID = "juripattern";

	@Override
	public void write(KnowledgeBase knowledgeBase, OutputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("type", JuriRulePersistenceHandler.ID);
		root.setAttribute("system", "d3web");
		doc.appendChild(root);
		Element ksNode = doc.createElement("KnowledgeSlices");
		root.appendChild(ksNode);
		ArrayList<JuriRule> slices = new ArrayList<JuriRule>(
				knowledgeBase.getAllKnowledgeSlicesFor(JuriRule.KNOWLEDGE_KIND));
		Collections.sort(slices, new JuriRuleComparator());
		float cur = 0;
		int max = getEstimatedSize(knowledgeBase);
		for (KnowledgeSlice model : slices) {
			if (model instanceof JuriRule) {
				ksNode.appendChild(getRuleElement((JuriRule) model, doc));
				listener.updateProgress(++cur / max, "Saving knowledge base: Juri Rules");
			}
		}
		Util.writeDocumentToOutputStream(doc, stream);
	}

	@Override
	public int getEstimatedSize(KnowledgeBase knowledgeBase) {
		return knowledgeBase.getAllKnowledgeSlicesFor(JuriRule.KNOWLEDGE_KIND).size();
	}

	@Override
	public void read(KnowledgeBase knowledgeBase, InputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.streamToDocument(stream);
		loadKnowledgeSlices(knowledgeBase, doc, listener);
	}

	/**
	 * 
	 * @created 08.03.2012
	 * @param knowledgeBase
	 * @param doc
	 * @param listener
	 */
	public KnowledgeBase loadKnowledgeSlices(KnowledgeBase kb, Document doc, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Loading knowledge base");
		NodeList jurirules = doc.getElementsByTagName("JuriRule");
		int cur = 0;
		int max = jurirules.getLength();
		for (int i = 0; i < jurirules.getLength(); i++) {
			Node current = jurirules.item(i);
			addKnowledge(kb, current);
			listener.updateProgress(++cur / max, "Loading knowledge base: Juri Rules");
		}

		return kb;
	}

	private void addKnowledge(KnowledgeBase kb, Node current) throws IOException {
		String isDisjunctive = getAttribute("disjunctive", current);

		JuriRule rule = new JuriRule();
		if (isDisjunctive != null) {
			rule.setDisjunctive(Boolean.parseBoolean(isDisjunctive));
		}
		NodeList elements = current.getChildNodes();
		for (int i = 0; i < elements.getLength(); i++) {
			if (elements.item(i).getNodeName().equals("Question")) {
				rule.setFather((QuestionOC)
						PersistenceManager.getInstance().readFragment(
								(Element) elements.item(i), kb));
			}
			else if (elements.item(i).getNodeName().equals("Children")) {
				NodeList children = elements.item(i).getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					rule.addChild((QuestionOC)
							PersistenceManager.getInstance().readFragment(
									(Element) children.item(j), kb));
				}
			}
		}
		kb.getKnowledgeStore().addKnowledge(JuriRule.KNOWLEDGE_KIND, rule);
	}

	public Element getRuleElement(JuriRule jurirule, Document doc) throws IOException {
		Element ruleelement = doc.createElement("JuriRule");
		Element children = doc.createElement("Children");
		ruleelement.appendChild(PersistenceManager.getInstance().writeFragment(
				jurirule.getFather(), doc));
		for (QuestionOC child : jurirule.getChildren()) {
			children.appendChild(PersistenceManager.getInstance().writeFragment(child, doc));
		}
		ruleelement.appendChild(children);
		ruleelement.setAttribute("disjuntice", "" + jurirule.isDisjunctive());
		return ruleelement;
	}

	private class JuriRuleComparator implements Comparator<JuriRule> {

		@Override
		public int compare(JuriRule r1, JuriRule r2) {
			return (r1.getFather().getName().compareTo(r2.getFather().getName()));
		}

	}

	private String getAttribute(String name, Node node) {
		if ((node != null) && (node.getAttributes() != null)
				&& node.getAttributes().getNamedItem(name) != null) {
			return node.getAttributes().getNamedItem(name).getNodeValue();
		}
		return null;
	}
}