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
 package de.knowwe.ophtovis;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

 /**
 *
 * @author adm_rieder
 * @created 01.10.2012
 */
 public class DataBaseHelper {

	public static List<String> getConnectedNodeNamesOfType(String startNode, String conType, boolean reverse)
	{
		List<String> connectedNodesList = new ArrayList<String>();
		QueryResultTable table = null;
		startNode = createSparqlURI(startNode);

		if (reverse) {
			table = Rdf2GoCore.getInstance().sparqlSelect(
					"SELECT ?a WHERE {" + startNode + " lns:" + conType + " ?a}");
		}
		else {
			table = Rdf2GoCore.getInstance().sparqlSelect(

					"SELECT ?a WHERE { ?a  lns:" + conType + " " + startNode + "}");
		}
		System.out.println("Query was + " + startNode + " " + conType + "found :");
		for (QueryRow row : table) {
			Node node = row.getValue("a");// .toString();// in der Hashmap das
			String keyurl = Rdf2GoUtils.getLocalName(node); // Praedikat
			try {
				keyurl = URLDecoder.decode(keyurl, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}


			String key = keyurl.substring(keyurl.indexOf("=") + 1);// hier wird
			if (!key.contains("Resource")) {
				connectedNodesList.add(key);
			}

		}

		return connectedNodesList;

	}



	private static String createSparqlURI(String name) {
		name = name.replaceAll(" ", "+");
		if (name.contains("+") || name.contains(".")) {
			String localNamespace = Rdf2GoCore.getInstance().getLocalNamespace();

			return "<" + localNamespace + name + ">";
			// return name;
		}

		try {
			return "lns:" + URLDecoder.decode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}

