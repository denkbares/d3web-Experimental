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
package de.d3web.we.diaflux.anomalies;

import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaflux.coverage.Path;
import de.d3web.diaflux.coverage.PathGenerator;
import de.d3web.we.diaflux.anomalystrategies.DeadPathStrategy;
import de.d3web.we.diaflux.pathcoloring.AnomalyManager;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * @author Reinhard Hatko
 * @created 06.08.2012
 */
public class DeadPath extends AbstractAnomalyTest {

	@Override
	protected String test(KnowledgeBase kb) {
		DeadPathStrategy strategy = new DeadPathStrategy(kb);
		PathGenerator generator = new PathGenerator(kb, strategy);
		generator.createPaths();
		Map<Path, String> anomalies = strategy.getAnomalies();

		for (Path path : anomalies.keySet()) {
			for (DiaFluxElement element : path) {
				if (element instanceof Node) AnomalyManager.getAnomalyManager().addAnomaly(
						element.getFlow(), (Node) element, "DeadPath");
				else AnomalyManager.getAnomalyManager().addAnomaly(element.getFlow(),
						(Edge) element, "DeadPath");

			}

		}

		if (anomalies.isEmpty()) return "";
		else return KnowWEUtils.maskJSPWikiMarkup(anomalies.values().toString());

	}

}
