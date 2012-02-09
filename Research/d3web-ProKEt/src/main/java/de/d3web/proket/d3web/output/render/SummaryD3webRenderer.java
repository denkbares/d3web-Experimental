package de.d3web.proket.d3web.output.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.*;
import de.d3web.core.utilities.Pair;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webUtils;
import java.util.Date;
import javax.servlet.http.HttpSession;

public class SummaryD3webRenderer extends AbstractD3webRenderer {

    public enum SummaryType {

        QUESTIONNAIRE,
        GRID
    }

    public String renderSummaryDialog(Session d3webSession, SummaryType type, HttpSession http) {

        StringBuilder bui = new StringBuilder();

        if (type == SummaryType.GRID) {
            fillGridSummary(d3webSession, bui);
        } else if (type == SummaryType.QUESTIONNAIRE) {
            TerminologyObject root = d3webSession.getKnowledgeBase().getRootQASet();
            fillQuestionnaireSummaryChildren(d3webSession, bui, root, http);
        }

        return bui.toString();
    }

    private void fillGridSummary(Session d3webSession, StringBuilder bui) {
        KnowledgeBase knowledgeBase = d3webSession.getKnowledgeBase();
        for (Resource resource : knowledgeBase.getResources()) {
            String pathName = resource.getPathName();

            boolean isGrid = pathName.endsWith(".txt");
            if (!isGrid) {
                continue;
            }

            int lastSlash = pathName.lastIndexOf('/') + 1;
            isGrid = pathName.substring(lastSlash).toLowerCase().startsWith("grid");
            if (!isGrid) {
                continue;
            }

            StringBuilder content = getText(resource);

            List<Pair<Pair<Integer, Integer>, Pair<Pair<String, Object>, Pair<String, Object>>>> result = parse(content.toString());
            if (result.isEmpty()) {
                continue;
            }

            boolean nothingAnswered = enrichGrid(d3webSession, content, result);
            if (nothingAnswered) {
                continue;
            }

            bui.append(content.toString());
        }
        if (bui.length() == 0) {

            // VERY EURAHS Specific check of a quesiton-answer finding
            boolean checkPara =
                    checkQuestionForTargetValue(
                    "Please select your hernia route",
                    "Parastomal hernia route",
                    d3webSession);
            if (checkPara) {
                bui.append("There is no grid summary available for parastomal hernias. <br />Please see complete summary.");
            } else {
                bui.append("No data available yet.");
            }
        }
    }

    private boolean checkQuestionForTargetValue(
            String question, String targetValue, Session d3webSession) {

        KnowledgeBase kb = d3webSession.getKnowledgeBase();
        Question q =
                kb.getManager().searchQuestion(question);
        Value val = d3webSession.getBlackboard().getValue(q);

        return val.getValue().toString().equals(targetValue);
    }

    private boolean enrichGrid(Session d3webSession,
            StringBuilder content,
            List<Pair<Pair<Integer, Integer>, Pair<Pair<String, Object>, Pair<String, Object>>>> result) {

        boolean nothingAnswered = true;
        for (Pair<Pair<Integer, Integer>, Pair<Pair<String, Object>, Pair<String, Object>>> parsePair : result) {
            int matchStart = parsePair.getA().getA();
            int matchEnd = parsePair.getA().getB();

            Pair<String, Object> questionAnswerPair1 = parsePair.getB().getA();
            Pair<String, Object> questionAnswerPair2 = parsePair.getB().getB();

            boolean isCondition1Matching = isConditionMatchingOrNull(d3webSession,
                    questionAnswerPair1);

            boolean isCondition2Matching = isConditionMatchingOrNull(d3webSession,
                    questionAnswerPair2);

            if (isCondition1Matching && isCondition2Matching) {
                Value value = getValue(d3webSession, questionAnswerPair1.getA());
                if (value.getValue() instanceof Double) {
                    content.replace(matchStart, matchEnd, String.valueOf(value.getValue()));
                } else {
                    content.replace(matchStart, matchEnd, "X");
                }
                nothingAnswered = false;
            } else {
                content.replace(matchStart, matchEnd, "<div> </div>");
            }

        }
        return nothingAnswered;
    }

    private boolean isConditionMatchingOrNull(Session d3webSession, Pair<String, Object> questionAnswerPair) {

        if (questionAnswerPair == null) {
            return true;
        }

        String questionName = questionAnswerPair.getA();

        Value value = getValue(d3webSession, questionName);

        if (value == null || UndefinedValue.isUndefinedValue(value)) {
            return false;
        }

        Object answerTypeObject = questionAnswerPair.getB();

        if (answerTypeObject instanceof String[]) {
            return isMatchingChoiceCondition((String[]) answerTypeObject, value);
        } else {
            @SuppressWarnings("unchecked")
            Pair<Integer, Integer> interval = (Pair<Integer, Integer>) answerTypeObject;
            return isMatchingNumCondition(interval, value);
        }
    }

    private Value getValue(Session d3webSession, String questionName) {
        Blackboard bb = d3webSession.getBlackboard();
        KnowledgeBase knowledgeBase = d3webSession.getKnowledgeBase();

        Question question = knowledgeBase.getManager().searchQuestion(questionName);

        if (question == null) {
            return null;
        }

        return bb.getValue(question);
    }

