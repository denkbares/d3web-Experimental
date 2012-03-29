/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.data;

import de.d3web.proket.utils.ClassUtils;

/**
 *
 * @author mafre
 */
public class LegalQuestion extends Question {

    protected String defining;
    protected String choices;
    protected Boolean dummy = false;

    public LegalQuestion() {
        super();
        style = new InheritableAttributes(this);
        VCNbase = "Question";
    }
    
    public void setDefining(String defining) {
        this.defining = defining;
    }

    public String getDefining() {
        return this.defining;
    }

    public void setDummy(Boolean dummy) {
        this.dummy = dummy;
    }

    public Boolean getDummy() {
        return this.dummy;
    }
    
    public void setChoices(String cs) {
        this.choices = cs;
    }

    public String getChoices() {
        return this.choices;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getVirtualClassName() {
        IDialogObject dialog = getRootParent();
        String vcn = ClassUtils.getVirtualClassName(dialog.getSubType(), dialog.getType(), getSubType(), getType() != null ? getType()
                : getInheritableAttributes().getAnswerType(), VCNbase);
       return vcn;
    }
}
