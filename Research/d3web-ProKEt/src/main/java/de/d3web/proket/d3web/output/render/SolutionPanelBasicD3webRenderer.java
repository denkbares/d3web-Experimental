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

import de.d3web.core.knowledge.terminology.*;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.proket.d3web.settings.UISolutionPanelSettings;
import de.d3web.proket.d3web.utils.SolutionNameComparator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpSession;

/**
 * Basic class for rendering the solution panel or explanation component of a
 * d3web dialog
 *
 * @author Martina Freiberg @date Oct 2012
 */
public class SolutionPanelBasicD3webRenderer {

    private UISolutionPanelSettings uiSolPanelSet = UISolutionPanelSettings.getInstance();

    /**
     * Entry point to solution panel rendering. Switches basic solution panel
     * style between "single solution", multiple solution as list or multiple
     * solution as table and calls according renderers.
     *
     * @param d3webSession
     * @param type
     * @param http
     * @return a String containing the rendered (HTML) representation of the
     * solution panel
     */
    public String renderSolutionPanel(Session d3webSession,
            HttpSession http) {

        StringBuilder bui = new StringBuilder();

        UISolutionPanelSettings.SolutionStructuring solStruc =
                uiSolPanelSet.getSolutionStructuring();
        UISolutionPanelSettings.SolutionRange solRange =
                uiSolPanelSet.getSolutionRange();


        if (solRange == UISolutionPanelSettings.SolutionRange.SINGLE) {
            // TODO: render only the one best solution presented
        } else {
            if (solStruc == UISolutionPanelSettings.SolutionStructuring.LISTING) {
                SolutionPanelListingD3webRenderer sListRend =
                        new SolutionPanelListingD3webRenderer();
                bui.append(sListRend.getSolutionListing(d3webSession));
            } else if (solStruc == UISolutionPanelSettings.SolutionStructuring.TABLE) {
                // TODO: table renderer for solution panel!
            }
        }

        return bui.toString();
    }

    /**
     * Returns given collection of valued solutions as sorted solutions
     * according to what is specified in settings.
     *
     * @param valued
     * @param d3websession
     * @return
     */
    protected Collection<Solution> sortSolutions(Collection<Solution> valued,
            Session d3websession) {

        UISolutionPanelSettings uisols = UISolutionPanelSettings.getInstance();

        if (uisols.getSolutionSorting().equals(UISolutionPanelSettings.SolutionSorting.ALPHABETICAL)) {
            return sortSolutionsAlphabetical(valued, d3websession);
        } else if (uisols.getSolutionSorting().equals(UISolutionPanelSettings.SolutionSorting.CATEGORICAL)) {
            return sortSolutionsCategorical(valued, d3websession);
        } else if (uisols.getSolutionSorting().equals(UISolutionPanelSettings.SolutionSorting.CATEGALPHA)) {
            return sortSolutionsCategoricalAndAlphabetical(valued, d3websession);
        }

        return new ArrayList<Solution>();
    }

    /**
     * Sorts solutions in alphabetical order only.
     *
     * @param valued
     * @param d3websession
     * @return
     */
    protected Collection<Solution> sortSolutionsAlphabetical(Collection<Solution> valued,
            Session d3websession) {

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

        List<String> solutionDepths = UISolutionPanelSettings.getInstance().getSolutionDepths();

        // if the shortcut ALL for getting ALL ratings is used
        if (solutionDepths.size() == 1 && solutionDepths.get(0).equals("ALL")) {
            result.addAll(established);
            result.addAll(suggested);
            result.addAll(excluded);
            result.addAll(unclear);
        } else {
            // otherwise check the different rating entries and add only the
            // chosen ones.
            for (String solDepth : solutionDepths) {
                if (solDepth.equals(Rating.State.ESTABLISHED.toString())) {
                    result.addAll(established);
                }
                if (solDepth.equals(Rating.State.SUGGESTED.toString())) {
                    result.addAll(suggested);
                }
                if (solDepth.equals(Rating.State.EXCLUDED.toString())) {
                    result.addAll(excluded);
                }
                if (solDepth.equals(Rating.State.UNCLEAR.toString())) {
                    result.addAll(unclear);
                }
            }
        }

        Collections.sort(result, new SolutionNameComparator());
        return result;

    }

