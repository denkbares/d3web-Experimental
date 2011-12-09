/**
 * Copyright (C) 2010/2011 Chair of Artificial Intelligence and Applied
 * Informatics Computer Science VI, University of Wuerzburg
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.CurrentQContainerFormStrategy;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.JPFPluginManager;
import de.d3web.proket.d3web.output.render.ImageHandler;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.utils.FileUtils;
import de.d3web.proket.utils.GlobalSettings;
import de.d3web.proket.utils.IDUtils;
import java.util.Locale;

/**
 * Util methods for the binding of d3web to the ProKEt system.
 *
 * @author Martina Freiberg Johannes Mitlmeier
 */
public class D3webUtils {

    /**
     * Create a d3web session with a given knowledge base. Per default creates a
     * session for a multiple-question dialog.
     *
     * @created 11.10.2010
     * @param kb the d3web knowledge base
     * @return the Session
     */
    public static Session createSession(KnowledgeBase kb) {
        return createSession(kb, DialogStrategy.NEXTFORM);
    }

    /**
     * Creates a new D3webSession for a a given KnowledgeBase and a certain
     * DialogType.
     *
     * @param kb d3web KnowledgeBase to be used.
     * @param dt the Dialog Type
     * @return A new D3webSession with the KnowledgeBase associated.
     */
    public static Session createSession(KnowledgeBase kb, DialogStrategy ds) {

        // if DialogType given
        if (ds != null) {
            if (ds == DialogStrategy.NEXTUAQUESTION) { // one question dialog
                return SessionFactory.createSession(
                        null, kb, new NextUnansweredQuestionFormStrategy(), new Date());
            } else if (ds == DialogStrategy.NEXTFORM) { // questionnaire based
                return SessionFactory.createSession(
                        null, kb, new CurrentQContainerFormStrategy(), new Date());
            }
        }

        // per default returns questionnaire based standard questionnaires
        return SessionFactory.createSession(
                null, kb, new CurrentQContainerFormStrategy(), new Date());
    }

    /**
     * Creates a new D3webSession for a given KnowledgeBase (by filename).
     *
     * @param kbFilename File name of the KnowledgeBase to be used. The file is
     *        searched in /resources/kb or at runtime in WEB-INF/classes/kb. The
     *        suffix ".jar" can be omitted.
     * @return A new D3webSession with the KnowledgeBase associated.
     */
    public static Session createSession(String kbFilename) {
        return createSession(getKnowledgeBase(kbFilename), DialogStrategy.DEFAULT);
    }

    /**
     * Util method: Get a String representation of the questions and solutions
     * of a given KnowledgeBase for debugging.
     *
     * @param kb KnowledgeBase to be debugged.
     * @return A String containing a textual representation of the
     *         KnowledgeBase's questions and solutions.
     */
    public static String debug(KnowledgeBase kb) {
        StringBuilder sb = new StringBuilder();
        debugRecurse(kb.getRootQASet(), sb, "");
        return sb.toString();
    }

    /**
     * The recursive util method needed to assemble a String representation of
     * the questions and solutions of a knowledge base.
     *
     * @created 11.10.2010
     * @param root The recursion start
     * @param sb a StringBuilder
     * @param indention indentation String
     */
    private static void debugRecurse(TerminologyObject root, StringBuilder sb,
            String indention) {

        // go through all children
        for (TerminologyObject element : root.getChildren()) {

            // append indentation and element name
            sb.append(indention).append(element.getName());

            // also append Choices of Questions
            if ((element instanceof QuestionChoice)) {
                List<Choice> alternatives = ((QuestionChoice) element).getAllAlternatives();
                sb.append(" (");
                for (Choice choice : alternatives) {
                    sb.append(choice.getName()).append(", ");
                }
                sb.append(")\n");

                // in case a Container/QASet is given, recursion goes on
            } else if ((element instanceof QContainer)
                    || (element instanceof QASet)) {
                sb.append("\n");
                debugRecurse(element, sb, indention + "\t");
            }
        }
    }

