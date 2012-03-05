/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.jurisearch.usersupport;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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
				Section<?> sub = subtree.getChildren().get(1);
				XWPFParagraph par = doc.createParagraph();
				XWPFRun run = par.createRun();
				run.setText(sub.getText());
			}

			// write all {@link QuestionDefinitionArea}
			List<Section<QuestionDefinitionArea>> qAreas =
					Sections.findSuccessorsOfType(markup, QuestionDefinitionArea.class);
			for (Section<QuestionDefinitionArea> area : qAreas)
			{
				XWPFParagraph par = doc.createParagraph();
				XWPFRun run = par.createRun();
				run.setText("\r\n" + area.getText() + "\r\n");
			}
			doc.write(out);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
