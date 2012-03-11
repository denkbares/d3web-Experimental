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
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
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
public class JuriModelPersistenceHandler implements KnowledgeReader,
		KnowledgeWriter {

	public final static String ID = "juripattern";

	@Override
	public void write(KnowledgeBase knowledgeBase, OutputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("type", JuriModelPersistenceHandler.ID);
		root.setAttribute("system", "d3web");
		doc.appendChild(root);

		ArrayList<JuriModel> models = new ArrayList<JuriModel>(
				knowledgeBase.getAllKnowledgeSlicesFor(JuriModel.KNOWLEDGE_KIND));
		for (JuriModel model : models) {
			Set<JuriRule> rules = model.getRules();
			float cur = 0;
			int max = getEstimatedSize(knowledgeBase);
			for (JuriRule rule : rules) {
				root.appendChild(getRuleElement(rule, doc));
				listener.updateProgress(++cur / max, "Saving knowledge base: Juri Rules");
			}
		}
		Util.writeDocumentToOutputStream(doc, stream);
	}

	@Override
	public int getEstimatedSize(KnowledgeBase knowledgeBase) {
		return knowledgeBase.getAllKnowledgeSlicesFor(JuriModel.KNOWLEDGE_KIND).size();
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
		JuriModel model = new JuriModel();

		for (int i = 0; i < jurirules.getLength(); i++) {
			Node current = jurirules.item(i);
			addRule(kb, model, current);
			listener.updateProgress(++cur / max, "Loading knowledge base: Juri Rules");
		}
		kb.getKnowledgeStore().addKnowledge(JuriModel.KNOWLEDGE_KIND, model);
		return kb;
	}

	private void addRule(KnowledgeBase kb, JuriModel model, Node current) throws IOException {
		String isDisjunctive = getAttribute("Disjunctive", current);
		String fatherquestion = getAttribute("FatherQuestion", current);

		JuriRule rule = new JuriRule();
		if (isDisjunctive != null) {
			rule.setDisjunctive(Boolean.parseBoolean(isDisjunctive));
		}
		if (fatherquestion != null) {
			QuestionOC father = (QuestionOC) kb.getManager().search(fatherquestion);
			rule.setFather(father);
		}
		NodeList elements = current.getChildNodes();
		for (int i = 0; i < elements.getLength(); i++) {
			if (elements.item(i).getNodeName().equals("Child")) {
				String childquestion = getAttribute("Question", elements.item(i));
				QuestionOC child = (QuestionOC) kb.getManager().search(childquestion);
				rule.addChild(child);
			}
		}
		model.addRule(rule);
	}

	public Element getRuleElement(JuriRule jurirule, Document doc) throws IOException {
		Element ruleelement = doc.createElement("JuriRule");

		ruleelement.setAttribute("FatherQuestion", jurirule.getFather().getName());

		for (QuestionOC child : jurirule.getChildren()) {
			Element childelement = doc.createElement("Child");
			childelement.setAttribute("Question", child.getName());
			ruleelement.appendChild(childelement);
		}

		ruleelement.setAttribute("Disjuntice", "" + jurirule.isDisjunctive());

		return ruleelement;
	}

	private String getAttribute(String name, Node node) {
		if ((node != null) && (node.getAttributes() != null)
				&& node.getAttributes().getNamedItem(name) != null) {
			return node.getAttributes().getNamedItem(name).getNodeValue();
		}
		return null;
	}
}