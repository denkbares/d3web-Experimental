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
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.proket.d3web.utils.D3webUtils;
import java.util.List;

/**
 * Basic class for rendering the solution panel or explanation component of a
 * d3web dialog
 *
 * @author Alina Coca @date Feb 2013
 */
public class SolutionExplanationTreeMapD3webRenderer {

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
        String text;
        Rating.State state = d3webSession.getBlackboard().getRating(sol).getState();
        if(state.equals(Rating.State.ESTABLISHED)){
         text = "2";   
        }else if(state.equals(Rating.State.EXCLUDED)){
            text="0";
        }else{
            text="1";
        }
        
        
    text += sol.getName();
    //get all the relevant rules for solution
    List<TerminologyObject> deriObjects  = D3webUtils.getDerivationObjectsPSMRulesFor(sol, d3webSession);

        //build string with relevant answers for solution
     for (TerminologyObject object : deriObjects) {             
            Value qvalue = d3webSession.getBlackboard().getValue((ValueObject) object);
	    //Value svalue = d3webSession.getBlackboard().getValue((ValueObject) sol); 
            text +="&"+ object.getName() + "=" + qvalue + ";";
       }   

         return text;
    }
    
    
}
