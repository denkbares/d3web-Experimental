/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.rdfs.vis;

import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author jochenreutelshofer
 * @created 29.04.2013
 */
public class DotRenderer {

	/**
	 * 
	 * @created 04.09.2012
	 * @param shape
	 */
	public static String buildLabel(RenderingStyle style) {
		return " shape=\"" + style.shape + "\" ";
	}

	/**
	 * 
	 * @created 04.09.2012
	 * @param arrowtail
	 */
	public static String buildRelation(String arrowtail, String color) {
		return " arrowtail=\"" + arrowtail + "\"";
	}

	/**
	 * Given the label of the inner relation, the method returns the String of
	 * the appearance of the relation.
	 * 
	 * @created 06.09.2012
	 * @param label
	 */
	public static String innerRelation(String label, String relationColorCodes) {
		// Basic Relation Attributes
		String arrowtail = "normal";

		String color = getRelationColorCode(label, relationColorCodes);

		return "[ label = \"" + label
				+ "\"" + buildRelation(arrowtail, color) + " ];\n";
	}

	/**
	 * 
	 * @created 07.12.2012
	 * @param label
	 * @return
	 */
	private static String getRelationColorCode(String label, String relationColorCodes) {
		if (relationColorCodes != null) {
			String codeList = relationColorCodes;
			String[] assignments = codeList.split(";");
			for (String assignment : assignments) {
				String[] ass = assignment.split(":");
				String relationName = ass[0];
				String colorCode = ass[1];
				if (relationName.equals(label)) {
					return colorCode;
				}
			}
		}
		return "black";
	}

	/**
	 * The sources from the maps are being written into the String-dotSource.
	 * 
	 * @created 18.08.2012
	 */
	public static String connectSources(String dotSource, Map<String, String> dotSourceLabel, Map<Edge, String> dotSourceRelations) {
		// iterate over the labels and add them to the dotSource
		Iterator<String> labelKeys = dotSourceLabel.keySet().iterator();
		while (labelKeys.hasNext()) {
			String key = labelKeys.next();
			dotSource += "\"" + key + "\"" + dotSourceLabel.get(key);
		}

		// iterate over the relations and add them to the dotSource
		Iterator<Edge> relationsKeys = dotSourceRelations.keySet().iterator();
		while (relationsKeys.hasNext()) {
			Edge key = relationsKeys.next();
			dotSource += "\"" + key.getSubject() + "\"" + " -> " + "\"" + key.getObject() + "\" "
					+ dotSourceRelations.get(key);
		}

		dotSource += "}";

		return dotSource;
	}

}
