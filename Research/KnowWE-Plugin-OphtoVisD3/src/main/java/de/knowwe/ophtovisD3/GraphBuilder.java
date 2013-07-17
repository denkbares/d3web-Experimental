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

import de.knowwe.ophtovisD3.utils.JsonFactory;
import de.knowwe.ophtovisD3.utils.NodeWithName;
import de.knowwe.ophtovisD3.utils.StringShortener;
import de.knowwe.ophtovisD3.utils.StringShortener.ElliminationType;
import de.knowwe.termbrowser.util.Tree;
import de.knowwe.wisskont.browser.WissassHierarchyProvider;

/**
 * 
 * @author adm_rieder
 * @created 22.10.2012
 */
public class GraphBuilder {

	static LinkedList<Integer[]> connections = new LinkedList<Integer[]>();
	static int indexNumber = 0;
	static Tree<NodeWithName> resultTree;
	static boolean treeIsHere = false;
	static StringShortener shorty = new StringShortener(ElliminationType.MIDDLE, 12);

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
	
	public static String buildGraphExperimental(String connectionType, String highlighted){
		LinkedList <String> resultList = DataBaseHelper.getAllObjectsConnectedBy(connectionType);
		resultTree = new Tree<NodeWithName>(new NodeWithName("Wurzel", "Wissensbasis"),
				new WissassHierarchyProvider());
		String label, result;
		for (String string : resultList) {
			label = shorty.shorten(string);
			resultTree.insertNode(new NodeWithName(string, label));
		}
		resultTree = hightlightConcept(resultTree, highlighted);
		result =JsonFactory.toJSON(resultTree);
		return result;
		
	}

	public static String buildGraph(String startConcept, String connectionType, String helpconnectionType, boolean getConnectionAmount) {
		String result, label;
		System.out.println("Graphbuilder started");
	    //	GraphbuilderForeignKB.buildTree();
		if (!false) {
			resultTree = new Tree<NodeWithName>(new NodeWithName("Wurzel", "Wissensbasis"),
					new WissassHierarchyProvider());
			label =shorty.shorten("Anamnese_Patientensituation");
			resultTree.insertNode(new NodeWithName("Anamnese_Patientensituation",label));
			String fatherOfTheMoment = "Anamnese_Patientensituation";
			while (!fatherOfTheMoment.isEmpty()) {
				getChildConcepts(fatherOfTheMoment, connectionType, getConnectionAmount,
						resultTree);
				List<String> nextfather = DataBaseHelper.getConnectedNodeNamesOfType(
						fatherOfTheMoment,
						helpconnectionType, true);
				if (nextfather.size() >= 1) {
					fatherOfTheMoment = nextfather.get(0);
					label=shorty.shorten(fatherOfTheMoment);
					resultTree.insertNode(new NodeWithName(fatherOfTheMoment,
							label));
				}else{
					break;
				}
			}
		}
		treeIsHere = true;
		resultTree = hightlightConcept(resultTree, startConcept);
		result =JsonFactory.toJSON(resultTree);
		return result;
	}
	
	public static Tree<NodeWithName>hightlightConcept(Tree<NodeWithName> tree, String conceptToHightlight){
		NodeWithName toAlter =tree.find(new NodeWithName(conceptToHightlight, shorty.shorten(conceptToHightlight)));
		if(!(toAlter==null)){
		toAlter.setHighligted();
		tree.insertNode(toAlter);
		}else{
			NodeWithName toDelete =null;
			Set<NodeWithName> nodes =tree.getNodes();
			for (NodeWithName nodeWithName : nodes) {
				if(nodeWithName.toString().equals(conceptToHightlight))
					toDelete=nodeWithName;
			}
			boolean worked= tree.removeNodeFromTree(toDelete);
			tree.insertNode(new NodeWithName(conceptToHightlight, null, shorty.shorten(conceptToHightlight), true));
		}
		return tree;
	}

	public static String builtPartTree(String startConcept, String connectionType) {
		String root = DataBaseHelper.getRootConcept(startConcept, connectionType);
		boolean highlight = (root.equals(startConcept));
		Tree<NodeWithName> resultTree = new Tree<NodeWithName>(new NodeWithName(root, highlight),
				new WissassHierarchyProvider());
		resultTree = getChildConceptTree(root, connectionType, resultTree, startConcept);

		return JsonFactory.toJSON(resultTree);

	}

	public static Tree<NodeWithName> getChildConceptTree(String father, String connectionType, Tree<NodeWithName> tree, String toHighlight) {
		List<String> childs = DataBaseHelper.getConnectedNodeNamesOfType(father, connectionType,
				false);
		if (childs.isEmpty()) {
			return tree;
		}
		else {
			for (int i = 0; i < childs.size(); i++) {
				String string = childs.get(i);
				if (!childs.get(i).equals(father)) {
					boolean highlight = false;
					if (childs.get(i).equals(toHighlight)) highlight = true;
					shorty.shorten(childs.get(i));
					tree.insertNode(new NodeWithName(childs.get(i),
							Integer.toString(DataBaseHelper.countQuerytresults(childs.get(i))),
							highlight));
					getChildConceptTree(string, connectionType, tree, toHighlight);
				}
			}
		}
		return tree;

	}

	public static String getChildConcepts(String father, String connectionType, boolean getConnectionAmount, Tree<NodeWithName> tree) {
		String resultString = "";
		String label;
		List<String> childs = DataBaseHelper.getConnectedNodeNamesOfType(father, connectionType,
				false);
		if (childs.isEmpty()) {
			return resultString += "}";
		}
		else {
			for (int i = 0; i < childs.size(); i++) {
				String string = childs.get(i);
				if (!childs.get(i).equals(father)) {
					label =shorty.shorten(childs.get(i));
					tree.insertNode(new NodeWithName(childs.get(i),
							label));
					resultString += getChildConcepts(string, connectionType, getConnectionAmount,
							tree);
				}
			}
		}
		return resultString;
	}
}
