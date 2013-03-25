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
package de.knowwe.sparql;

import java.io.IOException;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.ontoware.rdf2go.model.QueryResultTable;

import de.knowwe.core.Attributes;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.excel.CreateExcelFromSparql;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.sparql.SparqlContentType;
import de.knowwe.rdf2go.sparql.SparqlMarkupType;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

/**
 * 
 * @author Stefan Plehn
 * @created 22.03.2013
 */
public class DownloadSparqlResultAsExcel extends AbstractAction {

	public static final String PARAM_FILENAME = "filename";

	@Override
	public void execute(UserActionContext context) throws IOException {

		String filename = context.getParameter(PARAM_FILENAME);

		context.setContentType("application/vnd.ms-excel");
		context.setHeader("Content-Disposition", "attachment; filename=\""
				+ filename + "\"");

		WritableWorkbook workbook = null;

		try {

			Section<?> rootSection = Sections.getSection(context.getParameter(Attributes.SECTION_ID));
			Section<SparqlContentType> markupSection = Sections.findSuccessor(rootSection,
					SparqlContentType.class);
			Section<SparqlMarkupType> realMarkupSection = Sections.findAncestorOfType(
					markupSection,
					SparqlMarkupType.class);

			Rdf2GoCore core = Rdf2GoCore.getInstance(realMarkupSection);
			String sparql = Rdf2GoUtils.createSparqlString(markupSection);
			QueryResultTable resultSet = core.sparqlSelect(sparql);

			OutputStream outputStream = context.getOutputStream();

			workbook = Workbook.createWorkbook(outputStream);
			workbook = CreateExcelFromSparql.addSparqlResultAsSheet(workbook, resultSet,
					context, core);


			workbook.write();
			workbook.close();




		}
		catch (RowsExceededException e) {
			e.printStackTrace();
		}
		catch (WriteException e) {
			e.printStackTrace();
		}

	}

}
