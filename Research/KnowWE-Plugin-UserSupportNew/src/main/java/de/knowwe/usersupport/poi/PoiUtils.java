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
package de.knowwe.usersupport.poi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import officeconverter.Config;
import officeconverter.Converter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import de.d3web.we.knowledgebase.KnowledgeBaseType;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.ActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.usersupport.tables.CausalDiagnosisScore;
import de.knowwe.usersupport.tables.DecisionTable;
import de.knowwe.usersupport.tables.HeuristicDiagnosisTable;
import de.knowwe.usersupport.tables.ITable;
import de.knowwe.usersupport.tables.InnerTable;
import de.knowwe.usersupport.tables.TableCell;
import de.knowwe.usersupport.tables.TableHeaderCell;
import de.knowwe.usersupport.tables.TableHeaderLine;
import de.knowwe.usersupport.tables.TableLine;
import de.knowwe.usersupport.tables.TableUtils;


/**
 * 
 * Offers methods to be used when your are
 * ex(im)porting {@link CausalDiagnosisScore}, {@link DecisionTable} and
 * {@link HeuristicDiagnosisTable} with Apache-POI.
 * 
 * @author Johannes Dienst
 * @created 17.10.2011
 */
public class PoiUtils
{


	private static int idPostfix = 0;
	
	public static int getIdPostfix()
	{
		return idPostfix++;
	}
	
	private static CellStyle getErrorCellStyle(Workbook wb)
	{
		CellStyle cs = wb.createCellStyle();
		Font f = wb.createFont();
		f.setColor(IndexedColors.RED.getIndex());
		cs.setFont(f);
		return cs;
	}

	private static CellStyle getWarningCellStyle(Workbook wb)
	{
		CellStyle cs = wb.createCellStyle();
		Font f = wb.createFont();
		f.setColor(IndexedColors.ORANGE.getIndex());
		cs.setFont(f);
		return cs;
	}

	/**
	 * Constructs the table-markup from a given xls-File.
	 * 
	 * @created 19.10.2011
	 * @param file load xls from
	 * @param tableId
	 * @param article
	 * @return the table-markup
	 * @throws IOException
	 */
	public static String importTableFromFile(File in, String tableId, String article, ActionContext context) throws IOException
	{
		StringBuilder buildi = new StringBuilder();
		try
		{
		FileInputStream input = new FileInputStream(in);
		Workbook wb = new HSSFWorkbook(input);

		Sheet sheet = wb.getSheetAt(0);

		// If there are no rows do nothing
		if ( (sheet.getLastRowNum() == 0) && (sheet.getPhysicalNumberOfRows() == 0) )
			return null;

		// Iterate over rows and push each row in an array
		// So it is easier to build the new table
		List<String[]> rowsList = new ArrayList<String[]>();
		Row row = null;
		for ( Iterator<Row> it = sheet.rowIterator(); it.hasNext(); )
		{
			row = it.next();
			String[] cells = new String[row.getPhysicalNumberOfCells()];

			for ( int i = 0; i < row.getPhysicalNumberOfCells(); i++)
			{
				cells[i] = row.getCell(i).getStringCellValue();
			}

			rowsList.add(cells);
		}

		int maxCellLength = TableUtils.getWidestTableCellLengthPoiUtils(rowsList);

		// Rebuild the Table with the arrays
		for ( String[] arr : rowsList )
		{
			for (String c : arr)
			{
				buildi.append(c);
				buildi.append(TableUtils.generateFillString(c, maxCellLength));
				buildi.append("|");
			}
			buildi.replace(buildi.length()-1, buildi.length(), ",\r\n");
		}

		// Replace the old table with the new one
		ArticleManager manager =
				Environment.getInstance().getArticleManager(Environment.DEFAULT_WEB);
		Article art = manager.getArticle(article);
		List<Section<ITable>> itables = Sections.findSuccessorsOfType(art.getRootSection(), ITable.class);

		for (Section<ITable> table : itables)
		{
			if (table.getID().equals(tableId))
			{
				Map<String, String> nodeMap = new HashMap<String, String>();
				nodeMap.put(tableId, buildi.toString());

				Sections.replaceSections(context, nodeMap);
				Sections.findSuccessor(table, InnerTable.class);
			}
		}
		}
		catch (NullPointerException e1)
		{
			throw new IOException();
		}

		return buildi.toString();
	}

