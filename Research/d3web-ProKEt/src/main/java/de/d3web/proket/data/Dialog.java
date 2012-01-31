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

import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.proket.utils.ClassUtils;

/**
 * Models a dialog. This is the root element as well in the xml specification as
 * in the {@link DialogTree}. A dialog can have {@link Questionnaire}s and
 * {@link Question}s as children.
 *
 * @author Martina Freiberg, Johannes Mitlmeier
 *
 */
public class Dialog extends DefaultDialogObject {

    /**
     * Text to be displayed in the foot part of the template. This string is
     * inserted and parsed as html code.
     */
    protected String footer;
    /**
     * Text to be displayed in the head part of the template. This string is
     * inserted and parsed as html code.
     */
    protected String header;
    /**
     * Boolean value indicating if we shall create additional navigation for
     * questionnaires ("next").
     */
    protected Boolean questionnaireNav = false;
    /**
     * Text to be displayed in the sidebar of the template, if it is a dialog
     * which displays solutions, the content is placed below of that usually.
     * This string is inserted and parsed as html code.
     */
    protected String sidetext;
    protected Boolean logging = false;
    protected Boolean feedback = false;
    protected Boolean uequest = false;

    public Dialog() {
        style = new InheritableAttributes(this);
        VCNbase = "Dialog";
    }

    public String getFooter() {
        return footer;
    }

    public String getHeader() {
        return header;
    }

    @Override
    public String getId() {
        return id;
    }

    public Boolean getQuestionnaireNav() {
        return questionnaireNav;
    }

    public String getSidetext() {
        return sidetext;
    }

    @Override
    public String getVirtualClassName() {
        return ClassUtils.getVirtualClassName(getSubType(), getType(), VCNbase);
    }

    public Boolean isQuestionnaireNav() {
        return questionnaireNav;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setQuestionnaireNav(Boolean questionnaireNav) {
        this.questionnaireNav = questionnaireNav;
    }

    public void setSidetext(String sidetext) {
        this.sidetext = sidetext;
    }

    public void setLogging(Boolean logging) {
        this.logging = logging;
    }

    public Boolean isLogging() {
        return this.logging;
    }
    
    public void setFeedback(Boolean fb) {
        this.feedback = fb;
    }

    public Boolean hasFeedback() {
        return this.feedback;
    }
    
    public void setUequest(Boolean ueq) {
        this.uequest = ueq;
    }

    public Boolean hasUequest() {
        return this.uequest;
    }
}
