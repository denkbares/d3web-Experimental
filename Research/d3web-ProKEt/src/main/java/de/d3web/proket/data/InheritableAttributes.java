/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.data;

import org.w3c.dom.Element;

import de.d3web.proket.utils.XMLUtils;

/**
 * A container for inheritable Attributes of {@link IDialogObject}s. Those are
 * all attributes that can be inherited by elements from their parents.
 *
 * @author Martina Freiberg, Johannes Mitlmeier
 *
 */
public class InheritableAttributes {

    /**
     * Number of answers to be placed in a row next to each other
     */
    private int answerColumns = -1;
    /**
     * Type of the {@link Answer}, e.g. oc or mc
     */
    private String answerType;
    /**
     * Type of the connection---AND or OR---between sub-elements; used in
     * prototypes to imitate d3web rules
     */
    private String andOrType;
    /**
     * Number of columns in a box layout to be spanned by this
     * {@link IDialogObject}
     */
    private int colspan = -1;
    /**
     * Number of questions to be placed in a row next to each other.
     */
    private int columns = -1;
    /**
     * Maximum levels the attributes in the container shall be inherited; -1
     * means unlimited.
     */
    private int inheritanceLevel = -1;
    /**
     * Object associated with this {@link InheritableAttributes}.
     */
    private IDialogObject idialogobject;
    /**
     * ???? TODO
     */
    private Boolean selectRandomly;
    /**
     * Show a button for actively sending the given answers to the underlaying
     * system?
     */
    private Boolean sendButton;

    /**
     * Generate an {@link InheritableAttributes} from the XML tag linked in a
     * {@link IDialogObject}.
     *
     * @param object
     *            {@link IDialogObject} to take the XML tag from
     */
    public InheritableAttributes(IDialogObject object) {
        // save the reference to the object which uses this
        // InheritableAttributes
        this.idialogobject = object;
        Element element = object.getXMLTag();

        // id it exists
        if (element != null) {

            // id it is an answer-node
            if (element.getNodeName().equals("answer")) {

                // TODO: refactor: attributes need to be the same,
                String elementValue = XMLUtils.getStr(element, "type");
                if (elementValue != null) {
                    setAnswerType(elementValue); // set answer type

                } else {
                    // use parent question's type if in doubt
                    try {
                        setAnswerType(getObject().getParent().getType());
                    } catch (NullPointerException npe) {
                        // no problem, naturally possible
                    }
                }
            } else {

                // if element was no answer, the answer type can be
                // received by reading the answer-type attribute
                setAnswerType(XMLUtils.getStr(element, "answer-type"));
            }
        }
    }

    @Override
    /**
     * Method for cloning this set of InheritableAttributes
     */
    protected Object clone() throws CloneNotSupportedException {
        InheritableAttributes result = new InheritableAttributes(idialogobject);
        result.setAnswerColumns(this.getAnswerColumns());
        result.setInheritanceLevel(this.getInheritanceLevel());
        result.setColumns(this.getColumns());
        result.setAnswerType(this.getAnswerType());
        result.setSendButton(this.getSendButton());
        result.setSelectRandomly(this.getSelectRandomly());
        result.setColspan(this.getColspan());
        result.setAndOrType(this.getAndOrType());

        return result;
    }

    /**
     * Compiles this object, including recursively inheriting not set values
     * from parent {@link IDialogObject}s.
     */
    public InheritableAttributes compileInside() {
        return compileOn(this);
    }

    /**
     * Inherits attributes from the parents of the given InheritableAttributes
     *
     * @created 09.10.2010
     *
     * @param ac the InheritableAttributes that inherits from its parents
     * @return the InheritableAttributes that has inherited attributes from its
     * parents
     */
    private InheritableAttributes compileOn(InheritableAttributes ac) {

        // get the parent's IA
        InheritableAttributes parentStyle = getIAFromParent();
        int level = 1;

        if (ac == null) {
            return null;
        }

        while (parentStyle != null) {

            // inheritance allowed?
            if (parentStyle.isInheritable(level)) {
                ac.inherit(parentStyle);
            }

            // recursion: get the IA of the parent's parent, i.e.
            // go further up in the hierarchy, count up the level
            // that way, attributes are inherited top-down!
            parentStyle = parentStyle.getIAFromParent();
            level++;
        }

        // WHAT FOR? TODO... CHECK
        // get BasicType from root element
        parentStyle = this;
        while (true) {
            // recursion
            if (parentStyle.getIAFromParent() == null) {
                break;
            } else {
                parentStyle = parentStyle.getIAFromParent();
            }
        }

        return ac;
    }

