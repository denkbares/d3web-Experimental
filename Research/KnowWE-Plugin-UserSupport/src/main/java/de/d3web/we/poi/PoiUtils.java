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
package de.d3web.we.poi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import de.d3web.we.tables.DecisionTable;
import de.d3web.we.tables.TableCell;
import de.d3web.we.tables.TableLine;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;


/**
 * 
 * @author Johannes Dienst
 * @created 17.10.2011
 */
public class PoiUtils {

	public static Workbook createBlankHSSFWorkbook() throws IOException {

		// create a new file
		FileOutputStream out = new FileOutputStream("workbook.xls");

		Workbook wb = new HSSFWorkbook();
		Sheet s = wb.createSheet();
		Row r = null;
		Cell c = null;

		// create 3 cell styles
		CellStyle cs = wb.createCellStyle();
		CellStyle cs2 = wb.createCellStyle();
		CellStyle cs3 = wb.createCellStyle();
		DataFormat df = wb.createDataFormat();

		/**
		 * How to create Fonts
		 */
		// create 2 fonts objects
		Font f = wb.createFont();
		Font f2 = wb.createFont();
		//set font 1 to 12 point type
		f.setFontHeightInPoints((short) 12);
		//make it blue
		f.setColor( (short)0xc );
		// make it bold
		//arial is the default font
		f.setBoldweight(Font.BOLDWEIGHT_BOLD);
		//set font 2 to 10 point type
		f2.setFontHeightInPoints((short) 10);
		//make it red
		f2.setColor( Font.COLOR_RED );
		//make it bold
		f2.setBoldweight(Font.BOLDWEIGHT_BOLD);
		f2.setStrikeout( true );

		/**
		 * How to set cell styles
		 */
		//set cell stlye
		cs.setFont(f);
		//set the cell format
		cs.setDataFormat(df.getFormat("#,##0.0"));

		//set a thin border
		cs2.setBorderBottom(cs2.BORDER_THIN);
		//fill w fg fill color
		cs2.setFillPattern(CellStyle.SOLID_FOREGROUND);
		//set the cell format to text see DataFormat for a full list
		cs2.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));

		// set the font
		cs2.setFont(f2);

		// set the sheet name in Unicode
		wb.setSheetName(0, "\u0422\u0435\u0441\u0442\u043E\u0432\u0430\u044F " +
				"\u0421\u0442\u0440\u0430\u043D\u0438\u0447\u043A\u0430" );
		// in case of plain ascii
		// wb.setSheetName(0, "HSSF Test");

		/**
		 * Create a sheet with 30 rows
		 */
		int rownum;
		for (rownum = (short) 0; rownum < 30; rownum++)
		{
			// create a row
			r = s.createRow(rownum);
			// on every other row
			if ((rownum % 2) == 0)
			{
				// make the row height bigger  (in twips - 1/20 of a point)
				r.setHeight((short) 0x249);
			}

			//r.setRowNum(( short ) rownum);
			// create 10 cells (0-9) (the += 2 becomes apparent later
			for (short cellnum = (short) 0; cellnum < 10; cellnum += 2)
			{
				// create a numeric cell
				c = r.createCell(cellnum);
				// do some goofy math to demonstrate decimals
				c.setCellValue(rownum * 10000 + cellnum
						+ (((double) rownum / 1000)
								+ ((double) cellnum / 10000)));

				String cellValue;

				// create a string cell (see why += 2 in the
				c = r.createCell((short) (cellnum + 1));

				// on every other row
				if ((rownum % 2) == 0)
				{
					// set this cell to the first cell style we defined
					c.setCellStyle(cs);
					// set the cell's string value to "Test"
					c.setCellValue( "Test" );
				}
				else
				{
					c.setCellStyle(cs2);
					// set the cell's string value to "\u0422\u0435\u0441\u0442"
					c.setCellValue( "\u0422\u0435\u0441\u0442" );
				}


				// make this column a bit wider
				s.setColumnWidth((short) (cellnum + 1), (short) ((50 * 8) / ((double) 1 / 20)));
			}
		}

		s = wb.createSheet();
		wb.setSheetName(1, "DeletedSheet");
		wb.removeSheetAt(1);
		//end deleted sheet

		// Write to outputfile
		wb.write(out);
		out.close();

		return wb;
	}

	public static void writeCausalDiagnosisScoreToFile() {

	}

	public static void writeHeuristicDiagnosTableToFile() {

	}

	public static void writeDecisionTableToFile(Section<DecisionTable> decisionTable) {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("Table");

		Row row = null;
		List<Section<TableLine>> lines = Sections.findChildrenOfType(decisionTable, TableLine.class);
		Section<TableLine> line = null;

		for ( int i = 0; i < lines.size(); i++) {
			row = sheet.createRow(i);
			line = lines.get(i);
			PoiUtils.writeTableLine(line, row);
		}

	}



	public static void writeTableLine(Section<TableLine> line, Row row) {

		List<Section<TableCell>> cells = Sections.findChildrenOfType(line, TableCell.class);

		Cell c = null;
		Section<TableCell> cell = null;
		for (int i = 0; i < cells.size(); i++) {
			cell = cells.get(i);
			c = row.createCell(i);
			c.setCellValue(cell.getText());
		}
	}
}
