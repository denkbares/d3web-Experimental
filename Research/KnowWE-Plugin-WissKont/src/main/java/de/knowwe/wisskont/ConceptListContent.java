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
package de.knowwe.wisskont;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;
import de.d3web.utils.Log;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.annotation.type.list.ListObjectIdentifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.AbstractKnowledgeUnitCompileScript;
import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.InvalidReference;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinder;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;
import de.knowwe.kdom.sectionFinder.SplitSectionFinderUnquotedNonEmpty;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.wisskont.dss.KnowledgeBaseInstantiation;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.06.2013
 */
public class ConceptListContent extends AbstractType {

	public ConceptListContent() {
		this.setSectionFinder(new LineSectionFinder());
		this.addChildType(new ObjectSegment());
		this.addChildType(new ListSeparatorType());
	}

	class ListSeparatorType extends AbstractType {

		/**
		 * 
		 */
		public ListSeparatorType() {
			this.setSectionFinder(AllTextFinder.getInstance());
			// this.setRenderer(new StyleRenderer("float:left;"));
		}
	}

	class ObjectSegment extends AbstractKnowledgeUnitType<ObjectSegment> {

		public ObjectSegment() {
			this.setSectionFinder(new SplitSectionFinderUnquotedNonEmpty(","));
			this.addChildType(new ListObjectIdentifier(new OIDeleteItemRenderer()));
			this.setCompileScript(new ObjectSegmentCompileScript());
		}

		class ObjectSegmentCompileScript extends AbstractKnowledgeUnitCompileScript<ObjectSegment> {

			@Override
			public void insertIntoRepository(Section<ObjectSegment> section) {

				if (section.getText().length() == 0) return;

				@SuppressWarnings("rawtypes")
				Section<IncrementalTermDefinition> conceptDefinition = MarkupUtils.getConceptDefinition(section);
				if (conceptDefinition == null) {
					return; // do nothing
				}

				Section<Term> objectSection = Sections.findSuccessor(section,
						Term.class);

				if (objectSection == null) {
					System.out.println("Objectsection is null! " + section.getArticle().getTitle());
				}

				// if there is a compile error, do not insert knowledge
				boolean hasError = !IncrementalCompiler.getInstance().getTerminology().isValid(
						objectSection.get().getTermIdentifier(objectSection));
				if (hasError) return;

				URI subjectURI = RDFSUtil.getURI(conceptDefinition);
				Section<RelationMarkup> relationMarkup = Sections.findAncestorOfType(
						section, RelationMarkup.class);
				URI predicateURI = relationMarkup.get().getRelationURI();

				URI objectURI = RDFSUtil.getURI(objectSection);
				Statement statement = Rdf2GoCore.getInstance().createStatement(subjectURI,
						predicateURI,
						objectURI);
				if (relationMarkup.get().isInverseDir()) {
					statement = Rdf2GoCore.getInstance().createStatement(objectURI,
							predicateURI, subjectURI
							);
				}

				Rdf2GoCore.getInstance().addStatements(section,
						new Statement[] { statement });

				Section<RelationMarkup> markup = Sections.findAncestorOfType(section,
						RelationMarkup.class);
				createD3webDerivationRule(section, conceptDefinition, objectSection, markup);

				/*
				 * finally commit triples
				 */
				Rdf2GoCore.getInstance().commit();

			}

