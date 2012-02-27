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
package de.d3web.we.testcase;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author Reinhard Hatko
 * @created 01.09.2011
 */
public class TestcaseImportAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		if (!ServletFileUpload.isMultipartContent(context.getRequest())) {
			context.getWriter().write("no multipart data.");
			return;
		}

		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		upload.setHeaderEncoding("UTF-8");
		List<?> items;

		try {
			items = upload.parseRequest(context.getRequest());
		}
		catch (FileUploadException e) {
			e.printStackTrace(new PrintWriter(context.getWriter()));
			return;
		}

		String sectionID = null;
		FileItem file = null;

		for (Object object : items) {
			FileItem item = (FileItem) object;

			if (item.getFieldName().equals("kdomid")) sectionID = item.getString();
			else if (item.getFieldName().equalsIgnoreCase("testcasefile")) file = item;
		}

		if (file.getSize() == 0) {
			context.getWriter().write("No file specified.");
			return;
		}

		Section<TestcaseTableType> section = (Section<TestcaseTableType>) Sections.getSection(sectionID);
		String master = DefaultMarkupType.getAnnotation(section,
				TestcaseTableType.ANNOTATION_MASTER);

		KnowledgeBase kb = D3webUtils.getKnowledgeBase(context.getWeb(), master);
		List<SequentialTestCase> cases;
		try {

			cases = TestPersistence.getInstance().loadCases(
					file.getInputStream(), kb);
		}
		catch (Exception e) {
			e.printStackTrace(new PrintWriter(context.getWriter()));
			return;
		}
		if (cases == null) {
			context.getWriter().write("No testcases found. " +
					"<a href='Wiki.jsp?page=" + section.getTitle() + "'>back</a>");
			return;
		}
		String stcs = STCToTestcaseTableConverter.convert(cases, master);

		Map<String, String> nodesMap = new HashMap<String, String>();
		nodesMap.put(sectionID, stcs);
		Sections.replaceSections(context, nodesMap);
		context.getResponse().sendRedirect("Wiki.jsp?page=" + section.getTitle());
	}

}
