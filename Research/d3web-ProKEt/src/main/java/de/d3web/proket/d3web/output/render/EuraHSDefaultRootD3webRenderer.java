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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.d3web.utils.FileNameComparator;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

/**
 * Basic Renderer Class for d3web-based dialogs. Defines the basic rendering of
 * d3web dialogs and methods, required by all rendering sub-classes.
 *
 *
 * @author Martina Freiberg, Albrecht Striffler @created 13.01.2011
 */
public class EuraHSDefaultRootD3webRenderer extends DefaultRootD3webRenderer {

    private static final String COLOR_OK = "green";
    private static final String COLOR_LATE = "red";
    private static final long DAY = 1000 * 60 * 60 * 24;
    private static final long WEEK = DAY * 7;
    private static final long SIX_WEEKS = WEEK * 6;
    private static final long YEAR = DAY * 365;
    private static final long YEARS2 = DAY * 365 * 2;
    private static final SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat DD_MM_YYYY_HH_MM = new SimpleDateFormat(
            "dd.MM.yyyy HH:mm");
    private static final String OPERATION_DATE_QUESTION_NAME = "Operation date";
    private static final String FOLLOW_UP1_NAME_SUFFIX = "Follow up (6 weeks)"; //"(Follow up 1)";
    private static final String FOLLOW_UP2_NAME_SUFFIX = "Follow up (12 month)"; //"(Follow up 2)";
    private static final String FOLLOW_UP3_NAME_SUFFIX = "Follow up (24 month)"; //"(Follow up 3)";
    private static final String OPERATION_DATE = "operationDate";
    private static final String OPERATION_DATE_ANSWERED = "operationDateAnswered";
    private static final String LAST_CASE_CHANGE = "lastCaseChange";
    private static final String LAST_FILE_CHANGE = "lastFileChange";
    private static final String FOLLOW_UP1_DONE = "followUp1Done";
    private static final String FOLLOW_UP2_DONE = "followUp2Done";
    private static final String FOLLOW_UP3_DONE = "followUp3Done";
    private static final double PERCENTAGE_ANSWERED_QUESTIONS_NEEDED = 0.5;

    // just to be sure to use the correct terminology each time
    private static enum FollowUp {

        FIRST, SECOND, THIRD
    }
    private static Map<String, Map<String, Object>> caseCache 
            = new HashMap<String, Map<String, Object>>();

    @Override
    public void setDialogSpecificAttributes(HttpSession httpSession, StringTemplate st, HttpServletRequest request) {
        // ONLY FOR HERNIA, 3 custom buttons
        st.setAttribute("summary", true);
        st.setAttribute("statistics", true);
        st.setAttribute("followupbutton", true);

        String ehsintro = request.getParameter("ehsintro");
        Object ehsHttp = httpSession.getAttribute("ehsintro");
        // Maybe better pull out, handle button click per JS and call a goToStatistics Ajax etc

        if (ehsHttp != null && ehsHttp.toString().equals("done")) {
            st.setAttribute("eurahsmiddle", "true");
            st.removeAttribute("eurahsintro");
        } else // when coming from login, there is nothing like ehsintro set
        if (ehsintro == null || ehsintro.equals("")
                || ehsHttp == null || ehsHttp.toString().equals("")) {
            st.setAttribute("eurahsintro", true);
            st.removeAttribute("eurahsmiddle");
            httpSession.setAttribute("ehsintro", "done");
        }

        // TODO: was only in for testing. Maybe make configuration in specs
        // st.setAttribute("eurahsmiddle", "true");
        // st.removeAttribute("eurahsintro");

        if (httpSession.getAttribute("level1qs") == null) {
            httpSession.setAttribute("level1qs", parseLevel1Questions(httpSession));
        }
        // get the popup for the follow up table dialog window
        String opts = renderFollowUpTable((String) httpSession.getAttribute("user"));
        st.setAttribute("followupdialog", opts);

    }

