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
package de.d3web.we.diaflux.anomalystrategies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaflux.coverage.AllPathsStrategy;
import de.d3web.diaflux.coverage.DFSStrategy;
import de.d3web.diaflux.coverage.Path;
import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.evaluators.EvaluatorManager;

/**
 * 
 * @author Reinhard Hatko
 * @created 06.08.2012
 */
public class DeadPathStrategy implements DFSStrategy {

	private final KnowledgeBase kb;
	private final DFSStrategy delegate;
	private final Map<Path, String> anomalies;
	private final Map<Path, EvalResult> evals;

	public DeadPathStrategy(KnowledgeBase kb) {
		this.kb = kb;
		this.delegate = new AllPathsStrategy(false, kb);
		this.anomalies = new HashMap<Path, String>();
		this.evals = new HashMap<Path, EvalResult>();
	}

	public void found(Path path) {
	}

	public List<Path> getInitialStartPaths() {
		List<Path> paths = delegate.getInitialStartPaths();
		// for (Path path : paths) {
		// evals.put(path, new EvalResult());
		// }
		return paths;
	}

	public boolean followEdge(Edge edge, Path path) {
		return delegate.followEdge(edge, path);
	}

	public boolean offer(DiaFluxElement el, Path path) {
		EvalResult resultBefore = calculateEvalResult(path);

		boolean offer = delegate.offer(el, path);

		if (offer) {
			EvalResult result = calculateEvalResult(path);
			result = result.intersectWith(resultBefore);

			for (Domain domain : result.getDomains()) {
				if (domain.isEmpty()) {
					this.anomalies.put(path, "Dead path on edge " + el); // TODO
				}
			}
			return anomalies.isEmpty();

		}

		return offer;
	}

	/**
	 * 
	 * @created 06.08.2012
	 * @param path
	 * @return
	 */
	private EvalResult calculateEvalResult(Path path) {

		EvalResult result = new EvalResult();
		for (DiaFluxElement el : path) {
			if (el instanceof Edge) {
				Condition condition = ((Edge) el).getCondition();
				EvalResult eval = EvaluatorManager.getInstance().evaluate(
						condition, kb);
				result = result.intersectWith(eval);
			}
			else {
				// TODO eval actions
			}
		}
		evals.put(path, result);
		return result;
	}

	@Override
	public void finished(Path path) {
		delegate.finished(path);
	}

	public Path createStartPath(Path path) {
		// do not continue, after an anomaly is found
		return null;
	}

	public boolean enterSubflow(ComposedNode node, Path path) {
		return delegate.enterSubflow(node, path);
	}

	public Map<Path, String> getAnomalies() {
		return anomalies;
	}

}
