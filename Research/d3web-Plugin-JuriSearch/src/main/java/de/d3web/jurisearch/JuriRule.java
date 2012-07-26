/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.jurisearch;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import java.util.*;

/**
 * This class represents a tailored rule type for the hierarchical
 * (clarification) dialog with specific yes/no/maybe questions. Thereby, one
 * such rule describes the relation between exactly one parent and potentially
 * several children
 *
 * Such questions are ordered hierarchically (and by a parent-child relation
 * internally) and the values/rating of children is propagated via the parents
 * completely up in the tree hierarchy. Thereby, certain AND/OR connections are
 * implemented which aggregate the value of a parent based on its childens'
 * values.
 *
 * Thereby, each Rule has exactly one parent object, and a list of several
 * children.
 *
 * POTENTIAL ADAPTIONS - include ITreeObjects for representing parent and
 * children - integrate other confirming values than yes and no (when swapped
 * rule)
 *
 * @author grotheer @created 04.05.2012
 * @author martina freiberg; major adaption july 2012
 */
public class JuriRule implements KnowledgeSlice, Comparable<JuriRule> {

    /*
     * some common values
     */
    public static final Choice YES = new Choice("ja");
    public static final Choice NO = new Choice("nein");
    public static final Choice MAYBE = new Choice("vielleicht");
    public static final Value YES_VALUE = new ChoiceValue(YES);
    public static final Value NO_VALUE = new ChoiceValue(NO);
    public static final Value MAYBE_VALUE = new ChoiceValue(MAYBE);
    public static final Value UNKNOWN_VALUE = Unknown.getInstance();
    private QuestionOC parent;
    private HashMap<QuestionOC, List<Value>> children;
    private ArrayList<Value> allValuesOfParent;
    // corresponds to OR-type! Default type is conjunction (AND)
    private boolean disjunctive;

    /*
     * some constructors...
     */
    public JuriRule(QuestionOC father) {
        this(father, new HashMap<QuestionOC, List<Value>>());
    }

    public JuriRule(QuestionOC father, List<QuestionOC> children) {
        this(father, new HashMap<QuestionOC, List<Value>>());
        addChildren(children);
    }

    public JuriRule(QuestionOC father, HashMap<QuestionOC, List<Value>> children) {
        this.parent = father;
        this.children = children;
        disjunctive = false;
    }

    /**
     * Getter for the parent @created 04.05.2012
     *
     * @return The parent object contained in this rule
     */
    public QuestionOC getParent() {
        return parent;
    }

    /**
     * Setter for the parent @created 04.05.2012
     *
     * @param parent The parent object containend in this rule
     */
    public void setFather(QuestionOC parent) {
        this.parent = parent;
    }

    /**
     * Getter for the children @created 04.05.2012
     *
     * @return A HashMap containing the children contained in this rule
     */
    public HashMap<QuestionOC, List<Value>> getChildren() {
        return children;
    }

    /**
     * Setter for the children @created 04.05.2012
     *
     * @param children The children to be contained in this rule
     */
    public void setChildren(HashMap<QuestionOC, List<Value>> children) {
        this.children = children;
    }

    /**
     * Add a child to the rule with the default confirming value YES @created
     * 30.03.2012
     *
     * @param q
     */
    public void addChild(QuestionOC q) {
        addChild(q, YES_VALUE);
    }

    /**
     * Add a child to the rule with the single confirming value @created
     * 30.03.2012
     *
     * @param q The question object
     * @param confirmingValue The confirming Value
     */
    public void addChild(QuestionOC q, Value confirmingValue) {
        List<Value> list = new LinkedList<Value>();
        list.add(confirmingValue);
        children.put(q, list);
    }

    /**
     * Add a child to the rule with multiple confirming values @created
     * 30.03.2012
     *
     * @param q The question object
     * @param confirmingValues The confirming values
     */
    public void addChild(QuestionOC q, List<Value> confirmingValues) {
        children.put(q, confirmingValues);
    }

    /**
     * Add multiple children with the default confirming value @created
     * 30.03.2012
     *
     * @param c A collection of Question objects
     */
    public void addChildren(Collection<QuestionOC> c) {
        for (QuestionOC q : c) {
            addChild(q);
        }
    }

