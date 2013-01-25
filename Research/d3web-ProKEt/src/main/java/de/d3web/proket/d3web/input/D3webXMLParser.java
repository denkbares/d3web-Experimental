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
import java.util.HashMap;
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

    // get definition what to do with contra indicated objects of the knowledge
    // base, e.g. hide or show greyed
    public String getShowContraIndicated() {
        if (XMLUtils.getStr((Element) globalUIOpts, "showcontraindicated") != null) {
            return XMLUtils.getStr((Element) globalUIOpts, "showcontraindicated");
        }
        return "HIDE";
    }

    // get definition what to do with non indicated objects of the knowledge
    // base, e.g. hide or show greyed
    public String getShowNonIndicated() {
        if (XMLUtils.getStr((Element) globalUIOpts, "shownonindicated") != null) {
            return XMLUtils.getStr((Element) globalUIOpts, "shownonindicated");
        }
        return "HIDE";
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
    // how many questions are displayed next to each other in one dialog line
    public int getQuestionnaireColumns() {
        if (XMLUtils.getInt((Element) globalUIOpts, "questionnairecolumns") != null) {
            return XMLUtils.getInt((Element) globalUIOpts, "questionnairecolumns");
        }

        return 1;
    }

    // get the (global) number of columns for displaying within questions
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

    public String[] getSolutionDepths() {
        if (XMLUtils.getStr((Element) globalUIOpts, "solutionDepth") != null) {
            String depths = XMLUtils.getStr((Element) globalUIOpts, "solutionDepth");
            String[] solDepths = null;

            //current = SolutionDepth.valueOf(depths);
            //System.out.println(depths);
            if (!depths.equals("") && depths.contains(";;;")) {
                solDepths = depths.split(";;;");
                //System.out.println(Arrays.toString(solDepths));
            } else {
                solDepths = new String[1];
                solDepths[0] = depths;
            }
            return solDepths;
        }

        String[] def = new String[]{SolutionDepth.ALL.toString()};
        return def;
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

    /*
     * Retrieve LOCALUIOPTS Properties
     */
    // get all questions (in a Hashmap) that have a number of question columns
    public HashMap<String, Integer> getNrColumnsQuestions() {
        HashMap questions = new HashMap<String, Integer>();
        if (locQuestioncolumns != null) {
            if (XMLUtils.getStr((Element) locQuestioncolumns, "one") != null) {
                String qids = XMLUtils.getStr((Element) locQuestioncolumns, "one");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, 1);
                }
            }
            if (XMLUtils.getStr((Element) locQuestioncolumns, "two") != null) {
                String qids = XMLUtils.getStr((Element) locQuestioncolumns, "two");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, 2);
                }
            }
            if (XMLUtils.getStr((Element) locQuestioncolumns, "three") != null) {
                String qids = XMLUtils.getStr((Element) locQuestioncolumns, "three");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, 3);
                }
            }
        }
        return questions;
    }

    // get all questions (in a Hashmap) that have a dropdown option specified
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

    // get all questions (in a Hashmap) that have unknown visible option specified
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
     // get all questions (in a Hashmap) that have unknown visible option specified
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
     // get all questions (in a Hashmap) that have unknown visible option specified
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
     // get all questions (in a Hashmap) that have unknown visible option specified
    public HashMap<String, String> getAutocolumnsQuestions() {
        HashMap questions = new HashMap<String, String>();
        if (locAutocolumns != null) {
            if (XMLUtils.getStr((Element) locAutocolumns, "T4:::C2") != null) {
                String qids = XMLUtils.getStr((Element) locAutocolumns, "T4:::C2");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, "T4:::C2");
                }
            }
            if (XMLUtils.getStr((Element) locAutocolumns, "T8:::C3") != null) {
                String qids = XMLUtils.getStr((Element) locAutocolumns, "T4:::C2");
                String[] qidsSplit = qids.split(";;;");
                for (String qid : qidsSplit) {
                    questions.put(qid, "T4:::C2");
                }
            }
        }
        return questions;
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
    // multiple groups that use different artifacts
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

        System.out.println("- - -");
        System.out.println("LOCAL UI OPTS:");
        System.out.println("unknownvisible questions: \t" + parser.getUnknownVisibleQuestions().toString());
        System.out.println("dropdown questions: \t\t" + parser.getDropdownQuestions().toString());
        System.out.println("nrcolumns questions: \t\t" + parser.getNrColumnsQuestions());
    }
}
