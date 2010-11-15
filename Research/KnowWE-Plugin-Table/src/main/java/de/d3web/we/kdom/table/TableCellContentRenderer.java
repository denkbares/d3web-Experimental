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

package de.d3web.we.kdom.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

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

	private static TableCellContentRenderer instance = null;

	public static TableCellContentRenderer getInstance() {
		if (instance == null) {
			instance = new TableCellContentRenderer();
		}
		return instance;
	}

	@Override
	public void render(KnowWEArticle article, Section<TableCellContent> sec, KnowWEUserContext user, StringBuilder string) {
		string.append(wrappContent(sec.getOriginalText(), sec, user));
	}

	/**
	 * Wraps the content of the cell (sectionText) with the HTML-Code needed for
	 * the table
	 */
	protected String wrappContent(String sectionText, Section<TableCellContent> sec, KnowWEUserContext user) {

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

	protected void generateContent(String sectionText, Section<TableCellContent> sec,
			KnowWEUserContext user, String sectionID, StringBuilder html) {
		if (sec.hasQuickEditModeSet(user.getUserName())) {
			Section<Table> father = sec.findAncestorOfType(Table.class);
			String[] values = null;
			String size = null, rows = null, cols = null;

			if (father != null && father.getObjectType() instanceof Table) {
				values = ((Table) father.getObjectType()).getTableAttributesProvider().getAttributeValues(
						sec);
				size = ((Table) father.getObjectType()).getTableAttributesProvider().getWidthAttribute(
						sec.findAncestorOfType(Table.class));
				cols = ((Table) father.getObjectType()).getTableAttributesProvider().getNoEditColumnAttribute(
						sec.findAncestorOfType(Table.class));
				rows = ((Table) father.getObjectType()).getTableAttributesProvider().getNoEditRowAttribute(
						sec.findAncestorOfType(Table.class));
			}

			if (TableUtils.isEditable(sec, rows, cols)) {
				if (values != null) {
					html.append(createDefaultValueDropDown(values, sectionText, sectionID, size));
				}
				else {
					html.append("<input type='text' name='" + sectionText + "' id='" + sectionID
							+ "' value='" + sectionText
							+ "' class='table-edit-node' " + TableUtils.getWidth(size) + "/>");
				}
			}
			else {
				html.append(TableUtils.quote(translateTextForView(sectionText, sec)));
			}
		}
		else {
			html.append(TableUtils.quote(translateTextForView(sectionText, sec)));
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
