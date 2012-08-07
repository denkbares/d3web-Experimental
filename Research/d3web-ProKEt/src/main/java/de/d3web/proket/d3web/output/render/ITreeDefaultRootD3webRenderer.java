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
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webUserSettings;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.input.D3webXMLParser;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.antlr.stringtemplate.StringTemplate;

public class ITreeDefaultRootD3webRenderer extends DefaultRootD3webRenderer {

    /**
     * Basic rendering of the root, i.e., the framing stuff of a dialog, like
     * basic structure, styles etc. Initiates the rendering of child-objects.
     */
    @Override
    public ContainerCollection renderRoot(ContainerCollection cc,
            Session d3webSession, HttpSession http, HttpServletRequest request) {

        // get the d3web base template according to dialog type
        String userprefix = D3webConnector.getInstance().getUserprefix();
        StringTemplate st = TemplateUtils.getStringTemplate(
                userprefix + "D3webDialog",
                "html");

        /*
         * fill some basic attributes
         */
        st.setAttribute("headertext", D3webConnector.getInstance().getHeader());
        st.setAttribute("title", "ITree UI - Based on Num-Question Model");

        // load case list dependent from logged in user, e.g. MEDIASTINITIS
        String opts = renderUserCaseList((String) http.getAttribute("user"));
        st.setAttribute("fileselectopts", opts);

        //String info = renderHeaderInfoLine(d3webSession);
        //st.setAttribute("info", info);

        // set language variable for StringTemplate Widgets
        String lang = D3webConnector.getInstance().getLanguage();
        if (lang.equals("de")) {
            st.setAttribute("langDE", "de");
        } else if (lang.equals("en")) {
            st.setAttribute("langEN", "en");
        }

        // add some buttons for basic functionality
        st.setAttribute("loadcase", "true");
        st.setAttribute("savecase", "true");
        st.setAttribute("reset", "true");

        if (D3webConnector.getInstance().getFeedbackForm()) {
            st.setAttribute("feedback", "true");
        }

        String ueq = D3webConnector.getInstance().getUEQuestionnaire();
        if (!ueq.equals("NONE")) {
            st.setAttribute("ueq", "true");

            if (ueq.equals("SUS")) {
                st.setAttribute("sus", true);
            } else if (ueq.equals("OWN")) {
                st.setAttribute("own", true);
            }
        }
        /*
         * handle custom ContainerCollection modification, e.g., enabling
         * certain JS stuff
         */
        D3webXMLParser.LoginMode loginMode = D3webConnector.getInstance().getD3webParser().getLogin();
        cc.js.setLoginMode(loginMode);
        if (loginMode == D3webXMLParser.LoginMode.usrdat) {
            st.setAttribute("login", "true");
        }

        if (D3webConnector.getInstance().getD3webParser().getLogging().equals("ON")) {
            st.setAttribute("logging", true);
        }

        // if logo is provided by KB
        if (D3webUtils.isImageProvided("logo")) {
            st.setAttribute("logo", true);
        }

        // handle Css
        handleCss(cc);

        D3webUserSettings us =
                (D3webUserSettings) http.getAttribute("userSettings");

        // render the children
        renderChildrenITreeNum(st, d3webSession, cc, D3webConnector.getInstance().getKb().getRootQASet(),
                us.getLanguageId(), http);

        // global JS initialization
        defineAndAddJS(cc);

        st.setAttribute("fullcss", cc.css.generateOutput());
        st.setAttribute("fulljs", cc.js.generateOutput());
        st.setDefaultArgumentValues();

        setDialogSpecificAttributes(http, st, request);

        cc.html.add(st.toString());
        return cc;
    }

    @Override
    public void handleCss(ContainerCollection cc) {

        D3webConnector d3wcon = D3webConnector.getInstance();
        // css code from the specification XML
        String css = d3wcon.getCss();

        if (css != null) {
            // file reference or inline css?
            // regex prüft ob der css-String was in der Form
            // "file1, file2, file3" ist, also 1-mehrere CSS File Angaben
            if (css.matches("[\\w-,\\s]*")) {
                String[] parts = css.split(","); // aufspilitten

                for (String partCSS : parts) {
                    // replace whitespace characters with empty string
                    // and then get the corresponding css file
                    StringTemplate stylesheet =
                            TemplateUtils.getStringTemplate(partCSS.replaceAll("\\s", ""), "css");

                    // if not at the end of stylesheet string
                    if (stylesheet != null) {

                        // Write css into codecontainer
                        cc.css.add(stylesheet.toString());
                    }
                }
            }
        }
    }

    /**
     * Defines the necessary JavaScript required by this renderer/dialog, and
     * adds it to the JS into the ContainerCollection.
     *
     * @created 15.01.2011
     *
     * @param cc The ContainerCollection
     */
    @Override
    public void defineAndAddJS(ContainerCollection cc) {
        cc.js.enableD3Web();
        cc.js.setITree();

        if (D3webConnector.getInstance().isLogging()) {
            cc.js.enableClickLogging();
        }

        cc.js.add("$(function() {iTreeInit();});", 1);
    }

    
}
