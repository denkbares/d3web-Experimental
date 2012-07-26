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

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

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

    public static final Choice YES = new Choice("ja");
    public static final Choice NO = new Choice("nein");
    public static final Choice MAYBE = new Choice("vielleicht");
    public static final Value YES_VALUE = new ChoiceValue(YES);
    public static final Value NO_VALUE = new ChoiceValue(NO);
    public static final Value MAYBE_VALUE = new ChoiceValue(MAYBE);
    public static final Value UNKNOWN_VALUE = Unknown.getInstance();
    
    private QuestionOC parent;
    private HashMap<QuestionOC, List<Value>> children;
    // corresponds to OR-type! Default type is conjunction (AND)
    private boolean disjunctive;

    
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
    	int nrOfYesAnswers = 0;
    	int nrOfNoAnswers = 0;
    	int nrOfMaybeAnswers = 0;

    	// count the number of the respective answer types
        for (QuestionOC child : children.keySet()) {
            Blackboard bb = session.getBlackboard();
            if (bb.getAnsweredQuestions().contains(child)) {
            	Value v = bb.getValue(child); 
                if (v != null && UndefinedValue.isNotUndefinedValue(v)) {
                    if (YES_VALUE.equals(v)) nrOfYesAnswers++;
                    if (NO_VALUE.equals(v)) nrOfNoAnswers++;
                    if (MAYBE_VALUE.equals(v)) nrOfMaybeAnswers++;
                } else {
                    nrOfMaybeAnswers++;
                }
            }
        }
        return getFiringVal(nrOfYesAnswers, nrOfNoAnswers, nrOfMaybeAnswers, session);
    }
        
        
    private Fact getFiringVal(int nrOfYesAnswers, int nrOfNoAnswers, int nrOfMaybeAnswers, Session session) {
        if (!disjunctive) { // this is an AND-rule
        	if (nrOfNoAnswers > 0) {
        		return createFact(session, NO_VALUE);
        	} else { // no "no"-answers here
        		if (nrOfMaybeAnswers > 0) {
        			return createFact(session, MAYBE_VALUE);
        		} else if (nrOfYesAnswers > 0) { // all remaining answers must be yes-answers
            		return createFact(session, YES_VALUE);
        		}
        	}
        } else { // this is an OR-rule
        	if (nrOfYesAnswers > 0) {
        		return createFact(session, YES_VALUE);
        	} else { // no "yes" answers here
        		if (nrOfMaybeAnswers > 0) {
        			return createFact(session, MAYBE_VALUE);
        		} else if (nrOfNoAnswers > 0) { // all remaining answers must be no-answers
            		return createFact(session, NO_VALUE);
        		}
        	}
        }    	
    	return null; // should not happen 
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
    	if (obj instanceof JuriRule) {
            JuriRule other = (JuriRule) obj;
    		if (disjunctive != other.disjunctive) return false;
            if (parent == null) {
                if (other.parent != null) return false;
            } else if (!parent.equals(other.parent)) return false;
            if (!children.equals(other.children)) return false;
            return true;
    	} 
    	return false;
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





