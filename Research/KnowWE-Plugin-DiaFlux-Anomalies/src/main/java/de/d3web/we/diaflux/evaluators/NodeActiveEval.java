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
import de.d3web.diaFlux.inference.NodeActiveCondition;
import de.d3web.we.diaflux.datamanagement.EvalResult;


/**
 * 
 * @author Reinhard Hatko
 * @created 29.06.2012
 */
public class NodeActiveEval implements Evaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(NodeActiveCondition.class);
	}

	@Override
	public EvalResult evaluate(Condition condition, KnowledgeBase kb) {
		NodeActiveCondition nac = (NodeActiveCondition) condition;

		return null;
	}

}