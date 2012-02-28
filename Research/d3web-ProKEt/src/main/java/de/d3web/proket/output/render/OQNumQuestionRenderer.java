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

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.data.LegalQuestion;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

/**
 *
 * @author Martina Freiberg
 *
 */
public class OQNumQuestionRenderer extends LegalQuestionRenderer {

    @Override
    protected void renderChildren(StringTemplate st, ContainerCollection cc,
            IDialogObject dialogObject, boolean force) {

        super.renderChildren(st, cc, dialogObject, force);

        if (dialogObject instanceof LegalQuestion) {
            LegalQuestion lq = (LegalQuestion) dialogObject;

            // write the defining num value or intervall from xml into html
            String defining = lq.getDefining();
            String defArray[] = defining.split("-");
            if (defArray != null && defArray.length == 2 && 
                    defArray[0] != null && defArray[1] != null) {
                st.setAttribute("defmin", defArray[0]);
                st.setAttribute("defmax", defArray[1]);
            } else {
                st.setAttribute("defmin", defArray[0]);
                st.setAttribute("defmax", defArray[0]);
            }
        }
    }
}
