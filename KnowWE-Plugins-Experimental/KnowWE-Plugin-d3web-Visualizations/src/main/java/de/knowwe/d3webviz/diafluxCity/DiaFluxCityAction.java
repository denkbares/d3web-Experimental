/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3webviz.diafluxCity;

import java.util.Arrays;
import java.util.Collections;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Node;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.d3webviz.AbstractD3webVizAction;
import de.knowwe.d3webviz.diafluxCity.metrics.IncomingEdgesMetric;
import de.knowwe.d3webviz.diafluxCity.metrics.MetricsSet;
import de.knowwe.d3webviz.diafluxCity.metrics.NodeTypeColorMetric;
import de.knowwe.d3webviz.diafluxCity.metrics.OutgoingEdgesMetric;

/**
 * 
 * @author Reinhard Hatko
 * @created 26.02.2013
 */
public class DiaFluxCityAction extends AbstractD3webVizAction {

	@Override
	protected String createOutput(KnowledgeBase kb, Section<?> section, UserActionContext context) {
		return GLCityGenerator.generateCity(createMetrics(), kb).toString();
	}

	private static MetricsSet<Node> createMetrics() {
		MetricsSet<Node> metrics = new MetricsSet<>(Collections.singletonList("Wait"), true);

		metrics.setLengthMetric(new IncomingEdgesMetric());
		metrics.setHeightMetric(new IncomingEdgesMetric());
		metrics.setWidthMetric(new OutgoingEdgesMetric());
		metrics.setColorMetric(new NodeTypeColorMetric());
		return metrics;
	}

}
