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
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
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
import de.d3web.core.session.values.ChoiceValue;

/**
 * PersistenceHandler for JuriModels
 * 
 * @author grotheer
 * 
 */
public class JuriModelPersistenceHandler implements KnowledgeReader,
		KnowledgeWriter {

	private static final String JURI_RULE = "JuriRule";
	private static final String DISJUNCTIVE = "Disjunctive";
	private static final String FATHER_QUESTION = "FatherQuestion";
	private static final String CHILD = "Child";
	private static final String QUESTION = "Question";
	public final static String ID = "juripattern";
	private static final String CONFIRMING_VALUE = "ConfirmingValue";

	@Override
	public void write(KnowledgeBase knowledgeBase, OutputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("type", JuriModelPersistenceHandler.ID);
		root.setAttribute("system", "d3web");
		doc.appendChild(root);

		// Get all juri models
		ArrayList<JuriModel> models = new ArrayList<JuriModel>(
				knowledgeBase.getAllKnowledgeSlicesFor(JuriModel.KNOWLEDGE_KIND));
		for (JuriModel model : models) {
			Set<JuriRule> rules = model.getRules();
			float cur = 0;
			int max = getEstimatedSize(knowledgeBase);
			for (JuriRule rule : rules) {
				// Append all rules to the doc
				root.appendChild(createRuleElement(rule, doc));
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
	public KnowledgeBase loadKnowledgeSlices(KnowledgeBase kb, Document doc, ProgressListener listener) {
		listener.updateProgress(0, "Loading knowledge base");
		NodeList jurirules = doc.getElementsByTagName(JURI_RULE);
		int cur = 0;
		int max = jurirules.getLength();
		JuriModel model = new JuriModel();

		for (int i = 0; i < jurirules.getLength(); i++) {
			Node current = jurirules.item(i);
			JuriRule rule = createRuleFromNode(kb, current);
			if (rule != null) {
				model.addRule(rule);
			}
			listener.updateProgress(++cur / max, "Loading knowledge base: Juri Rules");
		}
		kb.getKnowledgeStore().addKnowledge(JuriModel.KNOWLEDGE_KIND, model);
		return kb;
	}

	/**
	 * Creates a rule from the node
	 * 
	 * @created 04.05.2012
	 * @param kb
	 * @param node
	 * @return
	 * @throws IOException
	 */
	private JuriRule createRuleFromNode(KnowledgeBase kb, Node node) {
		String isDisjunctive = getAttribute(DISJUNCTIVE, node);
		String fatherquestion = getAttribute(FATHER_QUESTION, node);

		JuriRule rule;
		if (fatherquestion != null) {
			QuestionOC father = (QuestionOC) kb.getManager().search(fatherquestion);
			rule = new JuriRule(father);
			if (isDisjunctive != null) {
				rule.setDisjunctive(Boolean.parseBoolean(isDisjunctive));
			}

			NodeList child_elements = node.getChildNodes();
			for (int i = 0; i < child_elements.getLength(); i++) {
				if (child_elements.item(i).getNodeName().equals(CHILD)) {
					NodeList confirmingValueNodes = child_elements.item(i).getChildNodes();
					List<ChoiceValue> confirmingValues = new LinkedList<ChoiceValue>();
					for (int j = 0; j < confirmingValueNodes.getLength(); j++) {
						if (confirmingValueNodes.item(j).getNodeName().equals(CONFIRMING_VALUE)) {
							String confirmingValue = confirmingValueNodes.item(j).getTextContent();
							confirmingValues.add(new ChoiceValue(confirmingValue));
						}
					}
					String childquestion = getAttribute(QUESTION, child_elements.item(i));
					QuestionOC child = (QuestionOC) kb.getManager().search(childquestion);
					rule.addChild(child, confirmingValues);
				}
			}
			return rule;
		}
		return null;
	}

	/**
	 * Create element for a rule
	 * 
	 * @created 04.05.2012
	 * @param jurirule the JuriRule
	 * @param doc the Document
	 * @return the created element
	 * @throws IOException
	 */
	public Element createRuleElement(JuriRule jurirule, Document doc) {
		Element ruleelement = doc.createElement(JURI_RULE);

		ruleelement.setAttribute(FATHER_QUESTION, jurirule.getFather().getName());

		for (Entry<QuestionOC, List<ChoiceValue>> child : jurirule.getChildren().entrySet()) {
			Element childelement = createChildElement(doc, child);
			ruleelement.appendChild(childelement);
		}

		if (jurirule.isDisjunctive()) {
			ruleelement.setAttribute(DISJUNCTIVE, "" + true);
		}

		return ruleelement;
	}

	/**
	 * Create element for a child of a rule
	 * 
	 * @created 04.05.2012
	 * @param doc
	 * @param child
	 * @param childelement
	 */
	private Element createChildElement(Document doc, Entry<QuestionOC, List<ChoiceValue>> child) {
		Element childelement = doc.createElement(CHILD);
		childelement.setAttribute(QUESTION, child.getKey().getName());
		for (ChoiceValue value : child.getValue()) {
			Element confirmingValue = doc.createElement(CONFIRMING_VALUE);
			confirmingValue.setTextContent(value.getAnswerChoiceID());
			childelement.appendChild(confirmingValue);
		}
		return childelement;
	}

	private String getAttribute(String name, Node node) {
		if ((node != null) && (node.getAttributes() != null)
				&& node.getAttributes().getNamedItem(name) != null) {
			return node.getAttributes().getNamedItem(name).getNodeValue();
		}
		return null;
	}
}