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
import org.ontoware.rdf2go.util.SparqlUtil;

import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

 /**
 *
 * @author adm_rieder
 * @created 01.10.2012
 */
 public class DataBaseHelper {

	public static List<String> getConnectedNodeNamesOfType(String startNode, String conType)
	{

		List<String> connectedNodesList = new ArrayList<String>();

		// connectedNodesList.add(startNode);

		QueryResultTable table = null;
		// String temp = startNode.replace("+", "_");
		// System.out.println("temp" + temp);
		// startNode = temp;

		try {
			startNode = URLDecoder.decode(startNode, "UTF-8");
			System.out.println(URLDecoder.decode(startNode, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		startNode = startNode.replace(" ", "_");
		startNode = startNode.replace("(", "_");
		startNode = startNode.replace(")", "_");
		startNode = startNode.replace(".", "_");
		startNode = startNode.replace("?", "_");
		// startNode = SparqlUtil.(startNode);
		System.out.println("vor sparql------------" + startNode);

		startNode = SparqlUtil.sparqlEncode(startNode);
		table = Rdf2GoCore.getInstance().sparqlSelect(
				// gib mir die verbindungen vom subjekt start zu
				// allen anderen
				//
				// gibt den oberknoten vom unterkoncept aus???

				"SELECT ?a WHERE {?a" + " lns:" + conType + " lns:" + startNode + "}");

		for (QueryRow row : table) {

			Node node = row.getValue("a");// .toString();// in der Hashmap das
			String keyurl = Rdf2GoUtils.getLocalName(node); // Praedikat
			System.out.println("keyurl" + keyurl);

			// macht die plus weg
			try {
				keyurl = URLDecoder.decode(keyurl, "UTF-8");
				System.out.println(URLDecoder.decode(keyurl, "UTF-8"));
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("hal" + keyurl);
			// keyurl, am wert
			// hängt die url mit
			// dran

			String key = keyurl.substring(keyurl.indexOf("=") + 1);// hier wird
			// key = key.replace(" ", "_");
			// key = key.replace(".", "a");
			System.out.println("replaced" + key);
			// die url
			// information
			// abgeschnitten

			// einfügen in die hashmap
			if (!key.contains("Resource")) {
				connectedNodesList.add(key);
			}

		}

		return connectedNodesList;

	}

	// //////////////////////////////////////////////////////////////////////////////////////////777

	public static List<String> getConnectedNodeNamesOfTypeReverse(String startNode, String conType)
	{

		List<String> connectedNodesList = new ArrayList<String>();

		// connectedNodesList.add(startNode);

		QueryResultTable table = null;
		// String temp = startNode.replace("+", "_");
		// System.out.println("temp" + temp);
		// startNode = temp;

		try {
			startNode = URLDecoder.decode(startNode, "UTF-8");
			System.out.println(URLDecoder.decode(startNode, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		startNode = startNode.replace(" ", "_");
		startNode = startNode.replace("(", "_");
		startNode = startNode.replace(")", "_");
		startNode = startNode.replace(".", "_");
		startNode = startNode.replace("?", "_");
		// startNode = SparqlUtil.(startNode);
		System.out.println("vor sparql------------" + startNode);

		startNode = SparqlUtil.sparqlEncode(startNode);
		table = Rdf2GoCore.getInstance().sparqlSelect(
				// gib mir die verbindungen vom subjekt start zu
				// allen anderen
				//
				// gibt den oberknoten vom unterkoncept aus???

				// "SELECT ?a WHERE {?a" + " lns:" + conType + " lns:" +
				// startNode + "}");
				"SELECT ?a WHERE {lns:" + startNode + " lns:" + conType + " ?a}");
		for (QueryRow row : table) {

			Node node = row.getValue("a");// .toString();// in der Hashmap das
			String keyurl = Rdf2GoUtils.getLocalName(node); // Praedikat
			System.out.println("keyurl" + keyurl);

			// macht die plus weg
			try {
				keyurl = URLDecoder.decode(keyurl, "UTF-8");
				System.out.println(URLDecoder.decode(keyurl, "UTF-8"));
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("hal" + keyurl);
			// keyurl, am wert
			// hängt die url mit
			// dran

			String key = keyurl.substring(keyurl.indexOf("=") + 1);// hier wird
			// key = key.replace(" ", "_");
			// key = key.replace(".", "a");
			System.out.println("replaced" + key);
			// die url
			// information
			// abgeschnitten

			// einfügen in die hashmap
			if (!key.contains("Resource")) {
				connectedNodesList.add(key);
			}

		}

		return connectedNodesList;

	}
	// ////////////////////////////////////////////////////////////////////////////////////////////////7

}