    /**
     * Render the window with the Follow Up Table
     *
     * @param user the given user (needed to optain the cases)
     * @return the String Representation of the Follow Up Table
     */
    private String renderFollowUpTable(String user) {

        // get the cases of this user
        //List<File> caseFiles = PersistenceD3webUtils.getCaseListNumbered(user);
        List<File> caseFiles = PersistenceD3webUtils.getCaseList(user);
        StringBuilder followUpTable = new StringBuilder();

        // if this user has some stored cases go through each one
        if (caseFiles != null && caseFiles.size() > 0) {

            // basic table framing
            followUpTable.append("<table style='border-spacing: 0px' border='1'>");

            renderTableHeader(followUpTable); // insert the table header

            Collections.sort(caseFiles); // sort the user's cases

            for (File caseFile : caseFiles) {

                // for all cases except the autocase file
                if (!caseFile.getName().startsWith(PersistenceD3webUtils.AUTOSAVE)
                        && user != null) {

                    // render one row in the follow up table
                    renderRow(user, caseFile, followUpTable);
                }
            }
            followUpTable.append("</table>"); // basic table framing
        }
        return followUpTable.toString();
    }

    /**
     * Render the header of the Follow Up Table (de facto one -- the first --
     * row of that table.
     *
     * @param followUpTable the StringBuilder that contains already the table
     * framing
     */
    private void renderTableHeader(StringBuilder followUpTable) {
        followUpTable.append("<tr>");
        renderHeaderCell("Case Name", followUpTable);
        renderHeaderCell("Last Modified", followUpTable);
        renderHeaderCell("Date of Surgery", followUpTable);
        renderHeaderCell("Follow Up (6 weeks)", followUpTable);
        renderHeaderCell("Follow Up (12 month)", followUpTable);
        renderHeaderCell("Follow Up (24 month)", followUpTable);
        followUpTable.append("</tr>");
    }

    /**
     * Render one Table Header Cell
     *
     * @param content the content to be displayed in the cell
     * @param followUpTable The StringBuilder containing the entire table
     */
    private void renderHeaderCell(String content, StringBuilder followUpTable) {
        followUpTable.append("<th>" + content + "</th>");
    }

    /**
     * Render one row of the Follow Up Table
     *
     * @param user the respective user
     * @param caseFile the case file represented by that row
     * @param followUpTable The StringBuilder containing the entire table
     */
    private void renderRow(String user, File caseFile, StringBuilder followUpTable) {

        // first parse the relevant attributes of the case into a map
        Map<String, Object> parsedCase = parseCase(user, caseFile);

        followUpTable.append("<tr>"); // open the row

        renderFileNameCell(caseFile, followUpTable); // render file name
        renderLastModifiedCell(caseFile, followUpTable); // render modified date
        renderOperationDateCell(parsedCase, followUpTable); // render op date

        // render the notification cells for each of the (currently 3) existing
        // follow ups
        renderFollowUpCell(parsedCase, FollowUp.FIRST, followUpTable);
        renderFollowUpCell(parsedCase, FollowUp.SECOND, followUpTable);
        renderFollowUpCell(parsedCase, FollowUp.THIRD, followUpTable);

        followUpTable.append("</tr>"); // close the row
    }

    /**
     * Render the filename of a case into the first column of the table
     * @param caseFile the respective vase
     * @param followUpTable The StringBuilder containing the entire FU table
     */
    private void renderFileNameCell(File caseFile, StringBuilder followUpTable) {
        // cut the file ending from the filename
        String filename = 
                caseFile.getName()
                    .substring(0, caseFile.getName().lastIndexOf("."));
        
        // and render the cell
        renderCell(filename, followUpTable);
    }

    /**
     * Render the last modified date of the case into a cell
     * @param caseFile the respective case
     * @param followUpTable The StringBuilder containing the entire FU table
     */
    private void renderLastModifiedCell(File caseFile, StringBuilder followUpTable) {
        
        // get last modified date and format it
        Date lastModified = new Date(caseFile.lastModified());
        String lastModifiedFormatted = DD_MM_YYYY_HH_MM.format(lastModified) + " h";
        renderCell(lastModifiedFormatted, followUpTable); // render it into cell
    }

