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
package de.knowwe.wisskont.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;
import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.D3webCondition;
import de.d3web.we.kdom.condition.Finding;
import de.d3web.we.kdom.condition.KDOMConditionFactory;
import de.d3web.we.kdom.condition.NumericalFinding;
import de.d3web.we.kdom.condition.NumericalIntervallFinding;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.annotation.type.list.ListObjectIdentifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.AbstractKnowledgeUnitCompileScript;
import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.renderer.ReferenceSurroundingRenderer;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.render.CompositeRenderer;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.termbrowser.DroppableTargetSurroundingRenderer;
import de.knowwe.wisskont.ValuesMarkup;
import de.knowwe.wisskont.dss.KnowledgeBaseInstantiation;
import de.knowwe.wisskont.relationMarkup.RelationMarkupUtils;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 06.08.2013
 */
public class RuleMarkup extends AbstractKnowledgeUnitType<RuleMarkup> {

	/**
	 * 
	 */
	public RuleMarkup() {

		// breaks rendering (linkbreaks after keywords)
		// this.setRenderer(new DefaultMarkupRenderer());

		String key = "WENN";
		String REGEX = RelationMarkupUtils.getLineRegex(key, false);
		this.setSectionFinder(new RegexSectionFinder(REGEX,
				Pattern.MULTILINE | Pattern.DOTALL));

		this.addChildType(new RuleMarkupContentType(REGEX));
		RuleKeyType keyType = new RuleKeyType(RelationMarkupUtils.getKeyRegex(key, false));
		CompositeRenderer renderer = new CompositeRenderer(new StyleRenderer("font-weight:bold;"),
				new DroppableTargetSurroundingRenderer());
		keyType.setRenderer(renderer);
		this.addChildType(keyType);

		// this.setRenderer(renderer);
		this.setIgnorePackageCompile(true);

		this.setCompileScript(new WisskontRuleCreator());
	}

	class RuleMarkupContentType extends AbstractType {

		/**
		 * 
		 */
		public RuleMarkupContentType(String regex) {
			this.setSectionFinder(new RegexSectionFinder(regex, Pattern.MULTILINE
					| Pattern.DOTALL,
					2));
			this.addChildType(new RuleContent());
		}
	}

	class RuleContent extends AbstractType {

		/**
		 * 
		 */
		public RuleContent() {
			this.setSectionFinder(new AllTextFinderTrimmed());

			this.addChildType(new RuleActionKeyType());

			// condition
			this.addChildType(new ConditionArea());

			// action
			this.addChildType(new ActionArea());

		}
	}

	class ConditionArea extends AbstractType {

		/**
		 * 
		 */
		public ConditionArea() {
			SectionFinder finder = new AllTextFinderTrimmed();
			ConstraintSectionFinder csf = new ConstraintSectionFinder(finder,
					SingleChildConstraint.getInstance());
			this.setSectionFinder(csf);

			CompositeCondition compositeCondition = new CompositeCondition();
			compositeCondition.setAllowedTerminalConditions(getTerminalConditions());
			this.addChildType(compositeCondition);
		}

		public List<Type> getTerminalConditions() {
			List<Type> termConds = new ArrayList<Type>();

			// add all the various allowed TerminalConditions here
			termConds.add(new Finding());
			termConds.add(new NumericalFinding());
			termConds.add(new NumericalIntervallFinding());
			termConds.add(new WisskontChoiceFinding());
			return termConds;
		}
	}

	class WisskontChoiceFinding extends D3webCondition<WisskontChoiceFinding> {

		/**
		 * 
		 */
		public WisskontChoiceFinding() {
			this.setSectionFinder(new AllTextSectionFinder());
			CompositeRenderer renderer = new CompositeRenderer(DelegateRenderer.getInstance(),
					new ReferenceSurroundingRenderer());
			this.addChildType(new ListObjectIdentifier(renderer));
			this.setIgnorePackageCompile(true);
		}

		@Override
		protected Condition createCondition(Article article, Section<WisskontChoiceFinding> section) {
			Section<ListObjectIdentifier> concept = Sections.findSuccessor(section,
					ListObjectIdentifier.class);

			boolean valid = IncrementalCompiler.getInstance().getTerminology().isValid(
					concept.get().getTermIdentifier(concept));
			if (!valid) return null;

			Section<? extends SimpleDefinition> conceptDefinition = MarkupUtils.getConceptDefinitionGlobal(concept);
			Section<ValuesMarkup> markup = Sections.findAncestorOfType(conceptDefinition,
					ValuesMarkup.class);

			if (markup == null) return null;

			Object o = KnowWEUtils.getStoredObject(
					Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
							KnowledgeBaseInstantiation.WISSKONT_KNOWLEDGE),
					markup, ValuesMarkup.VALUE_STORE_KEY);

