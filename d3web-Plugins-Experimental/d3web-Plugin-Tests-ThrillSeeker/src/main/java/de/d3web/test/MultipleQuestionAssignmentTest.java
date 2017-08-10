package de.d3web.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.abstraction.ActionSetQuestion;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.session.values.ChoiceValue;

/**
 * This test checks whether the questions of a knowledge base have multiple assigned values, which may indicate
 * contradictions. Questions identified with this class can be excluded with the ignore parameter.
 *
 * @author Simon Maurer
 * @created 03.08.2017
 */
public class MultipleQuestionAssignmentTest extends KBObjectsTest {

	public MultipleQuestionAssignmentTest() {
		super("Question has multiple assigned values:");
	}

	@Override
	protected List<TerminologyObject> doTest(KnowledgeBase kb, List<TerminologyObject> objects, String[] args) {

		Map<Choice, Rule> choiceRuleMap = new HashMap<>();
		Map<QuestionNum, Rule> questionNumRuleMap = new HashMap<>();
		List<TerminologyObject> ErrorObjects = new ArrayList<>();

		for (TerminologyObject terminologyObject : objects) {
			Collection<Rule> ruleCollection = getRuleCollection(terminologyObject);

			for (Rule rule : ruleCollection) {
				PSAction psAction = rule.getAction();
				Question question = ((ActionSetQuestion) psAction).getQuestion();
				if (question instanceof QuestionNum) {
					if (questionNumRuleMap.get(question) == null) {
						questionNumRuleMap.put((QuestionNum) question, rule);
					}
					else if (!ErrorObjects.contains(question)) {
						ErrorObjects.add(question);
					}
				}
				if (((ActionSetQuestion) psAction).getValue() instanceof ChoiceValue) {
					ChoiceValue choiceValue = (ChoiceValue) ((ActionSetQuestion) psAction).getValue();
					if (question instanceof QuestionChoice) {
						Choice choice = choiceValue.getChoice((QuestionChoice) question);
						if (choiceRuleMap.get(choice) == null) {
							choiceRuleMap.put(choice, rule);
						}
						else if (!ErrorObjects.contains(choice.getQuestion())) {
							ErrorObjects.add(choice.getQuestion());
						}
					}
				}
			}
		}
		return ErrorObjects;
	}

	/**
	 * Extracts a Rule collection from an TerminologyObject, if it is an instance of Question.
	 *
	 * @param terminologyObject The TerminologyObject a rule collection should be extracted from
	 * @return Rule collection from the given TerminologyObject
	 */
	private Collection<Rule> getRuleCollection(TerminologyObject terminologyObject) {

		Collection<Rule> ruleCollection = new ArrayList<>();

		if (terminologyObject instanceof Question) {
			KnowledgeSlice[] knowledgeSlices = terminologyObject.getKnowledgeStore().getKnowledge();
			for (KnowledgeSlice knowledgeSlice : knowledgeSlices) {
				if (knowledgeSlice instanceof RuleSet) {
					Collection<Rule> actionCollection = ((RuleSet) knowledgeSlice).getRules();
					for (Rule rule : actionCollection) {
						PSAction action = rule.getAction();
						if (action instanceof ActionSetQuestion) {
							Question question = ((ActionSetQuestion) action).getQuestion();
							if (question.equals(terminologyObject)) {
								ruleCollection.add(rule);
							}
						}
					}
				}
			}
		}
		return ruleCollection;
	}

	@Override
	protected List<TerminologyObject> getBaseObjects(KnowledgeBase kb, String[] args) {
		return new ArrayList<>(kb.getManager().getQuestions());
	}

	@Override
	public String getDescription() {
		return "Detects multiple rule assignments of questions, which may indicate contradicting solutions.";
	}
}
