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
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.data.IndicationMode;
import java.util.*;

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
    private static final SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat DD_MM_YYYY_HH_MM = new SimpleDateFormat(
            "dd.MM.yyyy HH:mm");
    private static final String OPERATION_DATE_QUESTION_NAME = "Operation date";
    private static final String FOLLOW_UP1_NAME_SUFFIX = "(Follow up 1)";
    private static final String FOLLOW_UP2_NAME_SUFFIX = "(Follow up 2)";
    private static final String OPERATION_DATE = "operationDate";
    private static final String OPERATION_DATE_ANSWERED = "operationDateAnswered";
    private static final String LAST_CASE_CHANGE = "lastCaseChange";
    private static final String LAST_FILE_CHANGE = "lastFileChange";
    private static final String FOLLOW_UP1_DONE = "followUp1Done";
    private static final String FOLLOW_UP2_DONE = "followUp2Done";
    private static final double PERCENTAGE_ANSWERED_QUESTIONS_NEEDED = 0.5;

    private static enum FollowUp {

        FIRST, SECOND
    }
    private static Map<String, Map<String, Object>> caseCache = new HashMap<String, Map<String, Object>>();

    @Override
    public void setDialogSpecificAttributes(HttpSession httpSession, StringTemplate st) {
        // ONLY FOR HERNIA, 3 custom buttons
        st.setAttribute("summary", true);
        st.setAttribute("statistics", true);
        st.setAttribute("followupbutton", true);

        if (httpSession.getAttribute("level1qs") == null) {
            httpSession.setAttribute("level1qs", parseLevel1Questions(httpSession));
        }
        String opts = renderFollowUpTable((String) httpSession.getAttribute("user"));
        st.setAttribute("followupdialog", opts);

    }

    private String renderFollowUpTable(String user) {

        List<File> caseFiles = PersistenceD3webUtils.getCaseList(user);
        StringBuilder followUpTable = new StringBuilder();
        if (caseFiles != null && caseFiles.size() > 0) {

            followUpTable.append("<table style='border-spacing: 0px' border='1'>");
            renderTableHeader(followUpTable);

            Collections.sort(caseFiles);

            for (File caseFile : caseFiles) {
                if (!caseFile.getName().startsWith(PersistenceD3webUtils.AUTOSAVE)) {
                    renderRow(user, caseFile, followUpTable);
                }
            }
            followUpTable.append("</table>");
        }
        return followUpTable.toString();
    }

    private void renderTableHeader(StringBuilder followUpTable) {
        followUpTable.append("<tr>");
        renderHeaderCell("Case Name", followUpTable);
        renderHeaderCell("Last Modified", followUpTable);
        renderHeaderCell("Date of Surgery", followUpTable);
        renderHeaderCell("Follow-Up 1", followUpTable);
        renderHeaderCell("Follow-Up 2", followUpTable);
        followUpTable.append("</tr>");
    }

    private void renderHeaderCell(String content, StringBuilder followUpTable) {
        followUpTable.append("<th>" + content + "</th>");
    }

    private void renderRow(String user, File caseFile, StringBuilder followUpTable) {
        Map<String, Object> parsedCase = parseCase(user, caseFile);

        followUpTable.append("<tr>");

        renderFileNameCell(caseFile, followUpTable);
        renderLastModifiedCell(caseFile, followUpTable);
        renderOperationDateCell(parsedCase, followUpTable);
        renderFollowUpCell(parsedCase, FollowUp.FIRST, followUpTable);
        renderFollowUpCell(parsedCase, FollowUp.SECOND, followUpTable);

        followUpTable.append("</tr>");
    }

    private void renderFileNameCell(File caseFile, StringBuilder followUpTable) {
        String filename = caseFile.getName().substring(0,
                caseFile.getName().lastIndexOf("."));
        renderCell(filename, followUpTable);
    }

    private void renderLastModifiedCell(File caseFile, StringBuilder followUpTable) {
        Date lastModified = new Date(caseFile.lastModified());
        String lastModifiedFormatted = DD_MM_YYYY_HH_MM.format(lastModified) + " h";
        renderCell(lastModifiedFormatted, followUpTable);
    }

    private void renderOperationDateCell(Map<String, Object> parsedCase, StringBuilder followUpTable) {
        boolean operationDateAnswered = (Boolean) parsedCase.get(OPERATION_DATE_ANSWERED);
        if (!operationDateAnswered) {
            renderCell("No date found", followUpTable);
        } else {
            Date operationDate = (Date) parsedCase.get(OPERATION_DATE);
            String operationDateFormatted = DD_MM_YYYY.format(operationDate);
            renderCell(operationDateFormatted, followUpTable);
        }
    }

    private void renderFollowUpCell(Map<String, Object> parsedCase, FollowUp followUp, StringBuilder followUpTable) {
        boolean operationDateAnswered = (Boolean) parsedCase.get(OPERATION_DATE_ANSWERED);
        if (!operationDateAnswered) {
            renderCell("Unknown", followUpTable);
        } else {
            boolean followUpDone = (Boolean) parsedCase.get(followUp == FollowUp.FIRST
                    ? FOLLOW_UP1_DONE
                    : FOLLOW_UP2_DONE);

            if (followUpDone) {
                renderColoredCell("Done", COLOR_OK, followUpTable);
            } else {
                Date operationDate = (Date) parsedCase.get(OPERATION_DATE);
                long time = operationDate.getTime();
                long add = (followUp == FollowUp.FIRST ? SIX_WEEKS : YEAR);
                Date followUpDueDate = new Date(time + add);
                Date now = new Date();
                String followUpDueFormatted = DD_MM_YYYY.format(followUpDueDate);
                if (followUpDueDate.after(now)) {
                    renderColoredCell("Due " + followUpDueFormatted, COLOR_OK, followUpTable);
                } else {
                    renderColoredCell("Was due " + followUpDueFormatted, COLOR_LATE, followUpTable);
                }
            }
        }
    }

    private void renderColoredCell(String content, String color, StringBuilder followUpTable) {
        renderCell(getColoredText(content, color), followUpTable);
    }

    private String getColoredText(String text, String colorCode) {
        return "<span style='color:" + colorCode + "'>" + text + "</span>";
    }

    private void renderCell(String content, StringBuilder followUpTable) {
        followUpTable.append("<td style='padding:3px'>" + content + "</td>");
    }

    private Map<String, Object> parseCase(String user, File caseFile) {
        Map<String, Object> parameters = caseCache.get(caseFile.getPath());
        if (parameters != null) {
            long lastLastFileChange = (Long) parameters.get(LAST_FILE_CHANGE);
            long lastFileChange = caseFile.lastModified();
            if (lastLastFileChange == lastFileChange) {
                return parameters;
            }
        }
        if (parameters == null) {
            parameters = new HashMap<String, Object>();
            caseCache.put(caseFile.getPath(), parameters);
        }
        parameters.put(LAST_FILE_CHANGE, caseFile.lastModified());
        Session loadedUserCase = PersistenceD3webUtils.loadUserCase(user, caseFile);
        parameters.put(LAST_CASE_CHANGE, loadedUserCase.getLastChangeDate().getTime());
        parseFollowUpParameters(parameters, loadedUserCase);
        parseOperationDate(parameters, loadedUserCase);

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
        getFollowUpQuestions(allQuestions, allFollowUp1Questions, allFollowUp2Questions);

        List<Question> allIndicatedFollowUp1Questions = getIndicatedOrTopLevelQuestions(
                allFollowUp1Questions,
                loadedUserCase);
        List<Question> allIndicatedFollowUp2Questions = getIndicatedOrTopLevelQuestions(
                allFollowUp2Questions,
                loadedUserCase);

        List<Question> allAnsweredQuestions = loadedUserCase.getBlackboard().getAnsweredQuestions();
        List<Question> allAnsweredFollowUp1Questions = new LinkedList<Question>();
        List<Question> allAnsweredFollowUp2Questions = new LinkedList<Question>();
        getFollowUpQuestions(allAnsweredQuestions, allAnsweredFollowUp1Questions,
                allAnsweredFollowUp2Questions);

        parameters.put(FOLLOW_UP1_DONE,
                isFollowUpDone(allAnsweredFollowUp1Questions, allIndicatedFollowUp1Questions));

        parameters.put(FOLLOW_UP2_DONE,
                isFollowUpDone(allAnsweredFollowUp2Questions, allIndicatedFollowUp2Questions));
    }

    private boolean isFollowUpDone(List<Question> answeredQuestions, List<Question> allIndicatedQuestions) {
        boolean percentageAnswered = answeredQuestions.size()
                >= allIndicatedQuestions.size() * PERCENTAGE_ANSWERED_QUESTIONS_NEEDED;
        return percentageAnswered && !answeredQuestions.isEmpty();
    }

    private List<Question> getIndicatedOrTopLevelQuestions(List<Question> questions, Session session) {
        Blackboard blackboard = session.getBlackboard();
        List<Question> indicatedOrTopLevelQuestions = new LinkedList<Question>();
        for (Question question : questions) {
            if (isIndicated(question, blackboard) || isTopLevelQuestion(question)) {
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

    private void getFollowUpQuestions(List<Question> allQuestions, List<Question> allFollowUp1Questions, List<Question> allFollowUp2Questions) {
        for (Question question : allQuestions) {
            if (question.getName().contains(FOLLOW_UP1_NAME_SUFFIX)) {
                allFollowUp1Questions.add(question);
            } else if (question.getName().contains(FOLLOW_UP2_NAME_SUFFIX)) {
                allFollowUp2Questions.add(question);
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
                        || !D3webUtils.isIndicated(child, d3webSession.getBlackboard())) {
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
}
