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

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.Session;
import de.d3web.jurisearch.JuriModel;
import de.d3web.jurisearch.JuriRule;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webUserSettings;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.input.D3webXMLParser;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.antlr.stringtemplate.StringTemplate;

public class ClarihieDefaultRootD3webRenderer extends DefaultRootD3webRenderer {

    private Set juriRules = null;

    /**
     * Basic rendering of the root, i.e., the framing stuff of a dialog, like
     * basic structure, styles etc. Initiates the rendering of child-objects.
     */
    @Override
    public ContainerCollection renderRoot(ContainerCollection cc,
            Session d3webSession, HttpSession http) {

        // get the d3web base template according to dialog type
        String userprefix = D3webConnector.getInstance().getUserprefix();
        StringTemplate st = TemplateUtils.getStringTemplate(
                userprefix + "D3webDialog",
                "html");

        /*
         * fill some basic attributes
         */
        st.setAttribute("headertext", D3webConnector.getInstance().getHeader());
        st.setAttribute("title", "Clarification Consultation --- iTree UI");

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
        renderChildren(st, d3webSession, cc, D3webConnector.getInstance().getKb().getRootQASet(),
                us.getLanguageId(), http);

        // global JS initialization
        defineAndAddJS(cc);

        st.setAttribute("fullcss", cc.css.generateOutput());
        st.setAttribute("fulljs", cc.js.generateOutput());
        st.setDefaultArgumentValues();

        setDialogSpecificAttributes(http, st);

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
        cc.js.setHierarchy();

        if (D3webConnector.getInstance().isLogging()) {
            cc.js.enableClickLogging();
        }
        cc.js.add("$(function() {init_all();});", 1);
        cc.js.add("function init_all() {", 1);
        cc.js.add("  hide_all_tooltips()", 2);
        cc.js.add("  generate_tooltip_functions();", 3);
        cc.js.add("}", 31);

    }

    @Override
    protected void renderChildren(StringTemplate st, Session d3webSession, ContainerCollection cc,
            TerminologyObject to, int loc, HttpSession httpSession) {

        final KnowledgeKind<JuriModel> JURIMODEL = new KnowledgeKind<JuriModel>(
                "JuriModel", JuriModel.class);

        StringBuilder childrenHTML = new StringBuilder();
        D3webConnector d3wcon = D3webConnector.getInstance();

        if (to.getName().equals("Q000")) {
            TerminologyObject rootNode = to.getChildren()[0];
            
            if (rootNode != null) {
                
                IQuestionD3webRenderer childRenderer =
                        AbstractD3webRenderer.getRenderer(rootNode);

                // TODO: how to get parent el in here correctly!?
                String childHTML =
                       childRenderer.renderTerminologyObject(d3webSession, cc, rootNode, to, loc, httpSession);
                if (childHTML != null) {
                    childrenHTML.append(childHTML);
                }
                     

                renderChildren(st, d3webSession, cc, rootNode, loc, httpSession);    
                // if children, fill the template attribute children with children-HTML 
                //st.setAttribute("children", childrenHTML.toString());
            }
        } else {


            if (juriRules == null) {
                JuriModel juriModel =
                        d3wcon.getKb().getKnowledgeStore().getKnowledge(JURIMODEL);
                juriRules = juriModel.getRules();
            }

            // get the children of the current to from the juri rules
            List<QuestionOC> toChildren = getChildQuestionsFromJuriRules(to);


            if (toChildren != null && !toChildren.isEmpty()) {

                
                for (Object newChildRoot : toChildren) {

                    // render the element as question
                     /*
                     * First and foremost render node itself
                     */
                    IQuestionD3webRenderer childRenderer =
                            AbstractD3webRenderer.getRenderer((TerminologyObject) newChildRoot);

                    // TODO: how to get parent el in here correctly!?
                    String childHTML =
                            childRenderer.renderTerminologyObject(d3webSession, cc, (TerminologyObject) newChildRoot, to, loc, httpSession);
                    if (childHTML != null) {
                        childrenHTML.append(childHTML);
                    }
                    
                    //renderChildren(st, d3webSession, cc, (TerminologyObject) newChildRoot, loc, httpSession);   

                }
                // if children, fill the template attribute children with children-HTML 
            }
        }
        st.setAttribute("children", childrenHTML.toString());
    }

    /**
     * traverse all jurisearch rules and filter out the one(s) containing the
     * currently rendered Terminology Object as parent
     *
     * @param parent the parent element the children of which are searched
     * @return ArrayList<QuestionOC> the list of child QuestionOCs
     */
    protected ArrayList<QuestionOC> getChildQuestionsFromJuriRules(TerminologyObject parent) {

        ArrayList<QuestionOC> toChildren = new ArrayList<QuestionOC>();

        if (juriRules != null && juriRules.size() != 0) {
            for (Object o : juriRules) {
                JuriRule rule = (JuriRule) o;
                if (rule.getFather().getName().equals(parent.getName())) {
                    HashMap children = rule.getChildren();
                    Set childKeys = children.keySet();
                    for (Object co : childKeys) {
                        if (co instanceof QuestionOC) {
                            toChildren.add(((QuestionOC) co));
                        }
                    }
                }
            }
        }
        return toChildren;
    }
}
