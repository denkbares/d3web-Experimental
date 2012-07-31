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
import de.d3web.core.knowledge.Indication;
import java.util.HashMap;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.*;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.jurisearch.JuriModel;
import de.d3web.jurisearch.JuriRule;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.data.IndicationMode;
import de.d3web.proket.output.container.ContainerCollection;
import java.util.*;
import javax.servlet.http.HttpSession;

/**
 * Basic Renderer Class for d3web-based dialogs. Defines the basic rendering of
 * d3web dialogs and methods, required by all rendering sub-classes.
 *
 * TODO CHECK: 1) renderRoot: other dialog types or maybe write specific
 * renderers for each one particularly? Maybe better... 2) renderRoot: basic
 * properties such as header, title, HTML header... 3) check global JS 5)
 * IMPORTANT think about how to include mechanism to get specific renderes for
 * specific "dialogs", e.g by defining "hierarchic" in the XML and having
 * HierarchicQuestionnaireRenderer etc used automatically (or if not existing,
 * just return to base renderer.)
 *
 * TODO CHECK: what happens for more deeply nested question/f-u question
 * hierarchies? Also check an exit-condition for endless recursion!
 *
 * TODO LATER: 1) renderRoot: navigation 4) refactor D3webConnector to
 * class-variable?! 5) makeTables: add varying colspans from the XML
 * specification into this method one day 2) Handle cycles!!! 3) handle MC
 * Questions
 *
 * @author Martina Freiberg @created 13.01.2011
 */
public abstract class AbstractD3webRenderer implements D3webRenderer {

    private static HashMap<String, String> nameToIdMap = new HashMap<String, String>();
    private static HashMap<String, String> idToNameMap = new HashMap<String, String>();

    /**
     * Retrieves the appropriate renderer class according to what base object is
     * given from the d3web knowledge base. EXCLUDES answers, as those need a
     * specific handling.
     *
     * @created 14.01.2011
     *
     * @param to the TerminologyObject that needs to retrieve the renderer.
     * @return the suiting renderer class
     */
    public static IQuestionD3webRenderer getRenderer(TerminologyObject to) {

        IQuestionD3webRenderer renderer =
                (IQuestionD3webRenderer) D3webRendererMapping.getInstance().getRenderer(to);

        return renderer;
    }

    /**
     * Retrieves the appropriate renderer class for answers,according to what
     * base object (question type) is given.
     *
     * @created 15.01.2011
     *
     * @param to the TerminologyObject that needs to retrieve the answer
     * renderer.
     * @return the suiting renderer class.
     */
    public static AnswerD3webRenderer getAnswerRenderer(TerminologyObject to, Session d3webSession) {

        AnswerD3webRenderer renderer =
                D3webRendererMapping.getInstance().getAnswerRendererObject(to, d3webSession);

        return renderer;
    }

    /**
     * Retrieves the renderer for the Unknown object (unknown option for
     * dialogs).
     *
     * @created 23.01.2011
     *
     * @return the suiting renderer class.
     */
    public static AnswerD3webRenderer getUnknownRenderer() {

        AnswerD3webRenderer renderer =
                D3webRendererMapping.getInstance().getUnknownRenderer();

        return renderer;
    }

