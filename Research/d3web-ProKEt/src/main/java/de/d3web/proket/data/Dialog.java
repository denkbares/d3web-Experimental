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

	protected QASet d3webRoot;



	/**
	 * Determine if we are dealing with a d3web dialog which is necessary to
	 * know in some cases.
	 */
	protected boolean d3web = false;
	/**
	 * Indicates the basic type of the dialog, so that d3web can adapt its
	 * nextFormStrategy to it.
	 */
	protected DialogType d3webDialogType = DialogType.MULTFORM;

	protected DialogStrategy d3webDialogStrategy = DialogStrategy.NEXTFORM;
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

	public Dialog() {
		style = new InheritableAttributes(this);
		VCNbase = "Dialog";
	}

	public DialogType getD3webDialogType() {
		return d3webDialogType;
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

	public boolean isD3web() {
		return d3web;
	}

	public Boolean isQuestionnaireNav() {
		return questionnaireNav;
	}

	public void setD3web(boolean d3web) {
		this.d3web = d3web;
	}

	public void setD3webDialogType(DialogType d3webDialogType) {
		this.d3webDialogType = d3webDialogType;

		// the type indicates some things
		/*
		 * if (d3webDialogType == DialogType.HIER) { setType("Hierarchy");
		 * setCss("mimic_hierarchy_classic, nohead, nofoot"); } else if
		 * (d3webDialogType == DialogType.COLHIER) { setType("Hierarchy");
		 * setSubType("Color");
		 * setCss("mimic_colorhierarchy_classic, nonavigation, nohead, nofoot");
		 * }
		 */
	}

	public void setD3webDialogStrategy(DialogStrategy strat) {
		this.d3webDialogStrategy = strat;

		// the type indicates some things
		/*
		 * if (d3webDialogType == DialogType.HIER) { setType("Hierarchy");
		 * setCss("mimic_hierarchy_classic, nohead, nofoot"); } else if
		 * (d3webDialogType == DialogType.COLHIER) { setType("Hierarchy");
		 * setSubType("Color");
		 * setCss("mimic_colorhierarchy_classic, nonavigation, nohead, nofoot");
		 * }
		 */
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


	public void setd3webRoot(QASet root) {

		this.d3webRoot = root;
	}


	public QASet getd3webRoot() {
		// TODO Auto-generated method stub
		return this.d3webRoot;
	}
}