			private void createD3webDerivationRule(Section<ObjectSegment> section, @SuppressWarnings("rawtypes") Section<IncrementalTermDefinition> conceptDefinition, Section<Term> objectSection, Section<? extends RelationMarkup> markup) {
				if (!(markup.get() instanceof MustMarkup || markup.get() instanceof CanMarkup || markup.get() instanceof CaveMarkup)) {
					return;
				}

				String keyword = markup.get().getDerivationMessagePrefix();

				String subjectTermName = conceptDefinition.get().getTermName(conceptDefinition);
				String objectTermName = objectSection.get().getTermName(objectSection);

				Article article = conceptDefinition.getArticle();
				Section<ValuesMarkup> values = Sections.findSuccessor(article.getRootSection(),
						ValuesMarkup.class);
				if (values != null) {

					KnowledgeBase kb = KnowledgeBaseInstantiation.getKB(section.getWeb());
					if (kb == null) return;

					TerminologyManager manager = kb.getManager();
					TerminologyObject object = manager.search(subjectTermName);
					QuestionOC question = null;
					if (object == null) {
						object = ValuesMarkup.createQuestionOCWithValues(values, manager,
								subjectTermName);
					}
					if (object instanceof QuestionOC) {
						question = (QuestionOC) object;
					}
					if (object instanceof QuestionNum) {
						// we cannot create a rule..
						return;
					}
					if (question == null) {
						Log.severe("Could not create Question for ValuesMarkup"
								+ objectSection.toString());
						throw new NullPointerException();
						// return;
					}

					TerminologyObject solObject = manager.search(objectTermName);
					Solution solution = null;
					if (solObject == null) {
						solution = KnowledgeBaseInstantiation.createSolution(objectSection,
								keyword, question);
					}
					else {
						if (solObject instanceof Solution) {
							solution = (Solution) solObject;
						}
					}
					boolean isYN = isYNQuestion(question);
					Condition cond = null;
					if (isYN) {
						Choice yes = getAnswerYes(question);
						cond = new CondEqual(question, new ChoiceValue(yes));
					}
					else {
						cond = new CondKnown(question);
					}

					ActionHeuristicPS a = null;
					if (solution != null) {
						a = new ActionHeuristicPS();
						a.setSolution(solution);
						a.setScore(Score.P7);
					}
					if (a != null && cond != null) {
						Rule r = RuleFactory.createRule(a, cond,
								null, PSMethodHeuristic.class);
						if (r != null) {
							KnowWEUtils.storeObject(D3webUtils.getCompiler(article), section,
									RULE_STORE_KEY, r);
						}
					}
				}

			}

			private static final String RULE_STORE_KEY = "RULE_STORE_KEY";

			/**
			 * 
			 * @created 25.07.2013
			 * @param question
			 */
			private boolean isYNQuestion(QuestionOC question) {
				return getAnswerYes(question) != null;
			}

			private Choice getAnswerYes(QuestionOC question) {
				List<Choice> allAlternatives = question.getAllAlternatives();
				for (Choice choice : allAlternatives) {
					if (choice.getName().equalsIgnoreCase("ja") && allAlternatives.size() == 2) {
						return choice;
					}
				}
				return null;
			}

			@Override
			public void deleteFromRepository(Section<ObjectSegment> section) {
				Rdf2GoCore.getInstance().removeStatementsForSection(section);
				Object storedObject = KnowWEUtils.getStoredObject(
						D3webUtils.getCompiler(section.getArticle()), section,
						RULE_STORE_KEY);
				if (storedObject instanceof Rule) {
					Rule r = (Rule) storedObject;
					KnowledgeBaseInstantiation.removeRuleFromKB(r);
				}
			}

			@Override
			public Collection<Section<? extends Term>> getExternalReferencesOfKnowledgeUnit(Section<? extends KnowledgeUnit> section) {
				Set<Section<? extends Term>> result = new HashSet<Section<? extends Term>>();
				Collection<Section<ConceptMarkup>> conceptDefinitions = MarkupUtils.getConecptDefinitionForLocalPage(section);
				for (Section<ConceptMarkup> def : conceptDefinitions) {
					result.add(Sections.findSuccessor(def,
							IncrementalTermDefinition.class));
				}
				if (conceptDefinitions.size() != 1) {
					Section<InvalidReference> invalidReference =
							Section.createSection("foo",
									new InvalidReference(), null);
					result.add(invalidReference);
				}
				return result;
			}

		}

	}
}
