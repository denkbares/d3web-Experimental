/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.we.hermes;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryRow;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;
import de.d3web.we.kdom.rendering.PageAppendHandler;
import de.d3web.we.user.UserContext;

/**
 * 
 * @author grotheer
 * @created 18.11.2010
 */
public class ConceptBrowsingHandler implements PageAppendHandler {

	@Override
	public String getDataToAppend(String topic, String web, UserContext user) {
		String query = "SELECT ?x WHERE {?x rdf:type lns:Hermes-Object} ORDER BY ASC(?x)";
		
		ClosableIterator<QueryRow> result =  Rdf2GoCore.getInstance().sparqlSelectIt(query);
		List<String> titleList = new ArrayList<String>();
		try {
			while (result.hasNext()) {
				QueryRow row = result.next();

				String title = row.getValue("x").toString();

				try {
					title = URLDecoder.decode(title, "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				title = title.substring(title.indexOf("#") + 1);
				if (KnowWEEnvironment.getInstance().getWikiConnector().doesPageExist(title)) {
					titleList.add(title);
				}
			}
		}
		catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ModelRuntimeException e) {
			e.printStackTrace();
		}

		if (titleList.contains(topic)) {
			int i = titleList.indexOf(topic);
			String str = "  [{If group='Editoren'\n\n\\\\ \\\\\n%%(text-align:center)\n";
			if (i > 0) {
				String before = titleList.get(i - 1);
				str += "[" + before + "] < < < < ";
			}
			str += topic;
			if (i < titleList.size() - 1) {
				String next = titleList.get(i + 1);
				str += " > > > > [" + next + "]";
			}
			str += "\n%%\n}]";
			return str;
		}

		return "";
	}

	@Override
	public boolean isPre() {
		return false;
	}

}