    public int getAnswerColumns() {
        return answerColumns;
    }

    public String getAnswerType() {
        return answerType;
    }

    public String getAndOrType() {
        return andOrType;
    }

    public int getColspan() {
        return colspan;
    }

    public int getColumns() {
        return columns;
    }

    /**
     * Get the InheritableAttributes with inheriting everything from the parents
     * where necessary
     *
     * @created 09.10.2010
     *
     * @return the InheritableAttributes after inheriting
     */
    public InheritableAttributes getCompiled() {
        try {
            InheritableAttributes compiled = (InheritableAttributes) this.clone();
            return compileOn(compiled);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    public int getInheritanceLevel() {
        return inheritanceLevel;
    }

    public IDialogObject getObject() {
        return idialogobject;
    }

    /**
     * Gets the InheritableAttributes from the parent object if such exists
     *
     * @created 09.10.2010
     *
     * @return the parent's InheritableAttributes
     */
    public InheritableAttributes getIAFromParent() {
        if (getObject() == null) {
            return null;
        }
        if (getObject().getParent() == null) {
            return null;
        }
        return getObject().getParent().getInheritableAttributes();
    }

    public Boolean getSelectRandomly() {
        return selectRandomly;
    }

    public Boolean getSendButton() {
        return sendButton;
    }

    /**
     * Inherit the styles from the parent element
     *
     * @created 09.10.2010
     *
     * @param parentStyle the InheritableAttributes of the parent
     */
    public void inherit(InheritableAttributes parentStyle) {

        // if no parent style, return
        if (parentStyle == null) {
            return;
        }

        // inherit columns
        if (getAnswerColumns() == -1) {
            setAnswerColumns(parentStyle.getAnswerColumns());
        }

        // columns; check if the object connected with these attributes
        // is an Answer, becaus no inherit to answers. They need to be set
        // explicitly
        if (getColumns() == -1 && !(getObject() instanceof Answer)) {
            setColumns(parentStyle.getColumns());
        }

        // answer type
        if (getAnswerType() == null) {
            setAnswerType(parentStyle.getAnswerType());
        }

        // andor type
        if (getAndOrType() == null) {
            setAndOrType(parentStyle.getAndOrType());
        }

        // TODO: check if we need this select randomly
        // select randomly?
        if (getSelectRandomly() == null) {
            setSelectRandomly(parentStyle.getSelectRandomly());
        }

        // send button
        if (getSendButton() == null) {
            setSendButton(parentStyle.getSendButton());
        }

        // colspan, don't inherit to answers, they need to be set explicitly
        if (getColspan() == -1 && !(getObject() instanceof Answer)) {
            setColspan(parentStyle.getColspan());
        }
    }

    /**
     * Checks, whether thus InheritableAttributes is inheritable: it is, if its
     * level is larger or equal the tested level, or when the level is -1 (means
     * unlimited inheritance)
     *
     * @created 09.10.2010
     *
     * @param level
     * @return
     */
    public boolean isInheritable(int level) {
        return (getInheritanceLevel() >= level)
                || (getInheritanceLevel() == -1);
    }

    public void setAnswerColumns(Integer answerColumns) {
        if (answerColumns == null) {
            return;
        }
        this.answerColumns = answerColumns;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public void setAndOrType(String andOrType) {
        this.andOrType = andOrType;
    }

    public void setColspan(Integer colSpan) {
        if (colSpan == null) {
            return;
        }
        this.colspan = colSpan;
    }

    public void setColumns(Integer columns) {
        if (columns == null) {
            return;
        }
        this.columns = columns;
    }

    public void setInheritanceLevel(Integer inheritanceLevel) {
        if (inheritanceLevel == null) {
            return;
        }
        this.inheritanceLevel = inheritanceLevel;
    }

    public void setSelectRandomly(Boolean selectRandomly) {
        this.selectRandomly = selectRandomly;
    }

    public void setSendButton(Boolean sendButton) {
        this.sendButton = sendButton;
    }
}