	public static void writeCausalDiagnosisScoreToFile(
			Section<CausalDiagnosisScore> diagnosisTable, FileOutputStream out) throws IOException {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("CausalDiagnosisTable");

		Row row = null;

		Section<TableHeaderLine> headerLine = Sections.findSuccessor(diagnosisTable, TableHeaderLine.class);
		int i = 0;
		if (headerLine != null)
		{
			row = sheet.createRow(i++);
			PoiUtils.writeTableHeaderLine(headerLine, row, out);
		}

		List<Section<TableLine>> lines =
				Sections.findChildrenOfType(diagnosisTable, TableLine.class);
		Section<TableLine> line = null;

		for (int j = 0;j < lines.size();j++)
		{
			row = sheet.createRow(i++);
			line = lines.get(j);
			PoiUtils.writeTableLine(line, row, out, wb, sheet);
		}

		PoiUtils.autoSizeSheetColumns(lines, sheet);

		wb.write(out);
	}

	public static void writeHeuristicDiagnosTableToFile(
			Section<HeuristicDiagnosisTable> heuristicTable, FileOutputStream out) throws IOException {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("CausalDiagnosisTableSheet");

		Row row = null;

		// write header
		Section<TableHeaderLine> headerLine = Sections.findSuccessor(heuristicTable, TableHeaderLine.class);
		int i = 0;
		if (headerLine != null)
		{
			row = sheet.createRow(i++);
			PoiUtils.writeTableHeaderLine(headerLine, row, out);
		}

		List<Section<TableLine>> lines =
				Sections.findChildrenOfType(heuristicTable, TableLine.class);
		Section<TableLine> line = null;

		for (int j = 0;j < lines.size();j++)
		{
			row = sheet.createRow(i++);
			line = lines.get(j);
			PoiUtils.writeTableLine(line, row, out, wb, sheet);
		}

		PoiUtils.autoSizeSheetColumns(lines, sheet);

		// Write workbook to file
		wb.write(out);
	}

	public static void writeDecisionTableToFile(Section<DecisionTable> decisionTable,
			FileOutputStream out) throws IOException {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("DecisionTable");

		Row row = null;

		Section<TableHeaderLine> headerLine = Sections.findSuccessor(decisionTable, TableHeaderLine.class);
		int i = 0;
		if (headerLine != null)
		{
			row = sheet.createRow(i++);
			PoiUtils.writeTableHeaderLine(headerLine, row, out);
		}

		List<Section<TableLine>> lines =
				Sections.findChildrenOfType(decisionTable, TableLine.class);
		Section<TableLine> line = null;

		for (int j = 0;j < lines.size();j++)
		{
			row = sheet.createRow(i++);
			line = lines.get(j);
			PoiUtils.writeTableLine(line, row, out, wb, sheet);
		}

		PoiUtils.autoSizeSheetColumns(lines, sheet);

		wb.write(out);
	}

