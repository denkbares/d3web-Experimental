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
package de.knowwe.jurisearch.usersupport.word;

import java.util.ResourceBundle;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;


/**
 * 
 * @author Johannes Dienst
 * @created 04.03.2012
 */
public class WordDefaultMarkupRenderer implements Renderer
{

	private static ResourceBundle bundle = ResourceBundle.getBundle("JuriSearch_messages");

	@Override
	public void render(Section<?> section, UserContext user, StringBuilder string)
	{
		new DefaultMarkupRenderer().render(section, user, string);
		StringBuilder buildi = new StringBuilder();
		this.renderExportImportButton(buildi, section);
		string.append(KnowWEUtils.maskHTML(buildi.toString()));
	}

	/**
	 * 
	 * @created 04.03.2012
	 * @param buildi
	 * @param content
	 */
	private void renderExportImportButton(StringBuilder buildi, Section<?> section)
	{
		String rel = "rel=\"{ " +
				"		objectId : '" + section.getID() + "',"
				+ "}\" ";

//		String exportButton =
//				"<div class=\"table_export_div\">" +
//						"<input class=\"button word-export\" type=\"button\" name=\"Export\" " +
//						"value=\"" + bundle.getString("export_button") + "\" id=\"" + section.getID()  + "-Export\"" + rel + ">" +
//						"<span class=\"table_export_result\" id=\"export-download" + section.getID() + "\"> </span>" +
//						"</div>"
//						;

		//		String inputForm = "<form action=\"\" method=\"post\" enctype=\"multipart/form-data\">" +
		//				"<input name=\"import-textfield-" + section.getID() +"\" type=\"file\" size=\"50\" maxlength=\"100000\" accept=\"application/msexcel/*\">" +
		//				"</form>";
		//
		//		String importButton =
		//				"<div>" +
		//						"<input class=\"button table-import\" type=\"submit\" name=\"Import\"" +
		//						" value=\"Import\" id=\"" + section.getID()  + "-Import\"" + rel + ">" +
		//						"<span id=\"import-upload" + section.getID() + "\">" +
		//						inputForm +
		//						"</span>" +
		//						"</div>";

		String importButton = "<form class=\"file_upload_form\" id=\"file_upload_form\" action=\"uploadreceptor?" +
				"tableId=" + section.getID() +
				"&article=" + section.getTitle() +
				"&filetype=word\" " +
				"method=\"post\" enctype=\"multipart/form-data\" accept-charset=\"UTF-8\">" +
				"<P>" +
				bundle.getString("word-upload") +
				"<P>" +
				"<INPUT type=\"file\" size=\"60\" name=\"excelcontent\">" +
				"<P>" +
				"<INPUT type=\"submit\" name=\"enter\" value=\"" +
				bundle.getString("upload_button") + "\">" +
				"</form>";

		StringBuilder buttons = new StringBuilder();
		buttons.append("<div class=\"table_export_frame\">");
		buttons.append("<div class=\"defaultMarkup\">");
		buttons.append(importButton);
		buttons.append("</div>");
		buttons.append("</div>");


		buildi.append(buttons);
	}

}
