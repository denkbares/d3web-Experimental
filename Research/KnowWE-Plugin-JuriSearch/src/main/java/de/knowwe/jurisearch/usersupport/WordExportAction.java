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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;


/**
 * 
 * @author Johannes Dienst
 * @created 04.03.2012
 */
public class WordExportAction extends AbstractAction
{

	private static ResourceBundle bundle = ResourceBundle.getBundle("Usersupport_messages");

	@Override
	public void execute(UserActionContext context) throws IOException
	{
		String title = context.getTitle();
		String user = context.getUserName();

		KnowWEArticle article =
				KnowWEEnvironment.getInstance().getArticle(context.getWeb(), context.getTitle());

		String extensionPath = System.getProperty("java.io.tmpdir") + "/docbook-" + title + ".docx";
		File file = new File(extensionPath);
		FileOutputStream out = new FileOutputStream(file);

		POIUtilsJuriSearch.writeSectionToWord(article.getSection(), out);

		out.flush();
		out.close();

		KnowWEEnvironment.getInstance().getWikiConnector().setPageLocked(title, user);

		// write the downloadlink beneath the exportbutton
		Writer writer = context.getWriter();
		writer.append(
				"<a href=\"file://"+ extensionPath + "\" target=\"_blank\">" +
						bundle.getString("download-export") +  "</a>"
				);
	}
}
