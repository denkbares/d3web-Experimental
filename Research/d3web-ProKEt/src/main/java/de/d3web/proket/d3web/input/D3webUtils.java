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

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.*;
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
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.DefaultSession;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.CurrentQContainerFormStrategy;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.core.session.values.*;
import de.d3web.indication.ActionIndication;
import de.d3web.indication.ActionInstantIndication;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.JPFPluginManager;
import de.d3web.proket.d3web.output.render.AbstractD3webRenderer;
import de.d3web.proket.d3web.output.render.ImageHandler;
import de.d3web.proket.d3web.output.render.JNV;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.data.DialogType;
import de.d3web.proket.utils.FileUtils;
import de.d3web.proket.utils.GlobalSettings;
import java.util.*;
import javax.servlet.http.HttpSession;

/**
 * Util methods for the binding of d3web to the ProKEt system.
 *
 * @author Martina Freiberg Johannes Mitlmeier
 */
public class D3webUtils {

    public static final String YESSTRING = "ja";
    public static final String NOSTRING = "nein";
    public static final String MAYBESTRING = "vielleicht";
    public static final String RETRACTSTRING = "retract";
    public static final Locale SPANISH = new Locale("es", "ES");
    public static final Locale POLISH = new Locale("pl", "PL");
    public static final Locale DUTCH = new Locale("nl", "NL");
    public static final Locale SWEDISH = new Locale("sv", "SV");
    public static final Locale PORTUGUESE = new Locale("pt", "PT");
    public static final Locale BRAZILIAN = new Locale("braz", "BRAZ");

    /**
     * Create a d3web session with a given knowledge base. Per default creates a
     * session for a multiple-question dialog.
     *
     * @created 11.10.2010
     *
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
    public static DefaultSession createSession(KnowledgeBase kb, DialogStrategy ds) {

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
     * searched in /resources/kb or at runtime in WEB-INF/classes/kb. The suffix
     * ".jar" can be omitted.
     * @return A new D3webSession with the KnowledgeBase associated.
     * @throws IOException
     */
    public static Session createSession(String kbFilename) throws IOException {
        return createSession(getKnowledgeBase(kbFilename), DialogStrategy.DEFAULT);
    }

    /**
     * Util method: Get a String representation of the questions and solutions
     * of a given KnowledgeBase for debugging.
     *
     * @param kb KnowledgeBase to be debugged.
     * @return A String containing a textual representation of the
     * KnowledgeBase's questions and solutions.
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
     *
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
     *
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
     * single value of x means the rating hasFollowUp to be <= x.
     * @param returnValues Return values based on the limits.
     * @param value Value to be discretized.
     * @return null on negative input value, the returnValue corresponding to
     * the lowest limit value higher than the value to check.
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
     * be included in the returned list.
     * @param maxCount Maximum number of {@link Solution}s to include in the
     * returned list.
     * @return List of the best {@link Solution}s according to the parameters
     * given. If there are established {@link Solution}s all of them are
     * returned regardless of the limit parameters. They only affect the list if
     * there are only indicated {@link Solution}s.
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
                return session.getBlackboard().getRating(o1).compareTo(
                        session.getBlackboard().getRating(o2));
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
     * such {@link KnowledgeBase} exists.
     * @throws IOException
     */
    public static KnowledgeBase getKnowledgeBase(String kbFilename) throws IOException {

        // add .jar if it's not already there
        if (!kbFilename.endsWith(".jar")
                && !kbFilename.endsWith(".d3web")) {
            kbFilename += ".d3web";
        }

        File kbFile;
        File libPath;
        // Paths here are relative to the WEB-INF/classes folder!!!
        // from the /specs/d3web folder
        kbFile = FileUtils.getResourceFile("/specs/d3web/" + kbFilename);
        // from the /lib folder
        libPath = FileUtils.getResourceFile("/../lib");

        // initialize PluginManager
        File[] files = null;
        files = getAllJPFPlugins(libPath);
        JPFPluginManager.init(files);
        PersistenceManager persistenceManager = PersistenceManager.getInstance();

        // try to load knowledge base
        return persistenceManager.load(kbFile);

    }