    /**
     * Renders the children of a given TerminologyObject and assembles the
     * result into the given StringTemplate and writes into the given
     * ContainerCollection.
     *
     * @created 15.01.2011
     *
     * @param st The StringTemplate for assembly
     * @param cc The ContainerCollection for writing into
     * @param to The TerminologyObject the children of which are rendered.
     */
    protected void renderChildren(StringTemplate st, Session d3webSession, ContainerCollection cc,
            TerminologyObject to, int loc, HttpSession httpSession) {

        boolean debug = false;

        if (httpSession.getAttribute("debug") != null) {
            String deb = httpSession.getAttribute("debug").toString();
            if (deb.equals("true")) {
                debug = true;
            }
        }


        Session s = ((Session) httpSession.getAttribute("d3webSession"));

        StringBuilder childrenHTML = new StringBuilder();
        D3webConnector d3wcon = D3webConnector.getInstance();

        // number of columns that is to be set for this element, default 1-col
        int columns = 1;
        if (to == d3webSession.getKnowledgeBase().getRootQASet()) {
            columns = d3wcon.getDialogColumns();
        } else if (to instanceof QContainer) {
            columns = d3wcon.getQuestionnaireColumns();
        } else if (to instanceof Question) {
            columns = d3wcon.getQuestionColumns();
        }

        // if more than one column is required, get open-table tag from
        // TableContainer and append it to the HTML
        if (columns > 1) {
            if (columns == 3
                    && to instanceof QContainer
                    && to.getChildren().length == 4) {
                // minor tweak to fix ugly display with 4 questions in one
                // questionnaire
                columns = 2;
            }
            String tableOpening =
                    cc.tc.openTable(to.getName().replace(" ", "_"), columns);
            childrenHTML.append(tableOpening);
        }

        // for each of the child elements
        TerminologyObject[] children = to.getChildren();
        // Blackboard bb = d3webSession.getBlackboard();
        for (TerminologyObject child : children) {


            // in debug mode, set everything to instant indicated!
            if (debug) {
                Fact f = FactFactory.createIndicationFact(
                        child,
                        new Indication(Indication.State.INSTANT_INDICATED),
                        PSMethodUserSelected.getInstance(),
                        PSMethodUserSelected.getInstance());
                d3webSession.getBlackboard().addInterviewFact(f);
            }


            // get the matching renderer
            IQuestionD3webRenderer childRenderer = AbstractD3webRenderer.getRenderer(child);

           //     System.out.println(d3webSession.getBlackboard().getValue((ValueObject) child));
            //    System.out.println(to.getName() + " - " + D3webUtils.isIndicated(to, d3webSession.getBlackboard()));
           //     System.out.println(child.getName() + " - " + D3webUtils.isIndicated(child, d3webSession.getBlackboard()));
            //    System.out.println(child.getName() + " CI - " + D3webUtils.isContraIndicated(child, d3webSession.getBlackboard()) + "\n");
           
            if (!debug) {
                // only show questions if they are NOT contraindicated
                // they ARE contraindicated by the EuraHS- only if construct
                if ((D3webConnector.getInstance().getIndicationMode() == IndicationMode.HIDE_UNINDICATED
                        && child instanceof Question
                        && D3webUtils.isContraIndicated(child, d3webSession.getBlackboard()))) {
                    continue;
                }
            }

            // receive the rendering code from the Renderer and append
            String childHTML =
                    childRenderer.renderTerminologyObject(d3webSession, cc, child, to, loc, httpSession);
            if (childHTML != null) {
                childrenHTML.append(childHTML);
            }

            // if the child is a question, check recursively for follow-up-qs
            // as this is done after having inserted the normal child, the
            // follow up is appended in between the child and its follow-up
            if (child instanceof Question) {
                childrenHTML.append(renderFollowUps(d3webSession, cc, child, to, loc, httpSession));
            }
        }

        // close the table that had been opened for multicolumn cases
        if (columns > 1) {
            String tableClosing = cc.tc.closeTable(to.getName().replace(" ", "_"));
            childrenHTML.append(tableClosing);
        }

        // if children, fill the template attribute children with children-HTML
        if (children.length > 0) {
            st.setAttribute("children", childrenHTML.toString());
        }
    }

