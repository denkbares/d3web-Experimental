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
package de.knowwe.d3webviz.diafluxHierarchy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Attributes;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;


/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 14.03.2013
 */
public class DiaFluxHierarchyAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String sectionID = context.getParameter(Attributes.SECTION_ID);

		Section<DiaFluxHierarchyType> section = Sections.getSection(sectionID,
				DiaFluxHierarchyType.class);

		if (section == null) {
			// TODO error handling
			return;
		}
		
		Iterator<Article> iterator = KnowWEUtils.getCompilingArticles(section).iterator();
		if (!iterator.hasNext()) return;
		
		Article article = iterator.next();
		KnowledgeBase kb = D3webUtils.getKnowledgeBase(section.getWeb(), article.getTitle());
		
		String result = createHierarchy(kb);
		
		context.setContentType("text/json");
		context.getWriter().write(result);
		
	}

	/**
	 * 
	 * @created 14.03.2013
	 * @param kb
	 * @param bob
	 */
	private String createHierarchy(KnowledgeBase kb) {

		Map<Flow, Collection<ComposedNode>> structure = DiaFluxUtils.createFlowStructure(kb);
		Map<Flow, String> result = new HashMap<Flow, String>();

		while (!structure.isEmpty()) {

			// find leaf
			Flow flow = null;
			for (Flow temp : structure.keySet()) {
				Collection<ComposedNode> calledFlows = structure.get(temp);
				if (calledFlows.isEmpty()) {
					flow = temp;
					structure.remove(temp);
					break;
				}
			}

			assert flow != null;

			// remove leaf from tree
			for (Flow temp : structure.keySet()) {
				Collection<ComposedNode> calledFlows = structure.get(temp);
				for (ComposedNode composedNode : new ArrayList<ComposedNode>(calledFlows)) {
					if (DiaFluxUtils.getCalledFlow(kb, composedNode) == flow) {
						calledFlows.remove(composedNode);
					}
				}
			}
			
			String createDistrict = createDistrict(kb, flow, result);
			result.put(flow, createDistrict);

		}

		List<StartNode> nodes = DiaFluxUtils.getAutostartNodes(kb);

		Flow mainFlow = nodes.get(0).getFlow();

		return result.get(mainFlow);

	}

	private String createDistrict(KnowledgeBase kb, Flow flow, Map<Flow, String> result) {
		StringBuilder bob = new StringBuilder();
		bob.append("{");
		bob.append("\"name\":");
		bob.append("\"" + flow.getName() + "\"");
		bob.append(",");

		Collection<String> childs = new ArrayList<String>();
		for (Iterator<ComposedNode> iterator = flow.getNodesOfClass(ComposedNode.class).iterator(); iterator.hasNext();) {
			ComposedNode node = iterator.next();
			Flow calledFlow = DiaFluxUtils.getCalledFlow(kb, node);
			String child = result.remove(calledFlow);
			// can be null in a leaf, that calls a flow, that came in
			// the tree earlier and is inserted elsewhere
			if (child != null) {
				childs.add(child);
			}

		}

		if (childs.isEmpty()) {
			bob.append("\"size\":" + (flow.getNodes().size() + flow.getEdges().size()));

		}
		else {


			bob.append("\"children\":");
			bob.append("[");
			for (Iterator<String> iterator = childs.iterator(); iterator.hasNext();) {
				bob.append(iterator.next());
				if (iterator.hasNext()) {
					bob.append(",");

				}

			}

			bob.append("]");
		}


		bob.append("}");
		
		
		
		return bob.toString();
	}

}