    /**
     * Add multiple children with multiple confirming values @created 30.03.2012
     *
     * @param m A HashMap containing children with several confirming values
     * each
     */
    public void addChildren(HashMap<QuestionOC, List<Value>> m) {
        children.putAll(m);
    }

    /**
     * Remove a single child @created 04.05.2012
     *
     * @param q The Question object to be removed from the rule's children
     */
    public void removeChild(QuestionOC q) {
        children.remove(q);
    }

    /**
     * Queries the connection type of the rule: Disjunctive (true) means
     * OR-Type, otherwise AND-Type @created 04.05.2012
     *
     * @return true if rule is disjunctive, else false
     */
    public boolean isDisjunctive() {
        return disjunctive;
    }

    /**
     * Define if the rule is disjunctive (OR-Type) or not (AND-Type) @created
     * 04.05.2012
     *
     * @param disjunctive
     */
    public void setDisjunctive(boolean disjunctive) {
        this.disjunctive = disjunctive;
    }

    /**
     * Create (fire) a fact. Therefore check all child rules of a parent are
     * evaluated according to the following scheme:<br> - Wenn A = ja und B = ja
     * dann X = ja“ bedeutet: <br> - Wenn A = ja und B = ja dann X = ja, <br> -
     * Wenn A = nein oder B = nein dann X = nein, <br> - Wenn A = vielleicht und
     * B = ja, dann X = Vielleicht, <br> - Wenn B = Vielleicht und A = ja, dann
     * X = Vielleicht.<br>
     *
     * - Wenn A = ja oder B = ja dann X = ja“ bedeutet: <br> - Wenn A = ja oder
     * B = ja dann X = ja, <br> - Wenn A = nein und B = nein dann X = nein, <br>
     * - Wenn A = vielleicht und B = nein, dann X = Vielleicht, <br> - Wenn B =
     * Vielleicht und A = nein, dann X = Vielleicht.<br>
     *
     * Die Semantik ist bei: --> AND/UND-Verknüpfung: Wenn alle Vorbedingungen
     * wahr sind, ist die Nachbedingung wahr. Wenn eine Vorbedingung falsch ist,
     * ist die Nachbedingung falsch. Ansonsten wenn alle Vorbedingungen entweder
     * wahr oder vielleicht sind, ist die Nachbedingung vielleicht. <br>
     * Oder-Verknüpfung: Wenn alle Vorbedingungen falsch sind, ist die
     * Nachbedingung falsch. Wenn eine Vorbedingung wahr ist, ist die
     * Nachbedingung wahr. Ansonsten wenn alle Vorbedingungen entweder falsch
     * oder vielleicht sind, ist die Nachbedingung vielleicht.
     *
     * @created 04.05.2012, extensively adapted July 2012
     *
     * @param session The current d3web Session
     * @return derived fact The d3web Fact which is stored on the blackboard of
     * the knowledge base
     */
    public Fact fire(Session session) {

        allValuesOfParent = new ArrayList<Value>();

        // run over all children of the rule, and store the value of each of them
        for (Entry<QuestionOC, List<Value>> child : children.entrySet()) {
            ChoiceValue value = null;
            // get the value for this child from the blackboard
            if (session.getBlackboard().getAnsweredQuestions().contains(child.getKey())) {

                if (session.getBlackboard().getValue(child.getKey()) != null
                        && UndefinedValue.isNotUndefinedValue(session.getBlackboard().getValue(child.getKey()))) {
                    value = (ChoiceValue) session.getBlackboard().getValue(child.getKey());

                    // if there is a value - apart from undefined or unknown - store it as is
                    allValuesOfParent.add(value);
                } else {

                    // otherwise, i.e. for undefined or unknown, store UNKNOWN value
                    allValuesOfParent.add(UNKNOWN_VALUE);
                }
            }
        }

        /*
         * AND connection evaluation
         */
        if (!disjunctive) {

            // one val yes, no val unknown, no val maybe, no val no -> parent yes
            if (allValuesOfParent.contains(YES_VALUE)
                    && !(allValuesOfParent.contains(UNKNOWN_VALUE))
                    && !(allValuesOfParent.contains(MAYBE_VALUE))
                    && !(allValuesOfParent.contains(NO_VALUE))) {
                return createFact(session, YES_VALUE);
            } // one value maybe, no value no -> parent maybe
            else if (allValuesOfParent.contains(MAYBE_VALUE)
                    && !(allValuesOfParent.contains(NO_VALUE))) {
                return createFact(session, MAYBE_VALUE);
            } // one value no --> parent no
            else if (allValuesOfParent.contains(NO_VALUE)) {
                return createFact(session, NO_VALUE);
            } // otherwise no value set for parent
            else {
                return null;
            }
        } /*
         * OR connection evaluation
         */ else {

            // one val yes --> parent yes
            if (allValuesOfParent.contains(YES_VALUE)) {
                return createFact(session, YES_VALUE);
            } // one val maybe, no unknown, no yes contained --> parent maybe
            else if (allValuesOfParent.contains(MAYBE_VALUE)
                    && !(allValuesOfParent.contains(UNKNOWN_VALUE))
                    && !(allValuesOfParent.contains(YES_VALUE))) {
                return createFact(session, MAYBE_VALUE);
            } // one val no, no unknown, no yes, no maybe -> parent no
            else if (allValuesOfParent.contains(NO_VALUE)
                    && !(allValuesOfParent.contains(MAYBE_VALUE))
                    && !(allValuesOfParent.contains(YES_VALUE))
                    && !(allValuesOfParent.contains(UNKNOWN_VALUE))) {
                return createFact(session, NO_VALUE);
            } // otherwise: no fact needs to be set
            else {
                return null;
            }
        }


        /*
         * THE OLD STUFF, remove if new stuff works fine <br>
         *
         * if (value != null) { // find out, if the value equals one of the //
         * specified confirming values boolean oneOfConfirmingValues = false;
         * for (ChoiceValue confirmingValue : child.getValue()) { if
         * (value.equals(confirmingValue)) { oneOfConfirmingValues = true; } }
         *
         * // if it is a AND question if (!disjunctive) {
         *
         * if (!oneOfConfirmingValues) { return createFact(session, NO_VALUE); }
         *
         * } else { // directly set parent to "yes" if the rule is disjunctive
         * // and has at least one child, which is set to a confirming // value.
         * if (oneOfConfirmingValues) { return createFact(session, YES_VALUE); }
         * }
         *
         * // set the maybe flag, if one child is set to the maybe value. if
         * (value.equals(MAYBE_VALUE)) { maybe = true; } } else { if
         * (!disjunctive) { // if a child question is not jet answered and the
         * rule is // not disjunctive, no new fact is returned (return null)
         * return null; } } }
         *
         * // if the for-loop is passed, there are four cases: if (maybe) { //
         * matches two cases: // 1. the rule is not disjunctive and all child
         * question values are // "maybe" or "yes" // 2. the rule is disjunctive
         * and all child question values are // "maybe" or "no" return
         * createFact(session, MAYBE_VALUE); } if (!disjunctive) { // the rule
         * is not disjunctive and all child question values are // "yes" return
         * createFact(session, YES_VALUE); } else { // the rule is disjunctive
         * and all child question values are "no" return createFact(session,
         * NO_VALUE); }
         */
    }

    /**
     * Utility method for creating the d3web facts; thereby, the PSMethodJuri
     * needs to be used, as to mark those facts as JuriRule-automatically-set
     * facts.
     *
     * @param session The current d3web session
     * @param value The value, a fact needs be created for
     * @return The fact
     */
    private Fact createFact(Session session, Value value) {
        return FactFactory.createFact(session, parent, value, this,
                session.getPSMethodInstance(PSMethodJuri.class));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        result = prime * result + (disjunctive ? 1231 : 1237);
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JuriRule other = (JuriRule) obj;
        if (children == null) {
            if (other.children != null) {
                return false;
            }
        } else if (!children.equals(other.children)) {
            return false;
        }
        if (disjunctive != other.disjunctive) {
            return false;
        }
        if (parent == null) {
            if (other.parent != null) {
                return false;
            }
        } else if (!parent.equals(other.parent)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "JuriRule [parent=" + parent + ", children=" + children + ", disjunctive="
                + disjunctive + "]";
    }

    @Override
    public int compareTo(JuriRule o) {
        // TODO Auto-generated method stub
        return hashCode() - o.hashCode();
    }
}