	public static String importWordFromFile(File in, String tableId, String article, ActionContext context) throws IOException
	{
		ArticleManager manager =
				Environment.getInstance().getArticleManager(Environment.DEFAULT_WEB);
		Article art = manager.getArticle(article);
		Section<Article> artSec = art.getRootSection();
		Environment.getInstance().getWikiConnector();

		// Save {@link KnowledgeBaseType} and {@link WordDefaultMarkup}
		StringBuilder recovery = new StringBuilder();
		List<Section<KnowledgeBaseType>> knowledgeBaseTypes = Sections.findSuccessorsOfType(artSec, KnowledgeBaseType.class);
		for (Section<KnowledgeBaseType> kType : knowledgeBaseTypes)
		{
			recovery.append(kType.getText());
		}
		recovery.append("%%WordExportImport \r\n");
		recovery.append("Text \r\n");
		recovery.append("@package: default \r\n");
		recovery.append("% \r\n");


		//		FileInputStream input = new FileInputStream(in);
		//		XWPFDocument doc = new XWPFDocument(input);
		//		List<XWPFParagraph> paragraphs = doc.getParagraphs();
		//		StringBuilder docText = new StringBuilder();

		String dirPath = in.getParent();
		String absolutePath = in.getAbsolutePath();
		Config conf = new Config("html", false, "UTF-8", dirPath , true, false, true);
		File inputFile = new File(absolutePath);
		boolean inputExists = inputFile.exists();
		File outputFile = new File(dirPath + "/test.html");
//		inputFile = new File("C:/ConverterTestDir/docbook-395f91f4.doc");
		boolean outputExists = outputFile.exists();
//		outputFile = new File("C:/ConverterTestDir/test.html");
		Converter.convertFile2File(inputFile,
						outputFile, conf);

		File testFile = new File(dirPath + "/test.html");
		BufferedReader r = new BufferedReader(new FileReader(testFile));
		StringBuilder buildi = new StringBuilder();

		List<String> docLines = new ArrayList<String>();
		String readMe;
		while ( (readMe = r.readLine()) != null) {
			PoiUtils.cleanHTMLLine(readMe, docLines);
		}

		StringBuilder docText = new StringBuilder();
		boolean isTree = false;
		for (String line : docLines)
		{
			if (line.startsWith("-") && !isTree)
			{
				isTree = true;
				docText.append("%%baum \r\n start \r\n");
			}
			if (!line.startsWith("-") && isTree)
			{
				docText.append("% \r\n");
				isTree = false;
			}

			docText.append(line + "\r\n");
		}

		//		boolean isTree = false;
		//		for (XWPFParagraph par : paragraphs)
		//		{
		//			String parText = par.getParagraphText();
		//			if (parText.startsWith("-") && !isTree)
		//			{
		//				isTree = true;
		//				docText.append("%%baum \r\n start \r\n");
		//			}
		//			if (!parText.startsWith("-") && isTree)
		//			{
		//				docText.append("% \r\n");
		//			}
		//
		//			docText.append(parText + "\r\n");
		//		}

		recovery.append(docText);

		Map<String, String> nodeMap = new HashMap<String, String>();
		nodeMap.put(artSec.getID(), recovery.toString());

		Sections.replaceSections(context, nodeMap);
		return null;
	}

	/**
	 * 
	 * @created 11.03.2012
	 * @param s
	 * @return
	 */
	private static String cleanHTMLLine(String s, List<String> docLines)
	{
		s = s.replaceAll("\\<p\\>FRAGE\\</p\\>", "FRAGE\\<br/\\>");
		s = s.replaceAll("\\<li\\>\\<p\\>", "\\<li\\>");
		s = s.replaceAll("\\</p\\>\\</li\\>", "\\</li\\>");
		s = s.replaceAll("\\<p\\>", "");
		s = s.replaceAll("\\</p\\>", "\\<br/\\>");
		
		String[] lines = s.split("\\<br/\\>");
		for (String l : lines)
			docLines.add(l);
		return s;
	}

	/**
	 * Autoformats the column width. So that the text fits.
	 * 
	 * @created 20.12.2011
	 * @param lines
	 * @param sheet
	 */
	private static void autoSizeSheetColumns(List<Section<TableLine>> lines, Sheet sheet) {

		if (lines.isEmpty()) return;

		int columnCount = Sections.findChildrenOfType(lines.get(0), TableCell.class).size();
		for (int k = 0; k < columnCount; k++)
			sheet.autoSizeColumn(k);
	}

	/**
	 * Writes a TableHeaderLine into the OutputStream.
	 * 
	 * @created 19.10.2011
	 * @param line
	 * @param row
	 * @param out
	 */
	private static void writeTableHeaderLine(Section<TableHeaderLine> line, Row row, FileOutputStream out) {

		List<Section<TableHeaderCell>> cells = Sections.findChildrenOfType(line, TableHeaderCell.class);

		Cell c = null;
		Section<TableHeaderCell> cell = null;
		for (int i = 0; i < cells.size(); i++)
		{
			cell = cells.get(i);
			c = row.createCell(i);
			c.setCellValue(cell.getText());
		}
	}

