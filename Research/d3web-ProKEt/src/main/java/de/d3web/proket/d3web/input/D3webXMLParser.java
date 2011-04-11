/**
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
package de.d3web.proket.d3web.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.data.DialogType;
import de.d3web.proket.utils.FileUtils;
import de.d3web.proket.utils.XMLUtils;

/**
 * Class for parsing a given XML file.
 * 
 * @author Martina Freiberg
 * @created 13.10.2010
 */
public class D3webXMLParser {

	// TODO create a default.xml better
	// default dialog/xml that is parsed if nothing else is given
	private String xMLFilename = "default.xml";
	private Node dialogSpec;
	private Node dataSpec;
	private Node specsSpec;

	/**
	 * Constructor specifying the XML file
	 * 
	 * @param xMLFilename
	 */
	public D3webXMLParser(Object xMLFilename) {
		super();
		this.xMLFilename = (String) xMLFilename;
		if (!this.xMLFilename.endsWith(".xml")) {
			this.xMLFilename += ".xml";
		}

		parse();
	}


	/**
	 * Parses the d3web-XML specification file to retrieve both the root node
	 * and the data node.
	 * 
	 * @created 13.10.2010
	 */
	private void parse() {

		// try to red the file depending on what was set in the constructor
		File inputFile = null;
		try {
			// try to get the corresponding XML from the resources folder
			inputFile = FileUtils.getResourceFile("/specs/d3web/" + xMLFilename);
		}
		catch (FileNotFoundException e2) {
		}

		if (inputFile != null) {
			try {
				// try to read xml root node
				dialogSpec = XMLUtils.getRoot(inputFile, null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		NodeList children = dialogSpec.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String name = child.getNodeName();
			if (name.startsWith("#")) {
				continue;
			}
			if (name.equals("data")) {
				dataSpec = child;
			}
			else if (name.equals("specs")) {
				specsSpec = child;
			}
		}
	}


	/**
	 * Return the knowledgebase as specified per its filename in the parsed XML
	 * 
	 * @created 13.10.2010
	 * @return the KnowledgeBase
	 */
	public KnowledgeBase getKnowledgeBase() {
		KnowledgeBase kb = null;
		String kbname = getKnowledgeBaseName();
		kb = D3webUtils.getKnowledgeBase(kbname);
		return kb;
	}

	/**
	 * Retrieve the name of the knowledge base specified in the parsed XML.
	 * 
	 * @created 13.10.2010
	 * @return the String representation of the name of the kb
	 */
	public String getKnowledgeBaseName() {
		String kbname = "";
		kbname = XMLUtils.getStr((Element) dataSpec, "kb", null);

		return kbname;
	}

	/**
	 * Reads the DialogStrategy from the parsed XML file. This attribute needs
	 * to be one of the values of DialogStrategy enum.
	 * 
	 * @created 13.10.2010
	 * @return the parsed DialogStrategy
	 */
	public DialogStrategy getStrategy() {

		String strat = XMLUtils.getStr((Element) dialogSpec, "dstrategy", null);
		return DialogStrategy.valueOf(strat.toUpperCase());
	}



	/**
	 * Reads the DialogType from the parsed XML file. This attribute needs to be
	 * one of the values of DialogType enum.
	 * 
	 * @created 13.10.2010
	 * @return the parsed DialogType
	 */
	public DialogType getType() {
		String type = XMLUtils.getStr((Element) dialogSpec, "dtype", null);
		return DialogType.valueOf(type.toUpperCase());
	}

	/**
	 * Reads the CSS attribute from the parsed XML file.
	 * 
	 * @created 13.10.2010
	 * @return the CSS String
	 */
	public String getCss() {
		return XMLUtils.getStr((Element) dialogSpec, "css", null);
	}

	public String getHeader() {
		return XMLUtils.getStr((Element) dialogSpec, "header", "");
	}

	public int getDialogColumns() {
		Integer col = XMLUtils.getInt((Element) dialogSpec, "dialogcolumns");
		if (col == null) {
			col = 1;
		}
		return col;
	}

	public int getQuestionnaireColumns() {
		Integer col = XMLUtils.getInt((Element) dialogSpec, "questionnairecolumns");
		if (col == null) {
			col = 1;
		}
		return col;
	}


	// returns false in case "no" is given OR nothing
	public boolean getLogin() {

		String log = XMLUtils.getStr((Element) dialogSpec, "login", null);
		if (log != null && log.toLowerCase().equals("yes")) {
			System.out.println("login xes");
			return true;
		}
		return false;
	}


	public String getUserPrefix() {
		return XMLUtils.getStr((Element) dialogSpec, "userprefix", "");
	}

	public HashMap<String, HashMap<String, String>> getSingleSpecs() {
		HashMap<String, HashMap<String, String>> specs =
				new HashMap<String, HashMap<String, String>>();

		/*
		 * NodeList children = specsSpec.getChildNodes(); for (int i = 0; i <
		 * children.getLength(); i++) { Node child = children.item(i); String
		 * name = child.getNodeName(); if (name.startsWith("#")) { continue; }
		 * else { String id = XMLUtils.getStr((Element) child, "id", "");
		 * HashMap<String, String> s = new HashMap<String, String>();
		 * s.put("id", id); String selectBox = XMLUtils.getStr((Element) child,
		 * "selectbox", ""); s.put("selectbox", selectBox); specs.put(id, s); }
		 * }
		 */

		return specs;
	}
}
