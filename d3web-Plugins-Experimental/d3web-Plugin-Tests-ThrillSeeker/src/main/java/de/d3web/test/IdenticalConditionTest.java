package de.d3web.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.denkbares.collections.DefaultMultiMap;
import com.denkbares.collections.MultiMap;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.testing.Message;
import de.d3web.testing.MessageObject;

/**
 * This test checks whether the conditions in a knowledge base have multiple assignments.
 *
 * @author Simon Maurer
 * @created 11.08.2017
 */
public class IdenticalConditionTest extends KBTest {

	@Override
	public Message execute(KnowledgeBase knowledgeBase, String[] args, String[]... ignores) {
		MultiMap<Condition, Rule> conditionSetHashMap = new DefaultMultiMap<>();
		Collection<KnowledgeSlice> knowledgeSlices = knowledgeBase.getAllKnowledgeSlices();
		Collection<Rule> rules = new ArrayList<>();
		List<Condition> errorConditions = new ArrayList<>();

		for (KnowledgeSlice knowledgeSlice : knowledgeSlices) {
			if (knowledgeSlice instanceof RuleSet) {
				rules.addAll(((RuleSet) knowledgeSlice).getRules());
			}
		}
		for (Rule rule : rules) {
			conditionSetHashMap.put(rule.getCondition(), rule);
		}
		for (Condition condition : conditionSetHashMap.keySet()) {
			if (conditionSetHashMap.getValues(condition).size() > 1) {
				errorConditions.add(condition);
			}
		}
		Collection<TerminologyObject> errorObjects = new ArrayList<>();
		for (Condition errorCondition : errorConditions) errorObjects.addAll(errorCondition.getTerminalObjects());
		Collection<MessageObject> messageObjects = new ArrayList<>();
		if (errorConditions.isEmpty()) {
			return Message.SUCCESS;
		}
		else {
			StringBuilder errorBuilder = new StringBuilder();
			errorBuilder.append("Identical conditions:\n\n\t");
			for (Condition errorCondition : errorConditions) {
				errorBuilder.append("- ").append(errorCondition).append(":\n");
				for (Rule rule : conditionSetHashMap.getValues(errorCondition)) {
					messageObjects.add(new MessageObject(String.valueOf(rule.hashCode()), Rule.class));
					StringBuilder ruleString = new StringBuilder();
					ruleString.append("# ").append(rule.hashCode()).append(": ");
					String[] thenString = rule.toString().split("\n");
					errorBuilder.append(ruleString).append("...").append(thenString[1]).append("\n");
				}
				errorBuilder.append("\n");
			}
			return new Message(Message.Type.FAILURE, errorBuilder.toString(), messageObjects);
		}
	}

	@Override
	public String getDescription() {
		return "Detects multiple rules for the same condition";
	}
}
