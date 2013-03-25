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
 * Basic class for rendering the solution panel or explanation component of a
 * d3web dialog
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

    private String renderTreemapExplanationRuleBased(Solution sol, Session d3webSession) {
        try {
            manager = new SolutionTreeMapManager();
            this.d3webSession = d3webSession;
            recurisvelyMapSolutions(sol, "", -1);
            return manager.getSolutionsAsText();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "Error!";
    }

    private void recurisvelyMapSolutions(Solution sol, String parent, int color) {
        int type = 0;
        Rating.State state = d3webSession.getBlackboard().getRating(sol).getState();
        if (state.equals(Rating.State.ESTABLISHED)) {
            type = 2;
        } else if (state.equals(Rating.State.EXCLUDED)) {
            type = 0;
        } else if (state.equals(Rating.State.SUGGESTED)) {
            type = 1;
        } else {
            type = 3;
        }

        if (color == -1) {
            color = type;
        }
        String solName = sol.getName() + " (" + state.toString().toLowerCase() + ")";

        solName = manager.addSolution(solName, parent, color);
        //get all the relevant rules for solution
        List<TerminologyObject> deriObjects = D3webUtils.getDerivationObjectsPSMRulesFor(sol, d3webSession);

        //build string with relevant answers for solution
        for (TerminologyObject object : deriObjects) {
            if (object instanceof Question) {
                Value qvalue = d3webSession.getBlackboard().getValue((ValueObject) object);
                String text = object.getName() + ": " + qvalue;
                manager.addDiagnostic(text, solName, color);
            }
            if (object instanceof Solution) {
                int newColor = -1;
                if (deriObjects.size() == 1) {
                    newColor = color;
                } else {
                    String name = object.getName();
                }
                recurisvelyMapSolutions((Solution) object, solName, newColor);

            }
        }
    }

    protected class SolutionTreeMapManager {

        private List<String> solutions;
        private List<String> solutionsParents;
        private List<String> diags;
        private List<String> diagsParents;
        private List<Integer> solutionType;
        private List<Integer> diagType;

        public SolutionTreeMapManager() {
            solutions = new ArrayList();
            diags = new ArrayList();
            solutionsParents = new ArrayList();
            diagsParents = new ArrayList();
            solutionType = new ArrayList<Integer>();
            diagType = new ArrayList<Integer>();
        }

        public String addSolution(String solution, String parent, int color) {
            String newName = generateName(solution, solutions);
            solutions.add(newName);
            solutionsParents.add(parent);
            solutionType.add(color);
            return newName;
        }

        private String generateName(String element, List<String> myMap) {
            String newName = element;
            int i = 1;
            while (myMap.contains(newName)) {
                newName = element + " (" + i + ")";
                i++;
            }

            return newName;
        }

        public void addDiagnostic(String diagnostic, String parent, int color) {
            String newName = generateName(diagnostic, diags);
            diags.add(newName);
            diagsParents.add(parent);
            diagType.add(color);
        }

        public String getSolutionsAsText() {
            String text = "";

            for (int i = 0; !solutions.isEmpty() && i < solutions.size(); i++) {
                text += solutionType.get(i) + solutions.get(i) + "@" + solutionsParents.get(i) + "&";
            }

            for (int i = 0; !diags.isEmpty() && i < diags.size(); i++) {
                text += diagType.get(i) + diags.get(i) + "@" + diagsParents.get(i) + "&";
            }
            if (text.length() > 2) {
                text = text.substring(0, text.length() - 1);
            }

            return text;
        }
    }

    public String renderAllSolutionsForTreeMap(List<Solution> solList, Session d3webSession) {
        try {
            manager = new SolutionTreeMapManager();
            this.d3webSession = d3webSession;
            manager.addSolution("All Solutions", "", 3);
            for (Solution sol : solList) {
                recurisvelyMapSolutions(sol, "All Solutions", -1);
            }
            return manager.getSolutionsAsText();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "Error!";

    }
}