    /**
     * Render a cell with the operation date
     * @param parsedCase the map containing all relevant data of the case
     * @param followUpTable The StringBuilder containing the entire FU table
     */ 
    private void renderOperationDateCell(Map<String, Object> parsedCase, StringBuilder followUpTable) {
        
        // get the operation date
        boolean operationDateAnswered = 
                (Boolean) parsedCase.get(OPERATION_DATE_ANSWERED);
        if (!operationDateAnswered) { // of no op date: "no date found" is rendered
            renderCell("No date found", followUpTable);
        } else {
            // otherwise get and render the operation date
            Date operationDate = (Date) parsedCase.get(OPERATION_DATE);
            String operationDateFormatted = DD_MM_YYYY.format(operationDate);
            renderCell(operationDateFormatted, followUpTable);
        }
    }

    /**
     * Render a follow up cell
     * @param parsedCase the respective case
     * @param followUp an enum value indicating if we have the 1., 2., or 3. FU
     * @param followUpTable The StringBuilder containing the entire FU table
     */
    private void renderFollowUpCell(Map<String, Object> parsedCase,
            FollowUp followUp, StringBuilder followUpTable) {

        // if no operation date was answered, we can't calculate any follow ups...
        boolean operationDateAnswered = 
                (Boolean) parsedCase.get(OPERATION_DATE_ANSWERED);
        if (!operationDateAnswered) {
            // if no op date, render "Unknown"
            renderCell("Unknown", followUpTable);
        } else {
            // else, get the respective FU value from the case properties map
            boolean followUpDone = false;
            if (followUp == FollowUp.FIRST) {
                followUpDone = (Boolean) parsedCase.get(FOLLOW_UP1_DONE);
            } else if (followUp == FollowUp.SECOND) {
                followUpDone = (Boolean) parsedCase.get(FOLLOW_UP2_DONE);
            } else if (followUp == FollowUp.THIRD) {
                followUpDone = (Boolean) parsedCase.get(FOLLOW_UP3_DONE);
            }

            //boolean followUpDone = (Boolean) parsedCase.get(followUp == FollowUp.FIRST
            //      ? FOLLOW_UP1_DONE
            //    : FOLLOW_UP2_DONE);

            if (followUpDone) { // if done successfully
                renderColoredCell("Done", COLOR_OK, followUpTable);
            } else {
                // otherwise, calculate due date by getting the op date and
                // adding a timespan according to the FU number
                Date operationDate = (Date) parsedCase.get(OPERATION_DATE);
                long time = operationDate.getTime();
                long add = 0;

                if (followUp == FollowUp.FIRST) {
                    add = SIX_WEEKS;
                } else if (followUp == FollowUp.SECOND) {
                    add = YEAR;
                } else if (followUp == FollowUp.THIRD) {
                    add = YEARS2;
                }

                Date followUpDueDate = new Date(time + add);
                Date now = new Date();
                String followUpDueFormatted = 
                        DD_MM_YYYY.format(followUpDueDate);
                
                // render a notification acordingly: if due is in future still,
                // render with OK color, if due was already in the past, render
                // with alert color
                if (followUpDueDate.after(now)) {
                    renderColoredCell("Due " + followUpDueFormatted, COLOR_OK, followUpTable);
                } else {
                    renderColoredCell("Was due " + followUpDueFormatted, COLOR_LATE, followUpTable);
                }
            }
        }
    }

    /**
     * Rendering a cell with specifically colored content
     * @param content the content
     * @param color the color string
     * @param followUpTable The StringBuilder containing the entire FU table
     */
    private void renderColoredCell(String content, String color, StringBuilder followUpTable) {
        renderCell(getColoredText(content, color), followUpTable);
    }

