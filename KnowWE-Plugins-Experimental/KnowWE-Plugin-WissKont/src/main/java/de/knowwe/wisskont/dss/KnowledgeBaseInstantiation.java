/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont.dss;

import java.util.Collection;
import java.util.Collections;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.plugin.Instantiation;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 25.07.2013
 */
public class KnowledgeBaseInstantiation implements Instantiation {

	public static final String PATIENTENDATEN = "Patientendaten";
	public static final String WISSKONT_KNOWLEDGE = "WisskontKnowledge";
	private String web;

	@Override
	public void init(String web) {
		this.web = web;
		KnowledgeBase knowledgeBase = getKB(web);
		TerminologyManager manager = knowledgeBase.getManager();
		manager.putTerminologyObject(new QContainer(knowledgeBase.getRootQASet(), PATIENTENDATEN));

	}

	public static KnowledgeBase getKB(String web) {
		KnowledgeBase knowledgeBase = D3webUtils.getKnowledgeBase(web, WISSKONT_KNOWLEDGE);
		return knowledgeBase;
	}

	public static Solution createSolution(Section<Term> term, String prefix, TerminologyObject q) {
		return createSolutionForObjects(term, prefix, Collections.singletonList(q));
	}

	public static Solution createSolutionForObjects(Section<? extends Term> term, String prefix, Collection<? extends TerminologyObject> derivingObjects) {
		KnowledgeBase kb = getKB(term.getWeb());
		String termName = prefix + " " + term.get().getTermName(term) + ";";
		termName = termName.trim();
		TerminologyObject found = kb.getManager().search(termName);
		if (found != null) {
			if (found instanceof Solution) {
				return (Solution) found;
			}
			else {
				//
				// throw new
				// IllegalStateException("Object with this name already existing! ("
				// + found.toString() + ")");
				// just create lazy
			}
		}
		Solution solution = new Solution(kb.getRootSolution(), termName);
		String shortExplanation = "";
		for (TerminologyObject terminologyObject : derivingObjects) {
			shortExplanation += (terminologyObject.getName() + " ,");
		}
		shortExplanation = shortExplanation.substring(0, shortExplanation.length() - 1);

		solution.getInfoStore().addValue(MMInfo.DESCRIPTION, "(" + shortExplanation + ")");
		String link = "";
		link += Environment.getInstance().getWikiConnector().getBaseUrl();
		link += "Wiki.jsp?page=";
		link += term.getArticle().getTitle();

		solution.getInfoStore().addValue(MMInfo.LINK, link);
		kb.getManager().putTerminologyObject(solution);
		return solution;
	}

	/**
	 * 
	 * @created 07.08.2013
	 * @param r
	 */
	public static void removeRuleFromKB(Rule r) {
		Collection<? extends TerminologyObject> forwardObjects = r.getCondition().getTerminalObjects();
		removeRuleFromObjects(r, forwardObjects);
		Collection<? extends TerminologyObject> backwardObjectsAction = r.getAction().getBackwardObjects();
		removeRuleFromObjects(r, backwardObjectsAction);
		Collection<? extends TerminologyObject> forwardObjectsAction = r.getAction().getForwardObjects();
		removeRuleFromObjects(r, forwardObjectsAction);
	}

	private static void removeRuleFromObjects(Rule r, Collection<? extends TerminologyObject> forwardObjects) {
		for (TerminologyObject terminologyObject : forwardObjects) {
			KnowledgeSlice[] slices = terminologyObject.getKnowledgeStore().getKnowledge();
			for (KnowledgeSlice knowledgeSlice : slices) {
				if (knowledgeSlice instanceof RuleSet) {
					((RuleSet) knowledgeSlice).removeRule(r);
				}
			}
		}
	}
}
