package de.d3web.we.drools.converter;

import java.util.LinkedList;
import java.util.List;

public class DroolsRule {
	
	/**
	 * Specifies the standard indent
	 */
	private final String INDENT = "  ";
	
	private String name;
	
	private List<String> conditions = new LinkedList<String>();
	
	private String action;
	
	public DroolsRule(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getConditions() {
		return conditions;
	}

	public void setConditions(List<String> conditions) {
		this.conditions = conditions;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("rule \"");
		result.append(name);
		result.append("\"\n");
		result.append(INDENT);
		result.append("when\n");
		for (String condition : conditions) 
			result.append(condition);
		result.append(action);
		result.append("end\n\n");
		return result.toString();
	}

}