	/**
	 * Writes a complete TableLine into the OutputStream.
	 * 
	 * @created 19.10.2011
	 * @param line
	 * @param row
	 * @param out
	 * @param sheet
	 */
	private static void writeTableLine(Section<TableLine> line, Row row, FileOutputStream out, Workbook wb, Sheet sheet)
	{

		List<Section<TableCell>> cells = Sections.findChildrenOfType(line, TableCell.class);

		Cell c = null;
		Section<TableCell> cell = null;
		for (int i = 0; i < cells.size(); i++)
		{
			cell = cells.get(i);
			c = row.createCell(i);

			// get the article compiling this cell
			Article compilingArticle = KnowWEUtils.getCompilingArticles(line).iterator().next();

			Collection<Message> allmsgs = Messages.getMessagesFromSubtree(compilingArticle, cell);

			// Render warnings/errors/notices
			CellStyle cs = PoiUtils.colorTableCell(allmsgs, wb);
			if (cs != null)
				c.setCellStyle(cs);

			c.setCellValue(cell.getText());

			PoiUtils.setCellComment(c, row, wb, sheet, allmsgs);
		}
	}

	/**
	 * 
	 * @created 17.02.2012
	 * @param c
	 * @param row
	 * @param wb
	 * @param sheet
	 * @param messages
	 */
	private static void setCellComment(Cell c, Row row, Workbook wb, Sheet sheet, Collection<Message> messages)
	{
		if (!Messages.getErrors(messages).isEmpty() || !Messages.getWarnings(messages).isEmpty())
		{
			CreationHelper factory = wb.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();

			// When the comment box is visible, have it show in a 1x3 space
			ClientAnchor anchor = factory.createClientAnchor();
			anchor.setCol1(c.getColumnIndex());
			anchor.setCol2(c.getColumnIndex()+1);
			anchor.setRow1(row.getRowNum());
			anchor.setRow2(row.getRowNum()+3);

			// Create the comment and set the text+author
			List<Message> theComments = new ArrayList<Message>();
			theComments.addAll(Messages.getErrors(messages));
			theComments.addAll(Messages.getWarnings(messages));
			StringBuilder buildi = new StringBuilder();
			for (Message m : theComments) {
				buildi.append(m.getVerbalization());
			}
			Comment comment = drawing.createCellComment(anchor);
			RichTextString str = factory.createRichTextString(buildi.toString());
			comment.setString(str);
			c.setCellComment(comment);
		}

	}

	private static CellStyle colorTableCell(Collection<Message> messages, Workbook wb)
	{
		if (!Messages.getErrors(messages).isEmpty())
		{
			return PoiUtils.getErrorCellStyle(wb);
		}
		else if (!Messages.getWarnings(messages).isEmpty())
		{
			return PoiUtils.getWarningCellStyle(wb);
		}
		//		else if (!Messages.getNotices(messages).isEmpty())
		//		{
		//			return PoiUtils.getNoticeCellStyle(wb);
		//		}
		else {
			return null;
		}
	}

	//	private static CellStyle getNoticeCellStyle(Workbook wb)
	//	{
	//		CellStyle cs = wb.createCellStyle();
	//		Font f = wb.createFont();
	//		f.setColor(IndexedColors.SKY_BLUE.getIndex());
	//		cs.setFont(f);
	//		return cs;
	//	}

