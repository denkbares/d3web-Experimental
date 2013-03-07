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
package test.tests;

import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.NOOPAction;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.we.diaflux.anomalies.DeadPath;

/**
 * 
 * @author Reinhard Hatko
 * @created 12.11.2012
 */
public abstract class TestBase {

	KnowledgeBase kb;

	@Before
	public void before() {
		kb = KnowledgeBaseUtils.createKnowledgeBase();
	}

	protected void runTest(Type expected) {
		DeadPath test = new DeadPath();
		Message message = test.execute(kb, null);
		Assert.assertThat(message.getType(), is(expected));
	}


	// Structure:
	// Start
	// |
	// v
	// questionNode1 (action1)
	// | (condition1)
	// v
	// snapshotNode1
	// |
	// v
	// questionNode2 (action2)
	// | (condition2)
	// v
	// End
	protected void createFlow(PSAction action1, PSAction action2, Condition condition1, Condition condition2) throws IOException {
		// InitPluginManager.init();
		Node startNode = new StartNode("Start_ID", "Start");
		Node endNode = new EndNode("End_ID", "Ende");
		Node snaphot1 = new SnapshotNode("Snap1_ID", "Snapshot1");
	
		ActionNode questionNode1 = new ActionNode("questionNode1_ID", action1);
	
		ActionNode questionNode2 = new ActionNode("questionNode2_ID", action2);
	
		LinkedList<Node> nodesList = new LinkedList<Node>(Arrays.asList(startNode, endNode,
				questionNode1, snaphot1, questionNode2));
	
		// ---------------------------------
	
		Edge startToQuestion1 = FlowFactory.createEdge("startToQuestionEdge1_ID", startNode,
				questionNode1,
				ConditionTrue.INSTANCE);
	
	
		Edge question1ToSnap = FlowFactory.createEdge("question1ToSnap_ID", questionNode1,
				snaphot1, condition1);
	
		Edge snap1ToQuestion2 = FlowFactory.createEdge("snap1ToQuestion2_ID", snaphot1,
				questionNode2, ConditionTrue.INSTANCE);
	
		Edge question2ToEnd = FlowFactory.createEdge("question2ToSnap2_ID", questionNode2,
				endNode, condition2);
	
	
		List<Edge> edgesList = new LinkedList<Edge>(Arrays.asList(startToQuestion1,
				question1ToSnap, snap1ToQuestion2, question2ToEnd));
	
		Flow testFlow = FlowFactory.createFlow(kb, "Main", nodesList, edgesList);
		testFlow.setAutostart(true);
	
		// ----------------------------------
	
	}

	protected void createFlow(Condition condition1, Condition condition2) throws IOException {
		createFlow(new NOOPAction(), new NOOPAction(), condition1, condition2);
	}

	protected void createFlow(PSAction action1, PSAction action2) throws IOException {
		createFlow(action1, action2, ConditionTrue.INSTANCE, ConditionTrue.INSTANCE);
	}

}