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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.data.DialogType;
import de.d3web.proket.utils.FileUtils;
import de.d3web.proket.utils.GlobalSettings;
import de.d3web.proket.utils.XMLUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parser for retrieving the contents of the specification XML for d3web ProKEt
 * artifacts Uses new specification structure.
 *
 * @author Martina Freiberg
 */
public class D3webXMLParser {
    // TODO create a default.xml better
    // default dialog/xml that is parsed if nothing else is given

    private String xmlFileName = "";
    // the various base nodes as contained in the specification XML
    private Node dialogOpts;
    private Node globalUIOpts;
    private Node localUIOpts;
    private Node dataOpts;
    private Node ueOpts;
    private Node locQuestioncolumns;
    private Node locDropdown;
    private Node locUnknownvisible;
    private Node locOverlay;
    private Node locLargetext;
    private Node locAutocolumns;
    private Node locGroups;
    private static String FILESEP = System.getProperty("file.separator");

    /**
     * Basic (empty) constructor. Always uses the default xmlFileName as file to
     * set for the parser.
     */
    public D3webXMLParser() {
        new D3webXMLParser("default.xml");
    }

    /**
     * Constructor taking a filename argument. Sets the provided filename as
     * file to parse.
     *
     * @param filename
     */
    public D3webXMLParser(String filename) {
        this.setSourceToParse(filename);
    }

    /**
     * Sets the provided filename as filename to parse for this parser instance
     *
     * @param xmlfilename
     */
    public void setSourceToParse(String xmlfilename) {
        if (xmlfilename != null && !xmlfilename.equals("")) {
            this.xmlFileName = xmlfilename;
            if (!this.xmlFileName.endsWith(".xml")) {
                this.xmlFileName += ".xml";
            }
        }
    }

