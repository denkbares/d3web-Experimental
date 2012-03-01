/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.output.render;

import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.data.LegalQuestion;
import de.d3web.proket.output.container.ContainerCollection;
import java.util.Vector;
import org.antlr.stringtemplate.StringTemplate;

/**
 *
 * @author Martina Freiberg
 *
 */
public class OQOcQuestionRenderer extends OQQuestionRenderer {

    @Override
    protected void renderChildren(StringTemplate st, ContainerCollection cc,
            IDialogObject dialogObject, boolean force) {

        super.renderChildren(st, cc, dialogObject, force);

        // TODO: refactor to OQ Questions
        if (dialogObject instanceof LegalQuestion) {
            LegalQuestion lq = (LegalQuestion) dialogObject;

            // retrieve the defining value(s) and write to html
            String defining = lq.getDefining();
            String choices = lq.getChoices();

            st.removeAttribute("defining");
            st.setAttribute("defining", defining);
            
           // assemble choices dropdown String
            StringBuilder bui = new StringBuilder();
            if (choices != null && !choices.equals("")) {
                String cSplit[] = choices.split("###");
                if (cSplit != null && cSplit.length != 0) {
                    for (String c : cSplit) {
                        bui.append("<option>");
                        bui.append(c);
                        bui.append("</option>");
                    }
                }
            }
            
            st.removeAttribute("choicesDropdownOptions");
            st.setAttribute("choicesDropdownOptions", bui.toString());
        }
    }
}
