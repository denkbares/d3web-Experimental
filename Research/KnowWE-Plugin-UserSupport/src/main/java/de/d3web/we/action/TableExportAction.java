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
package de.d3web.we.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;

import de.d3web.we.poi.PoiUtils;
import de.d3web.we.tables.CausalDiagnosisScore;
import de.d3web.we.tables.DecisionTable;
import de.d3web.we.tables.HeuristicDiagnosisTable;
import de.d3web.we.tables.TableUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;


/**
 * Exports a Table to a file on the server.
 * The user can download it and edit the file.
 * Meanwhile the page is locked for a given time.
 * The user has to manually reimport the file. See {@link TableImportAction}.
 * 
 * TODO Coloring of TableCells
 * TODO Storing the file on the server: Now hardcoded for testing
 * TODO Get the downloadlink to work properly
 * 
 * @author Johannes Dienst
 * @created 18.10.2011
 */
public class TableExportAction extends AbstractAction {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(UserActionContext context) throws IOException {

		String tableId = context.getParameter("tableId");
		String title = context.getTitle();
		String web = context.getWeb();
		String user = context.getUserName();

		// Get the table to be exported
		KnowWEArticle article =
				KnowWEEnvironment.getInstance().getArticle(context.getWeb(), context.getTitle());
		Section<?> table = TableUtils.getTableWithId(article, tableId);

		// TODO should i store this here?
		String extensionPath = System.getProperty("java.io.tmpdir");
		extensionPath += "/workbook-" +tableId+ ".xls";
		File file = new File(extensionPath);

		// Create the FileOutPutStream
		//		File file =
		//				new File("C:/Users/ManiaC/Vorlesungen/Diplomarbeit/Export/workbook-"
		//						+tableId+".xls");
		FileOutputStream out = new FileOutputStream(file);

		// Export the Table via PoiUtils
		if (table.getFather().get().isType(CausalDiagnosisScore.class))
			PoiUtils.writeCausalDiagnosisScoreToFile(
					(Section<CausalDiagnosisScore>) table, out);

		if (table.getFather().get().isType(DecisionTable.class))
			PoiUtils.writeDecisionTableToFile(
					(Section<DecisionTable>) table, out);

		if (table.getFather().get().isType(HeuristicDiagnosisTable.class))
			PoiUtils.writeHeuristicDiagnosTableToFile(
					(Section<HeuristicDiagnosisTable>) table, out);

		out.flush();
		out.close();

		// set page lock
		KnowWEEnvironment.getInstance().getWikiConnector().setPageLocked(title, user);

		// write the downloadlink beneath the exportbutton
		Writer writer = context.getWriter();
		writer.append(
				"<a href=\"file://"+ extensionPath + "\">Download exported file</a>"
				);

	}

}
