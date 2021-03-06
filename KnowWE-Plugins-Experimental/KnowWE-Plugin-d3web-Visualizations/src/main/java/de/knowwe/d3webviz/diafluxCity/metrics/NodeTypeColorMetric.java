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
package de.knowwe.d3webviz.diafluxCity.metrics;

import java.awt.Color;

import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.CommentNode;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.NOOPAction;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.indication.ActionNextQASet;


/**
 * 
 * @author Reinhard Hatko
 * @created 08.02.2012
 */
public class NodeTypeColorMetric implements Metric<Node, Color> {

	@Override
	public Color getValue(Node object) {

		if (object instanceof ActionNode) {
			if (((ActionNode) object).getAction() instanceof NOOPAction) return Color.DARK_GRAY;
			else if (((ActionNode) object).getAction() instanceof ActionNextQASet) return Color.YELLOW;
			else return Color.BLUE;
		}
		else if (object instanceof StartNode) {
			return Color.GREEN;
		}
		else if (object instanceof EndNode) {
			return Color.RED;
		}
		else if (object instanceof SnapshotNode) {
			return Color.PINK;
		}
		else if (object instanceof CommentNode) {
			return Color.ORANGE;
		}
		return Color.BLUE;
	}

}