			QuestionChoice qChoice = null;
			if (o instanceof QuestionChoice) {
				qChoice = (QuestionChoice) o;
			}
			if (qChoice == null) return null;

			List<Choice> allAlternatives = qChoice.getAllAlternatives();
			for (Choice choice : allAlternatives) {
				String longAnswerName = ValuesMarkup.getLongAnswerName(qChoice.getName(),
						choice.getName());
				if (longAnswerName.equals(concept.get().getTermName(concept))) {
					ChoiceValue v = new ChoiceValue(choice);
					return new CondEqual(qChoice, v);
				}
			}
			return null;
		}
	}

	class ActionArea extends AbstractType {

		/**
		 * 
		 */
		public ActionArea() {
			SectionFinder finder = new AllTextFinderTrimmed();
			ConstraintSectionFinder csf = new ConstraintSectionFinder(finder,
					AtMostOneFindingConstraint.getInstance());
			this.setSectionFinder(csf);
			CompositeRenderer renderer = new CompositeRenderer(DelegateRenderer.getInstance(),
					new ReferenceSurroundingRenderer());
			this.addChildType(new ListObjectIdentifier(renderer));
		}
	}

	class WisskontRuleCreator extends AbstractKnowledgeUnitCompileScript<RuleMarkup> {

		@Override
		public void insertIntoRepository(Section<RuleMarkup> section) {
			// create condition
			Section<CompositeCondition> cond = Sections.findSuccessor(section,
					CompositeCondition.class);
			if (cond == null) return;
			Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
					KnowledgeBaseInstantiation.WISSKONT_KNOWLEDGE);

			// make sure that terminals are created and stored
			List<Section<WisskontChoiceFinding>> terminals = Sections.findSuccessorsOfType(cond,
					WisskontChoiceFinding.class);
			for (Section<WisskontChoiceFinding> terminal : terminals) {
				Condition createdCondition = terminal.get().createCondition(article, terminal);
				KnowWEUtils.storeObject(article, terminal, "cond-store-key",
						createdCondition);
			}
			List<Section<NumericalFinding>> numFindingTerminals = Sections.findSuccessorsOfType(
					cond,
					NumericalFinding.class);
			for (Section<NumericalFinding> condNumSection : numFindingTerminals) {
				Condition createdCondition = CondUtils.createCondNum(condNumSection);
				KnowWEUtils.storeObject(article, condNumSection, "cond-store-key",
						createdCondition);
			}

			Condition d3Cond = KDOMConditionFactory.createCondition(
					article,
					cond);

			if (d3Cond == null) return;

			Section<ActionArea> actionArea = Sections.findSuccessor(section,
					ActionArea.class);

			List<Section<IncrementalTermReference>> targetConcepts = Sections.findSuccessorsOfType(
					actionArea, IncrementalTermReference.class);

			Set<Rule> rules = new HashSet<Rule>();
			for (Section<IncrementalTermReference> target : targetConcepts) {
				boolean valid = IncrementalCompiler.getInstance().getTerminology().isValid(
						target.get().getTermIdentifier(target));
				if (!valid) continue;

				Solution solution = KnowledgeBaseInstantiation.createSolutionForObjects(target, "",
						d3Cond.getTerminalObjects());

				Score score = D3webUtils.getScoreForString("P7");
				if (solution == null || score == null) continue;
				ActionHeuristicPS a = new ActionHeuristicPS();
				a.setSolution(solution);
				a.setScore(score);

				if (a != null && d3Cond != null) {
					Rule r = RuleFactory.createRule(a, d3Cond,
							null, PSMethodHeuristic.class);
					if (r != null) {
						rules.add(r);
					}
				}
			}
			KnowWEUtils.storeObject(article, section, RULE_STORE_KEY, rules);
		}

		private static final String RULE_STORE_KEY = "RULE_STORE_KEY";

		@Override
		public void deleteFromRepository(Section<RuleMarkup> section) {
			Object storedObject = KnowWEUtils.getStoredObject(
					Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
							KnowledgeBaseInstantiation.WISSKONT_KNOWLEDGE), section, RULE_STORE_KEY);
			if (storedObject instanceof Set) {
				Set<?> set = (Set<?>) storedObject;
				for (Object object : set) {
					if (object instanceof Rule) {
						Rule r = (Rule) object;
						KnowledgeBaseInstantiation.removeRuleFromKB(r);
					}
				}
				set.clear();
			}
		}

		@Override
		public Collection<Section<? extends Term>> getExternalReferencesOfKnowledgeUnit(Section<? extends KnowledgeUnit> section) {
			return Collections.emptyList();
		}
	}
}
