/**
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.run;

import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.ue.log.JSONLogger;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility class that contains all methods required for processing the
 * requests coming from the Dialog Servlets, such as D3webDialog, 
 * MediastinitisDialog, etc.
 * 
 * @author Martina Freiberg
 * 
 * @date 30.12.2011
 */
public class D3webDialogUtilities {
    
    protected static void logInfoPopup(HttpServletRequest request,
            String logfilename) {
        String prefix = request.getParameter("prefix");
        String ttwidget = request.getParameter("widget");
        ttwidget = ttwidget.replace("+", " ");
        String timestring = request.getParameter("timestring");

        JSONLogger logger = D3webConnector.getInstance().getLogger();

        // TODO rename tooltip --> infopopup
        logger.logClickedObjects(
                "INFOPOPUP_" + prefix, timestring, ttwidget);

        logger.writeJSONToFile(logfilename);
    }
}
