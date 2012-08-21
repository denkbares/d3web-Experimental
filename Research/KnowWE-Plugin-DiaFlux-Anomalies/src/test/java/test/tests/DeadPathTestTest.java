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
package test.tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;
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
import de.d3web.we.diaflux.anomalies.DeadPathTest;

/**
 * 
 * @author Reinhard Hatko
 * @created 06.08.2012
 */
public class DeadPathTestTest {

	private KnowledgeBase kb;
	private QuestionYN questionYN;
	private Value yes;
	private Value no;
	private ActionNode questionNode1;
	private ActionNode questionNode2;


	// Structure:
	// Start
	// |
	// v
	// questionNode1
	// | questionYN == YES
	// v
	// snapshotNode1
	// |
	// v
	// questionNode2
	// | questionYN == No
	// v
	// End
	@Before
	public void setUpFlux() throws IOException {
		// InitPluginManager.init();

		kb = KnowledgeBaseUtils.createKnowledgeBase();
		questionYN = new QuestionYN(kb.getRootQASet(), "QuestionYN");
		yes = KnowledgeBaseUtils.findValue(questionYN, "Yes");
		no = KnowledgeBaseUtils.findValue(questionYN, "No");

		Node startNode = new StartNode("Start_ID", "Start");
		Node endNode = new EndNode("End_ID", "Ende");
		Node snaphot1 = new SnapshotNode("Snap1_ID", "Snapshot1");

		questionNode1 = new ActionNode("questionNode1_ID", new NOOPAction());

		questionNode2 = new ActionNode("questionNode2_ID", new NOOPAction());

		LinkedList<Node> nodesList = new LinkedList<Node>(Arrays.asList(startNode, endNode,
				questionNode1, snaphot1, questionNode2));

		// ---------------------------------

		Edge startToQuestion1 = FlowFactory.createEdge("startToQuestionEdge1_ID", startNode,
				questionNode1,
				ConditionTrue.INSTANCE);

		Edge question1ToSnap = FlowFactory.createEdge("question1ToSnap_ID", questionNode1,
				snaphot1, new CondEqual(questionYN, yes));

		Edge snap1ToQuestion2 = FlowFactory.createEdge("snap1ToQuestion2_ID", snaphot1,
				questionNode2, ConditionTrue.INSTANCE);

		Edge question2ToEnd = FlowFactory.createEdge("question2ToSnap2_ID", questionNode2,
				endNode, new CondEqual(questionYN, no));


		List<Edge> edgesList = new LinkedList<Edge>(Arrays.asList(startToQuestion1,
				question1ToSnap, snap1ToQuestion2, question2ToEnd));

		Flow testFlow = FlowFactory.createFlow(kb, "Main", nodesList, edgesList);
		testFlow.setAutostart(true);

		// ----------------------------------

	}


	@Test
	public void test() {
		DeadPathTest test = new DeadPathTest();
		Message message = test.execute(kb, null);
		System.out.println(message.getText());
	}

}
