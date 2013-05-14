/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.ophtovisD3;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.google.gson.Gson;

import de.knowwe.ophtovisD3.utils.NodeWithName;

import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.wisskont.util.Tree;

/**
 * 
 * @author adm_rieder
 * @created 22.10.2012
 */
public class GraphBuilder {

	static LinkedList<Integer[]> connections = new LinkedList<Integer[]>();
	static int indexNumber = 0;
	static Tree <NodeWithName>resultTree;
	static boolean treeIsHere = false;

	public GraphBuilder() {
		connections = new LinkedList<Integer[]>();
		indexNumber = 0;
	}

	public static HashMap<Integer, String> buildNameandConnectionList(String startConcept, String connectionType) {
		HashMap<Integer, String> nameAndNumber = new HashMap<Integer, String>();
		String nextcocept = startConcept;
		do {
			nameAndNumber.put(indexNumber, nextcocept);
			nameAndNumber = getNameandConnectionof(nextcocept, connectionType, nameAndNumber,
					indexNumber);
			List<String> sucsessor = DataBaseHelper.getConnectedNodeNamesOfType(nextcocept,
					"temporalGraph", true);
			if (!sucsessor.isEmpty()) nextcocept = sucsessor.get(0);
			else nextcocept = "";
		} while (!nextcocept.isEmpty());
		return nameAndNumber;
	}

	public LinkedList<Integer[]> getconnections() {
		return connections;
	}

	public static HashMap<Integer, String> getNameandConnectionof(String startConcept, String connectionType, HashMap<Integer, String> list, int index) {
		int dadIndex = indexNumber;
		List<String> childs = DataBaseHelper.getConnectedNodeNamesOfType(startConcept,
				connectionType, false);
		if (!childs.isEmpty()) {
			for (String child : childs) {
				if (!list.containsValue(child)) {
					list.put(++indexNumber, child);
					Integer[] toAdd = {
							dadIndex, indexNumber };
					connections.add(toAdd);
					list = getNameandConnectionof(child, connectionType, list, indexNumber);
				}
			}

			return list;
		}
		else {
			return list;
		}
	}

	public String bulidNamesandConnectionsJSON(String startConcept, String connectionType) {
		return "{ " + buildNamesJSON(startConcept, connectionType) + " , " + buildConnectionsJSON()
				+ " } ";

	}

	public String buildNamesJSON(String startConcept, String connectionType) {
		String result = "\"nodes\": [";
		HashMap<Integer, String> map = buildNameandConnectionList(startConcept, connectionType);
		Set<Integer> keys = map.keySet();
		int i = 0;
		for (Integer key : keys) {
			i++;
			String value = map.get(key);
			result += "{ \"name\": \"" + value + "\"}";
			if (i < keys.size()) result += " , ";
		}
		return result += "]";
	}

	public String buildConnectionsJSON() {
		String result = "\"edges\": [";
		int i = 0;
		for (Integer[] ints : connections) {
			i++;
			result += "{ \"source\":" + ints[0] + ", \"target\":" + ints[1] + " }";
			if (i < connections.size()) result += " , ";
		}
		return result += "]";
	}

	public static String buildGraph(String startConcept, String connectionType, String helpconnectionType, boolean getConnectionAmount) {
		String result;
		if(!false){
		resultTree = new Tree<NodeWithName>(new NodeWithName("Wurzel","0"));
		System.out.println("Insert Wurzel");
		resultTree.insertNode(new NodeWithName(startConcept, DataBaseHelper.countQuerytresultstoString(startConcept)));
		System.out.println("insert " + startConcept);
		if (getConnectionAmount) {}
		String fatherOfTheMoment = startConcept;
		while (!fatherOfTheMoment.isEmpty()) {
			 getChildConcepts(fatherOfTheMoment, connectionType, getConnectionAmount,
					resultTree);
			List<String> nextfather = DataBaseHelper.getConnectedNodeNamesOfType(fatherOfTheMoment,
					helpconnectionType, true);
			System.out.println("fathers = " + nextfather.size());
			if (nextfather.size() >= 1) {
				fatherOfTheMoment = nextfather.get(0);
				resultTree.insertNode(new NodeWithName(fatherOfTheMoment ,DataBaseHelper.countQuerytresultstoString(fatherOfTheMoment)));
				System.out.println("insert node" + fatherOfTheMoment);
			}
			else {
				if (getConnectionAmount) {}
				Gson gson = new Gson();
				result = gson.toJson(resultTree);
				treeIsHere=true;
				return result;
			}
		}}
		treeIsHere=true;
		Gson gson = new Gson();
		result = gson.toJson(resultTree);
		return result;
	}
	public static String builtPartTree(String startConcept, String connectionType){
		String root =DataBaseHelper.getRootConcept( startConcept,  connectionType);
		Tree<NodeWithName> resultTree = new Tree<NodeWithName>(new NodeWithName(root));
		resultTree =getChildConceptTree(root, connectionType, resultTree);
		resultTree.removeNodeFromTree(new NodeWithName(startConcept,DataBaseHelper.countQuerytresultstoString(startConcept),false));
		resultTree.insertNode(new NodeWithName(startConcept, true));
		Gson gson = new Gson();
		return gson.toJson(resultTree);
		
	}
	
	public static Tree<NodeWithName> getChildConceptTree(String father, String connectionType, Tree<NodeWithName> tree) {
		List<String> childs = DataBaseHelper.getConnectedNodeNamesOfType(father, connectionType,
				false);
		if (childs.isEmpty()) {
			return tree;
		}
		else {
			for (int i = 0; i < childs.size(); i++) {
				String string = childs.get(i);
				tree.insertNode(new NodeWithName(childs.get(i),Integer.toString(DataBaseHelper.countQuerytresults(childs.get((i))))));
				getChildConceptTree(string, connectionType, tree);	
				}
			}
		return tree;

	}

	public static String getChildConcepts(String father, String connectionType, boolean getConnectionAmount, Tree<NodeWithName> tree) {
		String resultString = "";
		System.out.println("getchild aufgerufen mit " + father);
		List<String> childs = DataBaseHelper.getConnectedNodeNamesOfType(father, connectionType,
				false);
		System.out.println("Konzept hat " + childs.size());
		if (childs.isEmpty()) {
			return resultString += "}";
		}
		else {
			for (int i = 0; i < childs.size(); i++) {
				String string = childs.get(i);
				if(!childs.get(i).equals(father)){
				tree.insertNode(new NodeWithName(childs.get(i),Integer.toString(DataBaseHelper.countQuerytresults(childs.get(i)))));
				System.out.println("einfügen " + childs.get(i));
				resultString += getChildConcepts(string, connectionType, getConnectionAmount, tree);
				}
			}
		}
		return resultString;

	}

}
