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
package test.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.Node;
import de.d3web.we.diaflux.datamanagement.FlowDomain;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 20.08.2012
 */
public class FlowDomainTest {

	KnowledgeBase kb;
	Flow flow;
	EndNode n1;
	EndNode n2;
	EndNode n3;

	@Before
	public void setUp() throws Exception {
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		n1 = new EndNode("1", "1");
		n2 = new EndNode("2", "2");
		n3 = new EndNode("3", "3");

		List<Node> nodes = new ArrayList<Node>();
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);

		flow = FlowFactory.createFlow(kb, "TestFlow", nodes,
				new ArrayList<Edge>());
	}

	@Test
	public void testConstructorNode() {
		assertThat(new FlowDomain(n1).getEndNodes(), both(hasItems(n1)).and(not(hasItems(n2, n3))));
		assertThat(new FlowDomain(n2).getEndNodes(), both(hasItems(n2)).and(not(hasItems(n1, n3))));

		assertThat(new FlowDomain(n1).getFlow(), is(flow));
	}

	@Test
	public void testConstructorFlow() {
		assertThat(new FlowDomain(flow).getEndNodes(), hasItems(n1, n2, n3));

		assertThat(new FlowDomain(flow).getFlow(), is(flow));
	}

	@Test
	public void testAdd() {
		assertThat(((FlowDomain) new FlowDomain(n1).add(new FlowDomain(n1))).getEndNodes(),
				both(hasItems(n1)).and(not(hasItems(n2, n3))));

		assertThat(((FlowDomain) new FlowDomain(n1).add(new FlowDomain(n2))).getEndNodes(),
				both(hasItems(n1, n2)).and(not(hasItems(n3))));

		assertThat(((FlowDomain) new FlowDomain(n1, n2, n3).add(new FlowDomain(n2))).getEndNodes(),
				hasItems(n1, n2, n3));

	}

	@Test
	public void testContains() {
		assertThat(new FlowDomain(n1).contains(new FlowDomain(n1)), is(true));
		assertThat(new FlowDomain(n1, n2).contains(new FlowDomain(n1)), is(true));
		assertThat(new FlowDomain(n1, n2, n3).contains(new FlowDomain(n1, n2)), is(true));

		assertThat(new FlowDomain(flow).contains(new FlowDomain(n1, n2)), is(true));
		assertThat(new FlowDomain(flow).contains(new FlowDomain(n1, n2, n3)), is(true));

		assertThat(new FlowDomain(n1).contains(new FlowDomain(n2)), is(false));
		assertThat(new FlowDomain(n1, n3).contains(new FlowDomain(n1, n2)), is(false));
		assertThat(new FlowDomain(n1).contains(new FlowDomain(n1, n2)), is(false));

	}

	@Test
	public void testEmpty() {
		assertThat(new FlowDomain(n1).isEmpty(), is(false));
		assertThat(((FlowDomain) new FlowDomain(n1).intersect(new FlowDomain(n2))).isEmpty(),
				is(true));
	}

	@Test
	public void testIntersect() {
		assertThat(
				((FlowDomain) new FlowDomain(n1, n2, n3).intersect(new FlowDomain(n1))).getEndNodes(),
				both(hasItems(n1)).and(not(hasItems(n3, n2))));

		assertThat(
				((FlowDomain) new FlowDomain(n1, n2, n3).intersect(new FlowDomain(n1, n2, n3))).getEndNodes(),
				hasItems(n1, n2, n3));

	}

	@Test
	public void testIntersects() {
		assertThat(new FlowDomain(n1).intersects(new FlowDomain(n1)), is(true));
		assertThat(new FlowDomain(n1, n2).intersects(new FlowDomain(n1)), is(true));
		assertThat(new FlowDomain(n1).intersects(new FlowDomain(n1, n2)), is(true));

		assertThat(new FlowDomain(n1).intersects(new FlowDomain(n2)), is(false));
	}

	@Test
	public void testNegate() {
		assertThat(((FlowDomain) new FlowDomain(n1).negate()).getEndNodes(),
				both(hasItems(n2, n3)).and(not(hasItems(n1))));

		assertThat(((FlowDomain) new FlowDomain(flow).negate()).isEmpty(),
				is(true));

		assertThat(((FlowDomain) new FlowDomain(n1).negate().negate()).getEndNodes(),
				both(hasItems(n1)).and(not(hasItems(n3, n2))));

	}
}
