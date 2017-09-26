package de.d3web.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.denkbares.collections.DefaultMultiMap;
import com.denkbares.collections.MultiMap;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.testing.Message;
import de.d3web.testing.MessageObject;

/**
 * This test checks whether some {@link Condition}s in a {@link KnowledgeBase} are interdependent. All single conditions contained
 * inside a {@link CondAnd} are seen as dependent, as well as all {@link CondOr}s are seen as dependent from their
 * containing single conditions.
 *
 * @author Simon Maurer
 * @created 22.08.2017
 */
public class DependentConditionTest extends KBTest {

	private MultiMap<Condition, MessageObject> messageConditions;

	@Override
	public Message execute(KnowledgeBase knowledgeBase, String[] args, String[]... ignores) {

		MultiMap<Condition, Rule> conditionRuleMultiMap = new DefaultMultiMap<>();
		Collection<KnowledgeSlice> knowledgeSlices = knowledgeBase.getAllKnowledgeSlices();
		Collection<Rule> rules = new ArrayList<>();
		Collection<Condition> conditions = new ArrayList<>();
		for (KnowledgeSlice knowledgeSlice : knowledgeSlices) {
			if (knowledgeSlice instanceof RuleSet) {
				rules.addAll(((RuleSet) knowledgeSlice).getRules());
			}
		}
		for (Rule rule : rules) {
			conditionRuleMultiMap.put(rule.getCondition(), rule);
		}
		for (Rule rule : rules) {
			conditions.add(rule.getCondition());
		}

		//creates lists containing all and-, or-, and single-conditions
		List<Condition> andCondition = new ArrayList<>();
		List<Condition> singleCondition = new ArrayList<>();
		List<Condition> orCondition = new ArrayList<>();
		for (Condition condition : conditions) {
			if (condition instanceof CondOr) {
				orCondition.add(condition);
			}
			else if (condition instanceof CondAnd) {
				andCondition.add(condition);
			}
			else {
				singleCondition.add(condition);
			}
		}

		MultiMap<Condition, Condition> andDependents = mapAndDependents(andCondition, singleCondition, orCondition);
		MultiMap<Condition, Condition> singleDependents = mapSingleDependents(singleCondition, orCondition);

		fillMessageConditions(conditionRuleMultiMap, andDependents, singleDependents);

		if (singleDependents.isEmpty() && andDependents.isEmpty()) {
			return Message.SUCCESS;
		}
		else {
			return new Message(Message.Type.FAILURE,
					makeMessageString(andDependents, singleDependents), messageConditions.valueSet());
		}
	}

