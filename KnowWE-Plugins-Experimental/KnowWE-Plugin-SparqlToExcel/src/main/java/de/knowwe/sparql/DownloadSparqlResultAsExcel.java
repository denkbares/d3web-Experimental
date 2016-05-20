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
import java.util.Collection;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.denkbares.semanticcore.CachedTupleQueryResult;
import de.d3web.utils.Log;
import de.knowwe.core.Attributes;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message.Type;
import de.knowwe.excel.CreateExcelFromSparql;
import de.knowwe.notification.NotificationManager;
import de.knowwe.notification.StandardNotification;
import de.knowwe.ontology.sparql.SparqlContentType;
import de.knowwe.ontology.sparql.SparqlMarkupType;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

/**
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

		try {
			// find query
			Section<?> rootSection = Sections.get(context.getParameter(Attributes.SECTION_ID));
			Section<SparqlContentType> querySection = Sections.successor(rootSection, SparqlContentType.class);
			if (querySection == null) {
				context.sendError(410, "Query not found, probably the page has been edited while you visiting it. Please reload the page and try again, or contact the administrator if the error persists.");
				return;
			}

			Section<SparqlMarkupType> markupSection = Sections.ancestor(querySection, SparqlMarkupType.class);
			Collection<Rdf2GoCompiler> compilers = Compilers.getCompilers(markupSection, Rdf2GoCompiler.class);
			if (!compilers.isEmpty()) {
				Rdf2GoCore core = compilers.iterator().next().getRdf2GoCore();
				String sparql = Rdf2GoUtils.createSparqlString(core, querySection.getText());
				CachedTupleQueryResult resultSet = core.sparqlSelect(sparql);

				OutputStream outputStream = context.getOutputStream();
				WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
				try {
					CreateExcelFromSparql.addSparqlResultAsSheet(workbook, resultSet, context, core);
					workbook.write();
				}
				finally {
					workbook.close();
				}
			}

		}
		catch (RowsExceededException e) {
			NotificationManager.addNotification(context, new StandardNotification(
					"The maximum number of rows permitted on a worksheet been exceeded.", Type.ERROR));
		}
		catch (WriteException e) {
			Log.severe("error creating excel workbook", e);
		}
	}
}