    /**
     * Renders the choices of a given (question) TerminologyObject and assembles
     * the result into the given StringTemplate(s) and writes everything into
     * the given ContainerCollection.
     *
     * @created 15.01.2011
     *
     * @param st The StringTemplate
     * @param cc The ContainerCollection
     * @param to The TerminologyObject
     * @param d3webSession TODO
     */
    protected void renderChoices(StringTemplate st, ContainerCollection cc,
            TerminologyObject to, TerminologyObject parent, Session d3webSession,
            int loc, HttpSession httpSession) {

        StringBuilder childrenHTML = new StringBuilder();

        // number of columns that is to be set for this element
        int columns = 1;

        // CAREFUL: default setting: 1-col style for q's with input field,
        // ALSO if something different is set in xml
        if (to instanceof QuestionNum
                || to instanceof QuestionDate
                || to instanceof QuestionText) {
            columns = 1;
        } else if (D3webConnector.getInstance().getQuestionColumns() != -1) {
            columns = D3webConnector.getInstance().getQuestionColumns();
        } else {
            // default: set 2 columns for questions, i.e., answers displayed in
            // 2 cols
            columns = 2;
        }

        // if more than one column open table tag via TableContainer and
        // append
        if (columns > 1) {
            String tableOpening =
                    cc.tc.openTable(to.getName().replace(" ", "_"), columns);
            childrenHTML.append(tableOpening);
        }

        // for choice questions (oc only so far...)
        if (to instanceof QuestionChoice) {

            // here the grids are rendered for info questions
            if (to instanceof QuestionZC) {
                String gridString = to.getInfoStore().getValue(ProKEtProperties.GRID);
                if (gridString != null && !gridString.isEmpty()) {
                    childrenHTML.append(gridString);
                } // also image upload questions are based on ZC
                else {

                    // get the suiting child renderer (i.e., for answers)
                    AnswerD3webRenderer childRenderer = getAnswerRenderer(to, d3webSession);

                    // receive the matching HTML from the Renderer and append
                    Choice c = new Choice("uploadimages");
                    String childHTML =
                            childRenderer.renderTerminologyObject(cc, d3webSession, c, to, parent, loc, httpSession);
                    if (childHTML != null) {
                        childrenHTML.append(childHTML);
                    }
                }
            } else {


                String childHTML = "";
                AnswerD3webRenderer childRenderer = null;

                // if we have a dropdown based on choice question, we need to 
                // handle separately here before going into the all-choices loop
                String dropdownMenuOptions = to.getInfoStore().getValue(
                        ProKEtProperties.DROPDOWN_MENU_OPTIONS);

                if (dropdownMenuOptions != null) {

                    childRenderer = getAnswerRenderer(to, d3webSession);

                    childHTML =
                            childRenderer.renderTerminologyObject(cc, d3webSession, null, to, parent, loc, httpSession);
                    if (childHTML != null) {
                        childrenHTML.append(childHTML);
                    }

                } else {
                    for (Choice c : ((QuestionChoice) to).getAllAlternatives()) {

                        // get the suiting child renderer (i.e., for answers)
                        childRenderer = getAnswerRenderer(to, d3webSession);

                        // receive the matching HTML from the Renderer and append
                        childHTML =
                                childRenderer.renderTerminologyObject(cc, d3webSession, c, to, parent, loc, httpSession);

                        if (childHTML != null) {
                            childrenHTML.append(childHTML);
                        }
                    }
                    // }

                }
                // otherwise (num, text, date... questions)
            }
        } else {
            // get the suiting child renderer (i.e., for answers)
            AnswerD3webRenderer childRenderer = getAnswerRenderer(to, d3webSession);
            // System.out.println(childRenderer);

            // receive the matching HTML from the Renderer and append
            String childHTML =
                    childRenderer.renderTerminologyObject(cc, d3webSession, null, to, parent, loc, httpSession);
            if (childHTML != null) {
                childrenHTML.append(childHTML);
            }
        }

        // render unknown option only for NON-abstract questions
        if (!(to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION))
                && !(to instanceof QuestionZC)) {

            /*
             * Append result of the unknown-renderer, i.e., unknown option, if -
             * unknown by default option of KB is activated - AND nothing set
             * additionally for single questions
             */
            Boolean value = D3webConnector.getInstance().getKb().getInfoStore().getValue(
                    BasicProperties.UNKNOWN_VISIBLE);
            if (value != null && value) {

                Boolean toValue = to.getInfoStore().getValue(BasicProperties.UNKNOWN_VISIBLE);
                if (toValue == null || !toValue) {

                    AnswerD3webRenderer unknownRenderer = getUnknownRenderer();

                    // receive the matching HTML from the Renderer and append
                    String childHTML =
                            unknownRenderer.renderTerminologyObject(cc, d3webSession, null, to,
                            parent, loc, httpSession);
                    // System.out.println(childHTML);
                    if (childHTML != null) {
                        childrenHTML.append(childHTML);
                    }
                }
            }
        }

        // close the table that had been opened for multicolumn
        if (columns > 1) {
            String tableClosing = cc.tc.closeTable(to.getName().replace(" ", "_"));
            childrenHTML.append(tableClosing);
        }

