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
package de.d3web.proket.d3web.input;

import de.d3web.proket.d3web.utils.D3webUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.data.DialogType;
import de.d3web.proket.data.IndicationMode;
import de.d3web.proket.utils.FileUtils;
import de.d3web.proket.utils.GlobalSettings;
import de.d3web.proket.utils.XMLUtils;

/**
 * Class for parsing a given XML specification file (d3web coupled dialogs).
 *
 * @author Martina Freiberg @created 13.10.2010
 */
public class D3webXMLParser {

    // TODO create a default.xml better
    // default dialog/xml that is parsed if nothing else is given
    private String xMLFilename = "default.xml";
    private Node dialogSpec;
    private Node dataSpec;
    private Node ueSpec;

    /**
     * Constructor specifying the XML file
     *
     * @param xMLFilename
     */
    public D3webXMLParser() {
        super();
    }

    public void setSourceToParse(String xmlfilename) {
        if (xmlfilename != null && !xmlfilename.equals("")) {
            this.xMLFilename = xmlfilename;
            if (!this.xMLFilename.endsWith(".xml")) {
                this.xMLFilename += ".xml";
            }
        }
    }

    /**
     * Parses the d3web-XML specification file to retrieve both the root node
     * and the data node.
     *
     * @created 13.10.2010
     */
    public void parse() {

        // try to read the file depending on what was set in the constructor
        // first: "normal" case, i.e. Dialog standalone, no Dialog Manager
        File inputFile = null;
        try {
            // try to get the corresponding XML from the resources folder

            inputFile = FileUtils.getResourceFile(
                    GlobalSettings.getInstance().getD3webSpecsPath()
                    + "/" + xMLFilename);
            System.out.println("try to parse: " + xMLFilename + " result: " + inputFile);
        } catch (FileNotFoundException e2) {
        } catch (NullPointerException npe) {
        }

        // if normal parsing was not yet successful, try to read file from
        // DialogManager upload directory
        if (inputFile == null) {
            String path =
                    GlobalSettings.getInstance().getUploadFilesBasePath()
                    + "/specs/" + xMLFilename;
            inputFile = new File(path);
            System.out.println("try to parse2: " + xMLFilename + " result: " + inputFile);
        }

        // if we have a file, go on parsing
        if (inputFile != null) {
            try {
                // try to read xml root node
                dialogSpec = XMLUtils.getRoot(inputFile, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // read the children of the dialog tag, i.e. the data and the ue tag
            NodeList children = dialogSpec.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String name = child.getNodeName();
                if (name.startsWith("#")) {
                    continue;
                }
                if (name.equals("data")) {
                    dataSpec = child;
                } else if (name.equals("ue")) {
                    ueSpec = child;
                }
            }
        }
    }

    public void parse(File spec) {

        // try to red the file depending on what was set in the constructor
        File inputFile = spec;

        if (inputFile != null) {
            try {
                // try to read xml root node
                dialogSpec = XMLUtils.getRoot(inputFile, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // read the children of the dialog tag, i.e. the data and the ue tag
        NodeList children = dialogSpec.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();
            if (name.startsWith("#")) {
                continue;
            }
            if (name.equals("data")) {
                dataSpec = child;
            } else if (name.equals("ue")) {
                ueSpec = child;
            }
        }
    }

    /*
     * DIALOG PROPERTIES, i.e. global styling and config stuff
     */
    // Reads the DialogStrategy from the parsed XML file. 
    public DialogStrategy getStrategy() {

        String strat = XMLUtils.getStr((Element) dialogSpec, "dstrategy", null);
        return DialogStrategy.valueOf(strat.toUpperCase());
    }

    // Reads the DialogType from the parsed XML file. 
    public DialogType getType() {
        String type = XMLUtils.getStr((Element) dialogSpec, "dtype", null);
        return DialogType.valueOf(type.toUpperCase());
    }

    // TODO NEEDED? WORKING? Maybe refactor
    public IndicationMode getIndicationMode() {
        String mode = XMLUtils.getStr((Element) dialogSpec, "indicationmode",
                IndicationMode.NORMAL.toString());
        return IndicationMode.valueOf(mode.toUpperCase());
    }

    // Reads the CSS attribute from the parsed XML file.
    public String getCss() {
        return XMLUtils.getStr((Element) dialogSpec, "css", null);
    }

    // gets a possibly defined header 
    public String getHeader() {
        return XMLUtils.getStr((Element) dialogSpec, "header", "");
    }

    // get the specified (global) number of colums for displaying the UI
    public int getDialogColumns() {
        Integer col = XMLUtils.getInt((Element) dialogSpec, "dialogcolumns");
        if (col == null) {
            col = 1;
        }
        return col;
    }

    //  get the (global) number of columns for displaiyng within questionnaires
    public int getQuestionnaireColumns() {
        Integer col = XMLUtils.getInt((Element) dialogSpec, "questionnairecolumns");
        if (col == null) {
            col = 1;
        }
        return col;
    }

    // get the (global) number of columns for displaying within questions
    public int getQuestionColumns() {
        Integer col = XMLUtils.getInt((Element) dialogSpec, "questioncolumns");
        if (col == null) {
            col = 1;
        }
        return col;
    }

    // currently supported login modes of ProKEt: OFF = no login, USRDAT: based
    // on tailored textfile, DB: using SQL database connection
    public enum LoginMode {

        OFF, USRDAT, DB
    }

    // returns false in case "no" is given OR nothing
    public LoginMode getLoginMode() {
        LoginMode currentMode = LoginMode.OFF;
        String log = XMLUtils.getStr((Element) dialogSpec, "login", null);
        System.out.println(log);
        if (log != null) {
            currentMode = LoginMode.valueOf(log);
            System.out.println(currentMode);
        }
        return currentMode;
    }

    
    // defining the possible solution explanation types
    public enum SolutionExplanationType{
        NONE, TREEMAP, TABLE, CLARI, TEXTUAL, SOLGRAPH
    }
    
    // return the solution explanation type to be used
    public SolutionExplanationType getSolutionExplanationType() {
        SolutionExplanationType current = SolutionExplanationType.NONE;
        String solType = XMLUtils.getStr((Element) dialogSpec, "solutionExplanation", null);
        
        if (solType != null) {
            current = SolutionExplanationType.valueOf(solType);
            //System.out.println(current);
        }
        return current;
    }
    
    public enum SolutionDepth{
        ALL, ESTABLISHED, SUGGESTED, EXCLUDED
    }
    
    // return the solution depth to be used in solution panel
    public SolutionDepth getSolutionDepth() {
        SolutionDepth current = SolutionDepth.ALL;
        String solDepth = XMLUtils.getStr((Element) dialogSpec, "solutionDepth", null);
        
        if (solDepth != null) {
            current = SolutionDepth.valueOf(solDepth);
            //System.out.println(solDepth);
        }
        return current;
    }
    

    // returns the specification of required fields
    public String getRequired() {

        String req = XMLUtils.getStr((Element) dialogSpec, "required", null);
        if ((req != null && req.toLowerCase().equals("none"))
                || req.equals(null)) {
            return "";
        }
        return req;
    }

    // get the UI user Prefix, e.g. ITree or Hernia...
    public String getUIPrefix() {
        return XMLUtils.getStr((Element) dialogSpec, "uiprefix", "");
    }

    // get the language to be set for UI internal (non-KB) language stuff
    // TODO Refactor: multilingualism for ALL elements based on prop files?
    public String getLanguage() {
        return XMLUtils.getStr((Element) dialogSpec, "language", "");
    }

    public Boolean getDebug() {
        return XMLUtils.getBoolean((Element) dialogSpec, "debug", Boolean.FALSE);
    }
    
    public Boolean getDiagnosisNavi(){
        return XMLUtils.getBoolean((Element) dialogSpec, "diagnosisNavi", Boolean.FALSE);
    }
    
    public Boolean getQuestionnaireNavi(){
        return XMLUtils.getBoolean((Element) dialogSpec, "questionnaireNavi", Boolean.FALSE);
    }

    /*
     * DATA PROPERTIES, i.e. definition of the knowledge base
     */
    // Return the knowledgebase as specified per its filename in the parsed XML
    public KnowledgeBase getKnowledgeBase() throws IOException {
        KnowledgeBase kb = null;

        String kbname = getKnowledgeBaseName();
        kb = D3webUtils.getKnowledgeBase(kbname);
        return kb;
    }

    // Retrieve the name of the knowledge base specified in the parsed XML.
    public String getKnowledgeBaseName() {
        String kbname = "";
        kbname = XMLUtils.getStr((Element) dataSpec, "kb", null);

        return kbname;
    }

    
    /*
     * USABILITY EXTENSION STUFF TODO: write them to separate confic file (like:
     * UE CONNECTOR) and use them for creating the usability stuff
     */
    public Boolean getLogging() {
        return XMLUtils.getBoolean((Element) ueSpec, "logging", Boolean.FALSE);
    }

    public Boolean getFeedbackform() {
        return XMLUtils.getBoolean((Element) ueSpec, "feedbackform", Boolean.FALSE);
    }

    public D3webUESettings.UEQ getUEQuestionnaire() {
        String ueq = XMLUtils.getStr((Element) ueSpec, "questionnaire",
                D3webUESettings.UEQ.NONE.toString());
        return D3webUESettings.UEQ.valueOf(ueq);
    }

    public String getUEGroup() {
        return XMLUtils.getStr((Element) ueSpec, "uegroup", "");
    }

    public boolean getStudy() {
        return XMLUtils.getBoolean((Element) ueSpec, "study", Boolean.TRUE);
    }

    public String getLogfilePath() {
        return XMLUtils.getStr((Element) ueSpec, "logfile_path", "");
    }

    public String getAnalysisOutputPath() {
        return XMLUtils.getStr((Element) ueSpec, "analysisoutput_path", "");
    }
    
    
}
