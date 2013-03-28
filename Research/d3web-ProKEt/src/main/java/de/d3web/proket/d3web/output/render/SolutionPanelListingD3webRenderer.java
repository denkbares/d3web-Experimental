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

    protected void getSolutionStates(Collection<Solution> sortedSols, StringBuilder bui,
            Session d3webs) {

        //Alina
        renderAllSolutionsForTreeMap(sortedSols, bui, d3webs);

        for (Solution solution : sortedSols) {
            if (!solution.getName().contains("000")) {

                bui.append(renderState((Solution) solution, d3webs));
            }
        }
    }

    private void renderAllSolutionsForTreeMap(Collection<Solution> sortedSols, StringBuilder bui,
            Session d3webs) {
        UISolutionPanelSettings.ExplanationType expType =
                uiSolPanelSet.getExplanationType();

        if (expType != UISolutionPanelSettings.ExplanationType.TREEMAP || sortedSols.isEmpty()) {
            return;
        }
        List<Solution> solutions = new ArrayList<Solution>();

        for (Solution solution : sortedSols) {
            if (!solution.getName().contains("000")) {
                solutions.add(solution);
            }
        }

        if (solutions.isEmpty()) {
            return;
        }

        SolutionExplanationTreeMapD3webRenderer treeMapRenderer = new SolutionExplanationTreeMapD3webRenderer();

        StringTemplate stExp;
        stExp = StringTemplateUtils.getTemplate("solutionPanel/SolutionDynTreeLink");
        stExp.setAttribute("solid", "allSols");
        stExp.setAttribute("contentx", treeMapRenderer.renderAllSolutionsForTreeMap(solutions, d3webs));
        stExp.setAttribute("solutiontext", "View all Solutions as Treemap");
        bui.append(stExp.toString());

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

        SolutionExplanationBasicD3webRenderer expr = 
		new SolutionExplanationBasicD3webRenderer();

        UISolutionPanelSettings.ExplanationType expType =
                uiSolPanelSet.getExplanationType();

        String explanation = 
		expr.getExplanationForSolution(solution, d3webs, expType);

        // in the case of a treemap, a tailored popup will open in a new tab-window

        if (expType == UISolutionPanelSettings.ExplanationType.TREEMAP) {
            // explanation popup that shows a treemap
            stExp = StringTemplateUtils.getTemplate("solutionPanel/SolutionDynTreeListLink");
            stExp.setAttribute("solid", solution.getName());
            stExp.setAttribute("contentx", explanation);

            return decoreateSolutionWithScoring(solution, stExp, d3webs);



            //in case of a clarification visulisation

        } else if (expType == UISolutionPanelSettings.ExplanationType.CLARI) {
            //explanation popup with all the details
            stExp = StringTemplateUtils.getTemplate("baAlina/Clari"); //template for clarification popup
            stExp.setAttribute("sol_name", solution.getName()); //set the solution name/ title


            //get score from explanation String
            String[] split_score = explanation.split("\\|");
            String score = split_score[0];


            //set text for rating: explicit score for ratinf > 70, excluded solution for -Infinity, 
            //sugested or unclear solution for score < 70
            Blackboard bb = d3webs.getBlackboard();
            if (bb.getRating(solution).getState().equals(Rating.State.EXCLUDED)) {

                stExp.setAttribute("rating", "Excluded solution");
                stExp.setAttribute("color", "#EB4242");
            } else if (bb.getRating(solution).getState().equals(Rating.State.ESTABLISHED)) {
                stExp.setAttribute("rating", "Total score: " + score);
                stExp.setAttribute("color", "#7CF56E");
            } else if (bb.getRating(solution).getState().equals(Rating.State.SUGGESTED)) {

                stExp.setAttribute("rating", "Total score: " + score);
                stExp.setAttribute("color", "#EBF707");
            } else if (bb.getRating(solution).getState().equals(Rating.State.UNCLEAR)) {


                stExp.setAttribute("rating", "Unclear solution");
                stExp.setAttribute("color", "#B7BAB6");
            }

            stExp.setAttribute("content", split_score[1]);

        } else {
	    // basic explanation popup  
            stExp = StringTemplateUtils.getTemplate("solutionPanel/PopupExp");
	    stExp.setAttribute("popupcontent", explanation);
        }

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