    /**
     * Retrieve all {@link Question}s of a {@link KnowledgeBase} as a string.
     * TODO: needed?
     *
     * @param kb {@link KnowledgeBase} to operate on.
     * @return A string with one {@link Question} per line. The questions are
     * represented by their name.
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
     * returned. Otherwise this function returns a (possibly empty) list of
     * suggested {@link Solution}.
     */
    public static List<Solution> getSolutionsEstabSugg(Session session) {

        List<Solution> result =
                session.getBlackboard().getSolutions(State.ESTABLISHED);
        if (result.size() == 0) {
            return session.getBlackboard().getSolutions(State.SUGGESTED);
        }
        return result;
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
     *
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

    public static boolean isImageProvided(String imgName) {
        List<Resource> kbimages = D3webConnector.getInstance().getKb().getResources();

        if (kbimages != null && kbimages.size() != 0) {
            for (Resource r : kbimages) {
                String rname = r.getPathName();
                if (rname.contains(imgName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks, whether a potentially required value is already set in the KB or
     * is contained in the current set of values to write to the KB. If yes, the
     * method returns true, if no, false.
     *
     * @created 15.04.2011
     *
     * @param requiredVal The required value that is to check
     * @param sess The d3webSession
     * @param valToSet The single value to set
     * @param store The value store
     * @return TRUE of the required value is already set or contained in the
     * current set of values to set
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
     *
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
     *
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

    public static boolean isContraIndicated(TerminologyObject to, Blackboard bb) {
        for (QASet qaSet : bb.getSession().getKnowledgeBase().getManager().getQASets()) {
            // find the appropriate qaset in the knowledge base
            if (qaSet.getName().equals(to.getName())
                    && // and check its indication state
                    bb.getIndication((InterviewObject) to).getState() == de.d3web.core.knowledge.Indication.State.CONTRA_INDICATED) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a HashMap, that contains all TOs as keys that potentially
     * indicate further TOs when answered in a specific way. The values of the
     * HashMap are the indicated further TOs.
     *
     * @param bb
     * @return
     */
    public static HashMap getIndicationSetsL1(Blackboard bb, ArrayList<TerminologyObject> l1qs) {

        //System.out.println(l1qs);

        HashMap<TerminologyObject, Collection<TerminologyObject>> ancestors = new HashMap();

        for (KnowledgeSlice ks : bb.getSession().getKnowledgeBase().getAllKnowledgeSlices()) {

            if (ks instanceof RuleSet) {
                RuleSet rs = (RuleSet) ks;

                for (Rule r : rs.getRules()) {

                    if (r.getAction() instanceof ActionIndication
                            || r.getAction() instanceof ActionInstantIndication) {

                        List<TerminologyObject> backObjs = (List<TerminologyObject>) r.getAction().getBackwardObjects();
                        Collection<TerminologyObject> termObjs = (Collection<TerminologyObject>) r.getCondition().getTerminalObjects();


                        for (TerminologyObject termi : termObjs) {

                            if (l1qs.contains(termi)
                                    && !termi.getName().equals("Please choose database level")) {


                                if (ancestors.containsKey(termi)) {

                                    Collection<TerminologyObject> saveTermis =
                                            (Collection<TerminologyObject>) ancestors.get(termi);
                                    for (TerminologyObject newTermi : backObjs) {

                                        if (!newTermi.getName().equals("Number of mesh(s)")
                                                && !newTermi.getName().contains("Production company")) {



                                            if (!saveTermis.contains(newTermi)) {
                                                saveTermis.add(newTermi);
                                            }
                                        }
                                    }

                                    ancestors.put(termi, saveTermis);

                                } else {
                                    ancestors.put(termi, backObjs);
                                }
                            }

                            /*
                             * for(TerminologyObject pto: termi.getParents()){
                             * if(l1qs.contains(pto)){ if
                             * (ancestors.containsKey(termi)) {
                             *
                             * Collection<TerminologyObject> saveTermis =
                             * (Collection<TerminologyObject>)
                             * ancestors.get(termi); for (TerminologyObject
                             * newTermi : backObjs) { if
                             * (!saveTermis.contains(newTermi)) {
                             * saveTermis.add(newTermi); } }
                             *
                             * ancestors.put(termi, saveTermis);
                             *
                             * } else { ancestors.put(termi, backObjs); } } }
                             */
                        }
                    }


                    /*
                     * if (ancestors.containsKey(to)) { List<TerminologyObject>
                     * saveTOs = (List<TerminologyObject>) ancestors.get(to);
                     * for (TerminologyObject fto :
                     * r.getAction().getForwardObjects()) { if
                     * (!saveTOs.contains(fto)) { saveTOs.add(fto); } }
                     * ancestors.put(to, saveTOs);
                     *
                     * } else { ancestors.put(to,
                     * r.getAction().getForwardObjects()); }
                     */

                }
            }
        }

        return ancestors;
    }

    /**
     * TODO check if we need this here, we have too many indicated-check
     * methods! Utility method for checking whether the parent object of a given
     * terminology object is (instant) indicated.
     *
     * @created 09.03.2011
     *
     * @param to The terminology object, the parent of which is to be checked.
     * @param bb
     * @return True, if there exists a parent object of the given terminology
     * object that is indicated.
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

    public static boolean isInit(TerminologyObject to) {
        List<QASet> allQas = D3webConnector.getInstance().getKb().getInitQuestions();
        for (QASet qas : allQas) {
            if (qas.equals(to)) {
                return true;
            } else {
                TerminologyObject[] tocs = qas.getChildren();
                for (TerminologyObject toc : tocs) {
                    if (toc.equals(to)) {
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
     *
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
     * Utility method for resetting all children of a qcontainer or a question
     * that is not indicated (any more).
     *
     * @created 09.03.2011
     *
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
     * Retrieve a language specific popupPrompt text for a TerminologyObject. If
     * no language was specified, return the name of the TerminologyObject via
     * getName().
     *
     * @param to the TerminologyObject the popupPrompt is needed for.
     * @return the popupPrompt or the name repsectively.
     */
    public static String getTOPrompt(TerminologyObject to, int locIdent) {

        //int locIdent = GlobalSettings.getInstance().getLocaleIdentifier();
        //int locIdent = D3webConnector.getInstance().getUserSettings().getLanguageId();
        String prompt = null;

        switch (locIdent) {
            case 1: // german
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, Locale.GERMAN);
                break;
            case 2: // english
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, Locale.ENGLISH);
                break;
            case 3: // spanish
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, SPANISH);
                break;
            case 4: // italian
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, Locale.ITALIAN);
                break;
            case 5: // french
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, Locale.FRENCH);
                break;
            case 6: // polish
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, POLISH);
                break;
            case 7: // dutch
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, DUTCH);
                break;
            case 8: // swedish
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, SWEDISH);
                break;
            case 9: // portuguese
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, PORTUGUESE);
                break;
            case 10: // brazilian
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, BRAZILIAN);
                break;

        }

        // default fallback solution: popupPrompt in english
        if (prompt == null) {
            prompt = to.getInfoStore().getValue(MMInfo.PROMPT, Locale.ENGLISH);
        }

        // emergency fallback: getName() if no locale specific and no default
        // english was given
        return prompt == null ? to.getName() : prompt;
    }

    /**
     * Get the language-specific translation of the popup texts.
     *
     * @param to the Terminology Object the popup is displayed for
     * @param locIdent the locale identifier
     * @return the localized popup prompt
     */
    public static String getPopupPrompt(TerminologyObject to, int locIdent) {

        String popupPrompt = null;

        switch (locIdent) {
            case 1: // german
                popupPrompt =
                        to.getInfoStore().getValue(ProKEtProperties.POPUP, Locale.GERMAN);
                break;
            case 2: // english
                popupPrompt =
                        to.getInfoStore().getValue(ProKEtProperties.POPUP, Locale.ENGLISH);
                break;
            case 3: // spanish
                popupPrompt =
                        to.getInfoStore().getValue(ProKEtProperties.POPUP, SPANISH);
                break;
            case 4: // italian
                popupPrompt =
                        to.getInfoStore().getValue(ProKEtProperties.POPUP, Locale.ITALIAN);
                break;
            case 5: // french
                popupPrompt =
                        to.getInfoStore().getValue(ProKEtProperties.POPUP, Locale.FRENCH);
                break;
            case 6: // polish
                popupPrompt =
                        to.getInfoStore().getValue(ProKEtProperties.POPUP, POLISH);
                break;
            case 7: // dutch
                popupPrompt =
                        to.getInfoStore().getValue(ProKEtProperties.POPUP, DUTCH);
                break;
            case 8: // swedish
                popupPrompt =
                        to.getInfoStore().getValue(ProKEtProperties.POPUP, SWEDISH);
                break;
            case 9: // portuguese
                popupPrompt =
                        to.getInfoStore().getValue(ProKEtProperties.POPUP, PORTUGUESE);
                break;
            case 10: // brazilian
                popupPrompt =
                        to.getInfoStore().getValue(ProKEtProperties.POPUP, BRAZILIAN);
                break;
        }

        // default fallback solution: popupPrompt in english
        if (popupPrompt == null) {
            popupPrompt = to.getInfoStore().getValue(ProKEtProperties.POPUP, Locale.ENGLISH);
        }

        // emergency fallback: getName() if no locale specific and no default
        // english was given
        return popupPrompt == null ? to.getInfoStore().getValue(ProKEtProperties.POPUP) : popupPrompt;
    }

    public static String getDropdownDefaultPrompt(int locIdent) {
        String prompt = null;
        switch (locIdent) {
            case 1: // german
                prompt = "Bitte auswählen...";
                break;
            case 2: // english
                prompt = "Please select...";
                break;
            case 3: // spanish
                prompt = "";
                break;
            case 4: // italian
                prompt = "";
                break;
            case 5: // french
                prompt = "";
                break;
            case 6: // polish
                prompt = "";
                break;
            case 7: // dutch
                prompt = "";
                break;
            case 8: // swedish
                prompt = "";
                break;
            case 9: // portuguese
                prompt = "";
                break;
            case 10: // brazilian
                prompt = "";
                break;
        }

        // default fallback solution: popupPrompt in english
        if (prompt == null) {
            prompt = "Please select...";
        }

        // default popupPrompt = getName() if no locale specific was given
        return prompt;
    }

    /**
     * Retrieve a language specific popupPrompt text for a TerminologyObject
     * (Answer). If no language was specified, return the name of the
     * TerminologyObject via getName().
     *
     * @param to the TerminologyObject the popupPrompt is needed for.
     * @return the popupPrompt or the name repsectively.
     */
    public static String getAnswerPrompt(TerminologyObject to,
            Choice c, int locIdent) {

        if (to instanceof QuestionYN) {
            return getAnswerYNPrompt(c, locIdent);
        }

        //int locIdent = GlobalSettings.getInstance().getLocaleIdentifier();
        //int locIdent = D3webConnector.getInstance().getUserSettings().getLanguageId();
        String prompt = null;

        switch (locIdent) {
            case 1: // german
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, Locale.GERMAN);
                break;
            case 2: // english
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, Locale.ENGLISH);
                break;
            case 3: // spanish
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, SPANISH);
                break;
            case 4: // italian
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, Locale.ITALIAN);
                break;
            case 5: // french
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, Locale.FRENCH);
                break;
            case 6: // polish
                prompt =
                        c.getInfoStore().getValue(MMInfo.PROMPT, POLISH);
                break;
            case 7: // dutch
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, DUTCH);
                break;
            case 8: // swedish
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, SWEDISH);
                break;
            case 9: // portuguese
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, PORTUGUESE);
                break;
            case 10: // brazilian
                prompt =
                        to.getInfoStore().getValue(MMInfo.PROMPT, BRAZILIAN);
                break;
        }

        // default fallback solution: popupPrompt in english
        if (prompt == null) {
            prompt = to.getInfoStore().getValue(MMInfo.PROMPT, Locale.ENGLISH);
        }

        // default popupPrompt = getName() if no locale specific was given
        return prompt == null ? c.getName() : prompt;
    }

    /**
     * Util method for retrievinv a localized verbalization for YN Questions.
     * Maybe to be removed in case there will be a possibility to define
     * YN-options in multiple languages via markup.
     *
     * @param c the Choice, i.e. Yes or No
     * @return the String representation of the answer popupPrompt in the
     * respective language.
     *
     * TODO: NEEDED? WE CAN do that via knowledge base -> Check
     */
    private static String getAnswerYNPrompt(Choice c, int locIdent) {

        //int locIdent = GlobalSettings.getInstance().getLocaleIdentifier();
        //int locIdent = D3webConnector.getInstance().getUserSettings().getLanguageId();
        String prompt = null;

        if (c.getName().equals("Yes")) {

            switch (locIdent) {
                case 1: // german
                    prompt = "Ja";
                    break;
                case 2: // english
                    prompt = "Yes";
                    break;
                case 3: // spanish
                    prompt = "Sí";
                    break;
                case 4: // italian
                    prompt = "Sì";
                    break;
                case 5: // french
                    prompt = "Oui";
                    break;
                case 6: // polish
                    prompt = "Tak";
                    break;
                case 7: // dutch
                    prompt = "";
                    break;
                case 8: // swedish
                    prompt = "";
                    break;
                case 9: // portuguese
                    prompt = "";
                    break;
                case 10: // brazilian
                    prompt = "";
                    break;
            }

            // default fallback solution: popupPrompt in english
            if (prompt == null) {
                prompt = "Yes";
            }

        } else {

            switch (locIdent) {
                case 1: // german
                    prompt = "Nein";
                    break;
                case 2: // english
                    prompt = "No";
                    break;
                case 3: // spanish
                    prompt = "No";
                    break;
                case 4: // italian
                    prompt = "No";
                    break;
                case 5: // french
                    prompt = "Non";
                    break;
                case 6: // polish
                    prompt = "Nie";
                    break;
                case 7: // dutch
                    prompt = "";
                    break;
                case 8: // swedish
                    prompt = "";
                    break;
                case 9: // portuguese
                    prompt = "";
                    break;
                case 10: // brazilian
                    prompt = "";
                    break;
            }

            // default fallback solution: popupPrompt in english
            if (prompt == null) {
                prompt = "No";
            }
        }

        // default popupPrompt = getName() if no locale specific was given
        return prompt == null ? c.getName() : prompt;
    }

    /**
     * Retrieve a language specific popupPrompt text for the Unknown Choice. If
     * no language was specified, return "unknown" as default promp.
     *
     * @param to the TerminologyObject the popupPrompt is needed for.
     * @return the popupPrompt or the name repsectively.
     */
    public static String getUnknownPrompt(int locIdent) {

        //int locIdent = GlobalSettings.getInstance().getLocaleIdentifier();
        // int locIdent = D3webConnector.getInstance().getUserSettings().getLanguageId();
        String prompt = null;

        D3webConnector d3wcon = D3webConnector.getInstance();
        String defaultPrompt = "Unknown";

        switch (locIdent) {
            case 1: // german
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, Locale.GERMAN);
                break;
            case 2: // english
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, Locale.ENGLISH);
                break;
            case 3: // spanish
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, SPANISH);
                break;
            case 4: // italian
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, Locale.ITALIAN);
                break;
            case 5: // french
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, Locale.FRENCH);
                break;
            case 6: // polish
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, POLISH);
                break;
            case 7: // dutch
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, DUTCH);
                break;
            case 8: // swedish
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, SWEDISH);
                break;
            case 9: // portuguese
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, PORTUGUESE);
                break;
            case 10: // brazilian
                prompt =
                        d3wcon.getKb().getInfoStore().getValue(
                        MMInfo.UNKNOWN_VERBALISATION, BRAZILIAN);
                break;
        }

        // default popupPrompt = unknown if no locale specific popupPrompt was given
        return prompt == null ? defaultPrompt : prompt;
    }

    /**
     * Maps an integer value to the respective Locale for multilingualism 1-de
     * 2-en 3-es 4-it 5-fr 6-pl 7-nl 8-sv 9-pt 10-braz
     *
     * @param locIdent integer value
     * @return the Locale
     */
    public static Locale getCurrentLocale(int locIdent) {

        Locale loc = Locale.getDefault();

        switch (locIdent) {
            case 1: // german
                loc = Locale.GERMAN;
                break;
            case 2: // english
                loc = Locale.ENGLISH;
                break;
            case 3: // spanish
                loc = SPANISH;
                break;
            case 4: // italian
                loc = Locale.ITALIAN;
                break;
            case 5: // french
                loc = Locale.FRENCH;
                break;
            case 6: // polish
                loc = POLISH;
                break;
            case 7: // dutch
                loc = DUTCH;
                break;
            case 8: // swedish
                loc = SWEDISH;
                break;
            case 9: // portuguese
                loc = PORTUGUESE;
                break;
            case 10: // brazilian
                loc = BRAZILIAN;
                break;
        }

        // default popupPrompt = unknown if no locale specific popupPrompt was given
        return loc;
    }

    /**
     * Try to retrieve the questionnaire-ancestor of a given terminology object.
     *
     * @param to
     * @return the ancestor questionnaire or null if none available
     */
    public static TerminologyObject getQuestionnaireAncestor(TerminologyObject to) {
        if (to.getParents() != null) {
            for (TerminologyObject parent : to.getParents()) {
                if (parent instanceof QContainer) {
                    return parent;
                } else {
                    return getQuestionnaireAncestor(parent);
                }
            }
        }
        return null;
    }

    /**
     * Get the questionnaire-based differences of two sets of interview objects.
     * All questionnaires that are contained in the first set but not in the
     * second are added to the diff set.
     *
     * @param set1
     * @param set2
     * @param diff
     */
    public static void getDiff(Set<QASet> set1, Set<QASet> set2, Set<TerminologyObject> diff) {
        for (InterviewObject io : set1) {
            if (!set2.contains(io)) {
                if (io instanceof Question) {
                    diff.add(D3webUtils.getQuestionnaireAncestor(io));
                } else {
                    diff.add(io);
                }
            }
        }
    }

    /**
     * Retrieve the difference between two date objects in seconds
     *
     * @created 29.04.2011
     *
     * @param d1 First date
     * @param d2 Second date
     * @return the difference in seconds
     */
    public static float getDateDifference(Date d1, Date d2) {
        return (d1.getTime() - d2.getTime()) / 1000;
    }

    /**
     * Utility method for adding values. Adds a single value for a given
     * question to the current knowledge base in the current problem solving
     * session.
     *
     * @created 28.01.2011
     *
     * @param toId The ID of the TerminologyObject, the value is to be added.
     * @param valString The value, that is to be added for the TerminologyObject
     * with ID valID.
     */
    public static void setValue(String toId, String valueString, Session sess) {

        System.out.println(toId + " " + valueString);

        if (toId == null || valueString == null) {
            return;
        }

        String toName = AbstractD3webRenderer.getObjectNameForId(toId);

        Blackboard blackboard = sess.getBlackboard();
        Question question = D3webConnector.getInstance().getKb().getManager().searchQuestion(
                toName == null ? toId : toName);

        // if TerminologyObject not found in the current KB return & do nothing
        if (question == null) {
            return;
        }

        String valueName = AbstractD3webRenderer.getObjectNameForId(valueString);

        // init Value object...
        Value value = null;

        // check if unknown option was chosen
        if (valueName != null && valueName.equalsIgnoreCase("unknown")) {
            value = setQuestionToUnknown(sess, question);
        } // otherwise, i.e., for all other "not-unknown" values
        else {

            // CHOICE questions
            if (question instanceof QuestionChoice) {
                value = setQuestionChoice(question, valueString);
            } // TEXT questions
            else if (question instanceof QuestionText) {
                value = setQuestionText(question, valueString);
            } // NUM questions
            else if (question instanceof QuestionNum) {
                value = setQuestionNum(valueString);
            } // DATE questions
            else if (question instanceof QuestionDate) {
                value = setQuestionDate(question, valueString);
            }


            // if reasonable value retrieved, set it for the given
            // TerminologyObject
            if (value != null) {

                if (UndefinedValue.isNotUndefinedValue(value)) {
                    // add new value as UserEnteredFact
                    Fact fact = FactFactory.createUserEnteredFact(question, value);
                    blackboard.addValueFact(fact);
                }
            }
        }

        System.out.println("BLACKBOARD: ");
        for (Question q : blackboard.getValuedQuestions()) {

            System.out.println(q.getName() + " -> " + blackboard.getValue(q));
        }
        System.out.println("BLACKBOARD ENDE");
    }

    /**
     * Utility method for adding values specifically in the iTree dialog type.
     * (clarification hierarchy)
     *
     * @created 16.07.2012
     *
     * @param toId The ID of the TerminologyObject, the value is to be added.
     * @param valString The value, that is to be added for the TerminologyObject
     * with ID valID, in this case corresponds to number value 0 to 3.
     */
    public static void setValueITree(String toId, String valueString, Session sess) {

        if (toId == null || valueString == null) {
            return;
        }

        String toName = AbstractD3webRenderer.getObjectNameForId(toId);
        Blackboard blackboard = sess.getBlackboard();
        Question question = D3webConnector.getInstance().getKb().getManager().searchQuestion(
                toName == null ? toId : toName);

        //Question question_c =
        //      D3webConnector.getInstance().getKb().getManager().searchQuestion(
        //      toName == null ? toId.replace("_n", "") : toName.replace("_n", ""));

        // if TerminologyObject not found in the current KB return & do nothing
        if (question == null) {
            return;
        }

        Value value = null;

        // TODO: can be removed?!
        if (D3webConnector.getInstance().getDialogType().equals(DialogType.ITREE)) {


            if (valueString != null) {
                if (valueString.equals("1")) {
                    value = new ChoiceValue(JNV.J.toString());
                } else if (valueString.equals("2")) {
                    value = new ChoiceValue(JNV.V.toString());
                } else if (valueString.equals("3")) {
                    value = new ChoiceValue(JNV.N.toString());
                } else if (valueString.equals("0")) {
                }
            }
            
            if(question instanceof QuestionNum){
                Question choiceQFromNumQ = 
                        D3webConnector.getInstance().getKb().getManager().searchQuestion(
                        toName.replace("_n", ""));
                if(choiceQFromNumQ != null){
                    question = choiceQFromNumQ;
                }
            }
            
            
            // if reasonable value retrieved, set it for the given
            // TerminologyObject
            if (value != null) {

                if (UndefinedValue.isNotUndefinedValue(value)) {
                    // add new value as UserEnteredFact
                    Fact f2 = FactFactory.createUserEnteredFact(question, value);
                    blackboard.addValueFact(f2);
                }


            } else {
                setQuestionUndefined(sess, question);
            }

            System.out.println("BLACKBOARD: ");
            for (Question q : blackboard.getValuedQuestions()) {

                System.out.println(q.getName() + " -> " + blackboard.getValue(q));
            }
            System.out.println("BLACKBOARD ENDE");

        }

    }

    private static Value setQuestionDate(Question to, String valString) {
        Value value = null;
        try {
            value = new DateValue(new Date(Long.parseLong(valString)));
        } catch (NumberFormatException e) {
            // value still null, will not be set
        }
        return value;
    }

    private static Value setQuestionNum(String valString) {
        try {
            return new NumValue(Double.parseDouble(valString.replace(",", ".")));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Value setQuestionText(Question to, String valString) {
        Value value = null;
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
        return value;
    }

    private static Value setQuestionChoice(Question to, String valueId) {
        Value value = null;

        if (to instanceof QuestionOC) {
            // valueString is the html ID of the selected item
            String valueName = AbstractD3webRenderer.getObjectNameForId(valueId);

            if (valueId.equals(YESSTRING)) {
                value = KnowledgeBaseUtils.findValue(to,
                        valueName == null ? valueId : valueName);

            }
            value = KnowledgeBaseUtils.findValue(to,
                    valueName == null ? valueId : valueName);

        } else if (to instanceof QuestionMC) {
            if (valueId.equals("")) {
                value = Unknown.getInstance();
            } else {
                String[] choiceIds = valueId.split("##mcanswer##");
                List<Choice> choices = new ArrayList<Choice>();

                for (String choiceId : choiceIds) {
                    String choiceName = AbstractD3webRenderer.getObjectNameForId(choiceId);
                    choices.add(new Choice(choiceName == null ? choiceId : choiceName));
                }
                value = MultipleChoiceValue.fromChoices(choices);

            }
        }


        return value;
    }

    /**
     * Set given question in given session to unknown, i.e. retract previous
     * fact and set Unknown as new fact
     *
     * @param sess
     * @param to
     * @return The Unknown fact object for the question
     */
    private static Value setQuestionToUnknown(Session sess, Question to) {
        Blackboard blackboard = sess.getBlackboard();

        // remove a previously set value
        Fact lastFact = blackboard.getValueFact(to);
        if (lastFact != null) {
            blackboard.removeValueFact(lastFact);
        }

        // and add the unknown value
        Value value = Unknown.getInstance();
        Fact fact = FactFactory.createFact(sess, to, value,
                PSMethodUserSelected.getInstance(),
                PSMethodUserSelected.getInstance());
        blackboard.addValueFact(fact);
        return value;
    }

    /**
     * Set given question in the given session to Undefined
     *
     * @param sess
     * @param to
     * @return the (undefined) value object of the current session for the given
     * question
     */
    private static Value setQuestionUndefined(Session sess, Question to) {
        Blackboard blackboard = sess.getBlackboard();

        // remove a previously set value
        Fact lastFact = blackboard.getValueFact(to);
       
        if (lastFact != null) {
            blackboard.removeValueFact(lastFact);
        }
        return blackboard.getValue(to);
    }

    /**
     * Reset all questions, that are NOT currently indicated or
     * instant-indicated so if they are re-opened, they are unanswered again.
     * Therefore, all not currently ind. or inst-ind. questions and their
     * corresponding facts are removed from the blackboard. Exceptions - i.e.
     * questions that are not removed - are the required questions (they need to
     * be set always), of course all indicated and inst- indicated questions,
     * AND all questions that are always initially set (as e.g. in EuraHS all
     * dropdown - defaults).
     *
     * @param sess
     * @return a collection of all questions that have been removed from
     * blackboard
     */
    public static Collection<Question> resetAbandonedPaths(Session sess,
            HttpSession httpSess) {

        // get all questions that were set per default initially
        // e.g. in EuraHS, we need to init set all dropdowns as to enable the
        // follow up mechanisms
        ArrayList<String> initSetQuestions =
                (ArrayList<String>) httpSess.getAttribute("initsetquestions");
        Blackboard bb = sess.getBlackboard();
        Collection<Question> resetQuestions = new LinkedList<Question>();
        Set<QASet> initQuestions = new HashSet<QASet>(
                D3webConnector.getInstance().getKb().getInitQuestions());

        // check all questions that have been lately answered 
        for (Question question : bb.getAnsweredQuestions()) {

            // if a question is not active by being initQuestion
            if (!isActive(question, bb, initQuestions)
                    // and if question is not a required question
                    && !question.getName().equals(
                    D3webConnector.getInstance().getD3webParser().getRequired())) {

                // and if there are initSet questions and those do not contain q
                if (initSetQuestions != null && !initSetQuestions.contains(question.getName())) {

                    // then remove the corresponding fact from the blackboard
                    Fact lastFact = bb.getValueFact(question);
                    if (lastFact != null
                            && lastFact.getPSMethod() == PSMethodUserSelected.getInstance()) {
                        bb.removeValueFact(lastFact);
                        resetQuestions.add(question);
                    }
                }
            }
        }
        return resetQuestions;
    }

    /**
     * Retrieve all currently indicated QASets
     *
     * @param sess
     * @return
     */
    public static Set<QASet> getActiveSet(Session sess) {
        Set<QASet> activeSet = new HashSet<QASet>();
        Set<QASet> initQuestions = new HashSet<QASet>(sess.getKnowledgeBase().getInitQuestions());
        for (QASet qaset : sess.getKnowledgeBase().getManager().getQASets()) {
            if (D3webUtils.isActive(qaset, sess.getBlackboard(), initQuestions)) {
                activeSet.add(qaset);
            }
        }
        return activeSet;
    }

    /**
     * Utility method for checking whether a given terminology object is
     * indicated or instant_indicated or not in the current session.
     *
     * @created 09.03.2011
     *
     * @param to The terminology object to check
     * @param bb
     * @return True, if the terminology object is (instant) indicated.
     */
    public static boolean isActive(QASet qaset, Blackboard bb, Set<QASet> initQuestions) {
        boolean indicatedParent = false;
        for (TerminologyObject parentQASet : qaset.getParents()) {
            if (parentQASet instanceof QContainer
                    && isIndicated((QASet) parentQASet, bb, initQuestions)
                    && !isContraIndicated(qaset, bb)) {
                indicatedParent = true;
                break;
            }
        }
        return indicatedParent || isIndicated(qaset, bb, initQuestions);
    }

    private static boolean isIndicated(QASet qaset, Blackboard bb, Set<QASet> initQuestions) {
        return initQuestions.contains(qaset)
                || bb.getIndication(qaset).getState() == de.d3web.core.knowledge.Indication.State.INDICATED
                || bb.getIndication(qaset).getState() == de.d3web.core.knowledge.Indication.State.INSTANT_INDICATED;
    }

    /**
     * Get a set of all unknown-answered questions
     *
     * @param sess
     * @return
     */
    public static Set<TerminologyObject> getUnknownQuestions(Session sess) {
        Set<TerminologyObject> unknownQuestions = new HashSet<TerminologyObject>();
        for (TerminologyObject to : sess.getBlackboard().getValuedObjects()) {
            Fact mergedFact = sess.getBlackboard().getValueFact(to);
            if (mergedFact != null && Unknown.assignedTo(mergedFact.getValue())) {
                unknownQuestions.add(to);
            }
        }
        return unknownQuestions;
    }

    public static Set<TerminologyObject> getMCQuestions(Session sess) {
        Set<TerminologyObject> mcs = new HashSet<TerminologyObject>();
        for (TerminologyObject to : sess.getBlackboard().getValuedObjects()) {
            if (to instanceof QuestionMC) {
                mcs.add(to);

            }

        }
        return mcs;
    }

    public static String getFormattedDateFromString(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String f = sdf.format(date);
        return f;
    }

    public static Collection<TerminologyObject> getAbstractions(Session session) {
        Collection<TerminologyObject> abstractionQuestions =
                new ArrayList<TerminologyObject>();

        for (TerminologyObject aq : session.getKnowledgeBase().getManager().getQuestions()) {
            if (aq.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
                abstractionQuestions.add(aq);
            }
        }

        return abstractionQuestions;
    }

    public static Collection<TerminologyObject> getValuedAbstractions(Session session) {
        Collection<TerminologyObject> abstractionQuestions =
                getAbstractions(session);

        Collection<TerminologyObject> valuedAbstractions =
                new ArrayList<TerminologyObject>();

        Collection<TerminologyObject> valuedQuestions =
                session.getBlackboard().getValuedObjects();



        for (TerminologyObject abstracti : abstractionQuestions) {
            if (valuedQuestions.contains(abstracti)) {
                valuedAbstractions.add(abstracti);
            }
        }

        return valuedAbstractions;
    }

    public static boolean hasAnsweredChildren(TerminologyObject questionnaire, Session d3websession) {
        boolean has = false;
        if (questionnaire.getChildren().length > 0) {

            for (TerminologyObject child : questionnaire.getChildren()) {
                if (child instanceof QContainer) {
                    return hasAnsweredChildren(child, d3websession);
                } else if (child instanceof Question) {
                    Value val = d3websession.getBlackboard().getValue((ValueObject) child);
                    if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
                        return true;
                    }
                }
            }
        }

        return has;
    }

    /*
     * STUFF NEEDED FOR DATE QUESTIONS
     */
    public static String translateDropdownTitle(String titleID, int locIdent) {
        String translated = "";

        switch (locIdent) {
            case 1: // german
                if (titleID.equals("Y")) {
                    translated = "Jahr:";
                } else if (titleID.equals("M")) {
                    translated = "Monat:";
                } else if (titleID.equals("D")) {
                    translated = "Tag:";
                } else if (titleID.equals("H")) {
                    translated = "Stunde:";
                } else if (titleID.equals("Min")) {
                    translated = "Minute:";
                } else if (titleID.equals("S")) {
                    translated = "Sekunde:";
                }

                break;
            case 2: // english

                break;
            case 3: // spanish

                break;
            case 4: // italian

                break;
            case 5: // french

                break;
            case 6: // polish

                break;
        }
        if (translated == "") {
            if (titleID.equals("Y")) {
                translated = "Year";
            } else if (titleID.equals("M")) {
                translated = "Month";
            } else if (titleID.equals("D")) {
                translated = "Day";
            } else if (titleID.equals("H")) {
                translated = "Hour";
            } else if (titleID.equals("M")) {
                translated = "Minute";
            } else if (titleID.equals("S")) {
                translated = "Second";
            }
        }

        // emergency fallback
        return translated;
    }

    public static String createYearDropDownReverse(String selectedValue) {
        return createDropDownOptions(selectedValue, "Year", 2008, 2025);
    }

    public static String createYearDropDown(String selectedValue) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return createDropDownOptions(selectedValue, "Year", currentYear, 1900);
    }

    public static String createMonthDropDown(String selectedValue) {
        return createDropDownOptions(selectedValue, "Month", 1, 12);
    }

    public static String createDayDropDown(String selectedValue) {
        return createDropDownOptions(selectedValue, "Day", 1, 31);
    }

    public static String createHourDropDown(String selectedValue) {
        return createDropDownOptions(selectedValue, "Hour", 23);
    }

    public static String createMinuteDropDown(String selectedValue) {
        return createDropDownOptions(selectedValue, "Minute", 59);
    }

    public static String createSecondDropDown(String selectedValue) {
        return createDropDownOptions(selectedValue, "Second", 59);
    }

    public static String createDropDownOptions(String selectedValue, String name, int end) {
        return createDropDownOptions(selectedValue, name, 0, end);
    }

    public static String createDropDownOptions(String selectedValue, String name, int start, int end) {
        ArrayList<String> measure = new ArrayList<String>();
        boolean reverse = false;
        if (end < start) {
            reverse = true;
            int temp = start;
            start = end;
            end = temp;
        }
        for (int i = start; i <= end; i++) {
            measure.add(String.valueOf(i));
        }
        if (reverse) {
            Collections.reverse(measure);
        }
        return "<td><select type='" + name + "select' class='" + name + "select'>\n"
                + createDropDownOptionsWithDefault("", selectedValue,
                measure.toArray(new String[]{})) + "<select/></td>";
    }

    public static String createDropDownOptionsWithDefault(
            String defaultValue, String selectedValue, String... options) {
        StringBuilder builder = new StringBuilder();
        if (defaultValue != null) {
            builder.append("<option>" + defaultValue + "</option>\n");
        }

        for (String option : options) {
            option = option.trim();
            builder.append("<option value='" + option + "'"
                    + (option.equals(selectedValue) ? "selected='selected'" : "")
                    + ">" + option
                    + "</option>\n");
        }



        return builder.toString();
    }
}
