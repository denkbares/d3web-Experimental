/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.we.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.scoring.inference.PSMethodHeuristic;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.basic.WikiEnvironment;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.utils.ProblemSolverType;

public class ExplanationRenderer2 extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {

		Map<String, String> parameterMap = context.getParameters();

		String toExplain = parameterMap.get(KnowWEAttributes.EXPLAIN);
		// String kbid = parameterMap.get(KnowWEAttributes.NAMESPACE);
		String typeString = parameterMap.get(KnowWEAttributes.PROBLEM_SOLVER_TYPE);

		ProblemSolverType type = null;
		if (typeString == null) {
			List<ProblemSolverType> types = getProblemSolverTypes(parameterMap, toExplain);
			if (types != null && !types.isEmpty()) {
				type = types.get(0);
			}
		}
		else {
			type = ProblemSolverType.getType(typeString);

		}
		if (type == null) type = ProblemSolverType.heuristic;
		if (type.equals(ProblemSolverType.heuristic)) {
			String rendered = renderHeuristic(context, toExplain);
			return rendered;
		}
		else if (type.equals(ProblemSolverType.setcovering)) {
			String rendered = renderSCM(context, toExplain);
			return rendered;
		}
		else if (type.equals(ProblemSolverType.casebased)) {
			return "cbs explanation not ready";
		}
		return "error in ExplanationRenderer2";
	}

	public String renderHeuristic(UserActionContext context, String toExplain) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"de\">");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
		sb.append("<meta http-equiv=\"REFRESH\" content=\"0; url=faces/Controller;jsessionid="
				+ context.getSession().getId()
				+ "?knowweexplanation=true&toexplain="
				+ toExplain + "&id=" + context.getSession().getId() + "\">");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	public String renderSCM(UserActionContext context, String toExplain) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"de\">");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
		sb.append("<meta http-equiv=\"REFRESH\" content=\"0; url=faces/Controller;jsessionid="
				+ context.getSession().getId()
				+ "?knowwescm=true&toexplain="
				+ toExplain + "&id=" + context.getSession().getId() + "\">");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	private List<ProblemSolverType> getProblemSolverTypes(Map<String, String> map, String id) {
		List<ProblemSolverType> result = new ArrayList<ProblemSolverType>();
		String namespace = map.get(KnowWEAttributes.NAMESPACE);
		WikiEnvironment dpse = D3webModule.getDPSE(map);

		KnowledgeBase base = dpse.getKnowledgeBase(namespace);
		Solution diag = base.getManager().searchSolution(id);
		if (diag != null) {
			KnowledgeSlice heu = diag.getKnowledgeStore().getKnowledge(PSMethodHeuristic.BACKWARD);
			if (heu != null) {
				result.add(ProblemSolverType.heuristic);
			}
		}
		return result;
	}

}