    /**
     * Helper method for above renderedColoredCell method: returns a span
     * containing text with specific color style attribute
     * @param text the text to display
     * @param colorCode the color
     * @return the String representation of the colored-text span
     */
    private String getColoredText(String text, String colorCode) {
        return "<span style='color:" + colorCode + "'>" + text + "</span>";
    }

    /**
     * Render a table cell with given content
     * @param content the content for the cell
     * @param followUpTable the StringBuilder containing the entire table
     */
    private void renderCell(String content, StringBuilder followUpTable) {
        followUpTable.append("<td style='padding:3px'>" + content + "</td>");
    }

    /**
     * Parse a d3web case file (xml format) into a map, which contains certain 
     * parameters/properties needed later on for displaying the FollowUp Table.
     * @param user the respective user
     * @param caseFile the respective case File to be parsed
     * @return the Map representation of the parameters
     */
    private Map<String, Object> parseCase(String user, File caseFile) {
        // if exists, get the map for the respective case from the all-cases-
        // parameters-map-cache
        Map<String, Object> parameters = 
                caseCache.get(caseFile.getPath());
        
        // if there exists a map, and if the last modification is untouched
        // since the last modification, just return this map
        if (parameters != null) {
            long lastLastFileChange = (Long) parameters.get(LAST_FILE_CHANGE);
            long lastFileChange = caseFile.lastModified();
            if (lastLastFileChange == lastFileChange) {
                return parameters;
            }
        }
        // if the requested map does not yet exist
        if (parameters == null) {
            // create a new one and put it into the cache
            parameters = new HashMap<String, Object>();
            caseCache.put(caseFile.getPath(), parameters);
        }
        
        // add the parameters last file change, last case change, FU parameters.
        // op date etc.
        parameters.put(LAST_FILE_CHANGE, caseFile.lastModified());
        Session loadedUserCase = PersistenceD3webUtils.loadUserCaseUtil(user, caseFile);
        parameters.put(LAST_CASE_CHANGE, loadedUserCase.getLastChangeDate().getTime());
        parseFollowUpParameters(parameters, loadedUserCase);
        parseOperationDate(parameters, loadedUserCase);

        // return the map for the requested case
        return parameters;

    }

    
    private void parseOperationDate(Map<String, Object> parameters, Session loadedUserCase) {
        Question operationDateQuestion = loadedUserCase.getKnowledgeBase().getManager().searchQuestion(
                OPERATION_DATE_QUESTION_NAME);
        Date operationDate = null;
        boolean operationDateAnswered = false;
        if (operationDateQuestion == null) {
            Logger.getLogger(this.getClass().getSimpleName()).warning(
                    "Question '" + OPERATION_DATE_QUESTION_NAME
                    + "' expected but not found.");
        } else if (!(operationDateQuestion instanceof QuestionDate)) {
            Logger.getLogger(this.getClass().getSimpleName()).warning(
                    "Question '" + OPERATION_DATE_QUESTION_NAME
                    + "' is expected to be of the Type '"
                    + QuestionDate.class.getSimpleName()
                    + "' but was '" + operationDateQuestion.getClass().getSimpleName()
                    + "'.");
        } else {
            Value operationDateValue = loadedUserCase.getBlackboard().getValue(
                    operationDateQuestion);
            if (UndefinedValue.isNotUndefinedValue(operationDateValue)
                    && !(operationDateValue instanceof Unknown)) {
                operationDate = (Date) operationDateValue.getValue();
                parameters.put(OPERATION_DATE, operationDate);
                operationDateAnswered = true;
            }
        }
        parameters.put(OPERATION_DATE_ANSWERED, operationDateAnswered);
    }