    /**
     * Sorts solutions according to abstract rating = category only.
     *
     * @param valued
     * @param d3websession
     * @return
     */
    protected Collection<Solution> sortSolutionsCategorical(Collection<Solution> valued,
            Session d3websession) {

        Blackboard bb = d3websession.getBlackboard();
        Collection<Solution> result = new ArrayList<Solution>();
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

        List<String> solutionDepths = UISolutionPanelSettings.getInstance().getSolutionDepths();

        // if the shortcut ALL for getting ALL ratings is used
        if (solutionDepths.size() == 1 && solutionDepths.get(0).equals("ALL")) {
            result.addAll(established);
            result.addAll(suggested);
            result.addAll(excluded);
            result.addAll(unclear);
        } else {
            // otherwise check the different rating entries and add only the
            // chosen ones.
            for (String solDepth : solutionDepths) {
                if (solDepth.equals(Rating.State.ESTABLISHED.toString())) {
                    result.addAll(established);
                }
                if (solDepth.equals(Rating.State.SUGGESTED.toString())) {
                    result.addAll(suggested);
                }
                if (solDepth.equals(Rating.State.EXCLUDED.toString())) {
                    result.addAll(excluded);
                }
                if (solDepth.equals(Rating.State.UNCLEAR.toString())) {
                    result.addAll(unclear);
                }
            }
        }

        return result;
    }

    /**
     * Sorts solutions according to rating categories and within them
     * alphabetically
     *
     * @param valued
     * @param d3websession
     * @return
     */
    protected Collection<Solution> sortSolutionsCategoricalAndAlphabetical(Collection<Solution> valued,
            Session d3websession) {

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

        Collections.sort(established, new SolutionNameComparator());
        Collections.sort(suggested, new SolutionNameComparator());
        Collections.sort(excluded, new SolutionNameComparator());
        Collections.sort(unclear, new SolutionNameComparator());

        List<String> solutionDepths = UISolutionPanelSettings.getInstance().getSolutionDepths();

        // if the shortcut ALL for getting ALL ratings is used
        if (solutionDepths.size() == 1 && solutionDepths.get(0).equals("ALL")) {
            result.addAll(established);
            result.addAll(suggested);
            result.addAll(excluded);
            result.addAll(unclear);
        } else {
            // otherwise check the different rating entries and add only the
            // chosen ones.
            for (String solDepth : solutionDepths) {
                if (solDepth.equals(Rating.State.ESTABLISHED.toString())) {
                    result.addAll(established);
                }
                if (solDepth.equals(Rating.State.SUGGESTED.toString())) {
                    result.addAll(suggested);
                }
                if (solDepth.equals(Rating.State.EXCLUDED.toString())) {
                    result.addAll(excluded);
                }
                if (solDepth.equals(Rating.State.UNCLEAR.toString())) {
                    result.addAll(unclear);
                }
            }
        }

        return result;

    }

    /**
     * Basic Explanation Renderer switch. Calls corresponding subrenderer for
     * constructing an explanation according to what was specified in XML as
     * explanation type.
     *
     * @param solution
     * @return
     */
    protected String renderExplanationForSolution(Solution solution, Session d3webs) {

        String renderedExplanation = "";
        UISolutionPanelSettings.ExplanationType expType =
                uiSolPanelSet.getExplanationType();

        // ToDo differentiate between single/multiple solution
        if (expType == UISolutionPanelSettings.ExplanationType.TEXTUAL) {
            // TODO: render only a plain text representation here
            SolutionExplanationTextualD3webRenderer textualSolRenderer =
                    new SolutionExplanationTextualD3webRenderer();

            System.out.println("TEXTUAL EXPLANATION for solution" + solution.getName() + ": ");
            System.out.println(textualSolRenderer.renderExplanationForSolution(solution, d3webs));
                   

            return textualSolRenderer.renderExplanationForSolution(solution, d3webs);
        } else if (expType == UISolutionPanelSettings.ExplanationType.TREEMAP) {
            System.out.println("Treemap Explanation Rendering");
            // TODO: render treemap explanation
        } else if (expType == UISolutionPanelSettings.ExplanationType.RULEGRAPH) {
            // TODO: render rulegraph explanation
        } else if (expType == UISolutionPanelSettings.ExplanationType.CLARI) {
            System.out.println("Clarification Dialog Solution Rendering");
            // TODO render clarification dialog explanation
        }

        return renderedExplanation;
    }
}
