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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;

/**
 * Basic class for rendering the solution panel or explanation component of a
 * d3web dialog
 *
 * @author Martina Freiberg
 * @date Oct 2012
 *
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

    
/**
 * get States for all solutions
 * @param sortedSols
 * @param bui
 * @param d3webs 
 */
    protected void getSolutionStates(Collection<Solution> sortedSols, StringBuilder bui,
            Session d3webs) {
        
        //render the explantion for all solutions
        renderAllSolutionsForTreeMap(sortedSols, bui, d3webs);

        
        for (Solution solution : sortedSols) {
            if (!solution.getName().contains("000")) {

                //append all solutions to list
                bui.append(renderState((Solution) solution, d3webs));
            }
        }
    }

    /**
     * render explanation for all solutions of a session for treemap visualisation
     * @param sortedSols
     * @param bui
     * @param d3webs 
     */
    private void renderAllSolutionsForTreeMap(Collection<Solution> sortedSols, StringBuilder bui,
            Session d3webs) {
        //get explanation type
        UISolutionPanelSettings.ExplanationType expType =
                uiSolPanelSet.getExplanationType();

        //retrun if the explanation type is not treemap or the list of solutions is empty
        if (expType != UISolutionPanelSettings.ExplanationType.TREEMAP || sortedSols.isEmpty()) {
            return;
        }
        
        //build new list of solutions
        List<Solution> solutions = new ArrayList<Solution>();

        //add solutions to list, except the root solution
        for (Solution solution : sortedSols) {
            if (!solution.getName().contains("000")) {
                solutions.add(solution);
            }
        }

        //return if the list remains empty
        if (solutions.isEmpty()) {
            return;
        }

        //call treeMapRenderer
        SolutionExplanationTreeMapD3webRenderer treeMapRenderer = new SolutionExplanationTreeMapD3webRenderer();

        StringTemplate stExp;
        stExp = StringTemplateUtils.getTemplate("solutionPanel/SolutionDynTreeLink"); //use SolutionDynTreeLink - template
        stExp.setAttribute("solid", "allSols"); //set name to allSols
        stExp.setAttribute("contentx", treeMapRenderer.renderAllSolutionsForTreeMap(solutions, d3webs)); //set content to TreeMapRenderer explanation
        stExp.setAttribute("solutiontext", "View all Solutions as Treemap"); //set link text to "View all Solutions as Treemap"
        bui.append(stExp.toString()); //add link to the list of solutions

    }

    private String renderState(Solution solution, Session d3webs) {


        Blackboard bb = d3webs.getBlackboard();
        UISolutionPanelSettings uisols = UISolutionPanelSettings.getInstance();


        //System.out.println(uisols.getShowPreciseSolRating());
        //System.out.println(uisols.getShowAbstractSolRating());


        if (uisols.getShowAbstractSolRating()
                && uisols.getShowPreciseSolRating()) {
            if (uisols.getDynamics().equals(UISolutionPanelSettings.Dynamics.STATIC)) {
                return renderStateAbstractCirclePreciseText(solution, d3webs);
            } else {
                return renderStateAbstractCirclePreciseTextDynamic(solution, d3webs);
            }


        } else if (uisols.getShowAbstractSolRating()
                && !uisols.getShowPreciseSolRating()) {
            // only abstract rating
        } else if (!uisols.getShowAbstractSolRating()
                && uisols.getShowPreciseSolRating()) {
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
        st.setAttribute("solid", AbstractD3webRenderer.getID(solution));

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


        // in the case of a treemap, a tailored popup will open in a new tab-window

        if (expType == UISolutionPanelSettings.ExplanationType.TREEMAP) {
            // explanation popup that shows a treemap
            stExp = StringTemplateUtils.getTemplate("solutionPanel/SolutionDynTreeListLink"); //use SolutionDynTreeListLink Template
            stExp.setAttribute("solid", solution.getName()); //set solutio name
            stExp.setAttribute("contentx", explanation); //set explanation string

            return decoreateSolutionWithScoring(solution, stExp, d3webs); //decorate solutions with nummeric scoring and bullet in front of the name


            //in case of a clarification visulisation
        } else if (expType == UISolutionPanelSettings.ExplanationType.CLARI) {
            //explanation popup with all the details
            stExp = StringTemplateUtils.getTemplate("baAlina/Clari"); //use template for clarification popup
            stExp.setAttribute("sol_name", solution.getName()); //set the solution name/ title


            //get score from explanation String: sting had the form "score | text"
            String[] split_score = explanation.split("\\|");
            String score = split_score[0];


            //set solution name color according to state
            Blackboard bb = d3webs.getBlackboard();
            if (bb.getRating(solution).getState().equals(Rating.State.EXCLUDED)) {
                stExp.setAttribute("rating", "Excluded solution");
                stExp.setAttribute("color", "#EB4242");//red
            } else if (bb.getRating(solution).getState().equals(Rating.State.ESTABLISHED)) {
                stExp.setAttribute("rating", "Total score: " + score);
                stExp.setAttribute("color", "#7CF56E");//green
            } else if (bb.getRating(solution).getState().equals(Rating.State.SUGGESTED)) {
                stExp.setAttribute("rating", "Total score: " + score);
                stExp.setAttribute("color", "#EBF707");//yellow
            } else if (bb.getRating(solution).getState().equals(Rating.State.UNCLEAR)) {
                stExp.setAttribute("rating", "Unclear solution");
                stExp.setAttribute("color", "#B7BAB6"); //gray
            }

            stExp.setAttribute("content", split_score[1]);

        } else {
            // basic explanation popup  
            stExp = StringTemplateUtils.getTemplate("solutionPanel/PopupExp");
            stExp.setAttribute("popupcontent", explanation);
        }


        //set content string with the necesary HTML tags
        stExp.setAttribute("elementID", AbstractD3webRenderer.getID(solution));

        // retrieve template for the solution panel per se, first
        StringTemplate st = StringTemplateUtils.getTemplate("solutionPanel/SolutionDynLink");
        st.setAttribute("explanationpopup", stExp.toString());
        st.setAttribute("solid", AbstractD3webRenderer.getID(solution));

        /*
         * get scoring, dependent on applied problem solving method
         */

        return decoreateSolutionWithScoring(solution, st, d3webs);
    }

    /**
     * decorate solution link with scoring and matching bullet in fron of the name
     * @param solution
     * @param st
     * @param d3webs
     * @return 
     */
    private String decoreateSolutionWithScoring(Solution solution, StringTemplate st, Session d3webs) {
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


        //set solution name and nummeric scoring
        st.setAttribute("solutiontext", solution.getName());
        st.setAttribute("scoretext", "(" + score + ")");


        //set bullet according to the solution state
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