    private void parseFollowUpParameters(Map<String, Object> parameters, Session loadedUserCase) {

        List<Question> allQuestions = loadedUserCase.getKnowledgeBase().getManager().getQuestions();
        List<Question> allFollowUp1Questions = new LinkedList<Question>();
        List<Question> allFollowUp2Questions = new LinkedList<Question>();
        List<Question> allFollowUp3Questions = new LinkedList<Question>();

        getFollowUpQuestions(allQuestions,
                allFollowUp1Questions, allFollowUp2Questions, allFollowUp3Questions);

        List<Question> allIndicatedFollowUp1Questions =
                getIndicatedOrTopLevelQuestions(allFollowUp1Questions, loadedUserCase);
        List<Question> allIndicatedFollowUp2Questions =
                getIndicatedOrTopLevelQuestions(allFollowUp2Questions, loadedUserCase);
        List<Question> allIndicatedFollowUp3Questions =
                getIndicatedOrTopLevelQuestions(allFollowUp3Questions, loadedUserCase);

        List<Question> allAnsweredQuestions = loadedUserCase.getBlackboard().getAnsweredQuestions();
        List<Question> allAnsweredFollowUp1Questions = new LinkedList<Question>();
        List<Question> allAnsweredFollowUp2Questions = new LinkedList<Question>();
        List<Question> allAnsweredFollowUp3Questions = new LinkedList<Question>();

        getFollowUpQuestions(allAnsweredQuestions, allAnsweredFollowUp1Questions,
                allAnsweredFollowUp2Questions, allAnsweredFollowUp3Questions);

        parameters.put(FOLLOW_UP1_DONE,
                isFollowUpDone(allAnsweredFollowUp1Questions, allIndicatedFollowUp1Questions));

        parameters.put(FOLLOW_UP2_DONE,
                isFollowUpDone(allAnsweredFollowUp2Questions, allIndicatedFollowUp2Questions));

        parameters.put(FOLLOW_UP3_DONE,
                isFollowUpDone(allAnsweredFollowUp3Questions, allIndicatedFollowUp3Questions));
    }

    /**
     * Checks whether a Follow Up has been done completely - thereby all
     * answered questions are compared to the indicated questions, and the
     * percentage of answered questions is compared to the percentage needed for
     * a "done" follow up PERCENTAGE_ANSWERED_QUESTIONS_NEEDED: currently 0.5
     */
    private boolean isFollowUpDone(List<Question> answeredQuestions, List<Question> allIndicatedQuestions) {

        System.err.println("FU DONE - answeredQuestions: " + answeredQuestions.toString());
        System.err.println("FU DONE - allIndicatedQuestions: " + allIndicatedQuestions.toString());

        double neededNr = allIndicatedQuestions.size() * PERCENTAGE_ANSWERED_QUESTIONS_NEEDED;
        double answeredNr = answeredQuestions.size();

        System.err.println("FU DONE - neededNr: " + neededNr);
        System.err.println("FU DONE - answered: " + answeredNr);

        boolean percentageAnswered = answeredNr >= neededNr;

        System.err.println("FU DONE - percentageAnswered: " + percentageAnswered);

        // answeredQuestions.size() >= allIndicatedQuestions.size() * PERCENTAGE_ANSWERED_QUESTIONS_NEEDED;
        return percentageAnswered && !answeredQuestions.isEmpty();
    }

    /*
     * Checks, which of the given List of questions are actually indicated or
     * are top level questions
     */
    private List<Question> getIndicatedOrTopLevelQuestions(List<Question> questions, Session session) {
        Blackboard blackboard = session.getBlackboard();
        List<Question> indicatedOrTopLevelQuestions = new LinkedList<Question>();
        for (Question question : questions) {
            if (D3webUtils.isIndicatedPlain(question, blackboard) || isTopLevelQuestion(question)) {
                indicatedOrTopLevelQuestions.add(question);
            }
        }
        return indicatedOrTopLevelQuestions;
    }

    private boolean isTopLevelQuestion(Question question) {
        TerminologyObject[] parents = question.getParents();
        if (parents == null || parents.length == 0) {
            return true;
        }
        if (parents.length > 1) {
            return false;
        }
        TerminologyObject[] grandParents = parents[0].getParents();
        if (grandParents == null || grandParents.length == 0) {
            return true;
        }
        if (grandParents.length == 1
                && grandParents[0] == question.getKnowledgeBase().getRootQASet()) {
            return true;
        }
        return false;
    }

