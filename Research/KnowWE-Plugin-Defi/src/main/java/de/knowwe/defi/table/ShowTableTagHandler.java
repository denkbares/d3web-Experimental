/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.defi.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.table.Table;
import de.d3web.we.kdom.table.TableRenderer;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class ShowTableTagHandler extends AbstractTagHandler {

	public static String VERSION_KEY = "table-version";

	public ShowTableTagHandler() {
		super("Befragungstabelle");
	}

	private String renderTable(Section<DefineTableMarkup> myTable, UserContext user, String tableid) {
		Section<Table> table = Sections.findSuccessor(myTable, Table.class);

		Section<InputFieldCellContent> inputSec = Sections.findSuccessor(table,
				InputFieldCellContent.class);

		boolean previousInputExists = false;
		int versionsExisting = 1;
		if (inputSec != null) {
			String content = InputFieldCellContent.InputRenderer.getStoredContentForInput(
					inputSec, 0, user.getUserName());
			if (content.length() > 1) {
				previousInputExists = true;

			}
			Section<TableEntryType> entryContentTable = InputFieldCellContent.InputRenderer.findTableToShow(
					tableid, user.getUserName());
			List<Section<VersionEntry>> versions = new ArrayList<Section<VersionEntry>>();
			if (entryContentTable != null) {
				Sections.findSuccessorsOfType(entryContentTable, VersionEntry.class,
						versions);
				versionsExisting = versions.size();
			}
		}

		StringBuilder string = new StringBuilder();
		string.append(KnowWEUtils.maskHTML("<div id='" + myTable.getID()
				+ "' style=''>"));

		user.getParameters().put(TableRenderer.QUICK_EDIT_FLAG, "false");

		for (int versionIndex = 0; versionIndex < versionsExisting; versionIndex++) {
			user.getParameters().put(VERSION_KEY, Integer.toString(versionIndex));
			DelegateRenderer.getInstance().render(myTable.getArticle(),
					table.getFather(),
					user, string);
		}
		if (versionsExisting == 0) {
			DelegateRenderer.getInstance().render(myTable.getArticle(),
					table.getFather(),
					user, string);
		}

		String erneut = "";
		if (previousInputExists) {
			erneut = " erneut";
		}

		string.append(KnowWEUtils.maskHTML("<input type='button' onclick=\"submitTable('"
				+ myTable.getID() + "','" + user.getUserName()
				+ "','" + tableid
				+ "','" + versionsExisting
				+ "')\" name='speichern' value='Änderungen" + erneut + " speichern'/>"));
		if (previousInputExists) {
			string.append(KnowWEUtils.maskHTML("<input type='button' onclick=\"additionalTable('"
					+ myTable.getID() + "','" + user.getUserName()
					+ "','" + tableid
					+ "')\" name='speichern' value='weitere Tabelle hinzufügen'/>"));
		}
		string.append(KnowWEUtils.maskHTML("<span id='tableSubmit_" + tableid + "'>"));
		string.append(KnowWEUtils.maskHTML("</span>"));
		string.append(KnowWEUtils.maskHTML("</div>"));
		return string.toString();
	}

	private Section<DefineTableMarkup> findTableToShow(String id) {
		Collection<KnowWEArticle> articles = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticles();
		for (KnowWEArticle knowWEArticle : articles) {
			List<Section<DefineTableMarkup>> tables = new ArrayList<Section<DefineTableMarkup>>();
			Sections.findSuccessorsOfType(knowWEArticle.getSection(),
					DefineTableMarkup.class,
					tables);
			for (Section<DefineTableMarkup> table : tables) {
				String tableID = table.get().getAnnotation(table, "id");
				if (tableID != null) {
					if (tableID.equals(id)) {
						return table;
					}
				}

			}
		}
		return null;
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section,
			UserContext userContext, Map<String, String> parameters) {
		String id = parameters.get("id");
		if (id == null) {
			return "Error: no table id specified!";
		}
		Section<DefineTableMarkup> myTable = findTableToShow(id);

		if (myTable != null) {
			return renderTable(myTable, userContext, id);
		}
		else {
			return "no table definition found for specified id: " + id;
		}
	}

}