    /**
     * Initiate the parsing process
     */
    public void parse() {

        // first, try to retrieve the specified xml file from the default
        // specification folder within the webapp
        File inputFile = null;
        try {
            inputFile = FileUtils.getResourceFile(
                    GlobalSettings.getInstance().getD3webSpecsPath()
                    + FILESEP + xmlFileName);
            //System.out.println("try to parse: " + xMLFilename + " result: " + inputFile);
        } catch (FileNotFoundException e2) {
        } catch (NullPointerException npe) {
        }

        // if normal parsing did not get a valid file, try to retrieve one from
        // the upload tool's storage
        if (inputFile == null) {
            String path =
                    GlobalSettings.getInstance().getUploadFilesBasePath()
                    + FILESEP + "specs" + FILESEP + xmlFileName;
            inputFile = new File(path);
            //System.out.println("try to parse2: " + xMLFilename + " result: " + inputFile);
        }

        // if we have a file, go on parsing
        if (inputFile != null) {
            try {
                // try to read xml root node
                dialogOpts = XMLUtils.getRoot(inputFile, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // read the child elements of the dialog tag for retrieving the
            // child node objects
            NodeList children = dialogOpts.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String name = child.getNodeName();
                if (name.startsWith("#")) {
                    continue;
                }
                if (name.equals("globalUIOpts")) {
                    globalUIOpts = child;
                } else if (name.equals("localUIOpts")) {
                    localUIOpts = child;
                } else if (name.equals("dataOpts")) {
                    dataOpts = child;
                } else if (name.equals("ueOpts")) {
                    ueOpts = child;
                }
            }

            if (localUIOpts != null) {
                NodeList locChildren = localUIOpts.getChildNodes();
                if (locChildren.getLength() > 0) {
                    for (int i = 0; i < locChildren.getLength(); i++) {
                        Node locChild = locChildren.item(i);
                        String locName = locChild.getNodeName();
                        if (locName.equals("questioncolumns")) {
                            locQuestioncolumns = locChild;
                        } else if (locName.equals("dropdown")) {
                            locDropdown = locChild;
                        } else if (locName.equals("unknownvisible")) {
                            locUnknownvisible = locChild;
                        } else if (locName.equals("overlay")) {
                            locOverlay = locChild;
                        } else if (locName.equals("largetext")) {
                            locLargetext = locChild;
                        } else if (locName.equals("autocolumns")) {
                            locAutocolumns = locChild;
                        } else if (locName.equals("groups")) {
                            locGroups = locChild;
                        }
                    }
                }
            }
        }
    }

    /*
     * Retrieve DIALOG Properties
     */
    // gets the dialog header 
    public String getHeader() {
        if (XMLUtils.getStr((Element) dialogOpts, "header") != null) {
            return XMLUtils.getStr((Element) dialogOpts, "header");
        }
        return "";
    }

    // currently supported login modes: OFF = no login, USRDAT: based
    // on csv tailored textfile, DB: using SQL database connection
    public enum LoginMode {

        OFF, USRDAT, DB
    }

    // gets the login mode: returns login mode OFF as default
    public D3webXMLParser.LoginMode getLoginMode() {
        if (XMLUtils.getStr((Element) dialogOpts, "login") != null) {
            return D3webXMLParser.LoginMode.valueOf(
                    XMLUtils.getStr((Element) dialogOpts, "login").toUpperCase());
        }
        return D3webXMLParser.LoginMode.OFF;
    }

    // returns the specification of required fields: default value = ""
    public String getRequired() {

        if (XMLUtils.getStr((Element) dialogOpts, "required") != null) {
            return XMLUtils.getStr((Element) dialogOpts, "required");
        }
        return "";
    }

    // get the language to be set for UI internal (non-KB) language stuff
    // TODO Refactor: multilingualism for ALL elements based on prop files?
    public String getLanguage() {
        if (XMLUtils.getStr((Element) dialogOpts, "language") != null) {
            return XMLUtils.getStr((Element) dialogOpts, "language");
        }
        return "en";
    }

    // get flag whether to display dialog in debug mode, i.e. showing all 
    // elements, even non-indicated etc, as greyed elements
    public Boolean getDebug() {
        if (XMLUtils.getBoolean((Element) dialogOpts, "debug") != null) {
            return XMLUtils.getBoolean((Element) dialogOpts, "debug");
        }
        return Boolean.FALSE;
    }

    /*
     * Retrieve GLOBALUIOPTS Properties
     */
    // Reads the DialogType from the parsed XML file. 
    public DialogType getDialogType() {
        if (XMLUtils.getStr((Element) globalUIOpts, "dialogtype") != null) {
            return DialogType.valueOf(XMLUtils.getStr((Element) globalUIOpts, "dialogtype").toUpperCase());
        }
        return DialogType.QUESTIONARYCONS;
    }

    // Reads the DialogStrategy from the parsed XML file. 
    public DialogStrategy getDialogStrategy() {
        if (XMLUtils.getStr((Element) globalUIOpts, "dialogstrategy") != null) {
            return DialogStrategy.valueOf(XMLUtils.getStr((Element) globalUIOpts, "dialogstrategy").toUpperCase());
        }
        return DialogStrategy.NEXTFORM;
    }

    public enum IndicationRepresentation {

        HIDE, GREY
    }

    // get definition what to do with contra indicated objects of the knowledge
    // base, e.g. hide or show greyed
    public IndicationRepresentation getShowContraIndicated() {
        if (XMLUtils.getStr((Element) globalUIOpts, "showcontraindicated") != null) {

            String state = XMLUtils.getStr((Element) globalUIOpts, "showcontraindicated");
            try {
                return IndicationRepresentation.valueOf(state);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                return IndicationRepresentation.HIDE;
            }

        }
        return IndicationRepresentation.HIDE;
    }

    // get definition what to do with non indicated objects of the knowledge
    // base, e.g. hide or show greyed
    public IndicationRepresentation getShowNonIndicated() {
        if (XMLUtils.getStr((Element) globalUIOpts, "shownonindicated") != null) {
            String state = XMLUtils.getStr((Element) globalUIOpts, "shownonindicated");
            try {
                return IndicationRepresentation.valueOf(state);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                return IndicationRepresentation.HIDE;
            }
        }
        return IndicationRepresentation.HIDE;
    }

    // Reads the CSS attribute from the parsed XML file.
    public String getCss() {

        if (XMLUtils.getStr((Element) globalUIOpts, "css") != null) {
            return XMLUtils.getStr((Element) globalUIOpts, "css");
        }
        return "";
    }

    // get the specified (global) number of colums for displaying the UI, ie.
    // the number of columns of the dialog
    public int getDialogColumns() {
        if (XMLUtils.getInt((Element) globalUIOpts, "dialogcolumns") != null) {
            return XMLUtils.getInt((Element) globalUIOpts, "dialogcolumns");
        }
        return 1;
    }

    //  get the (global) number of columns for displaiyng  questionnaires, i.e.
    // how many colsQuestions are displayed next to each other in one dialog line
    public int getQuestionnaireColumns() {
        if (XMLUtils.getInt((Element) globalUIOpts, "questionnairecolumns") != null) {
            return XMLUtils.getInt((Element) globalUIOpts, "questionnairecolumns");
        }

        return 1;
    }

    // get the (global) number of columns for displaying within colsQuestions
    public int getQuestionColumns() {
        if (XMLUtils.getInt((Element) globalUIOpts, "questioncolumns") != null) {
            return XMLUtils.getInt((Element) globalUIOpts, "questioncolumns");
        }
        return 1;
    }

    public Boolean getUnknownVisible() {
        if (XMLUtils.getBoolean((Element) globalUIOpts, "unknownvisible") != null) {
            return XMLUtils.getBoolean((Element) globalUIOpts, "unknownvisible");
        }
        return Boolean.FALSE;
    }

    public Boolean getDropdown() {
        if (XMLUtils.getBoolean((Element) globalUIOpts, "dropdown") != null) {
            return XMLUtils.getBoolean((Element) globalUIOpts, "dropdown");
        }
        return Boolean.FALSE;
    }

    public Boolean getLargetext() {
        if (XMLUtils.getBoolean((Element) globalUIOpts, "largetext") != null) {
            return XMLUtils.getBoolean((Element) globalUIOpts, "largetext");
        }
        return Boolean.FALSE;
    }

    // TODO: maybe remove all enums from here to UISettings class!
    // defining the possible solution explanation types
    public enum SolutionExplanationType {

        NONE, TREEMAP, TABLE, CLARI, TEXTUAL, SOLGRAPH
    }

    // return the solution explanation type to be used
    public SolutionExplanationType getSolutionExplanationType() {
        if (XMLUtils.getStr((Element) globalUIOpts, "solutionExplanation") != null) {
            String solType = XMLUtils.getStr((Element) globalUIOpts, "solutionExplanation");
            try {
                return SolutionExplanationType.valueOf(solType);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                return SolutionExplanationType.NONE;
            }
        }
        return SolutionExplanationType.NONE;
    }

    public enum SolutionDepth {

        ALL, ESTABLISHED, SUGGESTED, EXCLUDED
    }
// return the solution depth to be used in solution panel

    public List getSolutionDepths() {
        if (XMLUtils.getStr((Element) globalUIOpts, "solutionDepth") != null) {
            String depths = XMLUtils.getStr((Element) globalUIOpts, "solutionDepth");
            String[] solDepths = null;
            List depthsFinal = new ArrayList();

            //current = SolutionDepth.valueOf(depths);
            //System.out.println(depths);
            if (!depths.equals("") && depths.contains(";;;")) {
                solDepths = depths.split(";;;");
                depthsFinal = Arrays.asList(solDepths);
                //System.out.println(Arrays.toString(solDepths));
            } else {
                depthsFinal.add(depths);
            }
            return depthsFinal;
        }

        List dFinal = new ArrayList();
        dFinal.add(SolutionDepth.ALL.toString());
        return dFinal;
    }

    public enum SolutionSorting {

        CATEGORICAL, ALPHABETICAL, CATEGALPHA
    }
// return the solution depth to be used in solution panel

    public SolutionSorting getSolutionSorting() {
        if (XMLUtils.getStr((Element) globalUIOpts, "solutionSorting") != null) {
            String sorting = XMLUtils.getStr((Element) globalUIOpts, "solutionSorting");

            try {
                return SolutionSorting.valueOf(sorting);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                return SolutionSorting.CATEGORICAL;
            }
        }
        return SolutionSorting.CATEGORICAL;
    }

    // get flag, whether dialog should include a questionnaire navigation sidepanel
    public Boolean getQuestionnaireNavi() {
        if (XMLUtils.getBoolean((Element) globalUIOpts, "questionnaireNavi") != null) {
            return XMLUtils.getBoolean((Element) globalUIOpts, "questionnaireNavi");
        }
        return Boolean.FALSE;
    }

    // get flag, whether dialog should include a solution navigation sidepanel
    public Boolean getSolutionNavi() {
        if (XMLUtils.getBoolean((Element) globalUIOpts, "solutionNavi") != null) {
            return XMLUtils.getBoolean((Element) globalUIOpts, "solutionNavi");
        }
        return Boolean.FALSE;
    }

    // get flag, whether yn colsQuestions are to be displayed horizontally by default
    public Boolean getYNFlatGlobal() {
        if (XMLUtils.getBoolean((Element) globalUIOpts, "ynFlat") != null) {
            return XMLUtils.getBoolean((Element) globalUIOpts, "ynFlat");
        }
        return Boolean.FALSE;
    }

    // get global definition for autocolumns. Per default return no autocolumning
    public String getAutocolumnsGlobal() {
        if (XMLUtils.getStr((Element) globalUIOpts, "autocolumns") != null) {
            return XMLUtils.getStr((Element) globalUIOpts, "autocolumns");
        }
        return "";
    }

    /*
     * Retrieve LOCALUIOPTS Properties
     */
    // get all colsQuestions (in a Hashmap) that have a number of question columns
    public HashMap<String, Integer> getNrColumnsQuestions() {
        HashMap colsQuestions = new HashMap<String, ArrayList>();
        if (locQuestioncolumns != null) {

            // get all group subchilds
            NodeList locChildren = locQuestioncolumns.getChildNodes();

            if (locChildren.getLength() > 0) {
                for (int i = 0; i < locChildren.getLength(); i++) {
                    Node locChild = locChildren.item(i);
                    String gName = locChild.getNodeName();
                    if (!gName.startsWith("#")) {

                        String gQuestions = ""; //default
                        if (XMLUtils.getStr((Element) locChild, "questions") != null) {
                            gQuestions = XMLUtils.getStr((Element) locChild, "questions");
                        }
                        String[] gQuestionsSplit;
                        List<String> questions = null;
                        if (!gQuestions.equals("")) {
                            gQuestionsSplit = gQuestions.split(";;;");
                            questions = Arrays.asList(gQuestionsSplit);
                        }
                        Integer cols = 0;
                        if (gName.equals("one")) {
                            cols = 1;
                        } else if (gName.equals("two")) {
                            cols = 2;
                        } else if (gName.equals("three")) {
                            cols = 3;
                        } else if (gName.equals("four")) {
                            cols = 4;
                        }

                        if (questions != null) {
                            for (String s : questions) {
                                colsQuestions.put(s, cols);
                            }
                        }
                    }
                }
            }
        }
        return colsQuestions;
    }

    // get all colsQuestions (in a Hashmap) that have a dropdown option specified
    public HashMap<String, Boolean> getDropdownQuestions() {
        HashMap questions = new HashMap<String, Boolean>();
        if (locDropdown != null) {
            if (XMLUtils.getStr((Element) locDropdown, "TRUE") != null) {
                String qids = XMLUtils.getStr((Element) locDropdown, "TRUE");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, Boolean.TRUE);
                }
            }
            if (XMLUtils.getStr((Element) locDropdown, "FALSE") != null) {
                String qids = XMLUtils.getStr((Element) locDropdown, "FALSE");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, Boolean.FALSE);
                }
            }
        }
        return questions;
    }

    // get all colsQuestions (in a Hashmap) that have unknown visible option specified
    public HashMap<String, Boolean> getUnknownVisibleQuestions() {
        HashMap questions = new HashMap<String, Boolean>();
        if (locUnknownvisible != null) {
            if (XMLUtils.getStr((Element) locUnknownvisible, "TRUE") != null) {
                String qids = XMLUtils.getStr((Element) locUnknownvisible, "TRUE");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, Boolean.TRUE);
                }
            }
            if (XMLUtils.getStr((Element) locUnknownvisible, "FALSE") != null) {
                String qids = XMLUtils.getStr((Element) locUnknownvisible, "FALSE");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, Boolean.FALSE);
                }
            }
        }
        return questions;
    }

    // TODO: implement with templates and UI stuff
    // get all colsQuestions (in a Hashmap) that have unknown visible option specified
    public HashMap<String, Boolean> getOverlayQuestions() {
        HashMap questions = new HashMap<String, Boolean>();
        if (locOverlay != null) {
            if (XMLUtils.getStr((Element) locOverlay, "TRUE") != null) {
                String qids = XMLUtils.getStr((Element) locOverlay, "TRUE");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, Boolean.TRUE);
                }
            }
            if (XMLUtils.getStr((Element) locOverlay, "FALSE") != null) {
                String qids = XMLUtils.getStr((Element) locOverlay, "FALSE");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, Boolean.FALSE);
                }
            }
        }
        return questions;
    }

    // TODO: implement with templates and UI stuff
    // get all colsQuestions (in a Hashmap) that have unknown visible option specified
    public HashMap<String, Boolean> getLargeTextEntryQuestions() {
        HashMap questions = new HashMap<String, Boolean>();
        if (locLargetext != null) {
            if (XMLUtils.getStr((Element) locLargetext, "TRUE") != null) {
                String qids = XMLUtils.getStr((Element) locLargetext, "TRUE");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, Boolean.TRUE);
                }
            }
            if (XMLUtils.getStr((Element) locLargetext, "FALSE") != null) {
                String qids = XMLUtils.getStr((Element) locLargetext, "FALSE");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, Boolean.FALSE);
                }
            }
        }
        return questions;
    }

    // TODO: implement with templates and UI stuff
    // get all colsQuestions (in a Hashmap) that have unknown visible option specified
    public HashMap<String, String> getAutocolumnsQuestions() {
        HashMap autocols = new HashMap<String, ArrayList>();
        if (locAutocolumns != null) {

            // get all group subchilds
            NodeList locChildren = locAutocolumns.getChildNodes();

            if (locChildren.getLength() > 0) {
                for (int i = 0; i < locChildren.getLength(); i++) {
                    Node locChild = locChildren.item(i);
                    String acName = locChild.getNodeName();
                    if (!acName.startsWith("#")) {

                        String acQuestions = ""; //default
                        if (XMLUtils.getStr((Element) locChild, "questions") != null) {
                            acQuestions = XMLUtils.getStr((Element) locChild, "questions");
                        }
                        String[] questionsSplit;
                        List<String> questions = null;
                        if (!acQuestions.equals("")) {
                            questionsSplit = acQuestions.split(";;;");
                            questions = Arrays.asList(questionsSplit);
                        }
                        if (questions != null && questions.size() > 0) {
                            for (String qString : questions) {
                                autocols.put(qString, acName);
                            }
                        }
                    }
                }
            }
        }
        return autocols;
    }

    // get all colsQuestions (in a Hashmap) that have grouping option specified
    public HashMap<String, ArrayList> getGroupedQuestions() {
        HashMap groupQuestions = new HashMap<String, ArrayList>();
        if (locGroups != null) {

            // get all group subchilds
            NodeList locChildren = locGroups.getChildNodes();

            if (locChildren.getLength() > 0) {
                for (int i = 0; i < locChildren.getLength(); i++) {
                    Node locChild = locChildren.item(i);
                    String gName = locChild.getNodeName();
                    if (!gName.startsWith("#")) {

                        String gQuestions = ""; //default
                        if (XMLUtils.getStr((Element) locChild, "questions") != null) {
                            gQuestions = XMLUtils.getStr((Element) locChild, "questions");
                        }
                        String[] gQuestionsSplit;
                        List questions = null;
                        if (!gQuestions.equals("")) {
                            gQuestionsSplit = gQuestions.split(";;;");
                            questions = Arrays.asList(gQuestionsSplit);
                        }
                        if (questions != null) {
                            groupQuestions.put(gName, questions);
                        }
                    }
                }
            }
        }
        return groupQuestions;
    }

    /*
     * Retrieve DATAOPTS Properties
     */
    // Return the knowledgebase as specified per its filename in the parsed XML
    public KnowledgeBase getKnowledgeBase() throws IOException {
        String kbname = getKnowledgeBaseName();
        if (!kbname.equals("")) {
            return D3webUtils.getKnowledgeBase(kbname);
        }
        return null;
    }

    // Retrieve the name of the knowledge base specified in the parsed XML.
    public String getKnowledgeBaseName() {
        if (XMLUtils.getStr((Element) dataOpts, "kb") != null) {
            return XMLUtils.getStr((Element) dataOpts, "kb");
        }
        return "";
    }

    /*
     * Retrieve UEOPTS Properties
     */
    // get the questionnaire to be used
    public D3webUESettings.UEQ getUEQuestionnaire() {
        if (XMLUtils.getStr((Element) ueOpts, "questionnaire") != null) {
            return D3webUESettings.UEQ.valueOf(
                    XMLUtils.getStr((Element) ueOpts, "questionnaire").toUpperCase());
        }
        return D3webUESettings.UEQ.NONE;
    }

    // get flag, whether this is a usability study (then automatically, some 
    // mechanisms are activated, such as logging)
    public boolean getStudy() {
        if (XMLUtils.getBoolean((Element) ueOpts, "study") != null) {
            return XMLUtils.getBoolean((Element) ueOpts, "study");
        }
        return Boolean.FALSE;
    }

    // get flag, whether logging should be used for this ProKEt artifact
    public Boolean getLogging() {
        if (XMLUtils.getBoolean((Element) ueOpts, "logging") != null) {
            return XMLUtils.getBoolean((Element) ueOpts, "logging");
        }
        return Boolean.FALSE;
    }

    // get flag, whether feedbackform should be integrated for this artifact
    public Boolean getFeedbackform() {
        if (XMLUtils.getBoolean((Element) ueOpts, "feedbackform") != null) {
            return XMLUtils.getBoolean((Element) ueOpts, "feedbackform");
        }
        return Boolean.FALSE;
    }

    // get a group definition String if usability study is performed with
    // multiple colsQuestions that use different artifacts
    public String getUEGroup() {
        if (XMLUtils.getStr((Element) ueOpts, "uegroup") != null) {
            return XMLUtils.getStr((Element) ueOpts, "uegroup");
        }
        return "";
    }

    // get the path, where logfiles should be stored during the study
    public String getLogfilePath() {
        if (XMLUtils.getStr((Element) ueOpts, "logfilepath") != null) {
            return XMLUtils.getStr((Element) ueOpts, "logfilepath");
        }
        return "";
    }

    // get the path, where ProKEt usability analysis should be stored
    public String getAnalysisOutputPath() {
        if (XMLUtils.getStr((Element) ueOpts, "analysisoutputpath") != null) {
            return XMLUtils.getStr((Element) ueOpts, "analysisoutputpath");
        }
        return "";
    }

    public static void main(String[] args) {
        D3webXMLParser parser = new D3webXMLParser("newXMLSpec.xml");
        parser.parse();

        System.out.println("DIALOG OPTS:");
        System.out.println("header: \t\t\t" + parser.getHeader());
        System.out.println("login mode: \t\t\t" + parser.getLoginMode().toString());
        System.out.println("required: \t\t\t" + parser.getRequired());
        System.out.println("language: \t\t\t" + parser.getLanguage());
        System.out.println("debug mode: \t\t\t" + parser.getDebug());

        System.out.println("- - -");
        System.out.println("GLOBAL UI OPTS:");
        System.out.println("dialog type: \t\t\t" + parser.getDialogType());
        System.out.println("dialog strategy: \t\t" + parser.getDialogStrategy());
        System.out.println("showcontraindicated: \t\t" + parser.getShowContraIndicated());
        System.out.println("shownonindicated: \t\t" + parser.getShowNonIndicated());
        System.out.println("css: \t\t\t\t" + parser.getCss());
        System.out.println("dialogcolumns: \t\t\t" + parser.getDialogColumns());
        System.out.println("questionnairecolumns: \t\t" + parser.getQuestionnaireColumns());
        System.out.println("questioncolumns: \t\t" + parser.getQuestionColumns());
        System.out.println("unknownvisible: \t\t" + parser.getUnknownVisible());
        System.out.println("solutionExplanantion: \t\t" + parser.getSolutionExplanationType());
        System.out.println("solutionSorting: \t\t" + parser.getSolutionSorting());
        System.out.println("solutionDepth: \t\t\t" + parser.getSolutionDepths());
        System.out.println("questionnaireNavi: \t\t" + parser.getQuestionnaireNavi());
        System.out.println("solutionNavi: \t\t\t" + parser.getSolutionNavi());
        System.out.println("ynFlat: \t\t\t" + parser.getYNFlatGlobal());
        System.out.println("autocolumns: \t\t\t" + parser.getAutocolumnsGlobal());

        System.out.println("- - -");
        System.out.println("LOCAL UI OPTS:");
        System.out.println("unknownvisible questions: \t" + parser.getUnknownVisibleQuestions().toString());
        System.out.println("dropdown questions: \t\t" + parser.getDropdownQuestions().toString());
        System.out.println("nrcolumns questions: \t\t" + parser.getNrColumnsQuestions());
        System.out.println("overlay questions: \t\t" + parser.getOverlayQuestions());
        System.out.println("largetext questions: \t\t" + parser.getLargeTextEntryQuestions());
        System.out.println("autocolumn questions: \t\t" + parser.getAutocolumnsQuestions());
        System.out.println("groups questions: \t\t" + parser.getGroupedQuestions());

    }
}
