/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.excel;

import java.io.UnsupportedEncodingException;
import java.util.List;

import jxl.Cell;
import jxl.CellView;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.sparql.RenderMode;
import de.knowwe.rdf2go.sparql.SparqlResultRenderer;

/**
 * 
 * @author Stefan Plehn
 * @created 23.03.2013
 */
public class CreateExcelFromSparql {

	public static WritableWorkbook addSparqlResultAsSheet(WritableWorkbook wb, QueryResultTable qrt, UserContext user, Rdf2GoCore core) throws RowsExceededException, WriteException, UnsupportedEncodingException {

		WritableSheet s = wb.createSheet("Result", 0);

		List<String> variables = qrt.getVariables();
		ClosableIterator<QueryRow> iterator = qrt.iterator();

		// create header
		for (int i = 0; i < variables.size(); i++) {
			s.addCell(new Label(i, 0, variables.get(i), getBoldCellFormat()));
		}

		int row = 1;
		while (iterator.hasNext()) {

			QueryRow queryRow = iterator.next();

			for (int i = 0; i < variables.size(); i++) {
				Node node = queryRow.getValue(variables.get(i));
				if (node != null) {
					String erg = SparqlResultRenderer.getInstance().renderNode(node,
							variables.get(i), false, user, core, RenderMode.PlainText);
					erg = RenderResult.unmask(erg, user);
					s.addCell(new Label(i, row, erg));
				}
			}
			row++;

		}
		sheetAutoFitColumns(s);
		return wb;
	}

	public static void sheetAutoFitColumns(WritableSheet sheet) {
		for (int i = 0; i < sheet.getColumns(); i++) {
			Cell[] cells = sheet.getColumn(i);
			int longestStrLen = -1;

			if (cells.length == 0) continue;

			// Find the widest cell in the column.
			for (int j = 0; j < cells.length; j++) {
				if (cells[j].getContents().length() > longestStrLen) {
					String str = cells[j].getContents();
					if (str == null || str.isEmpty()) continue;
					longestStrLen = str.trim().length();
				}
			}

			// If not found, skip the column.
			if (longestStrLen == -1) continue;

			// If wider than the max width, crop width.
			if (longestStrLen > 75) longestStrLen = 75;

			CellView cv = sheet.getColumnView(i);

			// Every character is 256 units wide, so scale it.
			cv.setSize(longestStrLen * 256 + 356);
			sheet.setColumnView(i, cv);
		}
	}

	private static WritableCellFormat getBoldCellFormat() throws WriteException {
		WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		WritableCellFormat cellFormat = new WritableCellFormat(boldFont);
		return cellFormat;
	}

}