	/**
	 * Creates a String containing a formatted message
	 *
	 * @param andDependents    map of all and-condition dependent conditions
	 * @param singleDependents map of all single-condition dependent conditions
	 * @return a formatted String message
	 */
	private String makeMessageString(MultiMap<Condition, Condition> andDependents,
									 MultiMap<Condition, Condition> singleDependents) {
		StringBuilder stringBuilder = new StringBuilder("Interdependent tests found:\n");
		for (Condition condition : andDependents.keySet()) {
			stringBuilder.append(condition).append(":\n");
			for (Condition condition1 : andDependents.getValues(condition)) {
				if (!(singleDependents.containsValue(condition1) && !andDependents.containsKey(condition1))) {
					stringBuilder.append("    ↳ ")    //&#8627 = ↳; using 4 'NON-BREAKING SPACEs' (U+00A0) to simulate tab
							// in html
							.append(condition1).append(" (").append(appendRuleHash(condition1)).append(")")
							.append("\n");
				}
				//CondAnd => Condition => CondOr
				if (singleDependents.containsKey(condition1)) {
					for (Condition condition2 : singleDependents.getValues(condition1)) {

						stringBuilder.append("        ↳ ") // 8 'NON-BREAKING SPACEs'
								.append(condition2).append(" (").append(appendRuleHash(condition2)).append(")")
								.append("\n");
					}
				}
			}
			stringBuilder.append("\n");
		}
		for (Condition condition : singleDependents.keySet()) {
			stringBuilder.append(condition).append(":\n");
			for (Condition condition3 : singleDependents.getValues(condition)) {
				stringBuilder.append("    ↳ ") // 4 'NON-BREAKING SPACEs'
						.append(condition3).append(" (").append(appendRuleHash(condition3)).append(")")
						.append("\n");
			}
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}

	/**
	 * Returns hashCodes for all Rules a Condition is used in as a String
	 *
	 * @param condition the Condition the rule-hashCodes should be obtained from
	 * @return a string of hashCodes
	 */
	private String appendRuleHash(Condition condition) {
		StringBuilder sb = new StringBuilder();
		for (MessageObject messageObject : messageConditions.getValues(condition)) {
			sb.append(messageObject.getObjectName());
			if (messageConditions.getValues(condition).size() > 1) {
				sb.append(", ");
			}
		}
		if (messageConditions.getValues(condition).size() > 1) sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	/**
	 * Creates a MultiMap with and-conditions as keys and Condition as their values. If a condition
	 * from singleList is existent inside a compound and-condition from andList, it will be added to the
	 * map alongside the and-condition as its key. Same rules apply for or-condition,
	 * see {@link #mapSingleDependents(List, List)}.
	 *
	 * @param andList    the list of compound and-conditions
	 * @param singleList the list of single conditions
	 * @param orList     the list of compound or-conditions
	 * @return a MultiMap containing all and-conditions with depending single or compound or-conditions
	 */
	private MultiMap<Condition, Condition> mapAndDependents(List<Condition> andList, List<Condition> singleList,
															List<Condition> orList) {
		MultiMap<Condition, Condition> andDependents = new DefaultMultiMap<>();
		for (Condition condition1 : andList) {
			List<Condition> andElements = ((CondAnd) condition1).getTerms();

			for (Condition andElement : andElements) {
				if (andElement instanceof CondAnd) {
					//if the and-condition contains another and-condition, this function compares
					// them with all single conditions, puts the valid ones
					// into the map
					for (Condition splitCondition : ((CondAnd) andElement).getTerms()) {
						for (Condition condition4 : singleList) {
							if (condition4.equals(splitCondition)) {
								andDependents.put(condition1, condition4);
							}
						}
					}
				}
				else {
					if (andElement instanceof CondOr) {
						//puts all valid or-conditions into the map
						for (Condition condition3 : orList) {
							if (condition3.equals(andElement)) {
								andDependents.put(condition1, condition3);
							}
						}
					}
					else {
						//puts all valid single conditions into the map
						for (Condition condition2 : singleList) {
							if (condition2.equals(andElement)) {
								andDependents.put(condition1, condition2);
							}
						}
					}
				}
			}
		}
		return andDependents;
	}

	/**
	 * Creates a MultiMap with single Conditions as keys and or-conditions as their values. If the
	 * compound or-condition contains a condition existent in singleList it will be added to the
	 * MultiMap
	 *
	 * @param singleList the list of single conditions
	 * @param orList     the list of compound or-conditions
	 * @return a MultiMap containing all single conditions and their depending or-conditions
	 */
	private MultiMap<Condition, Condition> mapSingleDependents
	(List<Condition> singleList, List<Condition> orList) {
		MultiMap<Condition, Condition> singleDependents = new DefaultMultiMap<>();
		for (Condition condition1 : singleList) {
			for (Condition condition2 : orList) {
				List<Condition> orElements = ((CondOr) condition2).getTerms();
				for (Condition c : orElements) {
					if (condition1.equals(c)) {
						singleDependents.put(condition1, condition2);
					}
				}
			}
		}
		return singleDependents;
	}

	/**
	 * Fills the global MultiMap messageCondition with all conditions previously mapped by {@link
	 * #mapSingleDependents(List, List)} and {@link #mapAndDependents(List, List, List)} as keys and adds the
	 * corresponding MessageObjects.
	 *
	 * @param conditionRuleMultiMap contains all conditions and the rules they are used in. Needed to create the
	 *                              MessageObjects containing the Rule hashCode.
	 * @param andDependents         all and-dependent conditions
	 * @param singleDependents      all or-dependent conditions
	 */
	private void fillMessageConditions(MultiMap<Condition, Rule> conditionRuleMultiMap,
									   MultiMap<Condition, Condition> andDependents,
									   MultiMap<Condition, Condition> singleDependents) {
		messageConditions = new DefaultMultiMap<>();
		for (Condition conditionKey : conditionRuleMultiMap.keySet()) {
			if (andDependents.containsKey(conditionKey)
					|| andDependents.containsValue(conditionKey)
					|| singleDependents.containsKey(conditionKey)
					|| singleDependents.containsValue(conditionKey)) {
				Set<Rule> ruleSet = conditionRuleMultiMap.getValues(conditionKey);
				for (Rule rule : ruleSet) {
					MessageObject ob = new MessageObject(String.valueOf(rule.hashCode()), Rule.class);
					messageConditions.put(conditionKey, ob);
				}
			}
		}
	}

	@Override
	public String getDescription() {
		return "Detects interdependent conditions.";
	}
}
