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
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.proket.d3web.utils.D3webUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic class for rendering a treemap represenatation of a solution and a treemap  represenattion for all solutions
 *
 * @author Alina Coca
 * @date Feb 2013
 */
public class SolutionExplanationTreeMapD3webRenderer {

    SolutionTreeMapManager manager;
    Session d3webSession;

    public String renderExplanationForSolution(Solution solution, Session d3webSession) {

        List<? extends PSMethod> psms = d3webSession.getPSMethods();
        for (PSMethod psm : psms) {
            if (psm instanceof PSMethodRulebased) {
                // we have rule based knowledge, so call rule-based expl. renderer
                return renderTreemapExplanationRuleBased(solution, d3webSession);

            }
        }
        return "textual rule based explanation n/a";
    }

    /**
     * render explanantion for treemap for one solution
     * @param sol
     * @param d3webSession
     * @return explanation
     */
    private String renderTreemapExplanationRuleBased(Solution sol, Session d3webSession) {
        try {
            manager = new SolutionTreeMapManager(); //use solituion manager for solution and the "teildiagnosen"
            this.d3webSession = d3webSession;
            recurisvelyMapSolutions(sol, "", -1); //map the solution in the lists used by the manager
            return manager.getSolutionsAsText(); //retrun the solution and the containing rules as string
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "Error!";
    }

    /**
     * map solution and the deducting rules 
     * @param sol
     * @param parent
     * @param color 
     */
    private void recurisvelyMapSolutions(Solution sol, String parent, int color) {
        int type = 0;
        Rating.State state = d3webSession.getBlackboard().getRating(sol).getState(); //set the type of a solution depending on state
        if (state.equals(Rating.State.ESTABLISHED)) {
            type = 2;
        } else if (state.equals(Rating.State.EXCLUDED)) {
            type = 0;
        } else if (state.equals(Rating.State.SUGGESTED)) {
            type = 1;
        } else {
            type = 3;
        }

        //set the color for solution used in the treemap represenatation
        if (color == -1) {
            color = type;
        }

        //build the name string for a solution adding the text "solution" and the state
        String solName = "Solution: " + sol.getName() + " (" + state.toString().toLowerCase() + ")";

        //add to the solution list
        solName = manager.addSolution(solName, parent, color);

        //get all the relevant rules for solution
        List<TerminologyObject> deriObjects = D3webUtils.getDerivationObjectsPSMRulesFor(sol, d3webSession);

        //build string with relevant rule for solution
        for (TerminologyObject object : deriObjects) {

            //in case the rule is a question with answer
            if (object instanceof Question) {
                Value qvalue = d3webSession.getBlackboard().getValue((ValueObject) object);
                String text = object.getName() + ": " + qvalue; //buid sting os form "question: answer"
                manager.addDiagnostic(text, solName, color); //add to the diagnostinc list (aka list of rules)
            }

            //in case the rule is a other solution
            if (object instanceof Solution) {
                int newColor = -1;
                //set the type of the teil-solution
                if (deriObjects.size() == 1) {
                    newColor = color;
                } else {
                    String name = object.getName();
                }
                //recursively get the rules for the Teil-solution
                recurisvelyMapSolutions((Solution) object, solName, newColor);

            }
        }
    }

    //manager class for the solution and the deduction rules 
    protected class SolutionTreeMapManager {

        private List<String> solutions; //list of solutions
        private List<String> solutionsParents; //list of parent nodes for solutions
        private List<String> diags; //list of rules for solution
        private List<String> diagsParents; //list of parents for the rules
        private List<Integer> solutionType; //list of types (depending on states) for solutions
        private List<Integer> diagType; //list of types (depending on states) for rules

        /**
         * constructor
         */
        public SolutionTreeMapManager() {
            solutions = new ArrayList();
            diags = new ArrayList();
            solutionsParents = new ArrayList();
            diagsParents = new ArrayList();
            solutionType = new ArrayList<Integer>();
            diagType = new ArrayList<Integer>();
        }

        /**
         * add solution to solutions list
         *
         * @param solution
         * @param parent
         * @param color
         * @return newName
         */
        public String addSolution(String solution, String parent, int color) {
            String newName = generateName(solution, solutions);//generate new name for solution
            solutions.add(newName); //add to solutions list
            solutionsParents.add(parent); //add parent for the solution
            solutionType.add(color); //add color
            return newName;
        }

        /**
         * generate a new name for the solution
         *
         * is necesary because the treemap does not allow duplicate nodes
         *
         * @param element
         * @param myMap
         * @return newName
         */
        private String generateName(String element, List<String> myMap) {
            String newName = element;
            int i = 1;
            while (myMap.contains(newName)) {
                newName = element + " (" + i + ")";
                i++;
            }

            return newName;
        }

        /**
         * add rule to the rules list with the right parent and color
         *
         * @param diagnostic
         * @param parent
         * @param color
         */
        public void addDiagnostic(String diagnostic, String parent, int color) {
            String newName = generateName(diagnostic, diags); //generate new name for rule
            diags.add(newName); //add to list of rules
            diagsParents.add(parent); //add solution as parent
            diagType.add(color); //add color for the rule, according to parent type
        }

        /**
         * build string from all the list -> type + solution name + rules both:
         * for main solution and for rules that are solutions
         * 
         * uses @ and & to separate part stings, later will be splitetd by this characters
         *
         * @return text
         */
        public String getSolutionsAsText() {
            String text = "";

            //for all solutions
            for (int i = 0; !solutions.isEmpty() && i < solutions.size(); i++) {
                text += solutionType.get(i) + solutions.get(i) + "@" + solutionsParents.get(i) + "&";
            }

            //for all rules
            for (int i = 0; !diags.isEmpty() && i < diags.size(); i++) {
                text += diagType.get(i) + diags.get(i) + "@" + diagsParents.get(i) + "&";
            }
            
            //delete last "&"
            if (text.length() > 2) {
                text = text.substring(0, text.length() - 1);
            }

            return text;
        }
    }

    /**
     * renderer for the treemap that contains all the solutions from one session
     *
     * @param solList
     * @param d3webSession
     * @return text
     */
    public String renderAllSolutionsForTreeMap(List<Solution> solList, Session d3webSession) {
        try {
            manager = new SolutionTreeMapManager();
            this.d3webSession = d3webSession; //get current session
            manager.addSolution("All Solutions", "", 3); //add  root node: name: all solutions, no parent node, type 3: unclear (contains solutions of all types)
            for (Solution sol : solList) { //for each solution in the list
                recurisvelyMapSolutions(sol, "All Solutions", -1); //map new solution with parent "all solutions", and type -1 (will be changed by the method recurisvelyMapSolutions)
            }
            return manager.getSolutionsAsText();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "Error!";

    }
}