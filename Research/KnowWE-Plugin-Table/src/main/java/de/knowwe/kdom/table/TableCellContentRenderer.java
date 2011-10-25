/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.kdom.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * <p>
 * Renders the content of a <code>TableCellContent</code> element depending on
 * the state of the QuickEditFlag. If <code>TRUE</code> each cell is rendered as
 * an HTML input field, containing the text of the cell.
 * </p>
 * <p>
 * If the <code>value</code> attribute (@see Table) is given the input filed is
 * replaced by an drop down list. If <code>FALSE</code> simple text is rendered.
 * </p>
 * 
 * <p>
 * e.g:
 * </p>
 * <code>
 * Cell given in JSPWiki syntax "| cell 1"
 * =>
 * "&lt;input type='text' name='sectionID' id='sectionID' value='cell 1' /&gt;"
 * </code>
 * 
 * <p>
 * where <code>sectionID</code> is the id in the KDOM.
 * </p>
 * 
 * @author smark
 * @see KnowWEDomRenderer
 * @see Table
 */
public class TableCellContentRenderer extends KnowWEDomRenderer<TableCellContent> {

	/**
	 * Determines of the DelegateRenderer is called for content
	 */
	private boolean callDelegate;

	/**
	 * Creates a new renderer.
	 * 
	 * @param callDelegate determines if the delegateRenderer should be called
	 *        for rendering the content
	 */
	public TableCellContentRenderer(boolean callDelegate) {
		this.callDelegate = callDelegate;
	}

	/**
	 * Creates a new Renderer which calls the delegateRenderer
	 */
	public TableCellContentRenderer() {
		this(true);
	}

	@Override
	public void render(KnowWEArticle article, Section<TableCellContent> sec, UserContext user, StringBuilder string) {

		String content;

		if (callDelegate) {
			StringBuilder builder = new StringBuilder();
			DelegateRenderer.getInstance().render(article, sec, user, builder);
			content = builder.toString();
		}
		else {
			content = sec.getOriginalText();
		}

		string.append(wrappContent(content, sec, user));
	}

	/**
	 * Wraps the content of the cell (sectionText) with the HTML-Code needed for
	 * the table
	 */
	protected String wrappContent(String sectionText, Section<TableCellContent> sec, UserContext user) {

		String sectionID = sec.getID();
		StringBuilder html = new StringBuilder();

		boolean sort = TableUtils.sortTest(sec);
		boolean tablehead = TableCellContent.isTableHeadContent(sec);

		if (sort) {
			html.append("<th class=\"sort\">");
		}
		else if (tablehead) {
			html.append("<th>");
		}
		else {
			html.append("<td>");
		}

		generateContent(sectionText, sec, user, sectionID, html);

		if (sort || tablehead) {
			html.append("</th>");
		}
		else {
			html.append("</td>");
		}
		return KnowWEUtils.maskHTML(html.toString());
	}

	protected void generateContent(String sectionText, Section<TableCellContent> s,
			UserContext user, String sectionID, StringBuilder html) {
		if (s.hasQuickEditModeSet(user.getUserName())) {
			Section<Table> father = Sections.findAncestorOfType(s, Table.class);
			String[] values = null;
			String size = null, rows = null, cols = null;

			if (father != null && father.get() instanceof Table) {
				values = (father.get()).getTableAttributesProvider().getAttributeValues(
						s);
				size = (father.get()).getTableAttributesProvider().getWidthAttribute(
						Sections.findAncestorOfType(s, Table.class));
				cols = (father.get()).getTableAttributesProvider().getNoEditColumnAttribute(
						Sections.findAncestorOfType(s, Table.class));
				rows = (father.get()).getTableAttributesProvider().getNoEditRowAttribute(
						Sections.findAncestorOfType(s, Table.class));
			}

			if (TableUtils.isEditable(s, rows, cols)) {
				if (values != null) {
					html.append(createDefaultValueDropDown(values, sectionText, sectionID, size));
				}
				else {
					html.append("<input type='text' name='" + sectionText + "' id='" + sectionID
							+ "' value='" + TableUtils.quote(sectionText)
							+ "' class='table-edit-node' " + TableUtils.getWidth(size) + "/>");
				}
			}
			else {
				html.append(translateTextForView(sectionText, s));
			}
		}
		else {
			html.append(translateTextForView(sectionText, s));
		}
	}

