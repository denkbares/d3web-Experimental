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
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.*;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.settings.UISolutionPanelSettings;
import de.d3web.proket.d3web.utils.StringTemplateUtils;
import de.d3web.scoring.HeuristicRating;
import de.d3web.scoring.inference.PSMethodHeuristic;
import de.d3web.xcl.inference.PSMethodXCL;
import java.util.Collection;
import org.antlr.stringtemplate.StringTemplate;

/**
 * Basic class for rendering the solution panel or explanation component of a
 * d3web dialog
 *
 * @author Martina Freiberg @date Oct 2012
 */
public class SolutionPanelListingD3webRenderer extends SolutionPanelBasicD3webRenderer {

    private UISolutionPanelSettings uiSolPanelSet = UISolutionPanelSettings.getInstance();
    
    /**
     * Rendering method for getting the textual listing representation of a
     * solution panel
     *
     * @param d3websession
     * @return the (HTML) representation of the rendered solution panel in
     * textual listing form
     */
    public String getSolutionListing(Session d3websession) {

        KnowledgeBase kb = D3webConnector.getInstance().getKb();
        StringBuilder bui = new StringBuilder();

        Collection<Solution> valuedSolutions =
                d3websession.getBlackboard().getValuedSolutions();
        Collection<Solution> sortedValuedSolutions =
                sortSolutions(valuedSolutions, d3websession);

        // the *real* rendering, i.e. getting the HTML representation
        getSolutionStates(sortedValuedSolutions, bui, d3websession);

        return bui.toString();
    }

