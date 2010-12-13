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

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.rendering.PageAppendHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;
import de.knowwe.semantic.sparql.SPARQLUtil;

/**
 * 
 * @author grotheer
 * @created 18.11.2010
 */
public class ConceptBrowsingHandler implements PageAppendHandler {

	@Override
	public String getDataToAppend(String topic, String web, KnowWEUserContext user) {
		String query = "SELECT ?x WHERE {?x rdf:type lns:Hermes-Object} ORDER BY ASC(?x)";
		TupleQueryResult result = SPARQLUtil.executeTupleQuery(query);
		List<String> titleList = new ArrayList<String>();
		try {
			while (result.hasNext()) {
				BindingSet set = result.next();

				String title = set.getBinding("x").getValue().stringValue();

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
		catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
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
