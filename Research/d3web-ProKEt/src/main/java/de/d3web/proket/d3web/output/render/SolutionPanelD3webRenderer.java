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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.*;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webXMLParser;
import de.d3web.proket.d3web.settings.UISettings;
import de.d3web.proket.d3web.utils.SolutionNameComparator;
import de.d3web.proket.d3web.utils.StringTemplateUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.HttpSession;
import org.antlr.stringtemplate.StringTemplate;

/**
 * Basic class for rendering the solution panel or explanation component of a
 * d3web dialog
 *
 * @author Martina Freiberg @date Oct 2012
 */
public class SolutionPanelD3webRenderer extends AbstractD3webRenderer {

    /**
     * Entry point to solution panel rendering. According to given EXPL-TYPE the
     * corresponding sub-method for rendering will be called.
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
        D3webXMLParser.SolutionExplanationType solType =
                UISettings.getInstance().getSolutionExplanationType();
       
        if (solType == D3webXMLParser.SolutionExplanationType.TEXTUAL) {
            System.out.println("textual");
            bui.append(getTextualListing(d3webSession));

        } else if (solType == D3webXMLParser.SolutionExplanationType.TABLE) {
            // TODO
        } else if (solType == D3webXMLParser.SolutionExplanationType.TREEMAP) {
            System.out.println("Treemap Solution Rendering");
            // TODO
        } else if (solType == D3webXMLParser.SolutionExplanationType.SOLGRAPH) {
            // TODO
        } else if (solType == D3webXMLParser.SolutionExplanationType.CLARI) {
            System.out.println("Clarification Dialog Solution Rendering");
            // TODO
        }

        return bui.toString();
    }

    /**
     * Rendering method for getting the textual listing representation of a
     * solution panel
     *
     * @param d3websession
     * @return the (HTML) representation of the rendered solution panel in
     * textual listing form
     */
    private String getTextualListing(Session d3websession) {

        KnowledgeBase kb = D3webConnector.getInstance().getKb();
        TerminologyObject rootSol = kb.getRootSolution();
        StringBuilder bui = new StringBuilder();

        Collection<Solution> valuedSolutions =
                d3websession.getBlackboard().getValuedSolutions();
        Collection<Solution> sortedValuedSolutions =
                sortSolutions(valuedSolutions, d3websession);

        // the *real* rendering, i.e. getting the HTML representation
        getSolutionStates(sortedValuedSolutions, bui, d3websession.getBlackboard());

        return bui.toString();
    }

    private Collection<Solution> sortSolutions(Collection<Solution> valued,
            Session d3websession) {

        UISettings uis = UISettings.getInstance();
        
        if(uis.getSolutionSorting().equals(D3webXMLParser.SolutionSorting.ALPHABETICAL)){
            return sortSolutionsAlphabetical(valued, d3websession);
        } else if (uis.getSolutionSorting().equals(D3webXMLParser.SolutionSorting.CATEGORICAL)){
            return sortSolutionsCategorical(valued, d3websession);
        } else if (uis.getSolutionSorting().equals(D3webXMLParser.SolutionSorting.CATEGALPHA)){
            return sortSolutionsCategoricalAndAlphabetical(valued, d3websession);
        }
        
        return new ArrayList<Solution>();
    }

    private void getSolutionStates(Collection<Solution> sortedSols, StringBuilder bui,
            Blackboard bb) {

        for (Solution solution : sortedSols) {
            if (!solution.getName().contains("000")) {

                bui.append(renderSolutionState((Solution) solution, bb));

            }
        }
    }

    private String renderSolutionState(Solution solution, Blackboard bb) {

        // retrieve template
        StringTemplate st = StringTemplateUtils.getTemplate("solutionPanel/Solution");

        // fill template attribute
        st.setAttribute("solid", solution.getName());
        st.setAttribute("solutiontext", solution.getName());

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
            //st.setAttribute("src", "/img/solExc.png");
            st.setAttribute("alt", "unclear");
            st.setAttribute("tt", "unclear");

        }


        return st.toString();
    }
    
     private Collection<Solution> sortSolutionsAlphabetical(Collection<Solution> valued,
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

        String[] solutionDepths = UISettings.getInstance().getSolutionDepths();

        // if the shortcut ALL for getting ALL ratings is used
        if (solutionDepths.length == 1 && solutionDepths[0].equals("ALL")) {
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
     
     private Collection<Solution> sortSolutionsCategorical(Collection<Solution> valued,
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

        String[] solutionDepths = UISettings.getInstance().getSolutionDepths();

        // if the shortcut ALL for getting ALL ratings is used
        if (solutionDepths.length == 1 && solutionDepths[0].equals("ALL")) {
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
     
     private Collection<Solution> sortSolutionsCategoricalAndAlphabetical(Collection<Solution> valued,
            Session d3websession) {
        
         System.out.println("CATEGORISCH UND ALPHABETISCH");
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
        
        String[] solutionDepths = UISettings.getInstance().getSolutionDepths();

        // if the shortcut ALL for getting ALL ratings is used
        if (solutionDepths.length == 1 && solutionDepths[0].equals("ALL")) {
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

}
