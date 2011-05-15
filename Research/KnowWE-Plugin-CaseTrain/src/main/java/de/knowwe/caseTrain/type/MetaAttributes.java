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
 * Managing the MetaAttributes used in {@link MetaDaten}
 * The attributes are taken from {@link http://casetrain.uni-wuerzburg.de/doku/format_case.shtml}
 * 
 * @author Johannes Dienst
 * @created 20.04.2011
 */
public class MetaAttributes {

	// Demanded attributes
	private static final String CASE_ID_KEY = "FALL_ID";
	private static final String CASE_TITLE = "FALL_TITEL";
	private static final String CASE_AUTHOR = "FALL_AUTOR";
	private static final String CASE_VERSION = "FALL_VERSION";
	private static final String CASE_DATE = "FALL_DATUM";
	public static final String CASE_POINTS = "FALL_PUNKTZAHL";
	public static final String CASE_PASS = "BESTEHEN_AB";

	// Optional attributes
	private static final String DURATION_MIN = "DAUER_MIN";
	private static final String DIFFICULTY = "SCHWIERIGKEIT";
	private static final String REQUIREMENTS = "VORAUSSETZUNGEN";
	private static final String HINT = "HINWEIS";
	private static final String COMMENT = "FALL_KOMMENTAR";
	private static final String KEYWORDS = "SCHLAGWORTE";
	public static final String CASE_TODO = "FALL_TODO";
	private static final String CASE_HISTORY = "HISTORIE";
	public static final String SHOW_TIME = "ZEIT_ANZEIGEN";
	public static final String TIME_WEIGHT = "ZEIT_GEWICHT";
	public static final String TIME_LIMIT100 = "ZEIT_GRENZE100";
	public static final String TIME_LIMIT0 = "ZEIT_GRENZE0";
	public static final String FEEDBACK = "FEEDBACK";
	private static final String BACKGROUND = "HINTERGRUNDWISSEN";
	private static final String HW_LINKTEXT = "HW_LINKTEXT";
	public static final String LANGUAGE = "SPRACHE";

	private MetaAttributes() {
		this.demandedAttributes = new TreeSet<String>();
		demandedAttributes.add(CASE_ID_KEY);
		demandedAttributes.add(CASE_TITLE);
		demandedAttributes.add(CASE_AUTHOR);
		demandedAttributes.add(CASE_DATE);
		demandedAttributes.add(CASE_VERSION);
		demandedAttributes.add(CASE_PASS);
		demandedAttributes.add(CASE_POINTS);

		this.optionalAttributes = new TreeSet<String>();
		optionalAttributes.add(DURATION_MIN);
		optionalAttributes.add(DIFFICULTY);
		optionalAttributes.add(REQUIREMENTS);
		optionalAttributes.add(HINT);
		optionalAttributes.add(COMMENT);
		optionalAttributes.add(KEYWORDS);
		optionalAttributes.add(CASE_HISTORY);
		optionalAttributes.add(CASE_TODO);
		optionalAttributes.add(SHOW_TIME);
		optionalAttributes.add(TIME_WEIGHT);
		optionalAttributes.add(TIME_LIMIT100);
		optionalAttributes.add(TIME_LIMIT0);
		optionalAttributes.add(FEEDBACK);
		optionalAttributes.add(BACKGROUND);
		optionalAttributes.add(HW_LINKTEXT);
		optionalAttributes.add(LANGUAGE);
	}

	private final TreeSet<String> demandedAttributes;
	private final TreeSet<String> optionalAttributes;

	private static MetaAttributes uniqueInstance;

	public static MetaAttributes getInstance() {
		if(uniqueInstance == null) {
			uniqueInstance = new MetaAttributes();
		}
		return uniqueInstance;
	}

	/**
	 * Returns true if attribute is specified.
	 * False otherwise.
	 * 
	 * @created 28.04.2011
	 * @param attribute
	 * @return
	 */
	public boolean contains(String attribute) {
		boolean retBool = demandedAttributes.contains(attribute);
		if(retBool) {return true;}
		return optionalAttributes.contains(attribute);
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

		String ori = "";
		for (Section<AttributeName> section : atts) {
			ori = section.getOriginalText().trim();
			if (foundOnes.contains(ori) && !ori.equals(CASE_TODO)) {
				messages.add(new DuplicateAttributeError(ori));
				continue;
			}
			if (!this.contains(ori)) {
				messages.add(new InvalidAttributeError(ori));
				continue;
			}
			foundOnes.add(ori);
		}

		for (String s : this.demandedAttributes) {
			if (!foundOnes.contains(s)) {
				messages.add(new MissingAttributeError(s));
			}
		}

		return messages;
	}

	private HashMap<String, String> attributesForXML;

	public HashMap<String, String> getAttributesForXMLMap() {
		if (this.attributesForXML != null)
			return this.attributesForXML;

		this.attributesForXML = new HashMap<String, String>();

		this.attributesForXML.put(CASE_ID_KEY, "ID");
		this.attributesForXML.put(CASE_TITLE, "Title");
		this.attributesForXML.put(CASE_AUTHOR, "Author");
		this.attributesForXML.put(CASE_VERSION, "Version");
		this.attributesForXML.put(COMMENT, "Comment");
		this.attributesForXML.put(CASE_TODO, "Todo");
		this.attributesForXML.put(CASE_HISTORY, "Historie");
		this.attributesForXML.put(CASE_DATE, "Date");
		this.attributesForXML.put(BACKGROUND, "URL");
		this.attributesForXML.put(HW_LINKTEXT, "URLText");
		this.attributesForXML.put(DURATION_MIN, "DurationMinutes");
		this.attributesForXML.put(REQUIREMENTS, "Prereqs");
		this.attributesForXML.put(DIFFICULTY, "Difficulty");
		this.attributesForXML.put(HINT, "Notice");
		this.attributesForXML.put(KEYWORDS, "Keywords");

		// Score
		this.attributesForXML.put(CASE_POINTS, "weight");
		this.attributesForXML.put(CASE_PASS, "minScoreForSuccess");
		this.attributesForXML.put(TIME_LIMIT100, "okTimeSeconds");
		this.attributesForXML.put(TIME_LIMIT0, "maxTimeSeconds");
		this.attributesForXML.put(TIME_WEIGHT, "weightTime");

		// Misc - Not in MetaDaten: colorScheme
		this.attributesForXML.put(FEEDBACK, "feedback");
		this.attributesForXML.put(LANGUAGE, "language");
		this.attributesForXML.put(SHOW_TIME, "showClock");

		return this.attributesForXML;
	}
}
