/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.jurisearch.usersupport;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.jurisearch.questionDef.QuestionDefinitionArea;
import de.knowwe.jurisearch.tree.QuestionTree;
import de.knowwe.kdom.dashtree.DashSubtree;

/**
 * 
 * @author Johannes Dienst
 * @created 04.03.2012
 */
public class POIUtilsJuriSearch
{

	public static void writeSectionToWord(Section<?> markup, FileOutputStream out)
	{
		try
		{
			XWPFDocument doc = new XWPFDocument();

			// write all {@link QuestionTree}
			List<Section<QuestionTree>> qTrees =
					Sections.findSuccessorsOfType(markup, QuestionTree.class);

			for (Section<QuestionTree> tree : qTrees)
			{
				Section<DashSubtree> subtree = Sections.findSuccessor(tree, DashSubtree.class);
				// Section<?> sub = subtree.getChildren().get(1);
				XWPFParagraph par = doc.createParagraph();
				String[] subLines = subtree.getText().split("\\r\\n");
				for (String line : subLines)
				{
					POIUtilsJuriSearch.createAndSetStyles(par, line);
				}
			}

			// write all {@link QuestionDefinitionArea}
			List<Section<QuestionDefinitionArea>> qAreas =
					Sections.findSuccessorsOfType(markup, QuestionDefinitionArea.class);
			for (Section<QuestionDefinitionArea> area : qAreas)
			{
				XWPFParagraph par = doc.createParagraph();
				XWPFRun run = par.createRun();
				String[] areaLines = area.getText().split("\\r\\n");
				for (String line : areaLines)
				{
					POIUtilsJuriSearch.createAndSetStyles(par, line);
				}
			}
			doc.write(out);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Reads out the styles from the text and creates runs accordingly in
	 * paragraph. Supported styles and markup are Italic: ''italic'', html-tag i
	 * Bold: __bold__, html-tag b Underlined: html-tag u
	 * 
	 * @created 09.03.2012
	 * @param par
	 * @param text
	 */
	private static void createAndSetStyles(XWPFParagraph par, String text)
	{
		text = text.replaceAll("''", "<i>");
		text = text.replaceAll("</i>", "<i>");
		text = text.replaceAll("__", "<b>");
		text = text.replaceAll("</b>", "<b>");
		text = text.replaceAll("</u>", "<u>");

		Pattern p = Pattern.compile("<");
		Matcher m = p.matcher(text);

		int start = 0;
		int end = 0;
		int startNormalText = 0;
		int endNormalText;
		while (m.find())
		{
			// Set run with normally styled text
			endNormalText = m.start();
			String runText = text.substring(startNormalText, endNormalText);
			XWPFRun run = par.createRun();
			if (runText.startsWith("*") || runText.startsWith("#")) runText = "    " + runText;
			run.setText(runText);

			// Styled text
			start = m.start() + 3;
			m.find();
			end = m.start();
			runText = text.substring(start, end);
			run = par.createRun();
			run.setText(runText);

			char styleTag = text.charAt(m.start() + 1);
			if (styleTag == 'i') run.setItalic(true);
			if (styleTag == 'b') run.setBold(true);
			if (styleTag == 'u') run.setUnderline(UnderlinePatterns.SINGLE);

			startNormalText = end + 3;
		}

		XWPFRun run = par.createRun();

		// some styling
		if (end != 0) end += 3;
		// no styling
		else if (end == 0 && (text.startsWith("*") || text.startsWith("#"))) text = "    " + text;

		String restText = text.substring(end);
		run.setText(restText);
		run.addBreak();
	}
}
