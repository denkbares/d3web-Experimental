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

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.D3webUESettings;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.d3web.settings.GeneralDialogSettings.LoginMode;
import de.d3web.proket.d3web.settings.UISettings;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.d3web.settings.GeneralDialogSettings;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.GlobalSettings;
import de.d3web.proket.utils.TemplateUtils;
import java.io.*;
import java.util.ArrayList;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.antlr.stringtemplate.StringTemplateGroup;

public class DefaultRootD3webRenderer extends AbstractD3webRenderer implements RootD3webRenderer {

    /**
     * Basic rendering of the root, i.e., the framing stuff of a dialog, like
     * basic structure, styles etc. Initiates the rendering of child-objects.
     */
    @Override
    public ContainerCollection renderRoot(ContainerCollection cc,
            Session d3webSession, HttpSession http, HttpServletRequest request) {

	GeneralDialogSettings gds = GeneralDialogSettings.getInstance();
        UISettings uis = UISettings.getInstance();
        Session s = ((Session) http.getAttribute("d3webSession"));

        // get the d3web base template according to dialog type
        String userprefix = uis.getDialogType().toString();
        StringTemplate st = null;

        if (userprefix.equals("QuestionaryCons")) {

            // First assemble the stringtemplate base paths within the servlet
            ServletContext context = request.getSession().getServletContext();
            String realStPath = context.getRealPath(request.getContextPath())
                    + "/WEB-INF/classes/stringtemp/html";

            // this is the topmost ST directory
            StringTemplateGroup stg =
                    new StringTemplateGroup("stGroup", realStPath);

            // the subdirectory which contains the specific framing template
            // for the document loader
            StringTemplateGroup stg_sub =
                    new StringTemplateGroup("stGroup", realStPath + "/questionaryCons");

            // need to tell the template dirs of their inheritance, so within
            // st files we just can normally call other templates
            stg_sub.setSuperGroup(stg);

            // Retrieve the basic DialogManager template
            st = stg_sub.getInstanceOf("QuestionaryConsD3webDialog");

        } else {
            st = TemplateUtils.getStringTemplate(
                    userprefix + "D3webDialog",
                    "html");
        }


        /*
         * fill some basic attributes
         */
        st.setAttribute("header", gds.getHeader());
        st.setAttribute("title", userprefix + "-Dialog");

        // load case list dependent from logged in user, e.g. MEDIASTINITIS
        String opts = renderUserCaseList((String) http.getAttribute("user"), http);
        st.setAttribute("fileselectopts", opts);

        String info = renderHeaderInfoLine(d3webSession);
        st.setAttribute("info", info);

        // set language variable for StringTemplate Widgets
        String lang = gds.getLanguage();
        if (lang.equals("de")) {
            st.setAttribute("langDE", "de");
        } else if (lang.equals("en")) {
            st.setAttribute("langEN", "en");
        }

        // add some buttons for basic functionality
        st.setAttribute("loadcase", "true");
        st.setAttribute("savecase", "true");
        st.setAttribute("reset", "true");

        if (D3webUESettings.getInstance().isFeedbackform()) {
            st.setAttribute("feedback", "true");
        }

        if (!D3webUESettings.getInstance().getUequestionnaire().equals("NONE")) {
            st.setAttribute("ueq", "true");
        }
        /*
         * handle custom ContainerCollection modification, e.g., enabling
         * certain JS stuff
         */
        LoginMode loginMode = D3webConnector.getInstance().getD3webParser().getLoginMode();
        cc.js.setLoginMode(loginMode);
        if (loginMode == LoginMode.USRDAT) {
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



        cc.html.add(st.toString());
        return cc;
    }

    @Override
    public void setDialogSpecificAttributes(HttpSession httpSession, StringTemplate st, HttpServletRequest request) {
        // overwrite if you have specific attributes
    }

    @Override
    public String renderHeaderInfoLine(Session d3webSession) {

        StringTemplate st = TemplateUtils.getStringTemplate("HeaderInfoLine", "html");

        TreeMap<Integer, Question> headerQuestions = new TreeMap<Integer, Question>();
        for (Question question : D3webConnector.getInstance().getKb().getManager().getQuestions()) {
            Boolean showInHeader = question.getInfoStore().getValue(ProKEtProperties.SHOW_IN_HEADER);
            if (showInHeader != null && showInHeader) {
                Integer pos = question.getInfoStore().getValue(ProKEtProperties.POSITION_IN_HEADER);
                headerQuestions.put(pos, question);
            }
        }
        StringBuilder infoStringBuilder = new StringBuilder();
        boolean first = true;
        for (Question headerQuestion : headerQuestions.values()) {
            Value value = d3webSession.getBlackboard().getValue(
                    headerQuestion);
            first = verbalizeHeaderQuestion(headerQuestion, value, infoStringBuilder, first);
        }
        st.setAttribute("infoVerbalization", infoStringBuilder.toString());
        return st.toString();

    }

    protected Boolean verbalizeHeaderQuestion(Question headerQuestion, Value headerQuestionValue, StringBuilder infoStringBuilder, Boolean first) {
        if (headerQuestionValue != null && UndefinedValue.isNotUndefinedValue(headerQuestionValue)
                && !Unknown.getInstance().equals(headerQuestionValue)) {
            String questionString = headerQuestion.getName();
            String specificQuestionString = headerQuestion.getInfoStore().getValue(
                    ProKEtProperties.HEADER_TEXT);
            if (specificQuestionString != null) {
                questionString = specificQuestionString;
            }
            String valueString = headerQuestionValue.toString();
            if (headerQuestionValue instanceof ChoiceValue) {
                Choice choice = ((ChoiceValue) headerQuestionValue).getChoice((QuestionChoice) headerQuestion);
                String specificHeaderText = choice.getInfoStore().getValue(
                        ProKEtProperties.HEADER_TEXT);
                if (specificHeaderText != null) {
                    valueString = specificHeaderText;
                }
            }
            if (!first) {
                infoStringBuilder.append(", ");
                String lb = "<br/>";
                int lastLb = infoStringBuilder.indexOf(lb);
                if (infoStringBuilder.length() - lastLb > 25) {
                    infoStringBuilder.append(lb);
                }
            }
            infoStringBuilder.append(questionString + ": " + valueString);
            first = false;
        }
        return first;
    }

    @Override
    public void handleCss(ContainerCollection cc) {

        D3webConnector d3wcon = D3webConnector.getInstance();
        // css code from the specification XML
        String css = UISettings.getInstance().getCss();

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
        if (D3webUESettings.getInstance().isLogging()) {
            cc.js.enableClickLogging();
        }
        cc.js.add("$(function() {init_all();});", 1);
        cc.js.add("function init_all() {", 1);
        // cc.js.add("building = true;", 2);
        // cc.js.add("building = false;", 2);
        cc.js.add("hide_all_tooltips()", 2);
        cc.js.add("generate_tooltip_functions();", 3);
        cc.js.add("}", 31);

    }

    @Override
    public String renderCaseList(HttpSession http) {
        return renderUserCaseList(null, http);
    }

    @Override
    public String renderUserCaseList(String user, HttpSession http) {

        List<File> files = PersistenceD3webUtils.getCaseList(user);
        
        StringBuffer cases = new StringBuffer();
        /*
         * add autosaved as first item always
         */
        cases.append("<option");
        cases.append(" title='" + PersistenceD3webUtils.AUTOSAVE + "'>");
        cases.append(PersistenceD3webUtils.AUTOSAVE);
        cases.append("</option>");

        if (files != null && files.size() > 0) {

            //Collections.sort(files);

            //int nr = 1;
            String nr = "";
            for (File f : files) {
                if (!f.getName().startsWith(PersistenceD3webUtils.AUTOSAVE)) {
                    cases.append("<option");
                    String filename = 
                            f.getName().substring(0, f.getName().lastIndexOf(".")).replace("+", " ");
                    //cases.append(" title='"
                    //        + filename + "'>");
                    cases.append(" title='"
                            + nr + filename + "'>");
                    cases.append(nr + filename);
                    cases.append("</option>");
                    //nr++;
                }
                
            }
        }

        return cases.toString();
    }
    
    private List<File> filterFilesForKB(String kbname, List<File> allFiles){
        
        List<File> filteredFiles = new ArrayList<File>();
        
        String kbToCaseMapFile = 
                GlobalSettings.getInstance().getCaseFolder() + "/KB2CASES.csv";
        File file = new File(kbToCaseMapFile);

        CSVReader csvr = null;
        String[] nextLine = null;
        List<String> filesFromCSV = null;

        try {
            csvr = new CSVReader(new FileReader(file.getName()));
            // go through file
            while ((nextLine = csvr.readNext()) != null) {
                // search for the right "knowledge base line"
                if (nextLine[0].startsWith(kbname)) {
                    // if username and pw could be found, return true
                    filesFromCSV = new ArrayList<String>();
                    for (String word : nextLine) {
                        filesFromCSV.add(word);
                    }
                }
            }
            
            
            // if there were previous entries
            if (filesFromCSV != null) {
               for(File f: allFiles){
                   if(filesFromCSV.contains(f.getName().replace(".xml", ""))){
                       filteredFiles.add(f);
                   }
               }

            } 
            
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        return filteredFiles;
    }
}
