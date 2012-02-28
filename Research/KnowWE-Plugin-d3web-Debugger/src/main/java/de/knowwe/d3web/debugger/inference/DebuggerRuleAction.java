/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.d3web.debugger.inference;

import java.util.List;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.indication.ActionContraIndication;
import de.d3web.indication.ActionInstantIndication;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.ActionSuppressAnswer;
import de.d3web.scoring.ActionHeuristicPS;

/**
 * A data type for rule's action in the debugger.
 * 
 * @author dupke
 */
public class DebuggerRuleAction {

	private final List<? extends TerminologyObject> actionObjects;
	private String actionText;

	/**
	 * Constructor. Keep the action's objects and text.
	 */
	public DebuggerRuleAction(PSAction action) {
		// getting objects
		actionObjects = action.getBackwardObjects();
		// getting text
		if (action instanceof ActionHeuristicPS) {
			ActionHeuristicPS ac = (ActionHeuristicPS) action;
			actionText = "<span class='debuggerActionSolution'>" + ac.getSolution().getName()
					+ "</span> = "
					+ ac.getScore();
		}
		else if (action instanceof ActionContraIndication) {
			actionText = action.toString();
		}
		else if (action instanceof ActionSuppressAnswer) {
			actionText = action.toString();
		}
		else if (action instanceof ActionInstantIndication) {
			actionText = action.toString();
		}
		else if (action instanceof ActionNextQASet) {
			ActionNextQASet anq = (ActionNextQASet) action;
			actionText = "";
			for (int i = 0; i < anq.getQASets().size(); i++) {
				if (i < anq.getQASets().size() - 1) actionText += "<span class='debuggerAction'>"
						+ anq.getQASets().get(i).getName()
						+ "</span>, ";
				else actionText += "<span class='debuggerAction'>"
						+ anq.getQASets().get(i).getName()
						+ "</span>";
			}
		}
		else if (action instanceof ActionSetValue) {
			ActionSetValue asv = (ActionSetValue) action;
			actionText = "<span class='debuggerAction'>" + asv.getQuestion()
					+ "</span> = <span class='debuggerValue'>"
					+ asv.getValue() + "</span>";
		}
		else {
			actionText = action.toString();
		}
	}

	public List<? extends TerminologyObject> getActionObjects() {
		return actionObjects;
	}

	public String render() {
		return actionText;
	}

	@Override
	public String toString() {
		return actionText;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DebuggerRuleAction other = (DebuggerRuleAction) obj;
		if (actionText == null) {
			if (other.actionText != null) return false;
		}
		else if (!actionText.equals(other.actionText)) return false;
		return true;
	}

}