	protected String translateTextForView(String sectionText, Section<?> sec) {
		// can be overriden by subclasses
		return sectionText;
	}

	/**
	 * Creates an DropDown element out of the specified default values.
	 * 
	 * @param values
	 * @param cellcontent
	 * @param nodeID
	 * @return
	 */
	protected String createDefaultValueDropDown(String[] values, String cellcontent, String nodeID, String width) {
		StringBuilder html = new StringBuilder();
		html.append("<select id='" + nodeID + "' class='table-edit-node' "
				+ TableUtils.getWidth(width) + ">");

		List<String> defaultValues = Arrays.asList(values);
		if (defaultValues.contains("[:;:]")) {
			return createTestcaseValueDropDown(defaultValues, cellcontent, nodeID, width);
		}

		if (!defaultValues.contains(cellcontent)) {
			html.append("<option value='" + cellcontent + "' selected=\"selected\">" + cellcontent
					+ "</option>");
		}

		for (String value : defaultValues) {
			if (cellcontent.equals(value)) html.append("<option value='" + cellcontent
					+ "' selected=\"selected\">" + cellcontent + "</option>");
			else html.append("<option value='" + value + "'>" + value + "</option>");
		}

		html.append("</select>");
		return html.toString();
	}

	public boolean isCallDelegate() {
		return callDelegate;
	}

	public void setCallDelegate(boolean callDelegate) {
		this.callDelegate = callDelegate;
	}

	/**
	 * creates the Dropdown for TestcaseTables
	 * 
	 * @created 03.08.2010
	 * @param values
	 * @param cellcontent
	 * @param nodeID
	 * @param width
	 * @return
	 */
	private String createTestcaseValueDropDown(List<String> values, String cellcontent, String nodeID, String width) {
		StringBuilder html = new StringBuilder();
		html.append("<select id='" + nodeID + "' class='table-edit-node' "
				+ TableUtils.getWidth(width) + ">");

		if (!values.contains(cellcontent)) {
			html.append("<option value='" + cellcontent + "' selected=\"selected\">" + cellcontent
					+ "</option>");
		}

		// extracts the questions and solutions from the values
		// and sorts them
		List<String> questions = new ArrayList<String>();
		List<String> solutions = new ArrayList<String>();
		boolean question = true;
		for (String s : values) {
			if (s.equals("[:;:]")) {
				question = false;
				;
			}
			else if (question) {
				questions.add(s);
			}
			else {
				solutions.add(s);
			}
		}

		Collections.sort(questions);
		Collections.sort(solutions);

		// build the select dropdown
		// with 2 optgroups, 1 for questions
		// and 1 for solutions
		html.append(createOptgroup(questions, "questions", cellcontent));
		html.append(createOptgroup(solutions, "solutions", cellcontent));

		return html.toString();
	}

	/**
	 * creates optgroups for dropdown, with type as label
	 * 
	 * @created 03.08.2010
	 * @param values
	 * @param type
	 * @param cellcontent
	 * @return
	 */
	private String createOptgroup(List<String> values, String type, String cellcontent) {
		StringBuilder html = new StringBuilder();
		html.append("<optgroup label=\"" + type + "\">");

		for (String s : values) {
			if (cellcontent.equals(s)) {
				html.append("<option value='" + cellcontent + "' selected=\"selected\">"
						+ cellcontent + "</option>");
			}
			else {
				html.append("<option value='" + s + "'>" + s + "</option>");
			}

		}

		html.append("</optgroup>");
		return html.toString();
	}
}