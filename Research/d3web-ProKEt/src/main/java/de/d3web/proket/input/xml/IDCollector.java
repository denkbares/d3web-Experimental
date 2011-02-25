/**
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

package de.d3web.proket.input.xml;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Collector for used IDs and double IDs.
 * 
 * @author Martina Freiberg
 * @author Johannes Mitlmeier, Tobias Mikschl
 * 
 */
public class IDCollector extends DefaultHandler {

	private List<String> doubleIds = new ArrayList<String>();
	private Hashtable<String, String> usedIds = new Hashtable<String, String>();

	public List<String> getDoubleIds() {
		return doubleIds;
	}

	public Hashtable<String, String> getUsedIds() {
		return usedIds;
	}

	@Override
	/**
	 * Process an element
	 * If id value is already contained in the usedIDs collection, put
	 * it into the doubleIDs collection, otherwise in the used collection.
	 */
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (atts.getValue("id") != null) {
			if (!usedIds.containsKey(atts.getValue("id"))) {
				usedIds.put(atts.getValue("id"), qName);
			} else {
				doubleIds.add(atts.getValue("id"));
			}
		}
	}
}
