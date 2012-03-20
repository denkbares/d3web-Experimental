/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.readbutton;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author dupke
 * @created 23.03.2011
 */
public class ReadbuttonType extends DefaultMarkupType {

	private static DefaultMarkup MARKUP = null;
	public static String HTMLID_READPAGES = "defi-readbuttontype";

	static {
		MARKUP = new DefaultMarkup("readbutton");
		MARKUP.addAnnotation("date", true);
		MARKUP.addAnnotation("realvalue", true);
		MARKUP.addAnnotation("value", false);
		MARKUP.addAnnotation("label", false);
		MARKUP.addAnnotation("discussed", true);
		MARKUP.addAnnotation("closed", true);
		MARKUP.addAnnotation("id", true);
	}

	/**
	 * @param markup
	 */
	public ReadbuttonType() {
		super(MARKUP);
	}

}