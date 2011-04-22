/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.caseTrain.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.knowwe.caseTrain.message.DuplicateAttributeError;
import de.knowwe.caseTrain.message.InvalidAttributeError;
import de.knowwe.caseTrain.message.MissingAttributeError;
import de.knowwe.caseTrain.type.MetaLine.AttributeName;


/**
 * Managing the MetaAttributes.
 * 
 * @author Johannes Dienst
 * @created 20.04.2011
 */
public class MetaAttributes {

	private static final String CASE_ID_KEY = "FALL_ID";
	private static final String CASE_TITLE = "FALL_TITEL";
	private static final String CASE_AUTHOR = "FALL_AUTOR";
	private static final String CASE_VERSION = "FALL_VERSION";
	private static final String CASE_DATE = "FALL_DATUM";
	private static final String CASE_PASS = "BESTEHEN_AB";
	private static final String CASE_POINTS = "FALL_PUNKTZAHL";
	private static final String CASE_HISTORY = "HISTORIE";

	private final HashMap<String, String> attributes;

	private static MetaAttributes uniqueInstance;

	public static MetaAttributes getInstance() {
		if(uniqueInstance == null) {
			uniqueInstance = new MetaAttributes();
		}
		return uniqueInstance;
	}

	/**
	 * TODO: fallpunktzahl Ã€ndern
	 * TODO: bessere bilder
	 * TODO: vervollstÃ€ndigen
	 */

	private MetaAttributes() {
		this.attributes = new HashMap<String, String>();
		attributes.put("CASE_ID_KEY", CASE_ID_KEY);
		attributes.put("CASE_TITLE", CASE_TITLE);
		attributes.put("CASE_AUTHOR", CASE_AUTHOR);
		attributes.put("CASE_VERSION", CASE_VERSION);
		attributes.put("CASE_DATE", CASE_DATE);
		attributes.put("CASE_PASS", CASE_PASS);
		attributes.put("CASE_POINTS", CASE_POINTS);
		attributes.put("CASE_HISTORY", CASE_HISTORY);
	}

	public boolean contains(String attribute) {
		for(String key : attributes.keySet()) {
			if(this.attributes.get(key).equals(attribute)) return true;
		}
		return false;
	}

	/**
	 * Compares the given List if it is conform with the
	 * required MetaAttrubites. Duplicates, Missing etc.
	 * 
	 * @param atts
	 * @return
	 */
	public Collection<KDOMReportMessage> compareAttributeList(
			List<Section<AttributeName>> atts) {

		List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>();
		Set<String> foundOnes = new TreeSet<String>();

		// TODO: Ugly. AttributeName should be without ":"
		String ori = "";
		for (Section<AttributeName> section : atts) {
			ori = section.getOriginalText().trim();
			if (foundOnes.contains(ori)) {
				messages.add(new DuplicateAttributeError(ori));
				continue;
			}
			if (!this.contains(ori)) {
				messages.add(new InvalidAttributeError(ori));
				continue;
			}
			foundOnes.add(ori);
		}

		for (String s : this.attributes.keySet()) {
			if (!foundOnes.contains(this.attributes.get(s))) {
				messages.add(new MissingAttributeError(s));
			}
		}

		return messages;
	}

}
