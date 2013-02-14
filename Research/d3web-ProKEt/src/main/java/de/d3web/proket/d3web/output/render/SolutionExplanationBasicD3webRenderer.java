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
public class SolutionExplanationBasicD3webRenderer {

    private UISolutionPanelSettings uiSolPanelSet = UISolutionPanelSettings.getInstance();

  /**
     * Basic Explanation Renderer switch. Calls corresponding subrenderer for
     * constructing an explanation according to what was specified in XML as
     * explanation type.
     *
     * @param solution
     * @return
     */
    protected String getExplanationForSolution(Solution solution, Session d3webs) {

        String renderedExplanation = "";
        UISolutionPanelSettings.ExplanationType expType =
                uiSolPanelSet.getExplanationType();

        // ToDo differentiate between single/multiple solution
        if (expType == UISolutionPanelSettings.ExplanationType.TEXTUAL) {
            // TODO: render only a plain text representation here
            SolutionExplanationTextualD3webRenderer textualSolRenderer =
                    new SolutionExplanationTextualD3webRenderer();

            //System.out.println("TEXTUAL EXPLANATION for solution" + solution.getName() + ": ");
            //System.out.println(textualSolRenderer.renderExplanationForSolution(solution, d3webs));
                   
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
