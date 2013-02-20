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
package de.d3web.proket.d3web.output.render;

import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.*;
import de.d3web.proket.d3web.settings.UISettings;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.d3web.utils.D3webToJSTreeUtils;
import de.d3web.proket.d3web.utils.StringTemplateUtils;
import de.d3web.proket.output.container.ContainerCollection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.antlr.stringtemplate.StringTemplate;

public class SingleFormDefaultRootD3webRenderer extends DefaultRootD3webRenderer {

    @Override
    public ContainerCollection renderRoot(ContainerCollection cc,
            Session d3webSession, HttpSession http, HttpServletRequest request) {

        UISettings uis = UISettings.getInstance();
        Session s = ((Session) http.getAttribute("d3webSession"));

        // get the d3web base template according to dialog type
        String userprefix = uis.getDialogType().toString();
      
        StringTemplate st = StringTemplateUtils.getTemplate("singleForm/SingleFormD3webDialog");

        /*
         * fill some basic attributes
         */
        st.setAttribute("header", uis.getHeader());
        st.setAttribute("title", userprefix + "-Dialog");

        // load case list dependent from logged in user, e.g. MEDIASTINITIS
        String opts = renderUserCaseList((String) http.getAttribute("user"), http);
        st.setAttribute("fileselectopts", opts);

        
        // add some buttons for basic functionality
        st.setAttribute("loadcase", "true");
        st.setAttribute("savecase", "true");
        st.setAttribute("reset", "true");

        if (D3webUESettings.getInstance().isFeedbackform()) {
            st.setAttribute("feedback", "true");
        }

        if (!D3webUESettings.getInstance().getUequestionnaire().equals(D3webUESettings.UEQ.NONE)) {
            st.setAttribute("ueq", "true");
        }
        /*
         * handle custom ContainerCollection modification, e.g., enabling
         * certain JS stuff
         */
        D3webXMLParser.LoginMode loginMode = D3webConnector.getInstance().getD3webParser().getLoginMode();
        cc.js.setLoginMode(loginMode);
        if (loginMode == D3webXMLParser.LoginMode.USRDAT) {
            st.setAttribute("login", "true");
        }

        if (D3webConnector.getInstance().getD3webParser().getLogging().equals("ON")) {
            st.setAttribute("logging", true);
        }

        // handle Css
        handleCss(cc);

        setDialogSpecificAttributes(http, st, request);

        int localeID = http.getAttribute("locale") != null
                ? Integer.parseInt(http.getAttribute("locale").toString()) : 2;
        // render the children
        renderChildren(st, d3webSession, cc, D3webConnector.getInstance().getKb().getRootQASet(),
                localeID, http, request);

        // global JS initialization
        defineAndAddJS(cc);

        st.setAttribute("fullcss", cc.css.generateOutput());
        st.setAttribute("fulljs", cc.js.generateOutput());
        st.setDefaultArgumentValues();

        if(UISettings.getInstance().hasDiagnosisNavi()){
            st.setAttribute("naviTreeSolutions",
                D3webToJSTreeUtils.getJSTreeHTMLFromD3webSolutions(
                D3webConnector.getInstance().getKb()));
        }
        
        SolutionPanelBasicD3webRenderer spr = 
                D3webRendererMapping.getInstance().getSolutionPanelRenderer();
        
        // render solution panel into here. For including other forms of
        // solution panels, adapt the EXPLANATIONTYPE attribute.
        /*st.setAttribute("solutions", 
                spr.renderSolutionPanel(d3webSession,
                    http
                    ));
*/
        cc.html.add(st.toString());
        return cc;
    }

    @Override
    public void defineAndAddJS(ContainerCollection cc) {
        cc.js.enableD3Web();
        if (D3webUESettings.getInstance().isLogging()) {
            cc.js.enableClickLogging();
        }
        
        cc.js.setQuestionaryCons();

        cc.js.add("$(function() {init_all();});", 1);
        cc.js.add("function init_all() {", 1);
        // cc.js.add("building = true;", 2);
        // cc.js.add("building = false;", 2);
        cc.js.add("hide_all_tooltips()", 2);
        cc.js.add("generate_tooltip_functions();", 3);
        cc.js.add("}", 31);

    }

}
