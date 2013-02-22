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
import de.d3web.proket.d3web.utils.SolutionRatingComparator;
import de.d3web.proket.d3web.utils.StringTemplateUtils;
import de.d3web.scoring.HeuristicRating;
import de.d3web.scoring.inference.PSMethodHeuristic;
import de.d3web.xcl.inference.PSMethodXCL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.antlr.stringtemplate.StringTemplate;

/**
 * Basic class for rendering the solution panel or explanation component of a
 * d3web dialog
 *
 * @author Martina Freiberg @date Oct 2012
 */
public class SolutionPanelSingleSolutionD3webRenderer extends SolutionPanelBasicD3webRenderer {

    /**
     * Rendering method for getting the textual listing representation of a
     * solution panel
     *
     * @param d3websession
     * @return the (HTML) representation of the rendered solution panel in
     * textual listing form
     */
    public String getSolutionRendering(Session d3websession) {

        KnowledgeBase kb = D3webConnector.getInstance().getKb();
        StringBuilder bui = new StringBuilder();

        Collection<Solution> valuedSolutions =
                d3websession.getBlackboard().getValuedSolutions();

        Solution s = getBestRatedSolution(valuedSolutions, d3websession);
        bui.append(renderState(s, d3websession));
        System.out.println("SOLUTIONPANELSINGLERENDEER" + renderState(s, d3websession));
        return bui.toString();
    }

    protected Solution getBestRatedSolution(Collection<Solution> valued,
            Session d3websession) {

        System.out.println(valued.toString());
        if (valued.size() > 0) {

            Blackboard bb = d3websession.getBlackboard();
            ArrayList<Solution> result = new ArrayList<Solution>();
            ArrayList<Solution> established = new ArrayList<Solution>();
            ArrayList<Solution> suggested = new ArrayList<Solution>();
            ArrayList<Solution> excluded = new ArrayList<Solution>();
            ArrayList<Solution> unclear = new ArrayList<Solution>();

            for (Solution s : valued) {
                if (bb.getRating(s).getState().equals(Rating.State.ESTABLISHED)) {
                    established.add(s);
                } else if (bb.getRating(s).getState().equals(Rating.State.SUGGESTED)) {
                    suggested.add(s);
                } else if (bb.getRating(s).getState().equals(Rating.State.EXCLUDED)) {
                    excluded.add(s);
                } else if (bb.getRating(s).getState().equals(Rating.State.UNCLEAR)) {
                    unclear.add(s);
                }
            }
            result.addAll(established);
            result.addAll(suggested);
            result.addAll(excluded);
            result.addAll(unclear);

            Collections.sort(result, new SolutionRatingComparator(d3websession));
            return result.get(0);
        } else {
            return null;
        }

    }

    private String renderState(Solution solution, Session d3webs) {

        Blackboard bb = d3webs.getBlackboard();
        UISolutionPanelSettings uisols = UISolutionPanelSettings.getInstance();
        if (solution == null) {
            return "solution state info n/a";
        } else {

            // TODO: adapt. Basic different rendering for single solutions somtime
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

        SolutionExplanationBasicD3webRenderer expr = new SolutionExplanationBasicD3webRenderer();
        // fill in the explanation into the explanation popup:
        StringTemplate stExp = StringTemplateUtils.getTemplate("solutionPanel/PopupExp");
        UISolutionPanelSettings.ExplanationType expType =
                uiSolPanelSet.getExplanationType();
        String explanation = expr.getExplanationForSolution(solution, d3webs, expType);
        stExp.setAttribute("elementID", solution.getName());
        stExp.setAttribute("popupcontent", explanation);
        System.out.println(stExp.toString());

        Blackboard bb = d3webs.getBlackboard();

        // retrieve template for the solution panel per se, first
        StringTemplate st = StringTemplateUtils.getTemplate("solutionPanel/SolutionDynLink");
        st.setAttribute("explanationpopup", stExp.toString());

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
