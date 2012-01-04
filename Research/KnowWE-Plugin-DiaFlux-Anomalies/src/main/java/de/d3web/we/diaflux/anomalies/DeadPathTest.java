package de.d3web.we.diaflux.anomalies;

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.knowwe.core.KnowWEEnvironment;


public class DeadPathTest extends AbstractCITest {

	@Override
	public CITestResult call() throws Exception {
		String articleName = getParameter(0);
		String config = "knowledge base article: " + articleName;

		KnowledgeBase kb =
				D3webModule.getKnowledgeBase(
						KnowWEEnvironment.DEFAULT_WEB, articleName);
		
		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);
		
		if(null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			
		}
		
		return res;
	}

}