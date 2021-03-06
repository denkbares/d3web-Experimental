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
package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.NodeActiveCondition;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.FlowDomain;


/**
 * 
 * @author Reinhard Hatko
 * @created 29.06.2012
 */
public class NodeActiveEval extends AbstractEvaluator {


	@Override
	protected Class<? extends Condition> getEvaluationClass() {
		return NodeActiveCondition.class;
	}

	@Override
	public EvalResult evaluate(Condition condition, KnowledgeBase kb) {
		NodeActiveCondition nac = (NodeActiveCondition) condition;
		EndNode endNode = DiaFluxUtils.findExitNode(kb, nac);
		FlowDomain domain = new FlowDomain(endNode);
		EvalResult result = new EvalResult(endNode.getFlow(), domain);
		return result;
	}

}