    /**
     * Discretize a rating with no limits/discretization values given, i.e.
     * using the defaults defined in this method.
     *
     * @created 11.10.2010
     * @param value The value to be discretized
     * @return String an abstract/discretized value
     */
    public static String discretizeRating(double value) {

        // some reasonable default discretizing values
        List<Double> limits = new ArrayList<Double>();
        limits.add(49.0);
        limits.add(79.0);
        limits.add(100.0);

        // and some default discretized (abstract) values
        List<String> returnValues = new ArrayList<String>();
        returnValues.add("rating-low");
        returnValues.add("rating-medium");
        returnValues.add("rating-high");

        return discretizeRating(limits, returnValues, value);
    }

    /**
     * Discretize a rating value with given limits and discretization values.
     *
     * @param limits Ordered list of limits starting from the lowest, where a
     *        single value of x means the rating has to be <= x.
     * @param returnValues Return values based on the limits.
     * @param value Value to be discretized.
     * @return null on negative input value, the returnValue corresponding to
     *         the lowest limit value higher than the value to check.
     */
    public static String discretizeRating(List<Double> limits,
            List<String> returnValues, double value) {
        if (value < 0) {
            return null;
        }
        for (int i = 0; i < limits.size(); i++) {
            if (value <= limits.get(i)) {
                return returnValues.get(i);
            }
        }

        // return the highest value by default
        return returnValues.get(returnValues.size() - 1);
    }

    /**
     * Util method to get all jpf-plugin-files of a given folder. Needed for the
     * PluginManager to work.
     *
     * @param folder Folder to operate in.
     * @return List of the JPF plugins in the given folder.
     */
    public static File[] getAllJPFPlugins(File folder) {
        List<File> fileList = new ArrayList<File>();

        for (File file : folder.listFiles()) {
            if (file.getName().contains("d3web-Plugin")) {
                fileList.add(file);
            }
        }
        return fileList.toArray(new File[fileList.size()]);
    }

    /**
     * Retrieves the best solutions a {@link Session} contains at this moment.
     * Thereby, "best" is defined by a minimum rating a solution must have and a
     * maximum number of solutions to be returned in total.
     *
     * TODO maybe this method needs refactoring as to return other sets of
     * solutions?!
     *
     * @param session {@link Session} to operate on.
     * @param minRating Minimum {@link Rating} a {@link Solution} must have to
     *        be included in the returned list.
     * @param maxCount Maximum number of {@link Solution}s to include in the
     *        returned list.
     * @return List of the best {@link Solution}s according to the parameters
     *         given. If there are established {@link Solution}s all of them are
     *         returned regardless of the limit parameters. They only affect the
     *         list if there are only indicated {@link Solution}s.
     */
    public static List<Solution> getBestSolutions(final Session session,
            Number minRating, int maxCount) {

        List<Solution> result = new LinkedList<Solution>();

        // get established and suggested solutions
        List<Solution> established = session.getBlackboard().getSolutions(
                State.ESTABLISHED);
        List<Solution> suggested = session.getBlackboard().getSolutions(
                State.SUGGESTED);

        // if established solutions, just return them not regarding any limits
        if (established.size() > 0) {
            return established;
        }

        // if no suggested solutions either, return empty list
        // TODO so other solutions than estab/sugg are ignored! --> rework
        if (suggested.size() == 0) {
            return result;
        }

        // sort suggested solutions according to a comparator that sorts
        // solution objects by their rating
        Collections.sort(suggested, new Comparator<Solution>() {

            @Override
            public int compare(Solution o1, Solution o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o2 == null) {
                    return 1;
                }
                if (o1 == null) {
                    return -1;
                }
                return session.getBlackboard().getRating(o1).compareTo(session.getBlackboard().getRating(o2));
            }
        });

        // limit for the number of solutions: minimum of either maxCount or
        // suggested.
        int limit = Math.min(maxCount, suggested.size());

