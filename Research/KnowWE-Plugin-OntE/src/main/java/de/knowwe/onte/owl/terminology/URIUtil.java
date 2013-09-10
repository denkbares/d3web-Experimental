/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.onte.owl.terminology;


public class URIUtil {

	public static final String CLASS = "Class";

	public static final String OBJECTPROPERTY = "ObjectProperty";

	public static final String DATATYPEPROPERTY = "DataTypeProperty";

	public static final String THING = "Thing";

	public static final String TYPE = "type";
	public static final String ISA = "isA";

	public static final String SUBCLASSOF = "subClassOf";

	public static final String SUBPROPERTYOF = "subPropertyOf";

	public static final String DOMAIN = "domain";
	public static final String RANGE = "range";

	public static final String[] OBJECT_VOCABULARY = new String[] {
			CLASS, OBJECTPROPERTY, DATATYPEPROPERTY, THING };

	public static final String[] PREDICATE_VOCABULARY = new String[] {
			TYPE, ISA, SUBCLASSOF, DOMAIN, RANGE, SUBPROPERTYOF };

	public static boolean checkForKnownTerms(String termName, String[] terms) {
		boolean found = false;
		for (String string : terms) {
			if (termName.equalsIgnoreCase(string)) {
				found = true;
			}

		}
		return found;
	}

}