        // if there had been children, fill the template attribute children
        if (childrenHTML.length() > 0) {
            st.setAttribute("children", childrenHTML.toString());
        }
    }

    /**
     * Handle the rendering of follow-up questions of question elements. If the
     * children of a question-child are questions again, those are inserted
     * right here (i.e., e.g. underneath the questionnaire), and next/right
     * after to the "parent"-question.
     *
     * @created 20.01.2011
     *
     * @param cc ContainerCollection to be used
     * @param child The (question) child of the TerminologyObject parent, that
     * might posess follow up questions.
     * @param parent The parent TerminologyObject.
     * @return
     */
    private String renderFollowUps(Session d3webSession, ContainerCollection cc, TerminologyObject child,
            TerminologyObject parent, int loc, HttpSession httpSession) {
        StringBuilder fus = new StringBuilder();

        boolean debug = false;

        if (httpSession.getAttribute("debug") != null) {
            String deb = httpSession.getAttribute("debug").toString();
            if (deb.equals("true")) {
                debug = true;
            }
        }

        // if child (question) has children and at least the 1st also a question
        if (child.getChildren() != null && child.getChildren().length != 0
                && child.getChildren()[0] instanceof Question) {


            // get the (probably question) children of the child
            for (TerminologyObject childsChild : child.getChildren()) {

                //System.out.println(childsChild.getName() + " Indicated: " 
                //      + D3webUtils.isIndicated(childsChild, d3webSession.getBlackboard())
                //    + " C-Indicated: " + D3webUtils.isContraIndicated(childsChild, d3webSession.getBlackboard()));
                if (!debug) {
                    if ((D3webConnector.getInstance().getIndicationMode() == IndicationMode.HIDE_UNINDICATED
                            && (D3webUtils.isContraIndicated(childsChild, d3webSession.getBlackboard()))
                            || (!isIndicated(childsChild, d3webSession.getBlackboard())))) {
                        continue;
                    }
                }

                // get appropriate renderer
                IQuestionD3webRenderer childRenderer = AbstractD3webRenderer.getRenderer(childsChild);

                // receive the rendering code from the Renderer and append
                StringBuilder childHTML = new StringBuilder(
                        childRenderer.renderTerminologyObject(d3webSession, cc, childsChild, parent, loc, httpSession));
                if (child instanceof Question) {
                    childHTML.append(renderFollowUps(d3webSession, cc, childsChild, parent, loc, httpSession));
                }
                if (childHTML != null) {
                    fus.append(childHTML);
                }
            }
        }
        return fus.toString();
    }

    /**
     * Prepares the table-framing for a given TerminologyObject by opening a new
     * cell from the "parent's view", closing it properly, adding those cells to
     * the given StringBuilder (i.e. opening BEFORE the to, and closing AFTER
     * the to) and adding everything to the CodeCollection.
     *
     * @created 15.01.2011
     *
     * @param to The TerminologyObject
     * @param parentID The parent or the TerminologyObject
     * @param cc The ContainerCollection
     * @param result The StringBuilder of the TerminologyObject that is
     * decorated.
     */
    protected void makeTables(TerminologyObject to, TerminologyObject parent,
            ContainerCollection cc, StringBuilder result) {

        // get the parent. If not existent, return
        if (parent.getName() == null) {
            return;
        }

        // int colspan = dialogObject.getInheritableAttributes().getColspan();
        int colspan = 1;

        // insert table cell opening string before the content of result which
        // is the content/rendering of the dialog object itself
        result.insert(0,
                cc.tc.getNextCellOpeningString(parent.getName().replace(" ", "_"), colspan));

        // append table cell closing
        result.append(cc.tc.getNextCellClosingString(parent.getName().replace(" ", "_"), colspan));

        // add to the table container
        cc.tc.addNextCell(parent.getName().replace(" ", "_"), colspan);
    }

    /**
     * Create tables structures (tr and td) for surrounding the given Choice
     * object.
     *
     * @created 23.01.2011
     *
     * @param c The choice to be put into the table.
     * @param parent The parent TerminologyObject, needed for
     * finalizing/inserting the table structure.
     * @param cc ContainerCollection to be used.
     * @param result StringBuilder, the tables are inserted into.
     */
    protected void makeTables(Choice c, TerminologyObject parent,
            ContainerCollection cc, StringBuilder result) {

        // get the parent. If not existent, return
        if (parent.getName() == null) {
            return;
        }

        // int colspan = dialogObject.getInheritableAttributes().getColspan();
        int colspan = 1;

        // insert table cell opening string before the content of result which
        // is the content/rendering of the dialog object itself
        result.insert(0,
                cc.tc.getNextCellOpeningString(parent.getName().replace(" ", "_"), colspan));

        // append table cell closing
        result.append(cc.tc.getNextCellClosingString(parent.getName().replace(" ", "_"), colspan));

        // add to the table container
        cc.tc.addNextCell(parent.getName().replace(" ", "_"), colspan);
    }

    /**
     * Retrieves the suiting StringTemplate (html) for a given base object name.
     * MAYBE MOVE TO TEMPLATE UTILS
     *
     * @created 23.01.2011
     *
     * @param baseObjectName Name of the base object.
     * @return The suitable StringTemplate name.
     */
    public String getTemplateName(String baseObjectName) {
        String tempName = "";
        D3webConnector d3w = D3webConnector.getInstance();
        String up = d3w.getUserprefix();
        if (up != "" && up != null) {
            // hier evtl noch einfügen Prüfung auf Großbuchstaben oder
            // automatisch umwandeln
            tempName = D3webConnector.getInstance().getUserprefix() + baseObjectName;
        } else {
            tempName = baseObjectName;
        }
        return tempName;
    }

    @Override
    public boolean isIndicated(TerminologyObject to, Blackboard bb) {
        // find the appropriate qaset in the knowledge base
        if ((bb.getIndication((InterviewObject) to).getState() == State.INDICATED || bb.getIndication(
                (InterviewObject) to).getState() == State.INSTANT_INDICATED)) {
            return true;
        }

        return false;
    }

    public boolean isParentOfFollowUpQuIndicated(TerminologyObject to, Blackboard bb) {
        for (Question q : bb.getSession().getKnowledgeBase().getManager().getQuestions()) {

            // and check its indication state
            if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(q)
                    || bb.getIndication(q).getState() == State.INDICATED
                    || bb.getIndication(q).getState() == State.INSTANT_INDICATED) {

                return true;
            }
        }
        return false;
    }

    public static String getID(NamedObject no) {

        String id = nameToIdMap.get(no);
        if (id == null) {
            String prefix = "";

            if (no instanceof Question) {
                prefix = "q";
            } else if (no instanceof QContainer) {
                prefix = "qc";
            } else if (no instanceof Choice) {
                prefix = "a_" + getID(((Choice) no).getQuestion());
            }
            if (no != null && no.getName() != null) {
                id = prefix + "_" + no.getName().replaceAll("\\W", "_");
                nameToIdMap.put(no.getName(), id);
                idToNameMap.put(id, no.getName());
            } else {
                // TODO: make generic
                id = prefix + " " + "imageUpload";
            }
        }
        return id;
    }

    // TODO: answer buttons need to be rendered by answer renderer, not within question
    public static String getObjectNameForId(String id) {
        return idToNameMap.get(id);
    }

    /**
     * TODO: maybe remove
     * traverse all jurisearch rules and filter out the one(s) containing the
     * currently rendered Terminology Object as parent
     *
     * @param parent the parent element the children of which are searched
     * @return ArrayList<QuestionOC> the list of child QuestionOCs
     */
    protected ArrayList<QuestionOC> getChildQuestionsFromJuriRules(TerminologyObject parent,
            Set juriRules) {

        ArrayList<QuestionOC> toChildren = new ArrayList<QuestionOC>();

        if (juriRules != null && juriRules.size() != 0) {
            for (Object o : juriRules) {
                JuriRule rule = (JuriRule) o;
                if (rule.getParent().getName().equals(parent.getName())) {
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

    /**
     * TODO: maybe remove
     * 
     * Check, whether a given terminology object is rated by its children by an
     * OR connection (default: AND connection)
     *
     * @param to
     * @param juriRules
     * @return
     */
    protected boolean isOrType(TerminologyObject to, Set juriRules) {

        if (juriRules != null && juriRules.size() != 0) {
            for (Object o : juriRules) {
                JuriRule rule = (JuriRule) o;
                if (rule.getChildren().containsKey(to)) {
                    //System.out.println(rule);
                    //F System.out.println(rule.isDisjunctive());
                    return rule.isDisjunctive();
                }
            }
        }
        return false;
    }

    /**
     * TODO: May be remove
     * 
     * Check whether a given terminology object rates the parent question in a
     * swapped y/n manner, i.e. "NO" is the positively rating answer.
     *
     * @param to
     * @param juriRules
     * @return
     */
    public static boolean isNoDefining(TerminologyObject to, Set juriRules) {
        if (juriRules != null && juriRules.size() != 0) {
            for (Object o : juriRules) {
                JuriRule rule = (JuriRule) o;

                // get all rules, where to is CHILD
                if (rule.getChildren().containsKey(to)) {

                    HashMap<QuestionOC, List<Value>> children = rule.getChildren();
                    for (Object ooc : children.keySet()) {

                        QuestionOC qoc = (QuestionOC) ooc;
                        if (children.get(qoc).contains(JuriRule.NO_VALUE)) {
                            if (qoc.equals(to)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * TODO aMaybe remove
     * @param st
     * @param d3webSession
     * @param cc
     * @param to
     * @param loc
     * @param httpSession 
     */
    protected void renderChildrenITree(StringTemplate st, Session d3webSession, ContainerCollection cc,
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

                st.setAttribute("children", childrenHTML.toString());

            }
        } else {
            JuriModel juriModel =
                    d3wcon.getKb().getKnowledgeStore().getKnowledge(JURIMODEL);
            Set juriRules = juriModel.getRules();

            // get the children of the current to from the juri rules
            List<QuestionOC> toChildren = getChildQuestionsFromJuriRules(to, juriRules);

            if (toChildren != null && !toChildren.isEmpty()) {

                for (Object newChildRoot : toChildren) {

                    IQuestionD3webRenderer childRenderer = null;
                    TerminologyObject newChild = (TerminologyObject) newChildRoot;

                    Boolean isDummy =
                            newChild.getInfoStore().getValue(
                            Property.getProperty("dummy", Boolean.class));

                    // TODO: render dummy nodes specially 
                    if (isDummy != null && isDummy.equals(true)) {
                        childRenderer = D3webRendererMapping.getInstance().getDummyITreeRenderer();
                    } else {
                        childRenderer =
                                AbstractD3webRenderer.getRenderer((TerminologyObject) newChildRoot);

                    }


                    String childHTML =
                            childRenderer.renderTerminologyObject(d3webSession, cc, (TerminologyObject) newChildRoot, to, loc, httpSession);
                    if (childHTML != null) {
                        childrenHTML.append(childHTML);
                    }

                }
                // if children, fill the template attribute children with children-HTML 
                st.setAttribute("children", childrenHTML.toString());
            }
        }

    }
    
    
     protected void renderChildrenITreeNum(StringTemplate st, Session d3webSession, ContainerCollection cc,
            TerminologyObject to, int loc, HttpSession httpSession) {

        StringBuilder childrenHTML = new StringBuilder();
        D3webConnector d3wcon = D3webConnector.getInstance();
        
        if (to.getName().equals("Q000")) {
            TerminologyObject rootNode = to.getChildren()[0].getChildren()[0];
           
            if (rootNode != null) {

                IQuestionD3webRenderer childRenderer =
                        AbstractD3webRenderer.getRenderer(rootNode);
               
                String childHTML =
                        childRenderer.renderTerminologyObject(d3webSession, cc, rootNode, to, loc, httpSession);
                if (childHTML != null) {
                    childrenHTML.append(childHTML);
                }

                st.setAttribute("children", childrenHTML.toString());

            }
        } else {
            
            // get the children of the current to from the juri rules
            TerminologyObject[] toChildren = to.getChildren();
            
            if (toChildren != null && toChildren.length > 0) {

                for (Object newChildRoot : toChildren) {

                    IQuestionD3webRenderer childRenderer = null;
                    TerminologyObject newChild = (TerminologyObject) newChildRoot;

                    Boolean isDummy =
                            newChild.getInfoStore().getValue(ProKEtProperties.DUMMY);

                    // TODO: render dummy nodes specially 
                    if (isDummy != null && isDummy.equals(true)) {
                        childRenderer = D3webRendererMapping.getInstance().getDummyITreeRenderer();
                    } else {
                        childRenderer =
                                AbstractD3webRenderer.getRenderer((TerminologyObject) newChildRoot);

                    }


                    String childHTML =
                            childRenderer.renderTerminologyObject(d3webSession, cc, (TerminologyObject) newChildRoot, to, loc, httpSession);
                    if (childHTML != null) {
                        childrenHTML.append(childHTML);
                    }

                }
                // if children, fill the template attribute children with children-HTML 
                st.setAttribute("children", childrenHTML.toString());
            }
        }

    }
     

    protected String createDropDownOptions(int loc, String selectedValue, String... options) {
        StringBuilder builder = new StringBuilder();

        for (String option : options) {
            option = option.trim();
            if(option.equals("Please select...")){
                option = D3webUtils.getDropdownDefaultPrompt(loc);
            }
            builder.append("<option value='" + option + "'"
                    + (option.equals(selectedValue) ? "selected='selected'" : "")
                    + ">" + option
                    + "</option>\n");
        }



        return builder.toString();
    }

    protected String createDropDownOptionsWithDefault(
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
