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

import de.d3web.proket.utils.ClassUtils;

/**
 * Models questions. TODO bonus texts in templates.
 *
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public class Question extends DefaultDialogObject {

    /**
     * Adiitional text associated with the question. Intended as more detailed
     * description than a tooltip.
     */
    protected String bonusText;
    protected Boolean sendButton = false;
    protected Boolean selectBox = false;
    protected String counter = "";
    protected String defining;
    
    // Default simple Constructor that initializes inherit.attributes and
    // the basic description String
    public Question() {
        style = new InheritableAttributes(this);
        VCNbase = "Question";
    }

    public String getBonusText() {
        return bonusText;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setDefining(String defining) {
        this.defining = defining;
    }

    public String getDefining() {
        return this.defining;
    }
    
    public Boolean getSendButton() {
        return sendButton;
    }

    public Boolean getSelectBox() {
        return selectBox;
    }

    @Override
    public String getVirtualClassName() {
        IDialogObject dialog = getRootParent();
        return ClassUtils.getVirtualClassName(dialog.getSubType(), dialog.getType(), getSubType(), getType() != null ? getType()
                : getInheritableAttributes().getAnswerType(), VCNbase);
    }

    public void setBonusText(String bonusText) {
        this.bonusText = bonusText;
    }

    public void setSendButton(Boolean sendButton) {
        this.sendButton = sendButton;
    }

    public void setSelectBox(Boolean selectBox) {
        this.selectBox = selectBox;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getCounter() {
        return this.counter;
    }
}
