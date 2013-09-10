/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.medicine;

public class MedicineParameter {

	public static final String STRING = "string";
	public static final String INTEGER = "integer";
	public static final String BOOLEAN = "boolean";

	private final String name;
	private final String tag;
	private final String typ;
	private final String splitStr;
	private final boolean split;
	private String splitPattern;

	public MedicineParameter(String name, String tag, String typ, String split, String pattern) {
		this.name = name;
		this.tag = tag;
		this.typ = typ;
		this.split = true;
		this.splitStr = split;
		this.splitPattern = pattern;
	}

	public MedicineParameter(String name, String tag, String typ) {
		this.name = name;
		this.tag = tag;
		this.typ = typ;
		this.split = false;
		this.splitStr = "";
	}

	public String getName() {
		return name;
	}

	public String getTag() {
		return tag;
	}

	public String getTyp() {
		return typ;
	}

	public String getSplitStr() {
		return splitStr;
	}

	public boolean isSplitted() {
		return split;
	}

	public String getSplitPattern() {
		return splitPattern;
	}
}