    protected void getSolutionStates(Collection<Solution> sortedSols, StringBuilder bui,
            Session d3webs) {

        for (Solution solution : sortedSols) {
            if (!solution.getName().contains("000")) {

                bui.append(renderState((Solution) solution, d3webs));
            }
        }
    }

    
    private String renderState(Solution solution, Session d3webs){
        
        Blackboard bb = d3webs.getBlackboard();
        UISolutionPanelSettings uisols = UISolutionPanelSettings.getInstance();
        
        System.out.println(uisols.getShowPreciseSolRating());
                System.out.println(uisols.getShowAbstractSolRating());
        
        if(uisols.getShowAbstractSolRating() && 
                uisols.getShowPreciseSolRating()){
            if(uisols.getDynamics().equals(UISolutionPanelSettings.Dynamics.STATIC)){
                return renderStateAbstractCirclePreciseText(solution, d3webs);
            } else {
                return renderStateAbstractCirclePreciseTextDynamic(solution, d3webs);
            }
            
        } else if (uisols.getShowAbstractSolRating() &&
                !uisols.getShowPreciseSolRating()){
            // only abstract rating
        } else if (!uisols.getShowAbstractSolRating() &&
                uisols.getShowPreciseSolRating()){
            // only precise rating
        }
        
        return "solution state info n/a";
    }
    /*
     * Assemble rendering of solution for display in a list, indicating the
     * abstract rating by circle icon and precise rating in ( ) after solution
     * text
     */
    private String renderStateAbstractCirclePreciseText(Solution solution, Session d3webs) {

        Blackboard bb = d3webs.getBlackboard();
        
        
        // retrieve template
        StringTemplate st = StringTemplateUtils.getTemplate("solutionPanel/Solution");

        // fill template attribute
        st.setAttribute("solid", solution.getName());

        /*
         * get scoring, dependent on applied problem solving method
         */
        double score = 0.0;
        Collection<PSMethod> contributingPSMethods =
                bb.getContributingPSMethods(solution);
        for (PSMethod psmethod : contributingPSMethods) {
            if (psmethod.getClass().equals(PSMethodHeuristic.class)) {
                score = ((HeuristicRating) bb.getRating(solution)).getScore();
                break;
            } // preparation for x
            else if (psmethod.equals(PSMethodXCL.class)) {
                System.out.println("xcl scoring");
                break;
            }
        }

        
        st.setAttribute("solutiontext", solution.getName());
        st.setAttribute("scoretext", "(" + score + ")");


        // TODO refactor that out of methods and make one own!
        if (bb.getRating(solution).getState().equals(Rating.State.ESTABLISHED)) {
            st.setAttribute("src", "img/solEst.png");
            st.setAttribute("alt", "established");
            st.setAttribute("tt", "established");

        } else if (bb.getRating(solution).getState().equals(Rating.State.SUGGESTED)) {
            st.setAttribute("src", "img/solSug.png");
            st.setAttribute("alt", "suggested");
            st.setAttribute("tt", "suggested");

        } else if (bb.getRating(solution).getState().equals(Rating.State.EXCLUDED)) {
            st.setAttribute("src", "img/solExc.png");
            st.setAttribute("alt", "excluded");
            st.setAttribute("tt", "excluded");

        } else if (bb.getRating(solution).getState().equals(Rating.State.UNCLEAR)) {
            st.setAttribute("src", "/img/solUnc.png");
            st.setAttribute("alt", "unclear");
            st.setAttribute("tt", "unclear");
        }

        return st.toString();
    }
    
    
    private String renderStateAbstractCirclePreciseTextDynamic(Solution solution, Session d3webs) {

        StringTemplate stExp;
        
        SolutionExplanationBasicD3webRenderer expr = new SolutionExplanationBasicD3webRenderer();
        
        UISolutionPanelSettings.ExplanationType expType =
                uiSolPanelSet.getExplanationType();
        
        String explanation = expr.getExplanationForSolution(solution, d3webs, expType);
        
        // in the case of a treemap, a tailored popup that uses the respective image
        // needs to be used
        if (expType == UISolutionPanelSettings.ExplanationType.TREEMAP){
            // explanation popup that shows image
            stExp = StringTemplateUtils.getTemplate("solutionPanel/PopupExpImage");
            stExp.setAttribute("expImagePath", explanation);
        } else {
            // basic explanation popup  
            stExp = StringTemplateUtils.getTemplate("solutionPanel/PopupExp");
            stExp.setAttribute("popupcontent", explanation);
        }
        
        stExp.setAttribute("elementID", solution.getName());
        
        System.out.println(stExp.toString());
        
        
        
        // retrieve template for the solution panel per se, first
        StringTemplate st = StringTemplateUtils.getTemplate("solutionPanel/SolutionDynLink");
        st.setAttribute("explanationpopup", stExp.toString());
        st.setAttribute("solid", solution.getName());

        /*
         * get scoring, dependent on applied problem solving method
         */
        Blackboard bb = d3webs.getBlackboard();
        double score = 0.0;
        Collection<PSMethod> contributingPSMethods =
                bb.getContributingPSMethods(solution);
        for (PSMethod psmethod : contributingPSMethods) {
            if (psmethod.getClass().equals(PSMethodHeuristic.class)) {
                score = ((HeuristicRating) bb.getRating(solution)).getScore();
                break;
            } // preparation for x
            else if (psmethod.equals(PSMethodXCL.class)) {
                System.out.println("xcl scoring");
                break;
            }
        }
        
        st.setAttribute("solutiontext", solution.getName());
        st.setAttribute("scoretext", "(" + score + ")");
        

        if (bb.getRating(solution).getState().equals(Rating.State.ESTABLISHED)) {
            st.setAttribute("src", "img/solEst.png");
            st.setAttribute("alt", "established");
            st.setAttribute("tt", "established");

        } else if (bb.getRating(solution).getState().equals(Rating.State.SUGGESTED)) {
            st.setAttribute("src", "img/solSug.png");
            st.setAttribute("alt", "suggested");
            st.setAttribute("tt", "suggested");

        } else if (bb.getRating(solution).getState().equals(Rating.State.EXCLUDED)) {
            st.setAttribute("src", "img/solExc.png");
            st.setAttribute("alt", "excluded");
            st.setAttribute("tt", "excluded");

        } else if (bb.getRating(solution).getState().equals(Rating.State.UNCLEAR)) {
            st.setAttribute("src", "/img/solUnc.png");
            st.setAttribute("alt", "unclear");
            st.setAttribute("tt", "unclear");
        }

        return st.toString();
    }
    
}
