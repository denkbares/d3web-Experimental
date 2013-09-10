/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.knowwe.defi.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.defi.utils.ReplaceSectionUtils;

public class InsertAdditionalTableVersionAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getUserName();
		String articleName = SubmitTableContentAction.getDataArticleNameForUser(username);
		ArticleManager articleManager = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);
		Article article = articleManager.getArticle(
				articleName);

		// find table for tableid
		String tableid = context.getParameter(SubmitTableContentAction.TABLE_ID);
		Section<TableEntryContentType> contentSectionForTableID = SubmitTableContentAction.findContentSectionForTableID(
				tableid, article);

		// find version blocks
		List<Section<VersionEntry>> versionEntries = new ArrayList<Section<VersionEntry>>();
		Sections.findSuccessorsOfType(contentSectionForTableID, VersionEntry.class,
				versionEntries);
		int numberOfExistingVersions = versionEntries.size();

		// find out number of inputs to generate
		Section<VersionEntry> version0 = versionEntries.get(0);
		List<Section<InputContent>> inputEntries = new ArrayList<Section<InputContent>>();
		Sections.findSuccessorsOfType(version0, InputContent.class, inputEntries);
		int numberOfInputs = inputEntries.size();

		// generate empty inputs
		Map<Integer, String> emptyInputData = new HashMap<Integer, String>();
		for (int i = 0; i < numberOfInputs; i++) {
			emptyInputData.put(new Integer(i), "");
		}

		// create new markup for empty input data
		String newContent = SubmitTableContentAction.renderMarkupForOneVersion(
				numberOfExistingVersions, emptyInputData);

		// insert new markup into page
		Map<String, String> nodesMap = new HashMap<String, String>();
		String oldText = contentSectionForTableID.getText();
		String completeNewText = oldText.substring(0,
				oldText.length() - 2)
				+ newContent + "\n-";
		nodesMap.put(contentSectionForTableID.getID(),
				completeNewText);
		ReplaceSectionUtils.replaceSections(context, nodesMap);
	}

}
