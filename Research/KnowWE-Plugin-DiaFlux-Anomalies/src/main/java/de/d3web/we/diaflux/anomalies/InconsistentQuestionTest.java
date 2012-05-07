package de.d3web.we.diaflux.anomalies;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.indication.ActionIndication;
import de.d3web.indication.ActionInstantIndication;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.ActionRepeatedIndication;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.diaflux.pathcoloring.AnomalyManager;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.utils.Strings;

public class InconsistentQuestionTest extends AbstractCITest {

	private enum IndicationType {
		NORMAL,
		INSTANT,
		REPEATED
	};

	@Override
	public CITestResult call() throws Exception {
		String articleName = getParameter(0);
		String config = "knowledge base article: " + articleName;

		KnowledgeBase kb =
				D3webUtils.getKnowledgeBase(
						Environment.DEFAULT_WEB, articleName);

		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);

		StringBuffer error = new StringBuffer();

		Hashtable<String, IndicationType> knownQuestions = new Hashtable<String, IndicationType>();

		if (null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			for (Flow flow : flowcharts) {
				List<Node> nodes = flow.getNodes();
				for (Node node : nodes) {
					if (node.getClass().equals(ActionNode.class)) {
						PSAction action = ((ActionNode) node).getAction();
						if (action instanceof ActionNextQASet) {
							List<String> vars = getAskedValues((ActionNextQASet) action);
							for (String var : vars) {

								IndicationType type = IndicationType.NORMAL;
								if (action instanceof ActionIndication) {
									type = IndicationType.NORMAL;
								}
								else if (action instanceof ActionInstantIndication) {
									type = IndicationType.INSTANT;
								}
								else if (action instanceof ActionRepeatedIndication) {
									type = IndicationType.REPEATED;
								}
								if (!knownQuestions.containsKey(var)) {
									knownQuestions.put(var, type);
								}
								else {
									IndicationType original = knownQuestions.get(var);
									if (original != type) {
										AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
										anomalyManager.addAnomaly(node.getFlow(), node,
												"Inconsistent Question");
										error.append("Inconsistent Question for Var. : " + var
												+ "<br>");
									}
								}
							}
						}
					}
				}
			}

		}
		String errormsg = error.toString();
		if (!errormsg.isEmpty()) {
			Strings.maskHTML(errormsg);
			res = new CITestResult(Type.FAILED, errormsg, config);
		}

		return res;
	}

	private List<String> getAskedValues(ActionNextQASet action) {
		List<String> result = new LinkedList<String>();

		for (QASet set : action.getQASets()) {
			if (set instanceof QContainer) {
				QContainer qc = (QContainer) set;
				List<Question> questions = KnowledgeBaseUtils.getSuccessors(qc, Question.class);
				for (Question q : questions) {
					result.add(q.getName());
				}
			}
			else {
				result.add(set.getName());
			}
		}
		return result;
	}
}