	//	private static Workbook createBlankHSSFWorkbook() throws IOException
	//	{
	//
	//		// create a new file
	//		FileOutputStream out = new FileOutputStream("workbook.xls");
	//
	//		Workbook wb = new HSSFWorkbook();
	//		Sheet s = wb.createSheet();
	//		Row r = null;
	//		Cell c = null;
	//
	//		// create 3 cell styles
	//		CellStyle cs = wb.createCellStyle();
	//		CellStyle cs2 = wb.createCellStyle();
	//		CellStyle cs3 = wb.createCellStyle();
	//		DataFormat df = wb.createDataFormat();
	//
	//		/**
	//		 * How to create Fonts
	//		 */
	//		// create 2 fonts objects
	//		Font f = wb.createFont();
	//		Font f2 = wb.createFont();
	//		//set font 1 to 12 point type
	//		f.setFontHeightInPoints((short) 12);
	//		//make it blue
	//		f.setColor( (short)0xc );
	//		// make it bold
	//		//arial is the default font
	//		f.setBoldweight(Font.BOLDWEIGHT_BOLD);
	//		//set font 2 to 10 point type
	//		f2.setFontHeightInPoints((short) 10);
	//		//make it red
	//		f2.setColor( Font.COLOR_RED );
	//		//make it bold
	//		f2.setBoldweight(Font.BOLDWEIGHT_BOLD);
	//		f2.setStrikeout( true );
	//
	//		/**
	//		 * How to set cell styles
	//		 */
	//		//set cell stlye
	//		cs.setFont(f);
	//		//set the cell format
	//		cs.setDataFormat(df.getFormat("#,##0.0"));
	//
	//		//set a thin border
	//		cs2.setBorderBottom(cs2.BORDER_THIN);
	//		//fill w fg fill color
	//		cs2.setFillPattern(CellStyle.SOLID_FOREGROUND);
	//		//set the cell format to text see DataFormat for a full list
	//		cs2.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
	//
	//		// set the font
	//		cs2.setFont(f2);
	//
	//		// set the sheet name in Unicode
	//		wb.setSheetName(0, "\u0422\u0435\u0441\u0442\u043E\u0432\u0430\u044F " +
	//				"\u0421\u0442\u0440\u0430\u043D\u0438\u0447\u043A\u0430" );
	//		// in case of plain ascii
	//		// wb.setSheetName(0, "HSSF Test");
	//
	//		/**
	//		 * Create a sheet with 30 rows
	//		 */
	//		int rownum;
	//		for (rownum = (short) 0; rownum < 30; rownum++)
	//		{
	//			// create a row
	//			r = s.createRow(rownum);
	//			// on every other row
	//			if ((rownum % 2) == 0)
	//			{
	//				// make the row height bigger  (in twips - 1/20 of a point)
	//				r.setHeight((short) 0x249);
	//			}
	//
	//			//r.setRowNum(( short ) rownum);
	//			// create 10 cells (0-9) (the += 2 becomes apparent later
	//			for (short cellnum = (short) 0; cellnum < 10; cellnum += 2)
	//			{
	//				// create a numeric cell
	//				c = r.createCell(cellnum);
	//				// do some goofy math to demonstrate decimals
	//				c.setCellValue(rownum * 10000 + cellnum
	//						+ (((double) rownum / 1000)
	//								+ ((double) cellnum / 10000)));
	//
	//				String cellValue;
	//
	//				// create a string cell (see why += 2 in the
	//				c = r.createCell((short) (cellnum + 1));
	//
	//				// on every other row
	//				if ((rownum % 2) == 0)
	//				{
	//					// set this cell to the first cell style we defined
	//					c.setCellStyle(cs);
	//					// set the cell's string value to "Test"
	//					c.setCellValue( "Test" );
	//				}
	//				else
	//				{
	//					c.setCellStyle(cs2);
	//					// set the cell's string value to "\u0422\u0435\u0441\u0442"
	//					c.setCellValue( "\u0422\u0435\u0441\u0442" );
	//				}
	//
	//
	//				// make this column a bit wider
	//				s.setColumnWidth((short) (cellnum + 1), (short) ((50 * 8) / ((double) 1 / 20)));
	//			}
	//		}
	//
	//		s = wb.createSheet();
	//		wb.setSheetName(1, "DeletedSheet");
	//		wb.removeSheetAt(1);
	//		//end deleted sheet
	//
	//		// Write to outputfile
	//		wb.write(out);
	//		out.close();
	//
	//		return wb;
	//	}
}
