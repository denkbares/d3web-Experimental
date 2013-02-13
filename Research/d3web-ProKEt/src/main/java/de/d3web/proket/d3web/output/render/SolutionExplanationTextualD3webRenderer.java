/**
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.output.render;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodRulebased;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.proket.d3web.utils.D3webUtils;
import java.util.List;

/**
 * Basic class for rendering the solution panel or explanation component of a
 * d3web dialog
 *
 * @author Martina Freiberg @date Oct 2012
 */
public class SolutionExplanationTextualD3webRenderer {

    public String renderExplanationForSolution(Solution solution, Session d3webSession) {

        List<? extends PSMethod> psms = d3webSession.getPSMethods();
        for (PSMethod psm : psms) {
            if (psm instanceof PSMethodRulebased) {
                // we have rule based knowledge, so call rule-based expl. renderer
                return renderExplanationRuleBased(solution, d3webSession);

            }
        }
        return "textual rule based explanation n/a";
    }

    private String renderExplanationRuleBased(Solution sol, Session d3webSession) {

        String explanationDefault = "textual rule based explanation n/a II";
        String explanation = "";
        
        List<Rule> rules = D3webUtils.getRulesHeuristicRatingFor(sol, d3webSession);
        
        for (Rule rule : rules) {
            
            String cond = rule.getCondition().toString();
            String[] condSplit =cond.split("=="); 
            String condQ = condSplit[0].trim();
            String condV = condSplit[1].trim();
            
            
            String action = rule.getAction().toString();
            String[] split1 = action.split("\\[");
            String[] split2 = split1[1].split("\\]");
            String[] actSplit = split2[0].split(":");
            String actS = actSplit[0].trim();
            String actV = actSplit[1].trim();
            
            explanation = "IF (" + condQ + " = " + condV + ")\nTHEN (" 
                    + actS + " -> " + actV + ")";
        }
        
        return explanation.equals("")?explanationDefault:explanation;
    }
}
