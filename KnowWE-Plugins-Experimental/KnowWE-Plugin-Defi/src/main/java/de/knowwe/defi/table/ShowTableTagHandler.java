/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.table.Table;

public class ShowTableTagHandler extends AbstractTagHandler {

	public static String VERSION_KEY = "table-version";

	public ShowTableTagHandler() {
		super("Befragungstabelle");
	}

	private String renderTable(Section<DefineTableMarkup> myTable, UserContext user, String tableid, boolean singleTableVersion) {
		Section<Table> table = Sections.successor(myTable, Table.class);

		Section<InputFieldCellContent> inputSec = Sections.successor(table,
				InputFieldCellContent.class);

		boolean previousInputExists = false;
		int versionsExisting = 1;
		if (inputSec != null) {
			String content = TableUtils.getStoredContentForInput(
					inputSec, 0, user.getUserName());
			if (content == null) content = "";
			if (content.length() > 1) {
				previousInputExists = true;

			}
			Section<TableEntryType> entryContentTable = TableUtils.findTableEntry(
					tableid, user.getUserName());
			List<Section<VersionEntry>> versions = new ArrayList<>();
			if (entryContentTable != null) {
				Sections.successors(entryContentTable, VersionEntry.class,
						versions);
				versionsExisting = versions.size();
			}
		}

		RenderResult string = new RenderResult(user);
		string.appendHtml("<div id='" + myTable.getID() + "' style=''>");

		// user.getParameters().put(TableRenderer.QUICK_EDIT_FLAG, "false");

		for (int versionIndex = 0; versionIndex < versionsExisting; versionIndex++) {
			user.getParameters().put(VERSION_KEY, Integer.toString(versionIndex));
			DelegateRenderer.getInstance().render(table.getParent(),
					user,
					string);
		}
		if (versionsExisting == 0) {
			DelegateRenderer.getInstance().render(table.getParent(),
					user,
					string);
		}

		String erneut = "";
		if (previousInputExists) {
			erneut = " erneut";
		}

		string.appendHtml("<input type='button' onclick=\"submitTable('"
				+ myTable.getID() + "','" + user.getUserName()
				+ "','" + tableid
				+ "','" + versionsExisting
				+ "')\" name='speichern' value='Änderungen" + erneut + " speichern'/>");

		// tables can be configured to only support single versions using the
		// single-attribute, then no additionalTable-button is rendered
		// if (previousInputExists && !singleTableVersion) {
		if (!singleTableVersion) {
			string.appendHtml("<input type='button' onclick=\"additionalTable('"
					+ myTable.getID() + "','" + user.getUserName()
					+ "','" + tableid
					+ "')\" name='speichern' value='weitere Tabelle hinzufügen'/>");
		}
		string.appendHtml("<span id='tableSubmit_" + tableid + "'>");
		string.appendHtml("</span>");
		string.appendHtml("</div>");
		return string.toStringRaw();
	}



	@Override
	public void render(Section<?> section, UserContext userContext,
			Map<String, String> parameters, RenderResult result) {
		String id = parameters.get("id");
		boolean singleVersionTable = parameters.containsKey("single");
		if (id == null) {
			result.append("Error: no table id specified!");
			return;
		}
		Section<DefineTableMarkup> myTable = TableUtils.findTableDefintion(id);

		if (myTable != null) {
			result.append(renderTable(myTable, userContext, id, singleVersionTable));
		}
		else {
			result.append("no table definition found for specified id: " + id);
		}
	}

}
