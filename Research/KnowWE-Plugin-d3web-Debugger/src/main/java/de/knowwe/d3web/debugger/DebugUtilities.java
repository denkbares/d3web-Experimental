/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3web.debugger;

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.we.kdom.rules.RuleContentType;
import de.d3web.we.kdom.rules.action.RuleAction;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * Some useful functions and variables for the d3web-debugger.
 * 
 * @author dupke
 */
public class DebugUtilities {
	/** BackgroundColors for TopNode(Solution) */
	public static final String COLOR_ESTABLISHED = "#33CC66";
	public static final String COLOR_SUGGESTED = "#99FF66";
	public static final String COLOR_UNCLEAR = "#CC9900";
	public static final String COLOR_EXCLUDED = "#FF0000";

	/**
	 * Get all questions from the given knowledgebase.
	 */
	public static List<TerminologyObject> getAllTOsFromKB(KnowledgeBase kb) {
		List<TerminologyObject> tos = new LinkedList<TerminologyObject>();
		for (TerminologyObject to : kb.getRootQASet().getChildren()) {
			tos.addAll(getChildTOs(to));
		}

		return tos;
	}

	/**
	 * Walk recursively through the questions.
	 */
	private static List<TerminologyObject> getChildTOs(TerminologyObject to) {
		List<TerminologyObject> tos = new LinkedList<TerminologyObject>();

		if (!(to instanceof QContainer)) tos.add(to);
		for (TerminologyObject too : to.getChildren()) {
			if (too.getChildren().length > 0) tos.addAll(getChildTOs(too));
			else tos.add(too);
		}

		return tos;
	}

	/**
	 * Get all TerminologyObjects which are influential for the given TO.
	 */
	public static List<TerminologyObject> getInfluentialTOs(TerminologyObject root, KnowledgeBase kb) {
		List<TerminologyObject> tos = new LinkedList<TerminologyObject>();
		List<Rule> rule = getRulesFromKB(kb);
		for (Rule r : rule) {
			if (r.getAction().getBackwardObjects().contains(root)) {
				for (TerminologyObject to : r.getCondition().getTerminalObjects()) {
					if (!tos.contains(to)) tos.add(to);
				}
			}
		}

		return tos;
	}

	/**
	 * Get all rules from the given knowledgebase.
	 */
	public static List<Rule> getRulesFromKB(KnowledgeBase kb) {
		List<Rule> rules = new LinkedList<Rule>();
		for (KnowledgeSlice ks : kb.getAllKnowledgeSlices()) {
			if (ks instanceof RuleSet) {
				for (Rule r : ((RuleSet) ks).getRules()) {
					if (!rules.contains(r)) rules.add(r);
				}
			}
		}

		return rules;
	}

	/**
	 * Get all solutions from the given knowledgebase.
	 */
	public static List<? extends TerminologyObject> getSolutionsFromKB(KnowledgeBase kb) {
		List<Solution> solutions = new LinkedList<Solution>();
		Solution root = kb.getRootSolution();
		for (TerminologyObject s : root.getChildren()) {
			solutions.addAll(getChildSolutions((Solution) s));
		}

		return solutions;
	}

	/**
	 * Get recursively the rootsolution's children.
	 */
	private static List<Solution> getChildSolutions(Solution root) {
		List<Solution> solutions = new LinkedList<Solution>();

		for (TerminologyObject s : root.getChildren()) {
			solutions.add((Solution) s);
			if (s.getChildren().length > 0) solutions.addAll(getChildSolutions((Solution) s));
		}

		return solutions;
	}

	/**
	 * Search in the given knowledgebase for rules containing to in their action
	 */
	public static List<Rule> getRulesWithTO(TerminologyObject to, KnowledgeBase kb) {
		List<Rule> rules = new LinkedList<Rule>();
		for (Rule r : getRulesFromKB(kb)) {
			if (!rules.contains(r) && r.getAction().getBackwardObjects().contains(to)) rules.add(r);
		}
		return rules;
	}

	/**
	 * Get the path to the rule's article.
	 */
	public static String getRuleResource(Rule r) {
		// TODO: Does not really work, don't know why.
		Rule rule;
		List<Section<RuleAction>> rules;

		for (KnowWEArticle article : KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticles()) {

			rules = Sections.findSuccessorsOfType(article.getSection(), RuleAction.class);
			for (Section<RuleAction> ruleAction : rules) {

				rule = (Rule) KnowWEUtils.getStoredObject(article, ruleAction,
							RuleContentType.ruleStoreKey);

				if (rule != null && rule.equals(r)) return article.getTitle();
			}
		}

		return "";
	}

}