    private boolean isMatchingChoiceCondition(String[] answerNames, Value value) {
        for (String answerName : answerNames) {
            if (value instanceof MultipleChoiceValue) {
                if (((MultipleChoiceValue) value).contains(new ChoiceID(answerName))) {
                    return true;
                }
            }
            if (value instanceof ChoiceValue) {
                if (((ChoiceValue) value).getChoiceID().equals(new ChoiceID(answerName))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMatchingNumCondition(Pair<Integer, Integer> interval, Value value) {
        if (value instanceof NumValue) {
            Double doubleValue = ((NumValue) value).getDouble();
            int intervalStart = interval.getA();
            int intervalEnd = interval.getB();
            if (doubleValue >= intervalStart
                    && doubleValue < intervalEnd) {
                return true;
            }
        }
        return false;
    }

    private List<Pair<Pair<Integer, Integer>, Pair<Pair<String, Object>, Pair<String, Object>>>> parse(String content) {

        String questionPattern = "(.+?)::";
        String numIntervalPattern = "num::(\\d*),(\\d*)";
        String choicePattern = "choice::(.+?)";

        String questionAnswerCondition = questionPattern + "(?:" + choicePattern + "|"
                + numIntervalPattern + ")";

        Pattern conditionPattern = Pattern.compile(
                "##" + questionAnswerCondition + "(&&" + questionAnswerCondition + ")?##");

        Matcher matcher = conditionPattern.matcher(content);
        LinkedList<Pair<Pair<Integer, Integer>, Pair<Pair<String, Object>, Pair<String, Object>>>> result =
                new LinkedList<Pair<Pair<Integer, Integer>, Pair<Pair<String, Object>, Pair<String, Object>>>>();
        while (matcher.find()) {

            Pair<Integer, Integer> startEndPair = new Pair<Integer, Integer>(matcher.start(),
                    matcher.end());

            Pair<String, Object> questionAnswerPair = getQuestionAnswerPair(matcher, 0);
            Pair<String, Object> questionAnswerPair2 = null;

            boolean secondCondition = matcher.group(5) != null;

            if (secondCondition) {
                questionAnswerPair2 = getQuestionAnswerPair(matcher, 5);
            }

            Pair<Pair<String, Object>, Pair<String, Object>> questionAnswerPairs =
                    new Pair<Pair<String, Object>, Pair<String, Object>>(
                    questionAnswerPair, questionAnswerPair2);

            result.addFirst(new Pair<Pair<Integer, Integer>, Pair<Pair<String, Object>, Pair<String, Object>>>(
                    startEndPair, questionAnswerPairs));
        }
        return result;
    }

    private Pair<String, Object> getQuestionAnswerPair(Matcher matcher, int groupIndex) {
        String question = matcher.group(groupIndex + 1);
        String choice = matcher.group(groupIndex + 2);
        String intervalStart = matcher.group(groupIndex + 3);
        String intervalEnd = matcher.group(groupIndex + 4);

        Pair<String, Object> questionAnswerPair = null;
        if (choice != null) {
            String[] choiceSplit = choice.trim().split("\\s*\\|\\|\\s*");
            questionAnswerPair = new Pair<String, Object>(question.trim(), choiceSplit);
        } else {
            int start = intervalStart.trim().isEmpty()
                    ? Integer.MIN_VALUE
                    : Integer.parseInt(intervalStart.trim());
            int end = intervalEnd.trim().isEmpty()
                    ? Integer.MAX_VALUE
                    : Integer.parseInt(intervalEnd.trim());
            questionAnswerPair = new Pair<String, Object>(question.trim(),
                    new Pair<Integer, Integer>(start, end));
        }

        return questionAnswerPair;
    }

    private StringBuilder getText(Resource resource) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }

    private void fillQuestionnaireSummaryChildren(Session d3webSession, StringBuilder bui, TerminologyObject to, HttpSession httpSession) {

        if (to instanceof QContainer && !to.getName().contains("Q000")) {
            if (D3webUtils.hasAnsweredChildren(to, d3webSession)) {
                bui.append("<div style='margin-top:10px;'><b>");
                bui.append(D3webConnector.getInstance().getID(to));
                bui.append(" ");
                bui.append(to.getName());
                bui.append("</b></div>\n");
            }
        } else if (to instanceof Question) {
            Value val = d3webSession.getBlackboard().getValue((ValueObject) to);
            if (val != null && UndefinedValue.isNotUndefinedValue(val)) {

                // handle date quesstions separately for formatting date representation
                if (to instanceof QuestionDate) {

                    // Format the date appropriately
                    String f = D3webUtils.getFormattedDateFromString((Date) val.getValue(), "dd.MM.yyyy");

                    bui.append("<div style='margin-left:10px;'>"
                            + D3webConnector.getInstance().getID(to) + " " + to.getName()
                            + " -- " + f + "</div>\n");
                } // handle abstraction questions separately, e.g. for rounding age quesstion
                else if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)
                        && to instanceof QuestionNum) {

                    //System.out.println("NUM VAL: " + val);
                    //System.out.println("As int: " + (int)Double.parseDouble(val.toString()));
                    int doubleAsInt = (int) Double.parseDouble(val.toString());
                    bui.append("<div style='margin-left:10px;'>"
                            + D3webConnector.getInstance().getID(to) + " " + to.getName()
                            + " -- " + doubleAsInt + "</div>\n");

                } // all other questions: just append question and val
                else {
                    bui.append("<div style='margin-left:10px;'>"
                            + D3webConnector.getInstance().getID(to) + " " + to.getName()
                            + " -- " + val + "</div>\n");

                }
            }
        }

        if (to.getChildren() != null && to.getChildren().length != 0) {
            for (TerminologyObject toc : to.getChildren()) {
                fillQuestionnaireSummaryChildren(d3webSession, bui, toc, httpSession);
            }
        }

    }
}