        // cut off
        for (int i = 0; i < limit; i++) {
            result.add(suggested.get(limit - 1 - i)); // upside-down
        }
        return result;
    }

    /**
     * Retrieve a {@link KnowledgeBase} by its file name. Search is performed in
     * /WEB-INF/classes/kb. The trailing ".jar" can be omitted.
     *
     * @param kbFilename File name of the {@link KnowledgeBase}.
     * @return The {@link KnowledgeBase} denoted by the given name, null if no
     *         such {@link KnowledgeBase} exists.
     */
    public static KnowledgeBase getKnowledgeBase(String kbFilename) {

        // add .jar if it's not already there
        if (!kbFilename.endsWith(".jar")
                && !kbFilename.endsWith(".d3web")) {
            kbFilename += ".d3web";
        }

        File kbFile;
        File libPath;
        try {
            // Paths here are relative to the WEB-INF/classes folder!!!
            // from the /specs/d3web folder
            kbFile = FileUtils.getResourceFile("/specs/d3web/" + kbFilename);
            // from the /lib folder
            libPath = FileUtils.getResourceFile("/../lib");
        } catch (FileNotFoundException e1) {
            return null;
        }

        // initialize PluginManager
        File[] files = null;
        files = getAllJPFPlugins(libPath);
        JPFPluginManager.init(files);
        PersistenceManager persistenceManager =
                PersistenceManager.getInstance();

        // try to load knowledge base
        try {
            return persistenceManager.load(kbFile);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Retrieve all {@link Question}s of a {@link KnowledgeBase} as a string.
     * TODO: needed?
     *
     * @param kb {@link KnowledgeBase} to operate on.
     * @return A string with one {@link Question} per line. The questions are
     *         represented by their name.
     */
    public static String getQuestionsAsString(KnowledgeBase kb) {
        StringBuilder sb = new StringBuilder();
        List<Question> questions = kb.getManager().getQuestions();
        for (Question question : questions) {
            sb.append(question.getName()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Retrieve solutions from a {@link Session}. Thereby solutions are
     * filtered. First we try to get only established ones, if there are none
     * established we want to get the suggested ones TODO maybe we just should
     * get all the solutions and order them accordingly!
     *
     * @param session {@link Session} to operate on.
     * @return If there is established {@link Solution}s, a list of those is
     *         returned. Otherwise this function returns a (possibly empty) list
     *         of suggested {@link Solution}.
     */
    public static List<Solution> getSolutionsEstabSugg(Session session) {

        List<Solution> result =
                session.getBlackboard().getSolutions(State.ESTABLISHED);
        if (result.size() == 0) {
            return session.getBlackboard().getSolutions(State.SUGGESTED);
        }
        return result;
    }

    /**
     * Retrieves a {@link TerminologyObject} from a {@link KnowledgeBase} by its
     * given ID.
     *
     * @param session The {@link Session} to operate on.
     * @param id The target ID.
     * @return The {@link TerminologyObject} with the given ID in the
     *         {@link Session}'s {@link KnowledgeBase}, or null if no such
     *         {@link TerminologyObject} exists.
     */
    public static TerminologyObject getTerminologyObjectByID(
            Session session, String id) {

        // get all questions
        List<Question> questions = session.getKnowledgeBase().getManager().getQuestions();
        for (TerminologyObject question : questions) {

            // if ids match, return the question
            if (question.getId().equals(id)) {
                return question;
            }
        }
        return null;
    }

    public static boolean isFollowUpToQCon(TerminologyObject to, TerminologyObject currentQCon) {

        TerminologyObject[] parents = to.getParents();

        if (parents != null && parents.length != 0) {
            for (TerminologyObject term : parents) {

                if (term.equals(currentQCon)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Retrieve the difference between two date objects in seconds
     *
     * @created 29.04.2011
     * @param d1 First date
     * @param d2 Second date
     * @return the difference in seconds
     */
    public static float getDifference(Date d1, Date d2) {
        return (d1.getTime() - d2.getTime()) / 1000;
    }

    /**
     * Stream images from the KB into intermediate storage in webapp
     *
     * @created 29.04.2011
     */
    public static void streamImages() {

        List<Resource> kbimages = D3webConnector.getInstance().getKb().getResources();

        if (kbimages != null && kbimages.size() != 0) {
            for (Resource r : kbimages) {
                String rname = r.getPathName();

                if (rname.endsWith(".jpg") || rname.endsWith(".JPG")) {
                    BufferedImage bui = ImageHandler.getResourceAsBUI(r);
                    try {
                        File file =
                                new File(GlobalSettings.getInstance().getKbImgFolder()
                                + "/" + rname);
                        ImageIO.write(bui, "jpg", file);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (rname.endsWith(".png") || rname.endsWith(".PNG")) {
                    BufferedImage bui = ImageHandler.getResourceAsBUI(r);
                    try {
                        File file =
                                new File(GlobalSettings.getInstance().getKbImgFolder()
                                + "/" + rname);
                        ImageIO.write(bui, "png", file);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Checks, whether a potentially required value is already set in the KB or
     * is contained in the current set of values to write to the KB. If yes, the
     * method returns true, if no, false.
     *
     * @created 15.04.2011
     * @param requiredVal The required value that is to check
     * @param sess The d3webSession
     * @param valToSet The single value to set
     * @param store The value store
     * @return TRUE of the required value is already set or contained in the
     *         current set of values to set
     */
    public static boolean checkReqVal(String requiredVal, Session sess, String valToSet, String store) {
        Blackboard blackboard = sess.getBlackboard();

        valToSet = valToSet.replace("q_", "").replace("_", " ");
        store = store.replace("_", " ");

        Question to = D3webConnector.getInstance().getKb().getManager().searchQuestion(requiredVal);

        Fact lastFact = blackboard.getValueFact(to);

        if (requiredVal.equals(valToSet)
                || (store.contains(requiredVal))
                || (lastFact != null && lastFact.getValue().toString() != "")) {
            return true;
        }
        return false;
    }

    /**
     * Utility method for resetting follow-up questions due to setting their
     * parent question to Unknown. Then, the childrens' value should also be
     * removed again, recursively also for childrens' children and so on.
     *
     * @created 31.01.2011
     * @param parent The parent TerminologyObject
     * @param blackboard The currently active blackboard
     */
    public static void resetFollowUpsIfParentUnknown(TerminologyObject parent,
            Blackboard blackboard) {

        if (parent.getChildren() != null && parent.getChildren().length != 0) {
            for (TerminologyObject c : parent.getChildren()) {

                Question qto = D3webConnector.getInstance().getKb().getManager().searchQuestion(
                        c.getName());

                if (!isIndicated(qto, blackboard)
                        || !isParentIndicated(qto, blackboard)) {

                    // remove a previously set value
                    Fact lastFact = blackboard.getValueFact(qto);
                    if (lastFact != null) {
                        blackboard.removeValueFact(lastFact);
                    }
                }

                resetFollowUpsIfParentUnknown(c, blackboard);
            }
        }
    }

    /**
     * TODO check if we need this. Too many indicated check methods! Utility
     * method for checking whether a given terminology object is indicated or
     * instant_indicated or not in the current session.
     *
     * @created 09.03.2011
     * @param to The terminology object to check
     * @param bb
     * @return True, if the terminology object is (instant) indicated.
     */
    public static boolean isIndicated(TerminologyObject to, Blackboard bb) {
        for (QASet qaSet : bb.getSession().getKnowledgeBase().getManager().getQASets()) {
            // find the appropriate qaset in the knowledge base
            if (qaSet.getName().equals(to.getName())
                    && // and check its indication state
                    (bb.getIndication((InterviewObject) to).getState() == de.d3web.core.knowledge.Indication.State.INDICATED
                    || bb.getIndication((InterviewObject) to).getState() == de.d3web.core.knowledge.Indication.State.INSTANT_INDICATED)) {
                return true;
            }
        }
        return false;
    }

    /**
     * TODO check if we need this here, we have too many indicated-check
     * methods! Utility method for checking whether the parent object of a given
     * terminology object is (instant) indicated.
     *
     * @created 09.03.2011
     * @param to The terminology object, the parent of which is to be checked.
     * @param bb
     * @return True, if there exists a parent object of the given terminology
     *         object that is indicated.
     */
    public static boolean isParentIndicated(TerminologyObject to, Blackboard bb) {
        for (QASet qaSet : bb.getSession().getKnowledgeBase().getManager().getQASets()) {

            // get questionnaires only
            if (qaSet instanceof QContainer) {
                QContainer qcon = (QContainer) qaSet;

                // and check its indication state
                if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(qcon)
                        || bb.getIndication(qcon).getState() == de.d3web.core.knowledge.Indication.State.INDICATED
                        || bb.getIndication(qcon).getState() == de.d3web.core.knowledge.Indication.State.INSTANT_INDICATED) {

                    // if questionnaire indicated, check whether to is its child
                    if (hasChild(qcon, to)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Utility method that checks, whether a given TerminologyObject child is
     * the child of another given TerminologyObject parent. That is, whether
     * child is nested hierarchically *somewhere* underneath parent.
     *
     * @created 30.01.2011
     * @param parent The parent TerminologyObject
     * @param child The child to check
     * @return True, if child is the child of parent
     */
    public static boolean hasChild(TerminologyObject parent, TerminologyObject child) {

        if (parent.getChildren() != null && parent.getChildren().length != 0) {
            for (TerminologyObject c : parent.getChildren()) {
                if (c.equals(child)) {
                    return true;
                }
            }
            for (TerminologyObject c : parent.getChildren()) {
                if (c.getChildren().length != 0) {
                    return hasChild(c, child);
                }
            }
        }
        return false;
    }

    /**
     * Utility method for adding values. Adds a single value for a given
     * question to the current knowledge base in the current problem solving
     * session.
     *
     * @created 28.01.2011
     * @param termObID The ID of the TerminologyObject, the value is to be
     *        added.
     * @param valString The value, that is to be added for the TerminologyObject
     *        with ID valID.
     */
    public static void setValue(String termObID, String valString, Session sess) {

        // TODO REFACTOR: can be removed, just provide ID without "q_"
        // remove prefix, e.g. "q_" in "q_BMI"
        termObID = IDUtils.removeNamspace(termObID);

        Fact lastFact = null;
        Blackboard blackboard = sess.getBlackboard();
        Question to =
                D3webConnector.getInstance().getKb().getManager().searchQuestion(termObID);

        // if TerminologyObject not found in the current KB return & do nothing
        if (to == null) {
            return;
        }

        // init Value object...
        Value value = null;

        // check if unknown option was chosen
        if (valString.contains("unknown")) {

            // remove a previously set value
            lastFact = blackboard.getValueFact(to);
            if (lastFact != null) {
                blackboard.removeValueFact(lastFact);
            }

            // and add the unknown value
            value = Unknown.getInstance();
            Fact fact = FactFactory.createFact(sess, to, value,
                    PSMethodUserSelected.getInstance(),
                    PSMethodUserSelected.getInstance());
            blackboard.addValueFact(fact);

        } // otherwise, i.e., for all other "not-unknown" values
        else {

            // CHOICE questions
            if (to instanceof QuestionChoice) {
                if (to instanceof QuestionOC) {
                    // valueString is the ID of the selected item
                    try {
                        valString = valString.replace("q_", "");
                        value = KnowledgeBaseUtils.findValue(to, valString);
                    } catch (NumberFormatException nfe) {
                        // value still null, will not be set
                    }
                } else if (to instanceof QuestionMC) {

                    if (valString.equals("")) {
                        value = UndefinedValue.getInstance();
                    } else {
                        String[] choices = valString.split(",");
                        List<Choice> cs = new ArrayList<Choice>();

                        for (String c : choices) {
                            cs.add(new Choice(c));
                        }
                        value = MultipleChoiceValue.fromChoices(cs);

                    }
                }
            } // TEXT questions
            else if (to instanceof QuestionText) {
                String textPattern = to.getInfoStore().getValue(ProKEtProperties.TEXT_FORMAT);
                Pattern p = null;
                if (textPattern != null && !textPattern.isEmpty()) {
                    try {
                        p = Pattern.compile(textPattern);
                    } catch (Exception e) {
                    }
                }
                if (p != null) {
                    Matcher m = p.matcher(valString);
                    if (m.find()) {
                        value = new TextValue(m.group());
                    }
                } else {
                    value = new TextValue(valString);
                }
            } // NUM questions
            else if (to instanceof QuestionNum) {
                try {
                    value = new NumValue(Double.parseDouble(valString));
                } catch (NumberFormatException ex) {
                    // value still null, will not be set
                }
            } // DATE questions
            else if (to instanceof QuestionDate) {
                String dateDescription = to.getInfoStore().getValue(ProKEtProperties.DATE_FORMAT);
                if (dateDescription != null && !dateDescription.isEmpty()) {
                    String[] dateDescSplit = dateDescription.split("OR");
                    for (String dateDesc : dateDescSplit) {
                        dateDesc = dateDesc.trim();
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat(dateDesc);
                            value = new DateValue(dateFormat.parse(valString));
                        } catch (IllegalArgumentException e) {
                            // value still null, will not be set
                        } catch (java.text.ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (value != null) {
                            break;
                        }
                    }
                } else {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        value = new DateValue(dateFormat.parse(valString));
                    } catch (java.text.ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            // if reasonable value retrieved, set it for the given
            // TerminologyObject
            if (value != null) {

                // remove previously set value
                lastFact = blackboard.getValueFact(to);
                if (lastFact != null) {
                    blackboard.removeValueFact(lastFact);
                }

                if (UndefinedValue.isNotUndefinedValue(value)) {
                    // add new value as UserEnteredFact
                    Fact fact = FactFactory.createUserEnteredFact(to, value);
                    blackboard.addValueFact(fact);
                }
            }
        }

        // TODO: CHECK whether we need both the resetNotIndicated and
        // checkChildren methods

        // check, that questions of all non-init and non-indicated
        // questionnaires are reset, i.e., no value
        for (QASet qaSet : D3webConnector.getInstance().getKb().getManager().getQContainers()) {
            // find the appropriate qaset in the knowledge base

            if (!D3webConnector.getInstance().getKb().getInitQuestions().contains(qaSet)
                    && !qaSet.getName().equals("Q000")
                    && (blackboard.getIndication(qaSet).getState() != de.d3web.core.knowledge.Indication.State.INDICATED
                    && blackboard.getIndication(
                    qaSet).getState() != de.d3web.core.knowledge.Indication.State.INSTANT_INDICATED)) {

                resetNotIndicatedTOs(qaSet, blackboard, sess);
            }
        }

        // ensure, that follow-up questions are reset if parent-question doesn't
        // indicate any more.
        D3webUtils.resetFollowUpsIfParentUnknown(to, blackboard);
    }

    /**
     * Utility method for resetting all children of a qcontainer or a question
     * that is not indicated (any more).
     *
     * @created 09.03.2011
     * @param parent the parent object
     * @param bb
     */
    public static void resetNotIndicatedTOs(TerminologyObject parent, Blackboard bb,
            Session sess) {

        if (parent.getChildren() != null && parent.getChildren().length != 0) {
            Fact lastFact = null;

            Blackboard blackboard =
                    sess.getBlackboard();

            // go through all questions of the qcontainer
            for (TerminologyObject to : parent.getChildren()) {

                if (to instanceof Question) {

                    Question qto = D3webConnector.getInstance().getKb().getManager().searchQuestion(
                            to.getName());

                    // workaround to assure that same question from other
                    // questionnaire is not reset, too
                    if (qto.getParents().length == 1) {
                        // remove a previously set value
                        lastFact = blackboard.getValueFact(qto);
                        if (lastFact != null) {
                            blackboard.removeValueFact(lastFact);
                        }
                        resetNotIndicatedTOs(to, bb, sess);
                    }

                }
            }
        }
    }

    /**
     * Retrieve a language specific prompt text for a TerminologyObject.
     * If no language was specified, return the name of the TerminologyObject 
     * via getName().
     * 
     * @param to the TerminologyObject the prompt is needed for.
     * @return the prompt or the name repsectively.
     */
    public static String getTOPrompt(TerminologyObject to) {

        int locIdent = GlobalSettings.getInstance().getLocaleIdentifier();
        String prompt = null;

        switch (locIdent) {
            case 1:   // german
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, Locale.GERMAN);
                System.out.println(prompt);
                break;
            case 2:   // english
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, Locale.ENGLISH);
                System.out.println(prompt);
                break;
            case 3:   // spanish
                Locale SPANISH = new Locale("es", "ES");
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, SPANISH);
                System.out.println(prompt);
                break;
            case 4:   // italian
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, Locale.ITALIAN);
                break;
            case 5:   // french
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, Locale.FRENCH);
                break;
            case 6:   // polish
                Locale POLISH = new Locale("pl", "PL");
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, POLISH);
                break;
        }


        // default prompt = getName() if no locale specific was given
        return prompt == null ? to.getName() : prompt;
    }

    
    /**
     * Retrieve a language specific prompt text for a TerminologyObject (Answer).
     * If no language was specified, return the name of the TerminologyObject 
     * via getName().
     * 
     * @param to the TerminologyObject the prompt is needed for.
     * @return the prompt or the name repsectively.
     */
    public static String getAnswerPrompt(TerminologyObject to,
            Choice c) {


        if (to instanceof QuestionYN) {
            return getAnswerYNPrompt(c);
        }

        int locIdent = GlobalSettings.getInstance().getLocaleIdentifier();
        String prompt = null;

        switch (locIdent) {
            case 1:   // german
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, Locale.GERMAN);
                System.out.println(prompt);
                break;
            case 2:   // english
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, Locale.ENGLISH);
                System.out.println(prompt);
                break;
            case 3:   // spanish
                Locale SPANISH = new Locale("es", "ES");
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, SPANISH);
                System.out.println(prompt);
                break;
            case 4:   // italian
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, Locale.ITALIAN);
                break;
            case 5:   // french
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, Locale.FRENCH);
                break;
            case 6:   // polish
                Locale POLISH = new Locale("pl", "PL");
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, POLISH);
                break;
        }

        // default prompt = getName() if no locale specific was given
        return prompt == null ? c.getName() : prompt;
    }

    
    /**
     * Util method for retrievinv a localized verbalization for YN Questions.
     * Maybe to be removed in case there will be a possibility to define
     * YN-options in multiple languages via markup.
     * 
     * @param c the Choice, i.e. Yes or No
     * @return the String representation of the answer prompt in the respective
     * language.
     */
    private static String getAnswerYNPrompt(Choice c) {

        int locIdent = GlobalSettings.getInstance().getLocaleIdentifier();
        String prompt = null;

        if (c.getName().equals("Yes")) {

            switch (locIdent) {
                case 1:   // german
                    prompt = "Ja"; 
                    break;
                case 2:   // english
                    prompt = "Yes";
                    break;
                case 3:   // spanish
                    prompt = "Sí";
                    break;
                case 4:   // italian
                    prompt = "Sì";
                    break;
                case 5:   // french
                    prompt = "Oui";
                    break;
                case 6:   // polish
                    prompt = "Tak"; 
                    break;
            }
            
        } else {
   
            switch (locIdent) {
                case 1:   // german
                    prompt = "Nein"; 
                    break;
                case 2:   // english
                    prompt = "No";
                    break;
                case 3:   // spanish
                    prompt = "No";
                    break;
                case 4:   // italian
                    prompt = "No";
                    break;
                case 5:   // french
                    prompt = "Non";
                    break;
                case 6:   // polish
                    prompt = "Nie"; 
                    break;
            }
        }
       
        // default prompt = getName() if no locale specific was given
        return prompt == null ? c.getName() : prompt;
    }

    
    /**
     * Retrieve a language specific prompt text for the Unknown Choice.
     * If no language was specified, return "unknown" as default promp.
     * 
     * @param to the TerminologyObject the prompt is needed for.
     * @return the prompt or the name repsectively.
     */
    public static String getUnknownPrompt() {

        int locIdent = GlobalSettings.getInstance().getLocaleIdentifier();
        String prompt = null;

        D3webConnector d3wcon = D3webConnector.getInstance();
        String defaultPrompt = "unknown";

        switch (locIdent) {
            case 1:   // german
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, Locale.GERMAN);
                break;
            case 2:   // english
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, Locale.ENGLISH);
                break;
            case 3:   // spanish
                Locale SPANISH = new Locale("es", "ES");
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, SPANISH);
                break;
            case 4:   // italian
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, Locale.ITALIAN);
                break;
            case 5:   // french
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, Locale.FRENCH);
                break;
            case 6:   // polish
                Locale POLISH = new Locale("pl", "PL");
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, POLISH);
                break;
        }

        // default prompt = unknown if no locale specific prompt was given
        return prompt == null ? defaultPrompt : prompt;
    }
}