    private void getFollowUpQuestions(List<Question> allQuestions,
            List<Question> allFollowUp1Questions,
            List<Question> allFollowUp2Questions,
            List<Question> allFollowUp3Questions) {
        for (Question question : allQuestions) {
            if (question.getName().contains(FOLLOW_UP1_NAME_SUFFIX)) {
                allFollowUp1Questions.add(question);
            } else if (question.getName().contains(FOLLOW_UP2_NAME_SUFFIX)) {
                allFollowUp2Questions.add(question);
            } else if (question.getName().contains(FOLLOW_UP3_NAME_SUFFIX)) {
                allFollowUp3Questions.add(question);
            }
        }
    }

    public static ArrayList<TerminologyObject> parseLevel1Questions(HttpSession http) {

        ArrayList<TerminologyObject> level1qs =
                new ArrayList<TerminologyObject>();

        Session d3webSession = (Session) http.getAttribute("d3webSession");
        TerminologyObject root =
                d3webSession.getKnowledgeBase().getRootQASet();


        parseLevel1Children(http, level1qs, root, d3webSession);
        ArrayList<TerminologyObject> finalA = new ArrayList<TerminologyObject>();

        HashMap<TerminologyObject, List<TerminologyObject>> indicationSet =
                D3webUtils.getIndicationSetsL1(d3webSession.getBlackboard(), level1qs);

        //System.out.println(level1qs);
        for (TerminologyObject to : level1qs) {
            finalA.add(to);

            if (indicationSet.containsKey(to)) {
                for (TerminologyObject ito : indicationSet.get(to)) {

                    if (!finalA.contains(ito)) {
                        finalA.add(ito);
                    }


                }
            }
        }
        // System.out.println(finalA);
        return finalA;
    }

    private static void parseLevel1Children(HttpSession http, ArrayList<TerminologyObject> level1,
            TerminologyObject to, Session d3webSession) {

        if (to.getChildren() != null && to.getChildren().length != 0) {

            for (TerminologyObject child : to.getChildren()) {


                if (D3webUtils.isContraIndicated(child, d3webSession.getBlackboard())
                        || !D3webUtils.isIndicatedByInitQuestionnaire(child, to, d3webSession.getBlackboard())) {
                    continue;
                } else {
                    level1.add(child);
                    // System.out.println(child.getName());
                    parseLevel1FUs(http, level1, child, d3webSession);
                }

            }
        }
    }

    private static void parseLevel1FUs(HttpSession http, ArrayList<TerminologyObject> level1,
            TerminologyObject to, Session d3webSession) {


        if (to.getChildren() != null && to.getChildren().length != 0
                && to.getChildren()[0] instanceof Question) {

            // get the (probably question) children of the child
            for (TerminologyObject childsChild : to.getChildren()) {

                if (D3webUtils.isContraIndicated(childsChild, d3webSession.getBlackboard())) {
                    continue;
                } else {
                    level1.add(childsChild);
                }

                parseLevel1FUs(http, level1, childsChild, d3webSession);
            }
        }
    }
    
    @Override
    public String renderUserCaseList(String user, HttpSession http) {

        Session d3web = (Session) http.getAttribute("d3webSession");
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

            Collections.sort(files, new FileNameComparator());
            //Collections.sort(files, new CaseCreationComparator());
             
    //         int nr = 1;
            String nr = "";
            for (File f : files) {
                if (!f.getName().startsWith(PersistenceD3webUtils.AUTOSAVE)) {
                    cases.append("<option");
                    String filename = 
                            f.getName().substring(0, f.getName().lastIndexOf(".")).replace("+", " ");
                   
                    cases.append(" title='"
                            + nr + filename + "'>");
                    cases.append(nr + filename);
                    cases.append("</option>");
                 //   nr++;
                }
                
            }
        }

        return cases.toString();
    }
}
